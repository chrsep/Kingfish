package com.directdev.portal.features.journal

import android.support.v7.widget.LinearLayoutManager
import com.directdev.portal.interactors.AuthInteractor
import com.directdev.portal.repositories.JournalRepository
import com.directdev.portal.repositories.TimeStampRepository
import javax.inject.Inject

class JournalPresenter @Inject constructor(
        private val authInteractor: AuthInteractor,
        private val view: JournalContract.View,
        private val timeStampRepo: TimeStampRepository,
        private val adapter: JournalRecyclerAdapter,
        private val journalRepo: JournalRepository,
        private val layoutManager: LinearLayoutManager
) : JournalContract.Presenter {
    private var isSyncing = false

    override fun onStart() {
        val today = timeStampRepo.today()
        val todayString = timeStampRepo.todayString()
        val schedules = journalRepo.findFutureSchedules(today.toDate())
        val todaySchedule = schedules.filter { it.date == today.toDate() }
        adapter.updateData(schedules)
        view.setRecyclerAdapter(layoutManager, adapter)
        view.logContentOpened()
        view.setupToolbar(todaySchedule, todayString)
    }

    override fun update() {
        val syncChain = authInteractor.execute().doOnSubscribe {
            isSyncing = true
        }
        if (!isSyncing) syncChain.subscribe({

        }, {

        })
    }
}