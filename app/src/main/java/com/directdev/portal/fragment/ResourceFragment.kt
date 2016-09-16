package com.directdev.portal.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.directdev.portal.R
import com.directdev.portal.adapter.ResourcesRecyclerAdapter
import com.directdev.portal.model.CourseModel
import com.directdev.portal.model.ResModel
import com.directdev.portal.model.TermModel
import com.directdev.portal.network.DataApi
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_resources.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.ctx
import kotlin.properties.Delegates

/**
 * Created by chris on 9/14/2016.
 */
class ResourceFragment : Fragment(), AnkoLogger {
    private var realm: Realm by Delegates.notNull()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_resources, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        realm = Realm.getDefaultInstance()
        val term = realm.where(TermModel::class.java).max("value")
        val courses = realm.where(CourseModel::class.java)
                .equalTo("term", term as Long)
                .equalTo("ssrComponent", "LEC")
                .findAll()
        val courseName = courses.map { it.courseName }.toSet()
        val spinnerAdapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, courseName.toList())
        courseResourceSpinner.adapter = spinnerAdapter
        DataApi.fetchResource(ctx, courses).subscribe({}, {})
        courseResourceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (realm.isClosed) return
                val selected = courses.filter { it.courseName == (p1 as TextView).text }
                val resources = realm.where(ResModel::class.java)
                        .equalTo("classNumber", selected[0].classNumber)
                        .findFirst() ?: return
                val outlineMap = resources.resources.map { it.courseOutlineTopicID }.toSet()
                resourceRecycler.visibility = View.VISIBLE
                resourceEmptyPlaceholder.visibility = View.GONE
                resourceRecycler.layoutManager = LinearLayoutManager(ctx)
                resourceRecycler.adapter = ResourcesRecyclerAdapter(ctx, outlineMap.toList(), resources)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }
}