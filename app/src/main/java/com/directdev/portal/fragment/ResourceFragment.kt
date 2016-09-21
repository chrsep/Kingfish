package com.directdev.portal.fragment

import android.app.Fragment
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.directdev.portal.R
import com.directdev.portal.adapter.ResourcesRecyclerAdapter
import com.directdev.portal.model.CourseModel
import com.directdev.portal.model.ResModel
import com.directdev.portal.model.TermModel
import com.directdev.portal.network.DataApi
import com.directdev.portal.utils.snack
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_resources.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.ctx
import org.jetbrains.anko.onClick
import org.jetbrains.anko.runOnUiThread
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
        try {
            refreshresourceButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor(ctx.getString(R.color.colorAccent)))
        } catch (e: NoSuchMethodError) {
        }
        refreshresourceButton.onClick {
            view.snack("Refreshing data, please wait...", Snackbar.LENGTH_INDEFINITE)
            DataApi.fetchResources(ctx, courses).subscribe({
                view?.snack("Success")
                runOnUiThread {
                    setRecycler(courseResourceSpinner.selectedView as TextView, courses)
                }
            }, {
                view?.snack("Failed")
            })
        }
        val courseName = courses.map { it.courseName }.toSet()
        val spinnerAdapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, courseName.toList())
        courseResourceSpinner.adapter = spinnerAdapter
        courseResourceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                setRecycler(p1, courses)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }

    private fun setRecycler(p1: View?, courses: RealmResults<CourseModel>) {
        if (realm.isClosed) return
        val selected = courses.filter { it.courseName == (p1 as TextView).text }
        if (selected.isEmpty()) {
            Crashlytics.setInt("course size", courses.size)
            Crashlytics.log("setRecycler")
            Crashlytics.log((p1 as TextView).text.toString())
        }
        val resources = realm.where(ResModel::class.java)
                .equalTo("classNumber", selected[0].classNumber)
                .findFirst()
        if (resources == null) {
            resourceEmptyPlaceholder.visibility = View.VISIBLE
            return
        }
        val outlineMap = resources.resources.map { it.courseOutlineTopicID }.toSet()
        resourceEmptyPlaceholder.visibility = View.GONE
        resourceRecycler.visibility = View.VISIBLE
        resourceRecycler.layoutManager = LinearLayoutManager(ctx)
        resourceRecycler.adapter = ResourcesRecyclerAdapter(ctx, outlineMap.toList(), resources)
    }
}