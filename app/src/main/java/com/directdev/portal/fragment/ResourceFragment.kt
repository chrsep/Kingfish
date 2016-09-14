package com.directdev.portal.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.directdev.portal.R
import com.directdev.portal.activity.MainActivity
import com.directdev.portal.adapter.FinancesRecyclerAdapter
import com.directdev.portal.model.CourseModel
import com.directdev.portal.model.FinanceModel
import com.directdev.portal.network.DataApi
import com.directdev.portal.utils.snack
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_finances.*
import kotlinx.android.synthetic.main.fragment_resources.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.ctx
import org.jetbrains.anko.onClick
import kotlin.properties.Delegates

/**
 * Created by chris on 9/14/2016.
 */
class ResourceFragment : Fragment(), AnkoLogger{
    private var realm: Realm by Delegates.notNull()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_resources, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        realm = Realm.getDefaultInstance()
        val courses = realm.where(CourseModel::class.java).findAll()
        fab.onClick {
            DataApi.fetchResource(ctx, courses).subscribe ({
                view.snack("Suckses")
            },{
                throw it
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        (ctx as MainActivity).menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }
}