package com.directdev.portal.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.adapter.JournalRecyclerAdapter
import com.directdev.portal.model.JournalModel
import com.directdev.portal.network.DataApi
import com.directdev.portal.utils.snack
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_journal.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.onClick
import java.util.*

class JournalFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_journal, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        fab.onClick {
            DataApi.fetchData(ctx).subscribe({
                view.snack("Success")
            }, {
                view.snack("Failed")
            })
        }

        journalToolbar.title = "Today - Holiday"
        journalToolbar.inflateMenu(R.menu.menu_journal)

        val realm = Realm.getDefaultInstance()
        val data = realm.where(JournalModel::class.java)
                .greaterThan("date", Date())
                .findAllSortedAsync("date")
        recyclerContent.layoutManager = LinearLayoutManager(ctx)
        recyclerContent.adapter = JournalRecyclerAdapter(realm, ctx, data, true)
    }
}
