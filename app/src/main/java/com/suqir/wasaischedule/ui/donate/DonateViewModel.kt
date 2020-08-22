package com.suqir.wasaischedule.ui.donate

import androidx.lifecycle.ViewModel
import com.suqir.wasaischedule.logic.Repository

/**
 * Author: Suqir
 * Date: 2020/8/19 16:46
 * Desc:
 **/
class DonateViewModel : ViewModel() {

    val donateList = Repository.getDonateList()
}