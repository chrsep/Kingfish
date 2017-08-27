package com.directdev.portal.features.journal

import android.support.v7.widget.LinearLayoutManager
import com.directdev.portal.BasePresenter
import com.directdev.portal.BaseView
import com.directdev.portal.models.JournalModel

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/26/17.
 *------------------------------------------------------------------------------------------------*/
interface JournalContract {
    interface View : BaseView<Presenter> {
        fun logContentOpened()
        fun setRecyclerAdapter(layoutManager: LinearLayoutManager, adapter: JournalRecyclerAdapter)
        fun setupToolbar(schedules: List<JournalModel>, dateString: String)
    }

    interface Presenter : BasePresenter {
        fun onStart()
        fun update()
    }
}