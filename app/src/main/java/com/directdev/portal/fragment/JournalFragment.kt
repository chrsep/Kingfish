package com.directdev.portal.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.adapter.JournalRecyclerAdapter
import com.directdev.portal.model.JournalModel
import com.directdev.portal.network.DataApi
import com.directdev.portal.utils.snack
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_journal.*
import org.jetbrains.anko.appcompat.v7.onMenuItemClick
import org.jetbrains.anko.ctx
import org.joda.time.DateTime
import kotlin.properties.Delegates

class JournalFragment : Fragment() {
    private var realm: Realm by Delegates.notNull()
    private var menuInflated = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.fragment_journal, container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        realm = Realm.getDefaultInstance()
        val journalDates = setupRecycler()
        setupToolbar(journalDates)
    }


    private fun setupToolbar(journalDates: RealmResults<JournalModel>?) {
        val today = DateTime.now().withTimeAtStartOfDay()
        val journalToday = journalDates?.filter {
            it.date == today.toDate()
        } ?: return
        if (journalToday.size > 0) {
            if (journalToday[0].session.size > 0) {
                journalToolbar.title = "Today"
            }
        } else journalToolbar.title = "Today - holiday"
        if (!menuInflated) {
            journalToolbar.inflateMenu(R.menu.menu_journal)
            menuInflated = true
        }
        journalToolbar.onMenuItemClick {
            when (it?.itemId) {
                R.id.action_refresh -> {
                    view?.snack("Updating", Snackbar.LENGTH_INDEFINITE)
                    if (DataApi.isActive) return@onMenuItemClick true
                    DataApi.fetchData(ctx).subscribe({
                        view?.snack("Success")
                    }, {
                        view?.snack("Failed")
                    })
                    true
                }
                R.id.action_setting -> true
                else -> return@onMenuItemClick true
            }
        }
    }

    private fun setupRecycler(): RealmResults<JournalModel>? {
        val today = DateTime().withTimeAtStartOfDay()
        val data = realm.where(JournalModel::class.java)
                .greaterThanOrEqualTo("date", today.toDate())
                .findAllSorted("date")
        recyclerContent.layoutManager = LinearLayoutManager(ctx)
        recyclerContent.adapter = JournalRecyclerAdapter(realm, ctx, data, true)
        return data
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }
}
