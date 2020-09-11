package com.suqir.wasaischedule.ui.settings

import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.suqir.wasaischedule.BuildConfig
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.logic.dao.AppWidgetDao
import com.suqir.wasaischedule.logic.dao.TableDao
import com.suqir.wasaischedule.logic.database.AppDatabase
import com.suqir.wasaischedule.ui.base_view.BaseListActivity
import com.suqir.wasaischedule.ui.donate.DonateActivity
import com.suqir.wasaischedule.ui.schedule_settings.ScheduleSettingsActivity
import com.suqir.wasaischedule.ui.settings.items.*
import com.suqir.wasaischedule.ui.widget.colorpicker.ColorPickerFragment
import com.suqir.wasaischedule.utils.AppWidgetUtils
import com.suqir.wasaischedule.utils.Const
import com.suqir.wasaischedule.utils.DonateUtils
import com.suqir.wasaischedule.utils.getPrefer
import es.dmoral.toasty.Toasty
import splitties.activities.start
import splitties.resources.color
import splitties.snackbar.longSnack
import splitties.snackbar.snack

class SettingsActivity : BaseListActivity(), ColorPickerFragment.ColorPickerDialogListener {

    private lateinit var dataBase: AppDatabase
    private lateinit var tableDao: TableDao
    private lateinit var widgetDao: AppWidgetDao
    private lateinit var tableName: String

    private val dayNightTheme by lazy(LazyThreadSafetyMode.NONE) {
        resources.getStringArray(R.array.day_night_setting)
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        getPrefer().edit {
            putInt(Const.KEY_THEME_COLOR, color)
        }
        mRecyclerView.longSnack("重启App后生效哦")
    }

    private var dayNightIndex = 2

    private val mAdapter = SettingItemAdapter()

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        return if (BuildConfig.CHANNEL == "google" || BuildConfig.CHANNEL == "huawei") {
            null
        } else {
            tvButton.text = "捐赠"
            tvButton.setTextColor(color(R.color.colorAccent))
            tvButton.setOnClickListener {
                start<DonateActivity>()
            }
            tvButton
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBase = AppDatabase.getDatabase(application)
        tableDao = dataBase.tableDao()
        widgetDao = dataBase.appWidgetDao()
        dayNightIndex = getPrefer().getInt(Const.KEY_DAY_NIGHT_THEME, 2)
        val items = mutableListOf<BaseSettingItem>()
        tableName = intent.getStringExtra("tableName") ?: "null"
        onItemsCreated(items)
        mAdapter.data = items
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.itemAnimator?.changeDuration = 250
        mRecyclerView.adapter = mAdapter
        mAdapter.addChildClickViewIds(R.id.anko_check_box)
        mAdapter.setOnItemChildClickListener { _, view, position ->
            when (val item = items[position]) {
                is SwitchItem -> onSwitchItemCheckChange(item, view.findViewById<AppCompatCheckBox>(R.id.anko_check_box).isChecked)
            }
        }
        mAdapter.setOnItemClickListener { _, view, position ->
            when (val item = items[position]) {
                is HorizontalItem -> onHorizontalItemClick(item, position)
                is VerticalItem -> onVerticalItemClick(item)
                is SwitchItem -> view.findViewById<AppCompatCheckBox>(R.id.anko_check_box).performClick()
                is SeekBarItem -> onSeekBarItemClick(item, position)
            }
        }
    }

    private fun onItemsCreated(items: MutableList<BaseSettingItem>) {
        val colorStr = getPrefer().getInt(Const.KEY_THEME_COLOR, color(R.color.colorAccent))
                .toString(16)
        items.add(CategoryItem("外观", true))
        items.add(VerticalItem("主题颜色", "调整大部分标签和虚拟键的颜色。\n以下关于虚拟键的设置，只对有虚拟键的手机有效哦，是为了有更好的沉浸效果~\n有实体按键或全面屏手势的手机本身就很棒啦~"))
        items.add(SwitchItem("主界面虚拟键沉浸", getPrefer().getBoolean(Const.KEY_HIDE_NAV_BAR, false)))

        items.add(CategoryItem("常规", false))
        items.add(HorizontalItem("课表设置", tableName))
        items.add(SwitchItem("自动检查更新", getPrefer().getBoolean(Const.KEY_CHECK_UPDATE, true)))
        items.add(SwitchItem("节数栏显示具体时间", getPrefer().getBoolean(Const.KEY_SCHEDULE_DETAIL_TIME, true), ""))
        items.add(SwitchItem("页面预加载", getPrefer().getBoolean(Const.KEY_SCHEDULE_PRE_LOAD, true), "开启后，滑动界面后会马上显示课表。关闭后，滑动界面后需要短暂的时间加载课表，不过理论上内存占用会更小，App启动速度也会更快。"))
        items.add(SwitchItem("课表下方增加留白区域", getPrefer().getBoolean(Const.KEY_SCHEDULE_BLANK_AREA, true), "开启后，课表下方会多出一段空白区域，便于将底部的课程滑动至屏幕中间查看。"))
        items.add(SwitchItem("显示日视图背景", getPrefer().getBoolean(Const.KEY_DAY_WIDGET_COLOR, false)))
        items.add(SwitchItem("显示空视图图片", getPrefer().getBoolean(Const.KEY_SHOW_EMPTY_VIEW, true)))
        items.add(SwitchItem("显示「潍科生活」", getPrefer().getBoolean(Const.KEY_SHOW_WEIKE_LIFE, true)))
        items.add(HorizontalItem("显示主题", dayNightTheme[dayNightIndex]))

        items.add(CategoryItem("上课提醒", false))
        items.add(VerticalItem("功能说明", "本功能处于<b><font color='#$colorStr'>试验性阶段</font></b>。由于国产手机对系统的定制不尽相同，本功能可能会在某些手机上失效。<b><font color='#$colorStr'>开启前提：设置好课程时间 + 往桌面添加一个日视图小部件 + 允许App后台运行</font></b>。<br>理论上<b><font color='#$colorStr'>每次设置之后</font></b>需要半天以上的时间才会正常工作，理论上不会很耗电。", true))
        items.add(SwitchItem("开启上课提醒", getPrefer().getBoolean(Const.KEY_COURSE_REMIND, false)))
        items.add(SwitchItem("提醒通知常驻", getPrefer().getBoolean(Const.KEY_REMINDER_ON_GOING, false)))
        items.add(SeekBarItem("提前几分钟提醒", getPrefer().getInt(Const.KEY_REMINDER_TIME, 20), 0, 90, "分钟"))
        //items.add(SwitchItem("提醒同时将手机静音", PreferenceUtils.getBooleanFromSP(applicationContext, "silence_reminder", false)))
    }

    private fun onSwitchItemCheckChange(item: SwitchItem, isChecked: Boolean) {
        when (item.title) {
            "自动检查更新" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_CHECK_UPDATE, isChecked)
                }
            }
            "页面预加载" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_SCHEDULE_PRE_LOAD, isChecked)
                }
                mRecyclerView.snack("重启App后生效哦")
            }
            "课表下方增加留白区域" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_SCHEDULE_BLANK_AREA, isChecked)
                }
                mRecyclerView.snack("重启App后生效哦")
            }
            "节数栏显示具体时间" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_SCHEDULE_DETAIL_TIME, isChecked)
                }
                mRecyclerView.snack("重启App后生效哦")
            }
            "显示空视图图片" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_SHOW_EMPTY_VIEW, isChecked)
                }
                mRecyclerView.snack("切换页面后生效哦")
            }
            "显示日视图背景" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_DAY_WIDGET_COLOR, isChecked)
                }
                mRecyclerView.longSnack("请点击小部件右上角的「切换按钮」查看效果")
            }
            "显示「潍科生活」" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_SHOW_WEIKE_LIFE, isChecked)
                }
            }
            "主界面虚拟键沉浸" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_HIDE_NAV_BAR, isChecked)
                }
                mRecyclerView.longSnack("重启App后生效哦")
                item.checked = isChecked
            }
            "开启上课提醒" -> {
                launch {
                    val task = widgetDao.getWidgetsByTypes(0, 1)
                    if (task.isEmpty()) {
                        mRecyclerView.longSnack("好像还没有设置日视图小部件呢")
                        getPrefer().edit {
                            putBoolean(Const.KEY_COURSE_REMIND, false)
                        }
                        item.checked = false
                        mAdapter.notifyDataSetChanged()
                    } else {
                        getPrefer().edit {
                            putBoolean(Const.KEY_COURSE_REMIND, isChecked)
                        }
                        AppWidgetUtils.updateWidget(applicationContext)
                        item.checked = isChecked
                    }
                }
            }
            "提醒通知常驻" -> {
                getPrefer().edit {
                    putBoolean(Const.KEY_REMINDER_ON_GOING, isChecked)
                }
                item.checked = isChecked
                mRecyclerView.longSnack("对下一次提醒通知生效哦")
            }
            "提醒同时将手机静音" -> {
                val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted) {
                    val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                    startActivity(intent)
                    item.checked = false
                } else {
                    getPrefer().edit {
                        putBoolean(Const.KEY_SILENCE_REMINDER, isChecked)
                    }
                    AppWidgetUtils.updateWidget(applicationContext)
                    item.checked = isChecked
                }
            }
        }
        item.checked = isChecked
    }

    private fun onHorizontalItemClick(item: HorizontalItem, position: Int) {
        when (item.title) {
            "课表设置" -> {
                launch {
                    val table = tableDao.getDefaultTable()
                    startActivityForResult(
                            Intent(this@SettingsActivity, ScheduleSettingsActivity::class.java).apply {
                                putExtra("tableData", table)
                            }, 180)
                }
            }
            "显示主题" -> {
                MaterialAlertDialogBuilder(this)
                        .setTitle("显示主题")
                        .setPositiveButton("确定") { _, _ ->
                            getPrefer().edit {
                                putInt(Const.KEY_DAY_NIGHT_THEME, dayNightIndex)
                            }
                            item.value = dayNightTheme[dayNightIndex]
                            mAdapter.notifyItemChanged(position)
                            when (dayNightIndex) {
                                0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                                2 -> {
                                    when {
                                        Build.VERSION.SDK_INT >= 29 -> {
                                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                                        }
                                        Build.VERSION.SDK_INT >= 23 -> {
                                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                                        }
                                        else -> {
                                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                        }
                                    }
                                }
                            }
                        }
                        .setSingleChoiceItems(dayNightTheme, dayNightIndex) { _, which ->
                            dayNightIndex = which
                        }
                        .show()
            }
        }
    }

    private fun onVerticalItemClick(item: VerticalItem) {
        when (item.title) {
            "如何解锁？" -> {
                if (DonateUtils.isAppInstalled(applicationContext, "com.eg.android.AlipayGphone")) {
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    val qrCodeUrl = Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=HTTPS://QR.ALIPAY.COM/FKX09148M0LN2VUUZENO9B?_s=web-other")
                    intent.data = qrCodeUrl
                    intent.setClassName("com.eg.android.AlipayGphone", "com.alipay.mobile.quinox.LauncherActivity")
                    startActivity(intent)
                    Toasty.success(applicationContext, "非常感谢(*^▽^*)").show()
                } else {
                    Toasty.info(applicationContext, "没有检测到支付宝客户端o(╥﹏╥)o").show()
                }
            }
            "主题颜色" -> {
                ColorPickerFragment.newBuilder()
                        .setShowAlphaSlider(true)
                        .setColor(getPrefer().getInt(Const.KEY_THEME_COLOR, color(R.color.colorAccent)))
                        .show(this)
            }
        }
    }

    private fun onSeekBarItemClick(item: SeekBarItem, position: Int) {
        val dialog = MaterialAlertDialogBuilder(this)
                .setTitle("提前${item.valueInt}分钟提醒")
                .setView(R.layout.dialog_edit_seekbar)
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton("恢复", null)
                .setPositiveButton(R.string.sure, null)
                .setCancelable(false)
                .create()
        dialog.show()
        val seekBar = dialog.findViewById<SeekBar>(R.id.seek_bar)
        seekBar?.let {
            it.max = item.max - item.min
            it.progress = item.valueInt - item.min
            it.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    dialog.setTitle("提前${seekBar?.progress?.plus(item.min)}分钟提醒")
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }
        val inputLayout = dialog.findViewById<TextInputLayout>(R.id.text_input_layout)
        val editText = dialog.findViewById<TextInputEditText>(R.id.edit_text)
        inputLayout?.helperText = "范围 ${item.min} ~ ${item.max}"
        inputLayout?.suffixText = item.unit
        editText?.inputType = InputType.TYPE_CLASS_NUMBER
        val valueStr = item.valueInt.toString()
        editText?.setText(valueStr)
        editText?.setSelection(valueStr.length)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val process = seekBar?.progress
            val valueInt = process?.plus(item.min) ?: item.valueInt
            when (item.title) {
                "提前几分钟提醒" -> {
                    getPrefer().edit {
                        putInt(Const.KEY_REMINDER_TIME, valueInt)
                    }
                    AppWidgetUtils.updateWidget(applicationContext)
                }
            }
            item.valueInt = valueInt
            mAdapter.notifyItemChanged(position)
            dialog.dismiss()
        }
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
            seekBar?.progress = item.valueInt - item.min
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 180) {
            setResult(RESULT_OK)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
