package com.suqir.wasaischedule.ui.apply_info

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.ui.base_view.BaseListActivity
import com.suqir.wasaischedule.ui.schedule_import.LoginWebActivity
import es.dmoral.toasty.Toasty
import splitties.activities.start
import splitties.dimensions.dip
import splitties.snackbar.action
import splitties.snackbar.longSnack

class ApplyInfoActivity : BaseListActivity() {

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        val iconFont = ResourcesCompat.getFont(this, R.font.iconfont)
        tvButton.typeface = iconFont
        tvButton.textSize = 20f
        tvButton.text = "\uE6D7"
        tvButton.setOnClickListener {

        }
        return tvButton
    }

    private val viewModel by viewModels<ApplyInfoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        showSearch = true
        textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s.toString())
                mRecyclerView.adapter?.notifyDataSetChanged()
                if (viewModel.filterList.isEmpty()) {
                    mRecyclerView.longSnack("没有找到你的学校哦") {
                        action("申请适配") {
                            start<LoginWebActivity> {
                                putExtra("import_type", "apply")
                            }
                        }
                    }
                }
            }

        }
        super.onCreate(savedInstanceState)
        mRecyclerView.adapter = ApplyInfoAdapter(R.layout.item_apply_info, viewModel.filterList).apply {
            this.setHeaderView(initHeaderView())
        }
        mRecyclerView.layoutManager = LinearLayoutManager(this)
//        viewModel.initData()
        viewModel.initData().observe(this, Observer { result ->
            val list = result.getOrNull()
            if (list != null) {
                viewModel.countList.clear()
                viewModel.countList.addAll(list)
                viewModel.filterList.clear()
                viewModel.filterList.addAll(viewModel.countList)
                mRecyclerView.adapter?.notifyDataSetChanged()
            } else {
                Toasty.error(applicationContext, "网络错误").show()
            }
        })
    }

    private fun initHeaderView(): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_apply_info_header, null)
        view.setPadding(0, getStatusBarHeight() + dip(48), 0, 0)
        return view
    }
}
