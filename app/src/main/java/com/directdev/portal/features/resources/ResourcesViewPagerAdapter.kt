package com.directdev.portal.features.resources

import android.app.Fragment
import android.app.FragmentManager
import android.support.v13.app.FragmentPagerAdapter

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 10/15/17.
 *------------------------------------------------------------------------------------------------*/
//TODO: title might not be needed
class ResourcesViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
    private val fragmentList = mutableListOf<Fragment>()
    private val fragmentTitleList = mutableListOf<String>()

    override fun getItem(position: Int) = fragmentList[position]

    override fun getCount() = fragmentList.size

    fun addFrag(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitleList.add(title)
    }

    fun clear() {
        fragmentTitleList.clear()
        fragmentList.clear()
    }

    override fun getPageTitle(position: Int): CharSequence = fragmentTitleList[position]
}