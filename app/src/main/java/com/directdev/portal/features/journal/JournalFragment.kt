package com.directdev.portal.features.journal

import android.app.Fragment
import android.content.Context
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
import dagger.android.AndroidInjection
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.fragment_journal.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import org.joda.time.DateTime
import org.joda.time.Hours
import javax.inject.Inject

class JournalFragment : Fragment(), JournalContract.View {

    @Inject override lateinit var fbAnalytics: FirebaseAnalytics
    @Inject override lateinit var presenter: JournalContract.Presenter

    private var menuInflated = false

    override fun onAttach(context: Context?) {
        AndroidInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.fragment_journal, container, false)

    override fun onStart() {
        super.onStart()
        presenter.onStart()
        if (!menuInflated) {
            journalToolbar.inflateMenu(R.menu.menu_journal)
            menuInflated = true
        }
        journalToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_refresh -> {
                    update()
                }
                R.id.action_setting -> {
                    startActivity<SettingsActivity>()
                }
            }
            true
        }
    }

    override fun setRecyclerAdapter(layoutManager: LinearLayoutManager, adapter: JournalRecyclerAdapter) {
        recyclerContent.layoutManager = layoutManager
        recyclerContent.adapter = adapter
    }

    override fun setupToolbar(schedules: List<JournalModel>, dateString: String) {
        journalToolbar.title = if (schedules.isNotEmpty() &&
                (schedules[0].session.size > 0 || schedules[0].exam.size > 0))
            "Today - " + dateString
        else
            "Today - Holiday"
    }

    override fun logContentOpened() {
        val bundle = Bundle()
        bundle.putString("content", "journal")
        fbAnalytics.logEvent("content_opened", bundle)
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
}
