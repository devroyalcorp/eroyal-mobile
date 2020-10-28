package com.worka.eroyal.feature.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter

class FragmentPagerAdapter(fm: androidx.fragment.app.FragmentManager, val mFragmentList: List<Fragment>, val titles: List<String>? = null) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles?.get(position)
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

}