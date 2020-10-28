package com.worka.eroyal.feature.mycustomers.notes

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-23.
 */
class NotesItemViewModel(var imageAvatar: String?, var customerName: String?, var date: String?, var notes: String?): SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_customer_notes
}