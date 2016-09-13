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
import org.jetbrains.anko.appcompat.v7.onMenuItemClick
import org.jetbrains.anko.ctx
import org.jetbrains.anko.onClick
import java.util.*
import kotlin.properties.Delegates

class JournalFragment : Fragment() {
    private var realm: Realm by Delegates.notNull()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_journal, container, false)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        journalToolbar.inflateMenu(R.menu.menu_journal)
        journalToolbar.onMenuItemClick {
            when(it?.itemId){
                R.id.action_refresh -> {
                    if (DataApi.isActive) return@onMenuItemClick true
                    DataApi.initializeApp(ctx).subscribe({
                        view?.snack("Success")
                    }, {
                        view?.snack("Failed")
                    })
                    true
                }
                R.id.action_setting -> {
                    true
                }
                else -> return@onMenuItemClick true
            }
        }
        journalToolbar.title = "Today - Holiday"

        realm = Realm.getDefaultInstance()
        val data = realm.where(JournalModel::class.java)
                .greaterThan("date", Date())
                .findAllSortedAsync("date")
        recyclerContent.layoutManager = LinearLayoutManager(ctx)
        recyclerContent.adapter = JournalRecyclerAdapter(realm, ctx, data, true)
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }
}
