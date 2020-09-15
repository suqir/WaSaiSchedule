package com.suqir.wasaischedule.ui.schedule

import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ShareCompat
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.logic.bean.TableBean
import com.suqir.wasaischedule.logic.bean.TableSelectBean
import com.suqir.wasaischedule.ui.DonateFragment
import com.suqir.wasaischedule.ui.UpdateFragment
import com.suqir.wasaischedule.ui.base_view.BaseActivity
import com.suqir.wasaischedule.ui.course_add.AddCourseActivity
import com.suqir.wasaischedule.ui.intro.AboutActivity
import com.suqir.wasaischedule.ui.schedule_manage.ScheduleManageActivity
import com.suqir.wasaischedule.ui.schedule_settings.ScheduleSettingsActivity
import com.suqir.wasaischedule.ui.settings.SettingsActivity
import com.suqir.wasaischedule.ui.settings.TimeSettingsActivity
import com.suqir.wasaischedule.ui.weike_life.WeikeLifeActivity
import com.suqir.wasaischedule.utils.*
import com.suqir.wasaischedule.utils.UpdateUtils.getVersionCode
import es.dmoral.toasty.Toasty
import it.sephiroth.android.library.xtooltip.Tooltip
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.*
import kotlinx.coroutines.delay
import splitties.activities.start
import splitties.dimensions.dip
import splitties.resources.styledDimenPxSize
import splitties.snackbar.action
import splitties.snackbar.longSnack
import java.text.ParseException
import kotlin.math.roundToInt

class ScheduleActivity : BaseActivity() {

    private val REQUEST_CODE_CHOOSE_TABLE = 21
    private val REQUEST_CODE_CHOOSE_BG = 23

    private val viewModel by viewModels<ScheduleViewModel>()
    private var mAdapter: SchedulePagerAdapter? = null

    private lateinit var bottomSheetDialog: BottomSheetDialog

    private val preLoad by lazy(LazyThreadSafetyMode.NONE) {
        getPrefer().getBoolean(Const.KEY_SCHEDULE_PRE_LOAD, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getPrefer().getBoolean(Const.KEY_HIDE_NAV_BAR, false)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        setContentView(R.layout.activity_schedule)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet)
        val json = getPrefer().getString(Const.KEY_OLD_VERSION_COURSE, "")
        if (!json.isNullOrEmpty()) {
            launch {
                try {
                    viewModel.updateFromOldVer(json)
                    Toasty.success(applicationContext, "升级成功~").show()
                } catch (e: Exception) {
                    Toasty.error(applicationContext, "出现异常>_<\n${e.message}").show()
                }
            }
        }

        anko_cl_schedule.postDelayed({
            if (!getPrefer().getBoolean(Const.KEY_HAS_INTRO, false)) {
                initIntro()
            }
        }, 500)

        initView()

        val openTimes = getPrefer().getInt(Const.KEY_OPEN_TIMES, 0)
        if (openTimes % 10 != 0) {
            getPrefer().edit {
                putInt(Const.KEY_OPEN_TIMES, openTimes + 1)
            }
        } else if (openTimes != 0) {
            val dialog = DonateFragment.newInstance()
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, "donateDialog")
            getPrefer().edit {
                putInt(Const.KEY_OPEN_TIMES, openTimes + 1)
            }
        }

//        if (!getPrefer().getBoolean(Const.KEY_HAS_COUNT, false)) {
//            MyRetrofitUtils.instance.addCount(applicationContext)
//        }

        if (getPrefer().getBoolean(Const.KEY_CHECK_UPDATE, true)) {
            viewModel.getUpdateInfo().observe(this, Observer { result ->
                val updateInfo = result.getOrNull()
                if (updateInfo != null) {
                    if (updateInfo.version_code > getVersionCode(this@ScheduleActivity.applicationContext)) {
                        UpdateFragment.newInstance(updateInfo).show(supportFragmentManager, "updateDialog")
                    }
                }
            })
        }

        viewModel.initTableSelectList().observe(this, Observer {
            if (it == null) return@Observer
            viewModel.tableSelectList.clear()
            viewModel.tableSelectList.addAll(it)
            if (bottomSheetDialog.bottom_sheet_rv_table.adapter == null) {
                initTableMenu(viewModel.tableSelectList)
            } else {
                bottomSheetDialog.bottom_sheet_rv_table.adapter?.notifyDataSetChanged()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        initBottomSheetAction()
    }

    private fun initTheme() {
        if (viewModel.table.background != "") {
            val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
            Glide.with(this)
                    .load(viewModel.table.background)
                    .override(x, y)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            Toasty.error(this@ScheduleActivity, "无法检索背景图片，可能是它为某个应用私有所致，可以尝试在文件管理器中将它移动到其他位置，或是选择其它图片", Toasty.LENGTH_LONG).show()
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .error(R.drawable.main_background_2020_8)
                    .into(anko_iv_bg)
        } else {
            val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
            Glide.with(this)
                    .load(R.drawable.main_background_2020_8)
                    .override(x, y)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(anko_iv_bg)
        }

        for (i in 0 until anko_cl_schedule.childCount) {
            val view = anko_cl_schedule.getChildAt(i)
            when (view) {
                is AppCompatTextView -> view.setTextColor(viewModel.table.textColor)
                is AppCompatImageButton -> view.setColorFilter(viewModel.table.textColor)
            }
        }

        if (ViewUtils.judgeColorIsLight(viewModel.table.textColor)) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
        }

        viewModel.itemHeight = dip(viewModel.table.itemHeight)
    }

    private fun initTableMenu(data: MutableList<TableSelectBean>) {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val adapter = TableNameAdapter(R.layout.item_table_select_main, data)
        adapter.addChildClickViewIds(R.id.menu_setting)
        adapter.setOnItemChildClickListener { _, view, _ ->
            when (view.id) {
                R.id.menu_setting -> {
                    startActivityForResult(Intent(this,
                            ScheduleSettingsActivity::class.java).apply {
                        putExtra("tableData", viewModel.table)
                    }, Const.REQUEST_CODE_SCHEDULE_SETTING)
                }
            }
        }
        adapter.setOnItemClickListener { _, _, position ->
            if (position < data.size) {
                if (data[position].id != viewModel.table.id) {
                    launch {
                        viewModel.changeDefaultTable(data[position].id)
                        initView()
                        val list = viewModel.getScheduleWidgetIds()
                        val table = viewModel.getDefaultTable()
                        list.forEach {
                            when (it.detailType) {
                                1 -> AppWidgetUtils.refreshTodayWidget(applicationContext, appWidgetManager, it.id, table)
                            }
                        }
                    }
                }
            }
        }
        bottomSheetDialog.bottom_sheet_rv_table.adapter = adapter
    }

    private fun initBottomSheetAction() {
        hideBottomSheetDialog()
        bottomSheetDialog.bottom_sheet_weike_btn.visibility = if (getPrefer().getBoolean(Const.KEY_SHOW_WEIKE_LIFE, true)) View.VISIBLE else View.INVISIBLE
        bottomSheetDialog.bottom_sheet_rv_table.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }

        bottomSheetDialog.setOnShowListener {
            bottomSheetDialog.bottom_sheet_sv_week.smoothScrollTo(if (viewModel.selectedWeek > 4) (viewModel.selectedWeek - 4) * dip(56) else 0, 0)
            if (bottomSheetDialog.bottom_sheet_cg_week.checkedButtonId != viewModel.selectedWeek) {
                bottomSheetDialog.bottom_sheet_cg_week.check(viewModel.selectedWeek)
            }
        }
        bottomSheetDialog.bottom_sheet_create_schedule_btn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.setting_schedule_name)
                    .setView(R.layout.dialog_edit_text)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.sure, null)
                    .create()
            dialog.show()
            val inputLayout = dialog.findViewById<TextInputLayout>(R.id.text_input_layout)
            val editText = dialog.findViewById<TextInputEditText>(R.id.edit_text)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val value = editText?.text
                if (value.isNullOrBlank()) {
                    inputLayout?.error = "名称不能为空哦>_<"
                } else {
                    launch {
                        try {
                            viewModel.addBlankTable(editText.text.toString())
                            Toasty.success(this@ScheduleActivity, "新建成功~").show()
                        } catch (e: Exception) {
                            Toasty.error(this@ScheduleActivity, "操作失败>_<").show()
                        }
                        dialog.dismiss()
                    }
                }
            }
        }
        bottomSheetDialog.bottom_sheet_manage_schedule_btn.setOnClickListener {
            startActivityForResult(
                    Intent(this, ScheduleManageActivity::class.java), Const.REQUEST_CODE_SCHEDULE_SETTING)
        }
        bottomSheetDialog.bottom_sheet_change_week_btn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(this)
                    .setTitle("当前周：${viewModel.currentWeek}")
                    .setView(R.layout.dialog_edit_seekbar)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.sure, null)
                    .setCancelable(true)
                    .create()
            dialog.show()
            val seekBar = dialog.findViewById<SeekBar>(R.id.seek_bar)
            seekBar?.let {
                it.max = viewModel.table.maxWeek - 1
                it.progress = viewModel.currentWeek - 1
                it.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        dialog.setTitle("当前周：${seekBar?.progress?.plus(1)}")
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                })
            }
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val process = seekBar?.progress
                val valueInt = process?.plus(1)!!
                viewModel.setCurWeek(valueInt)
                launch {
                    viewModel.saveSettings()
                    initView()
                }
                dialog.dismiss()
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.bottom_sheet_modify_time_btn.setOnClickListener {
            startActivityForResult(Intent(this, TimeSettingsActivity::class.java).apply {
                putExtra("selectedId", viewModel.table.timeTable)
            }, REQUEST_CODE_CHOOSE_TABLE)
        }
        bottomSheetDialog.bottom_sheet_bg_btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            try {
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_BG)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
        bottomSheetDialog.bottom_sheet_check_course_btn.setOnClickListener {
            start<ScheduleManageActivity> {
                putExtra("selectedTable", TableSelectBean(
                        id = viewModel.table.id,
                        background = viewModel.table.background,
                        tableName = viewModel.table.tableName,
                        maxWeek = viewModel.table.maxWeek,
                        nodes = viewModel.table.nodes,
                        type = viewModel.table.type
                ))
            }
        }
        bottomSheetDialog.bottom_sheet_question_btn.setOnClickListener {
            Utils.openUrl(this, "https://support.qq.com/embed/191619/faqs-more")
        }

        bottomSheetDialog.bottom_sheet_about_btn.setOnClickListener {
            start<AboutActivity>()
        }
        bottomSheetDialog.bottom_sheet_setting_btn.setOnClickListener {
            startActivityForResult(Intent(this, SettingsActivity::class.java).apply {
                putExtra("tableName", viewModel.table.tableName)
            }, Const.REQUEST_CODE_SCHEDULE_SETTING)
        }
        bottomSheetDialog.bottom_sheet_feedback_btn.setOnClickListener {
            Utils.openUrl(this, "https://support.qq.com/product/191619")
        }
        bottomSheetDialog.bottom_sheet_weike_btn.setOnClickListener {
            start<WeikeLifeActivity>()
        }
    }

    private fun initIntro() {
        val builder = Tooltip.Builder(this@ScheduleActivity)
                .overlay(true)
                .maxWidth(dip(240))
                .customView(R.layout.my_tooltip, R.id.tv_tip)
        val jumpTooltip = builder
                .text("在这里快速回到当前周")
                .anchor(anko_tv_weekday)
                .create()
        val addBtnTooltip = builder
                .text("在这里手动添加课程")
                .anchor(anko_ib_add)
                .create()
        val importTooltip = builder
                .text("在这里导入课表")
                .anchor(anko_ib_import)
                .create()
        val shareTooltip = builder
                .text("在这里导出、分享课表")
                .anchor(anko_ib_share)
                .create()
        val moreTooltip = builder
                .text("在这里查看更多设置")
                .anchor(anko_ib_more)
                .create()
        jumpTooltip.doOnHidden {
            addBtnTooltip.doOnHidden {
                importTooltip.doOnHidden {
                    shareTooltip.doOnHidden {
                        moreTooltip.doOnHidden {
                            getPrefer().edit {
                                putBoolean(Const.KEY_HAS_INTRO, true)
                            }
                            showBottomSheetDialog()
                        }.show(anko_cl_schedule, Tooltip.Gravity.LEFT)
                        moreTooltip.contentView?.findViewById<TextView>(R.id.btn_next)?.apply {
                            text = "完成教程"
                            setOnClickListener {
                                moreTooltip.hide()
                            }
                        }
                    }.show(anko_cl_schedule, Tooltip.Gravity.LEFT)
                    shareTooltip.contentView?.findViewById<TextView>(R.id.btn_next)?.setOnClickListener {
                        shareTooltip.hide()
                    }
                }.show(anko_cl_schedule, Tooltip.Gravity.LEFT)
                importTooltip.contentView?.findViewById<TextView>(R.id.btn_next)?.setOnClickListener {
                    importTooltip.hide()
                }
            }.show(anko_cl_schedule, Tooltip.Gravity.BOTTOM)
            addBtnTooltip.contentView?.findViewById<TextView>(R.id.btn_next)?.setOnClickListener {
                addBtnTooltip.hide()
            }
        }.show(anko_cl_schedule, Tooltip.Gravity.BOTTOM)
        jumpTooltip.contentView?.findViewById<TextView>(R.id.btn_next)?.setOnClickListener {
            jumpTooltip.hide()
        }
    }

    override fun onStart() {
        super.onStart()
        anko_tv_date.text = CourseUtils.getTodayDate()
    }

    private fun initViewPage(maxWeek: Int, table: TableBean) {
        if (mAdapter == null) {
            mAdapter = SchedulePagerAdapter(maxWeek, preLoad, supportFragmentManager)
            anko_vp_schedule.adapter = mAdapter
            anko_vp_schedule.offscreenPageLimit = 1
        }
        mAdapter!!.maxWeek = maxWeek
        mAdapter!!.notifyDataSetChanged()
        if (CourseUtils.countWeek(table.startDate, table.sundayFirst) > 0) {
            // 取消切换动画
            anko_vp_schedule.setCurrentItem(CourseUtils.countWeek(table.startDate, table.sundayFirst) - 1, false)
        } else {
            anko_vp_schedule.currentItem = 0
        }
    }

    private fun initEvent() {
        anko_ib_add.setOnClickListener {
            start<AddCourseActivity> {
                putExtra("tableId", viewModel.table.id)
                putExtra("maxWeek", viewModel.table.maxWeek)
                putExtra("nodes", viewModel.table.nodes)
                putExtra("id", -1)
            }
        }

        anko_ib_more.setOnClickListener {
            showBottomSheetDialog()
        }

        bottomSheetDialog.bottom_sheet_cg_week.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                anko_vp_schedule.currentItem = checkedId - 1
            }
        }

        anko_ib_share.setOnClickListener {
            ExportSettingsFragment().show(supportFragmentManager, null)
        }

        anko_ib_import.setOnClickListener {
            ImportChooseFragment().show(supportFragmentManager, "importDialog")
        }

        anko_tv_weekday.setOnClickListener {
            anko_tv_weekday.text = CourseUtils.getWeekday()
            if (viewModel.currentWeek > 0) {
                anko_vp_schedule.currentItem = viewModel.currentWeek - 1
            } else {
                anko_vp_schedule.currentItem = 0
            }
        }

        anko_vp_schedule.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                viewModel.selectedWeek = position + 1
                try {
                    if (viewModel.currentWeek > 0) {
                        if (viewModel.selectedWeek == viewModel.currentWeek) {
                            anko_tv_week.text = "第${viewModel.selectedWeek}周"
                            anko_tv_weekday.text = CourseUtils.getWeekday()
                        } else {
                            anko_tv_week.text = "第${viewModel.selectedWeek}周"
                            anko_tv_weekday.text = "非本周"
                        }
                    } else {
                        anko_tv_week.text = "第${viewModel.selectedWeek}周"
                        anko_tv_weekday.text = "还没有开学哦"
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

            override fun onPageScrolled(a: Int, b: Float, c: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

    }

    private fun showBottomSheetDialog() {
        bottomSheetDialog.show()
    }

    private fun hideBottomSheetDialog() {
        bottomSheetDialog.dismiss()
    }

    private fun initView() {
        // 初始化属性suqir
        val statusBarMargin = ViewUtils.getStatusBarHeight(this)
        (titleBar.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = statusBarMargin
        }

        launch {
            viewModel.table = viewModel.getDefaultTable()
            viewModel.currentWeek = CourseUtils.countWeek(viewModel.table.startDate, viewModel.table.sundayFirst)
            viewModel.selectedWeek = viewModel.currentWeek
            if (viewModel.currentWeek > 0) {
                if (viewModel.currentWeek <= viewModel.table.maxWeek) {
                    anko_tv_week.text = "第${viewModel.currentWeek}周"
                } else {
                    anko_tv_week.text = "当前周已超出设定范围"
                    MaterialAlertDialogBuilder(this@ScheduleActivity)
                            .setTitle("提示")
                            .setMessage("发现当前周已超出设定的周数范围，是否去设置修改「当前周」或「开学日期」？")
                            .setPositiveButton("打开设置") { _, _ ->
                                startActivityForResult(Intent(this@ScheduleActivity,
                                        ScheduleSettingsActivity::class.java).apply {
                                    putExtra("tableData", viewModel.table)
                                }, Const.REQUEST_CODE_SCHEDULE_SETTING)
                            }
                            .setNegativeButton(R.string.cancel, null)
                            .show()
                }
            } else {
                anko_tv_week.text = "还没有开学哦"
            }

            bottomSheetDialog.bottom_sheet_cg_week.removeAllViews()
            bottomSheetDialog.bottom_sheet_cg_week.clearChecked()
            for (i in 1..viewModel.table.maxWeek) {
                val materialButton = LayoutInflater.from(this@ScheduleActivity).inflate(R.layout.outline_button, null) as MaterialButton
                bottomSheetDialog.bottom_sheet_cg_week.addView(materialButton.apply {
                    id = i
                    text = i.toString()
                    textSize = 12f
                }, dip(48), dip(48))
            }

            launch {
                delay(1000)
                if (bottomSheetDialog.bottom_sheet_cg_week.checkedButtonId != viewModel.selectedWeek) {
                    bottomSheetDialog.bottom_sheet_cg_week.check(viewModel.selectedWeek)
                }
                bottomSheetDialog.bottom_sheet_sv_week.smoothScrollTo(if (viewModel.selectedWeek > 4) (viewModel.selectedWeek - 4) * dip(56) else 0, 0)
            }

            anko_tv_weekday.text = CourseUtils.getWeekday()

            initTheme()

            viewModel.timeList = viewModel.getTimeList(viewModel.table.timeTable)

            viewModel.alphaInt = (255 * (viewModel.table.itemAlpha.toFloat() / 100)).roundToInt()

            initViewPage(viewModel.table.maxWeek, viewModel.table)

            initEvent()

            for (i in 1..7) {
                viewModel.getRawCourseByDay(i, viewModel.table.id).observe(this@ScheduleActivity, Observer { list ->
                    if (list == null) return@Observer
                    if (list.isNotEmpty() && list[0].tableId != viewModel.table.id) return@Observer
                    viewModel.allCourseList[i - 1].value = list
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK) {
            when (requestCode) {
                Const.REQUEST_CODE_EXPORT -> {
                    anko_cl_schedule.longSnack("导出是否遇到了问题？") {
                        action("查看教程") {
                            Utils.openUrl(this@ScheduleActivity, "https://support.qq.com/products/191619/faqs/77270")
                        }
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, data)
            return
        }
        when (requestCode) {
            Const.REQUEST_CODE_SCHEDULE_SETTING -> initView()
            Const.REQUEST_CODE_IMPORT -> {
                val total = data?.getIntExtra("course_total", 0)
                MaterialAlertDialogBuilder(this)
                        .setTitle("温馨提示")
                        .setView(AppCompatTextView(this).apply {
                            text = ViewUtils.getHtmlSpannedString("成功导入<b><font color='#fa6278'>${total}门</font></b>课程！<br>记得<b><font color='#fa6278'>仔细检查</font></b>有没有少课、课程信息有误的情况，不要到时候<b><font color='#fa6278'>一不小心就翘课</font></b>")
                            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            val space = styledDimenPxSize(R.attr.dialogPreferredPadding)
                            setPadding(space, dip(8), space, 0)
                        })
                        .setCancelable(false)
                        .setPositiveButton("我知道啦", null)
                        .show()
            }
            Const.REQUEST_CODE_EXPORT -> {
                val uri = data?.data
                launch {
                    try {
                        viewModel.exportData(uri)
                        showShareDialog("分享课程文件", uri!!)
                    } catch (e: Exception) {
                        Toasty.error(this@ScheduleActivity, "导出失败>_<${e.message}")
                    }
                }
            }
            Const.REQUEST_CODE_EXPORT_ICS -> {
                val uri = data?.data
                launch {
                    try {
                        viewModel.exportICS(uri)
                        showShareDialog("分享日历文件", uri!!)
                    } catch (e: Exception) {
                        Toasty.error(this@ScheduleActivity, "导出失败>_<${e.message}")
                    }
                }
            }
            REQUEST_CODE_CHOOSE_TABLE -> {
                viewModel.table.timeTable = data!!.getIntExtra("selectedId", 1)
                launch {
                    viewModel.saveSettings()
                    initView()
                    bottomSheetDialog.dismiss()
                }
            }
            REQUEST_CODE_CHOOSE_BG -> {
                val uri = data?.data
                if (uri != null) {
                    viewModel.table.background = uri.toString()
                }
                launch {
                    viewModel.saveSettings()
                    initView()
                    bottomSheetDialog.dismiss()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showShareDialog(title: String, uri: Uri) {
        MaterialAlertDialogBuilder(this)
                .setTitle("分享")
                .setMessage("成功导出至你指定的路径啦，是否还要分享出去呢？")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton("分享") { _, _ ->
                    val shareIntent = ShareCompat.IntentBuilder.from(this)
                            .setChooserTitle(title)
                            .setStream(uri)
                            .setType("*/*")
                            .createChooserIntent()
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(shareIntent)
                }
                .setCancelable(false)
                .show()
    }

    override fun onBackPressed() {
        when {
            bottomSheetDialog.isShowing -> bottomSheetDialog.dismiss()
            else -> super.onBackPressed()
        }
    }

    override fun onDestroy() {
        anko_vp_schedule.clearOnPageChangeListeners()
        bottomSheetDialog.bottom_sheet_cg_week.clearOnButtonCheckedListeners()
        AppWidgetUtils.updateWidget(applicationContext)
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        hideBottomSheetDialog()
    }

}