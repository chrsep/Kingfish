package com.directdev.portal.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.directdev.portal.R
import com.directdev.portal.activity.MainActivity
import com.directdev.portal.adapter.FinancesRecyclerAdapter
import com.directdev.portal.model.FinanceModel
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_assignments.*
import org.jetbrains.anko.ctx
import kotlin.properties.Delegates

class AssignmentFragment : Fragment(){
    private var realm: Realm by Delegates.notNull()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_assignments, container, false)
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