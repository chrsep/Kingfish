package com.directdev.portal.features.resources

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.utils.getInitials
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.AndroidInjection
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alert
import javax.inject.Inject

class ResourcesFragment : Fragment(), AnkoLogger, ResourcesContract.View {
    @Inject override lateinit var fbAnalytics: FirebaseAnalytics
    @Inject override lateinit var presenter: ResourcesContract.Presenter
    lateinit var adapter: ResourcesFragmentPagerAdapter

    override fun onAttach(context: Context?) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            AndroidInjection.inject(this)
            adapter = ResourcesFragmentPagerAdapter(fragmentManager)
        }
        super.onAttach(context)
    }

    override fun onAttach(activity: Activity?) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            AndroidInjection.inject(this)
            adapter = ResourcesFragmentPagerAdapter(childFragmentManager)
        }
        super.onAttach(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_resources, container, false)
        val semesterFab = view.findViewById<FloatingActionButton>(R.id.semesterFab)
        val toolbar = view.findViewById<Toolbar>(R.id.resourcesToolbar)
        val tab = view.findViewById<TabLayout>(R.id.tabs)
        val viewPager = view.findViewById<ViewPager>(R.id.tabViewPager)
        val termList = presenter.getSemesters()
        viewPager.adapter = adapter
        presenter.updateSelectedSemester(termList.last().first)
        tab.setupWithViewPager(viewPager)
        toolbar.title = termList.last().second
        semesterFab.setOnClickListener {
            alert {
                items(termList.map { it.second }) { _, o ->
                    toolbar.title = termList[o].second
                    presenter.updateSelectedSemester(termList[o].first)
                }
            }.show()
        }
        return view
    }

    override fun updateCourses(courses: List<Pair<String, Int>>) {
        adapter.clear()
        courses.map {
            adapter.addFrag(ResourcesListFragment.newInstance(it.first, it.second, presenter), it.first.getInitials())
        }
        adapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        val bundle = Bundle()
        bundle.putString("content", "resources")
        fbAnalytics.logEvent("content_opened", bundle)

        presenter.sync()
        /*realm = Realm.getDefaultInstance()
        val term = realm.where(TermModel::class.java).max("value")
        val courses = realm.where(CourseModel::class.java)
                .equalTo("term", term as Long)
                .equalTo("ssrComponent", "LEC")
                .findAll()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            refreshresourceButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor(ctx.getString(R.color.colorAccent)))
        }
        refreshresourceButton.setOnClickListener {
            view.snack("Refreshing data, please wait...", Snackbar.LENGTH_INDEFINITE)
            SyncManager.sync(ctx, SyncManager.RESOURCES, Action {
                view?.snack("Success")
                runOnUiThread {
                    // setRecycler(courseResourceSpinner.selectedView as TextView, courses)
                }
            }, Action {
                view?.snack("Failed")
            }, courses)
        }*/
    }

    /*private fun setRecycler(p1: View?, courses: RealmResults<CourseModel>) {
        if (realm.isClosed) return
        val selected = courses.filter { it.courseName == (p1 as TextView).text }
        if (selected.isEmpty()) {
            Crashlytics.setInt("course size", courses.size)
            Crashlytics.log("setRecycler")
            Crashlytics.log((p1 as TextView).text.toString())

            resourceEmptyPlaceholder.visibility = View.VISIBLE
            return
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
    }*/
}