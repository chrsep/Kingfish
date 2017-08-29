package com.directdev.portal.features.journal

import android.support.v7.widget.LinearLayoutManager
import com.directdev.portal.R
import com.directdev.portal.interactors.AuthInteractor
import com.directdev.portal.interactors.JournalInteractor
import javax.inject.Inject

class JournalPresenter @Inject constructor(
        private val authInteractor: AuthInteractor,
        private val view: JournalContract.View,
        private val adapter: JournalRecyclerAdapter,
        private val layoutManager: LinearLayoutManager,
        private val journalInteractor: JournalInteractor
) : JournalContract.Presenter {
    private var isSyncing = false
    private var menuInflated = false

    override fun onStart() {
        if (!menuInflated) {
            view.inflateMenu()
            menuInflated = true
        }
        val entries = journalInteractor.getFutureEntry()
        adapter.updateData(entries)
        view.setTitle(journalInteractor.checkIsHoliday())
        view.setRecyclerAdapter(layoutManager, adapter)
        view.logContentOpened()
        sync()
    }

    override fun sync() {
        if (isSyncing) return
        authInteractor.execute().flatMap {
            journalInteractor.sync(it)
        }.doOnSubscribe {
            view.showLoading()
            isSyncing = true
        }.doFinally {
            view.hideLoading()
            isSyncing = false
        }.subscribe({

        }, {
            throw it
        })
    }

    override fun onMenuItemClick(itemId: Int): Boolean {
        when (itemId) {
            R.id.action_refresh -> sync()
            R.id.action_setting -> view.navigateToSettings()
        }
        return true
    }

}