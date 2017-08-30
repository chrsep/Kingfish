package com.directdev.portal.features.journal

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.directdev.portal.BasePresenter
import com.directdev.portal.BaseView

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/26/17.
 *------------------------------------------------------------------------------------------------*/
interface JournalContract {
    interface View : BaseView<Presenter> {
        fun logContentOpened()
        fun navigateToSettings()
        fun showLoading()
        fun hideLoading()
        fun setTitle(toolbar: Toolbar, date: String)
        fun setRecyclerAdapter(view: RecyclerView, adapter: JournalRecyclerAdapter)
    }

    interface Presenter : BasePresenter {
        fun sync()
        fun onMenuItemClick(itemId: Int): Boolean
        fun onCreateView(toolbar: Toolbar, recyclerView: RecyclerView)
        fun onStop()
        fun onStart()
    }
}