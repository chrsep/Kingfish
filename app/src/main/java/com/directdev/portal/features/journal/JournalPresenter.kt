package com.directdev.portal.features.journal

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.directdev.portal.R
import com.directdev.portal.interactors.AuthInteractor
import com.directdev.portal.interactors.JournalInteractor
import javax.inject.Inject

class JournalPresenter @Inject constructor(
        private val authInteractor: AuthInteractor,
        private val view: JournalContract.View,
        private val adapter: JournalRecyclerAdapter,
        private val journalInteractor: JournalInteractor
) : JournalContract.Presenter {
    private var isSyncing = false
    private var isStopped = false

    override fun onCreateView(toolbar: Toolbar, recyclerView: RecyclerView) {
        val entries = journalInteractor.getFutureEntry()
        adapter.updateData(entries)
        view.setTitle(toolbar, journalInteractor.checkIsHoliday())
        view.setRecyclerAdapter(recyclerView, adapter)
        view.logContentOpened()
    }

    override fun onStart() {
        if (isSyncing) view.showLoading()
        isStopped = false
        sync()
    }

    override fun sync() {
        if (isSyncing) return
        authInteractor.execute().flatMap {
            journalInteractor.sync(it)
        }.doOnSubscribe {
            if (!isStopped) view.showLoading()
            isSyncing = true
        }.doFinally {
            if (!isStopped) view.hideLoading()
            isSyncing = false
        }.subscribe({

        }, {
            throw it
        })
    }

    override fun onStop() {
        isStopped = true
    }

    override fun onMenuItemClick(itemId: Int): Boolean {
        when (itemId) {
            R.id.action_refresh -> sync()
            R.id.action_setting -> view.navigateToSettings()
        }
        return true
    }

}