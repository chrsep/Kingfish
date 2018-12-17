package com.directdev.portal.features.journal

import androidx.appcompat.widget.Toolbar
import com.directdev.portal.BasePresenter
import com.directdev.portal.BaseView
import com.directdev.portal.models.JournalModel
import io.realm.RealmResults

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/26/17.
 *------------------------------------------------------------------------------------------------*/
interface JournalContract {
    interface View : BaseView<Presenter> {
        fun logAnalytics()
        fun navigateToSettings()
        fun showLoading()
        fun hideLoading()
        fun setTitle(toolbar: Toolbar, date: String)
        fun showSuccess(message: String)
        fun showFailed(message: String)
        fun updateAdapterData(data: RealmResults<JournalModel>)
    }

    interface Presenter : BasePresenter {
        fun onMenuItemClick(itemId: Int): Boolean
        fun onStop()
        fun onStart()
        fun sync(bypass: Boolean = false)
        fun onCreateView(toolbar: Toolbar)
    }
}