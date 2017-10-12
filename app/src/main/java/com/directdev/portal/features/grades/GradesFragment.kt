package com.directdev.portal.features.grades

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.models.CreditModel
import com.directdev.portal.models.ScoreModel
import com.directdev.portal.utils.action
import com.directdev.portal.utils.snack
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.AndroidInjection
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_grades.*
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener
import lecho.lib.hellocharts.model.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.ctx
import org.jetbrains.anko.runOnUiThread
import javax.inject.Inject

class GradesFragment : Fragment(), AnkoLogger, LineChartOnValueSelectListener, GradesContract.View {
    @Inject override lateinit var fbAnalytics: FirebaseAnalytics
    @Inject override lateinit var presenter: GradesContract.Presenter
    @Inject lateinit var adapter: GradesRecyclerAdapter

    override fun onAttach(context: Context?) {
        if (android.os.Build.VERSION.SDK_INT >= 23) AndroidInjection.inject(this)
        super.onAttach(context)
    }

    // This doesn't get called on android 23 and up
    override fun onAttach(activity: Activity?) {
        if (android.os.Build.VERSION.SDK_INT < 23) AndroidInjection.inject(this)
        super.onAttach(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_grades, container, false)
        val gradesRecycler = view.findViewById<RecyclerView>(R.id.gradesRecycler)
        gradesRecycler.layoutManager = LinearLayoutManager(activity)
        gradesRecycler.adapter = adapter
        return view
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    // Setup the GPA Graph from the given data (credit)
    override fun setGpaGraphData(credits: RealmResults<CreditModel>) {
        val lines = mutableListOf<Line>()
        val pointValues = turnGpaToPointValue(credits)
        val line = Line(pointValues).setCubic(true)
                .setColor(ContextCompat.getColor(ctx, R.color.colorAccent))
        lines.add(line)
        val data = LineChartData(lines)
        val xAxis = Axis()
        xAxis.formatter = SimpleAxisValueFormatter(1)
        xAxis.setHasSeparationLine(false)
        data.axisYLeft = xAxis
        chart.lineChartData = data
    }

    // Setup the styling of the GPA Graph
    override fun setGraphStyle() {
        val viewport = Viewport(
                chart.currentViewport.left,
                chart.currentViewport.top + 0.1f,
                chart.currentViewport.right,
                chart.currentViewport.bottom - 0.1f
        )
        chart.onValueTouchListener = this
        chart.isZoomEnabled = false
        chart.isValueSelectionEnabled = true
        chart.maximumViewport = viewport
        chart.currentViewport = viewport
    }

    // Called when the dots on the GPA graph is tapped
    override fun onValueSelected(p0: Int, p1: Int, p2: PointValue?) {
        p2?.x?.toInt()?.let { presenter.switchTerm(it) }
    }

    // Turns list of GPA from database, into point values for the GPA graph
    private fun turnGpaToPointValue(credits: RealmResults<CreditModel>): List<PointValue> {
        var i = 0
        val termAndGradesMap = credits.associateBy({ it.term }, { it.gpaCummulative.toFloat() })
        return termAndGradesMap.map {
            i++
            PointValue(i.toFloat(), it.value)
        }
    }

    override fun updateRecycler(grades: List<RealmResults<ScoreModel>>, credits: CreditModel) {
        adapter.updateData(grades, credits)
    }

    override fun hideGradesRecycler() {
        empty_placeholder.visibility = View.VISIBLE
        gradesRecycler.visibility = View.GONE
    }

    override fun showGradesRecycler() {
        empty_placeholder.visibility = View.GONE
        gradesRecycler.visibility = View.VISIBLE
    }

    override fun logAnalytics() {
        val bundle = Bundle()
        bundle.putString("content", "grades")
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

    override fun setToolbarTitle(title: String) {
        gradesToolbar.title = title
    }

    override fun showLoading() = runOnUiThread { gradesSyncProgress.visibility = View.VISIBLE }

    override fun hideLoading() = runOnUiThread { gradesSyncProgress.visibility = View.INVISIBLE }

    override fun onValueDeselected() {}
}
