package com.directdev.portal.features.journal

import android.app.Fragment
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.features.SettingsActivity
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
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.startActivity
import org.joda.time.DateTime
import org.joda.time.Hours
import javax.inject.Inject

class JournalFragment : Fragment(), JournalContract.View {

    @Inject override lateinit var fbAnalytics: FirebaseAnalytics
    @Inject override lateinit var presenter: JournalContract.Presenter

    override fun onAttach(context: Context?) {
        AndroidInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_journal, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.journalToolbar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerContent)
        toolbar.inflateMenu(R.menu.menu_journal)
        toolbar.setOnMenuItemClickListener { presenter.onMenuItemClick(it.itemId) }
        presenter.onCreateView(toolbar, recyclerView)
        return view
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun setRecyclerAdapter(view: RecyclerView,
                                    adapter: JournalRecyclerAdapter) {
        view.layoutManager = LinearLayoutManager(activity)
        view.adapter = adapter
    }

    override fun setTitle(toolbar: Toolbar, date: String) {
        toolbar.title = "Today - " + date
    }

    override fun logContentOpened() {
        val bundle = Bundle()
        bundle.putString("content", "journal")
        fbAnalytics.logEvent("content_opened", bundle)
    }

    override fun showLoading() {
        runOnUiThread {
            journalSyncProgress.visibility = View.VISIBLE
        }
    }

    override fun hideLoading() {
        runOnUiThread {
            journalSyncProgress.visibility = View.GONE
        }
    }

    override fun navigateToSettings() = startActivity<SettingsActivity>()

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
