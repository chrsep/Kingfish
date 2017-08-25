package com.directdev.portal.features.journal

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.features.SettingsActivity
import com.directdev.portal.models.JournalModel
import com.directdev.portal.network.DataApi
import com.directdev.portal.network.SyncManager
import com.directdev.portal.utils.action
import com.directdev.portal.utils.readPref
import com.directdev.portal.utils.snack
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.functions.Action
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_journal.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import org.joda.time.DateTime
import org.joda.time.Hours
import org.joda.time.format.DateTimeFormat
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
        // Analytics
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(ctx)
        val bundle = Bundle()
        bundle.putString("content", "journal")
        mFirebaseAnalytics.logEvent("content_opened", bundle)
        try {
            realm = Realm.getDefaultInstance()
        } catch (err: IllegalStateException){
            Realm.init(ctx.applicationContext)
            realm = Realm.getDefaultInstance()
        }

        if (DataApi.isActive) view?.snack("Updating", Snackbar.LENGTH_INDEFINITE)
        val journalDates = setupRecycler()
        setupToolbar(journalDates)
        checkLastUpdate()
    }

    private fun checkLastUpdate() {
        val savedData = ctx.readPref(R.string.last_update, "")
        var hours = "ages"
        var hoursInt = 0
        if (savedData != "") {
            val lastUpdate = DateTime.parse(savedData)
            val today = DateTime.now()
            hoursInt = Hours.hoursBetween(lastUpdate, today).hours
            hours = hoursInt.toString() + " hours"
        }
        if (hoursInt > 36)
            view.snack("""You last updated $hours ago""", Snackbar.LENGTH_LONG) {
                action("Update", Color.YELLOW) { update() }
            }
    }


    private fun setupToolbar(journalDates: RealmResults<JournalModel>?) {
        val today = DateTime.now().withTimeAtStartOfDay()
        val journalToday = journalDates?.filter {
            it.date == today.toDate()
        } ?: return
        if (journalToday.isNotEmpty() && (journalToday[0].session.size > 0 || journalToday[0].exam.size > 0) ) {
            journalToolbar.title = "Today - " + today.toString(DateTimeFormat.forPattern("dd MMMM"))
        } else journalToolbar.title = "Today - Holiday"
        if (!menuInflated) {
            journalToolbar.inflateMenu(R.menu.menu_journal)
            menuInflated = true
        }
        journalToolbar.setOnMenuItemClickListener {
            when (it?.itemId) {
                R.id.action_refresh -> {
                    update()
                }
                R.id.action_setting -> {
                    startActivity<SettingsActivity>()
                    true
                }
                else -> return@setOnMenuItemClickListener true
            }
        }
    }

    private fun update(): Boolean {
        view?.snack("Updating", Snackbar.LENGTH_INDEFINITE)
        if (DataApi.isActive) return true
        SyncManager.sync(ctx, SyncManager.COMMON, Action {
            view?.snack("Success")
        }, Action {
            // TODO: This is not how it suppose to be
            view?.snack(DataApi.decideCauseOfFailure(Exception()))
        })
        return true
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
