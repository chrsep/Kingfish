package com.directdev.portal.features.journal

import androidx.appcompat.widget.Toolbar
import com.directdev.portal.R
import com.directdev.portal.interactors.AuthInteractor
import com.directdev.portal.interactors.JournalInteractor
import com.directdev.portal.interactors.TermInteractor
import com.directdev.portal.utils.generateMessage
import javax.inject.Inject

// TODO: Presenter should not be dependent on Android libraries
class JournalPresenter @Inject constructor(
        private val view: JournalContract.View,
        private val authInteractor: AuthInteractor,
        private val journalInteractor: JournalInteractor,
        private val termInteractor: TermInteractor
) : JournalContract.Presenter {
    private var isSyncing = false
    private var isStopped = false

    override fun onCreateView(toolbar: Toolbar) {
        val entries = journalInteractor.getFutureEntry()
        view.updateAdapterData(entries)
        view.setTitle(toolbar, journalInteractor.checkIsHoliday())
        view.logAnalytics()
    }

    override fun onStart() {
        if (isSyncing) view.showLoading()
        isStopped = false
        sync()
    }

    override fun sync(bypass: Boolean) {
        if (!bypass and (isSyncing or !journalInteractor.isSyncOverdue())) return
        authInteractor.execute().flatMap {
            termInteractor.sync(it)
        }.flatMap {
            journalInteractor.sync(it)
        }.doOnSubscribe {
            if (!isStopped) view.showLoading()
            isSyncing = true
        }.doFinally {
            if (!isStopped) view.hideLoading()
            isSyncing = false
        }.subscribe({
            view.showSuccess("Journal & Finance updated")
        }, {
            authInteractor.resetLastSyncDate()
            view.showFailed(it.generateMessage())
        })
    }

    override fun onStop() {
        isStopped = true
    }

    override fun onMenuItemClick(itemId: Int): Boolean {
        when (itemId) {
            R.id.action_refresh -> sync(true)
            R.id.action_setting -> view.navigateToSettings()
        }
        return true
    }

}