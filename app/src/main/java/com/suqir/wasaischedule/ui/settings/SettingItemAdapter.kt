package com.suqir.wasaischedule.ui.settings

import com.chad.library.adapter.base.BaseProviderMultiAdapter
import com.suqir.wasaischedule.ui.settings.items.BaseSettingItem
import com.suqir.wasaischedule.ui.settings.provider.*

class SettingItemAdapter : BaseProviderMultiAdapter<BaseSettingItem>() {

    init {
        addItemProvider(CategoryItemProvider())
        addItemProvider(HorizontalItemProvider())
        addItemProvider(SeekBarItemProvider())
        addItemProvider(SwitchItemProvider())
        addItemProvider(VerticalItemProvider())
    }

    override fun getItemType(data: List<BaseSettingItem>, position: Int): Int {
        return data[position].getType()
    }

}