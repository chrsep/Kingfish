package com.directdev.portal.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import com.directdev.portal.R
import com.directdev.portal.activity.MainActivity
import com.directdev.portal.model.TermModel
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_grades.*
import kotlinx.android.synthetic.main.fragment_journal.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.ctx
import org.jetbrains.anko.info

class FinancesFragment : Fragment() , AnkoLogger {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_finances, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        val realm = Realm.getDefaultInstance()
        var counter = 0
        val terms = realm.where(TermModel::class.java).findAll().map {
            when(it.value.toString().substring(2)){
                "10" -> counter++
                "20" -> counter++
                else -> return@map counter.toString() + " (SP)"
            }
            info(it.value.toString().substring(2))
            info(counter)
            counter.toString()
        }
        termSpinner.adapter = ArrayAdapter<String>(ctx, R.xml.spinner_item, terms)

        (ctx as MainActivity).setSupportActionBar(journalToolbar)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        (ctx as MainActivity).menuInflater.inflate(R.menu.menu_main, menu)
    }
}