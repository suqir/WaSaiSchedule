package com.suqir.wasaischedule.ui.schedule_import

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.suqir.wasaischedule.ui.SplashActivity
import com.suqir.wasaischedule.ui.base_view.BaseActivity
import com.suqir.wasaischedule.utils.Const
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_login_web.*

class LoginWebActivity : BaseActivity() {

    private val viewModel by viewModels<ImportViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.extras?.getString("import_type")?.let {
            viewModel.importType = it
        }
        intent.extras?.getString("school_name")?.let {
            viewModel.school = it
        }

        val fragment = when (viewModel.importType) {
            "api" -> ApiImportFragment()
            "login" -> LoginWebFragment()
            "apply" -> SchoolInfoFragment()
            "file" -> FileImportFragment()
            "excel" -> ExcelImportFragment()
            "html" -> HtmlImportFragment()
            else -> {
                if (viewModel.importType.isNullOrEmpty() || viewModel.school.isNullOrEmpty()) {
                    null
                } else {
                    WebViewLoginFragment.newInstance(intent.getStringExtra("url")!!)
                }
            }
        }
        fragment?.let { frag ->
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(android.R.id.content, frag, viewModel.school)
            transaction.commit()
            if (viewModel.importType != "apply" && viewModel.importType != "file") {
                showImportSettingDialog()
            }
        }

        if (fragment == null && intent.action == Intent.ACTION_VIEW) {
            launch {
                viewModel.importId = viewModel.getNewId()
                viewModel.newFlag = true
                val uri = intent.data
                val path = uri?.path ?: ""
                val type = when {
                    path.contains("wasai_schedule") -> "file"
                    path.endsWith("csv") -> "csv"
                    path.endsWith("html") -> "html"
                    else -> ""
                }
                if (type.isEmpty()) {
                    Toasty.error(this@LoginWebActivity, "文件的扩展名不对哦", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@LoginWebActivity, SplashActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                    return@launch
                }
                val transaction = supportFragmentManager.beginTransaction()
                when (type) {
                    "file" -> transaction.add(android.R.id.content, FileImportFragment(), null)
                    "csv" -> transaction.add(android.R.id.content, ExcelImportFragment(), null)
                    "html" -> transaction.add(android.R.id.content, HtmlImportFragment(), null)
                }
                transaction.commit()
                if (type == "html") {
                    viewModel.htmlUri = uri
                } else {
                    try {
                        when (type) {
                            "file" -> viewModel.importFromFile(uri)
                            "csv" -> viewModel.importFromExcel(uri)
                        }
                        Toasty.success(this@LoginWebActivity, "导入成功", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@LoginWebActivity, SplashActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        Toasty.error(this@LoginWebActivity, "发生异常>_<\n${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun showImportSettingDialog() {
        ImportSettingFragment().apply {
            isCancelable = false
        }.show(supportFragmentManager, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            Const.REQUEST_CODE_IMPORT_FILE -> {
                launch {
                    try {
                        val totalSize = viewModel.importFromFile(data?.data)
                        Toasty.success(this@LoginWebActivity, "导入成功", Toast.LENGTH_LONG).show()
                        setResult(RESULT_OK, Intent().apply { putExtra("course_total", totalSize) })
                        finish()
                    } catch (e: Exception) {
                        Toasty.error(this@LoginWebActivity, "发生异常>_<\n请确保选择了扩展名为wasai_schedule的文件！", Toast.LENGTH_LONG).show()
                    }
                }
            }
            Const.REQUEST_CODE_IMPORT_CSV -> {
                launch {
                    try {
                        val totalSize = viewModel.importFromExcel(data?.data)
                        Toasty.success(this@LoginWebActivity, "导入成功", Toast.LENGTH_LONG).show()
                        setResult(RESULT_OK, Intent().apply { putExtra("course_total", totalSize) })
                        finish()
                    } catch (e: Exception) {
                        Toasty.error(this@LoginWebActivity, "发生异常>_<请确保所有应填的格子不为空\n且没有更改模板的属性\n${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        val suda = supportFragmentManager.findFragmentByTag("苏州大学")
        if (suda != null && fab_login.isExpanded) {
            fab_login.isExpanded = false
        } else {
            super.onBackPressed()
        }
    }

}
