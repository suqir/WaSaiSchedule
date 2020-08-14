package com.suqir.wasaischedule.ui.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.suqir.wasaischedule.R
import com.suqir.wasaischedule.ui.base_view.BaseTitleActivity
import es.dmoral.toasty.Toasty
import splitties.resources.color

class TimeSettingsActivity : BaseTitleActivity() {
    override val layoutId: Int
        get() = R.layout.activity_time_settings

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        tvButton.text = "保存"
        tvButton.typeface = Typeface.DEFAULT_BOLD
        tvButton.setTextColor(color(R.color.colorAccent))
        tvButton.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.timeTableFragment -> {
                    setResult(Activity.RESULT_OK, Intent().putExtra("selectedId", viewModel.selectedId))
                    finish()
                }
                R.id.timeSettingsFragment -> {
                    launch {
                        try {
                            viewModel.saveDetailData(viewModel.entryPosition)
                            navController.navigateUp()
                            Toasty.success(applicationContext, "保存成功").show()
                        } catch (e: Exception) {
                            Toasty.error(applicationContext, "出现错误>_<${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }

                }
            }
        }
        return tvButton
    }

    private val viewModel by viewModels<TimeSettingsViewModel>()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navGraph = navHostFragment.navController.navInflater.inflate(R.navigation.nav_time_settings)
        val fragDestination = navGraph.findNode(R.id.timeTableFragment)!!
        fragDestination.addArgument("selectedId", NavArgument.Builder()
                .setType(NavType.IntType).setIsNullable(false).setDefaultValue(intent.extras!!.getInt("selectedId")).build())
//        fragDestination.setDefaultArguments(Bundle().apply {
//            this.putInt("selectedId", intent.extras!!.getInt("selectedId"))
//        })
        navHostFragment.navController.graph = navGraph
        navController = Navigation.findNavController(this, R.id.nav_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            mainTitle.text = destination.label
        }
    }


    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            R.id.timeTableFragment -> {
                finish()
            }
            R.id.timeSettingsFragment -> {
                val dialog = MaterialAlertDialogBuilder(this)
                        .setMessage("本次修改将不会被保存，确定退出编辑吗？")
                        .setNegativeButton("再想想") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton("离开") { _, _ ->
                            navController.navigateUp()
                        }
                        .create()
                dialog.show()
            }
        }
    }

}
