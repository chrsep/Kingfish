package com.directdev.portal.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.ArrayAdapter
import com.directdev.portal.R
import com.directdev.portal.activity.MainActivity
import com.directdev.portal.adapter.FinancesRecyclerAdapter
import com.directdev.portal.adapter.JournalRecyclerAdapter
import com.directdev.portal.model.FinanceModel
import com.directdev.portal.model.JournalModel
import com.directdev.portal.model.TermModel
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_finances.*
import kotlinx.android.synthetic.main.fragment_grades.*
import kotlinx.android.synthetic.main.fragment_journal.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.ctx
import org.jetbrains.anko.info
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*
import kotlin.properties.Delegates

class FinancesFragment : Fragment() , AnkoLogger {
    private var realm: Realm by Delegates.notNull()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_finances, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        realm = Realm.getDefaultInstance()
        val data = realm.where(FinanceModel::class.java).findAll()
        val layout = LinearLayoutManager(ctx)
        layout.stackFromEnd = true
        layout.reverseLayout = true
        financesRecycler.layoutManager = layout
        financesRecycler.adapter = FinancesRecyclerAdapter(realm, ctx, data, true)
        financeToolbar.title = "Billing"
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        (ctx as MainActivity).menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }
}