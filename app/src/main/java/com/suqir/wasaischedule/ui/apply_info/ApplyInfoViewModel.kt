package com.suqir.wasaischedule.ui.apply_info

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.suqir.wasaischedule.logic.Repository
import com.suqir.wasaischedule.logic.model.ApplySchoolResponse

class ApplyInfoViewModel : ViewModel() {

    val gson = Gson()
    val filterList = arrayListOf<ApplySchoolResponse.ApplySchool>()
    val countList = arrayListOf<ApplySchoolResponse.ApplySchool>()

    fun initData() = Repository.getApplySchool()

    fun search(str: String?) {
        filterList.clear()
        if (str.isNullOrBlank()) {
            filterList.addAll(countList)
        } else {
            filterList.addAll(countList.filter {
                it.school.contains(str)
            })
        }
    }
}