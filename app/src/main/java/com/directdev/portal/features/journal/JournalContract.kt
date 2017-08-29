package com.directdev.portal.features.journal

import android.support.v7.widget.LinearLayoutManager
import com.directdev.portal.BasePresenter
import com.directdev.portal.BaseView

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/26/17.
 *------------------------------------------------------------------------------------------------*/
interface JournalContract {
    interface View : BaseView<Presenter> {
        fun logContentOpened()
        fun setRecyclerAdapter(layoutManager: LinearLayoutManager, adapter: JournalRecyclerAdapter)
        fun navigateToSettings()
        fun inflateMenu()
        fun setTitle(date: String)
        fun showLoading()
        fun hideLoading()
    }

    interface Presenter : BasePresenter {
        fun onStart()
        fun sync()
        fun onMenuItemClick(itemId: Int): Boolean
    }
}