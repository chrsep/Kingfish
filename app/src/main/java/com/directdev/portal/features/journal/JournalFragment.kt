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
import com.directdev.portal.models.JournalModel
import com.directdev.portal.utils.action
import com.directdev.portal.utils.snack
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.AndroidInjection
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_journal.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class JournalFragment : Fragment(), JournalContract.View {
    @Inject override lateinit var fbAnalytics: FirebaseAnalytics
    @Inject override lateinit var presenter: JournalContract.Presenter
    @Inject lateinit var adapter: JournalRecyclerAdapter

    override fun onAttach(context: Context?) {
        AndroidInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_journal, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.journalToolbar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerContent)
        toolbar.inflateMenu(R.menu.menu_journal)
        toolbar.setOnMenuItemClickListener { presenter.onMenuItemClick(it.itemId) }
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        presenter.onCreateView(toolbar)
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

    override fun setTitle(toolbar: Toolbar, date: String) {
        toolbar.title = "Today - " + date
    }

    override fun logContentOpened() {
        val bundle = Bundle()
        bundle.putString("content", "journal")
        fbAnalytics.logEvent("content_opened", bundle)
    }

    override fun showSuccess(message: String) {
        view?.snack(message, Snackbar.LENGTH_SHORT)
    }

    override fun showFailed(message: String) {
        view?.snack(message, Snackbar.LENGTH_INDEFINITE) {
            action("RETRY", Color.YELLOW, { presenter.sync(true) })
        }
    }

    override fun updateAdapterData(data: RealmResults<JournalModel>) = adapter.updateData(data)

    override fun navigateToSettings() = startActivity<SettingsActivity>()

    override fun showLoading() = runOnUiThread { journalSyncProgress.visibility = View.VISIBLE }

    override fun hideLoading() = runOnUiThread { journalSyncProgress.visibility = View.GONE }
}
