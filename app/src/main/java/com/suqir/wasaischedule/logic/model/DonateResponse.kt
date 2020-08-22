package com.suqir.wasaischedule.logic.model

/**
 * Author: Suqir
 * Date: 2020/8/19 16:40
 * Desc:
 **/
data class DonateResponse(val status: String, val data: List<Donate>) {
    data class Donate(val id: Int, val donate_name: String, val donate_fee: String)
}