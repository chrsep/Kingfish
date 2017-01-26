package com.directdev.portal.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.adapter.GradesRecyclerAdapter
import com.directdev.portal.model.CourseModel
import com.directdev.portal.model.CreditModel
import com.directdev.portal.model.ScoreModel
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_grades.*
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener
import lecho.lib.hellocharts.model.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.ctx
import kotlin.properties.Delegates

class GradesFragment : Fragment(), AnkoLogger, LineChartOnValueSelectListener {

    private var realm: Realm by Delegates.notNull()
    private val termAndValueMap = mutableMapOf<Int, Int>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_grades, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        realm = Realm.getDefaultInstance()
        val lines = mutableListOf<Line>()
        val credit = realm.where(CreditModel::class.java).findAll().sort("term")
        val pointValues = turnGpaToPointValue(credit)
        val line = Line(pointValues)
                .setCubic(true)
                .setColor(ContextCompat.getColor(ctx, R.color.colorAccent))
        lines.add(line)
        val axisX = Axis()
        val data = LineChartData(lines)
        data.axisYLeft = axisX
        setupChart(data)
        setDataByTerm(pointValues.size)
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }

    override fun onValueSelected(p0: Int, p1: Int, p2: PointValue?) {
        setDataByTerm(p2?.x?.toInt())
    }

    override fun onValueDeselected() {
    }

    private fun setupChart(data: LineChartData) {
        chart.lineChartData = data
        chart.onValueTouchListener = this
        chart.isZoomEnabled = false
        chart.isValueSelectionEnabled = true
        val viewport = Viewport(chart.currentViewport.left, chart.currentViewport.top + 0.1f, chart.currentViewport.right, chart.currentViewport.bottom - 0.1f)
        chart.maximumViewport = viewport
        chart.currentViewport = viewport
    }

    private fun turnGpaToPointValue(grades: RealmResults<CreditModel>): List<PointValue> {
        var i = 0
        val termAndGradesMap = grades.associateBy({ it.term }, { it.gpaCummulative.toFloat() })
        val valueMap = termAndGradesMap.map {
            i++
            termAndValueMap.put(i, it.key)
            i to it.value
        }.toMap()
        return valueMap.map { PointValue(it.key.toFloat(), it.value) }
    }

    private fun setDataByTerm(index: Int?) {
        val firstTermCode = termAndValueMap[1]
        val chosenTermCode = termAndValueMap[index]
        if (firstTermCode != null && chosenTermCode != null) {
            val year = ((chosenTermCode.toInt() + 99) / 100) - ((firstTermCode.toInt() + 99) / 100)
            val term = when (chosenTermCode.toString().substring(2)) {
                "10" -> ((year * 2) + 1).toString()
                "20" -> ((year * 2) + 2).toString()
                "30" -> ((year * 2) + 2).toString() + " ( SP )"
                else -> "N/A"
            }
            gradesToolbar.title = "Semester " + term

            val course = realm.where(CourseModel::class.java).equalTo("term", chosenTermCode).findAll()
            val courseIdSet = mutableSetOf<String>()
            val scoreResultList = mutableListOf<RealmResults<ScoreModel>>()
            course.forEach { courseIdSet.add(it.courseId) }
            courseIdSet.forEach {
                val result = realm.where(ScoreModel::class.java).equalTo("courseId", it).findAll()
                if (!result.isEmpty()) scoreResultList.add(result)
            }

            if (scoreResultList.isEmpty()) empty_placeholder.visibility = View.VISIBLE
            else empty_placeholder.visibility = View.GONE

            gradesRecycler.layoutManager = LinearLayoutManager(ctx)
            gradesRecycler.adapter = GradesRecyclerAdapter(realm, scoreResultList)
        }
    }
}
