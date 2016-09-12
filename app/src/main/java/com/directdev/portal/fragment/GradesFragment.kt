package com.directdev.portal.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.model.CreditModel
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_grades.*
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.Viewport
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
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
        val grades = realm.where(CreditModel::class.java).findAll().sort("term")
        val termAndGradesMap = mutableMapOf<Int, Float>()
        val valueMap = mutableMapOf<Int, Float>()
        val pointValues = mutableListOf<PointValue>()
        val lines = mutableListOf<Line>()
        var i = 0
        grades.forEach {
            termAndGradesMap.put(it.term, it.gpaCummulative.toFloat())
        }
        termAndGradesMap.map {
            i++
            termAndValueMap.put(i, it.key)
            valueMap.put(i, it.value)
        }
        valueMap.forEach { pointValues.add(PointValue(it.key.toFloat(), it.value)) }
        val line = Line(pointValues)
                .setCubic(true)
                .setColor(resources.getColor(R.color.colorAccent))
        lines.add(line)
        val data = LineChartData(lines)
        data.valueLabelTextSize = 16
        chart.lineChartData = data
        chart.onValueTouchListener = this
        chart.isZoomEnabled = false
        chart.isValueSelectionEnabled = true

        val viewport = Viewport(chart.currentViewport.left, chart.currentViewport.top + 0.1f, chart.currentViewport.right, chart.currentViewport.bottom - 0.1f)
        chart.maximumViewport = viewport
        chart.currentViewport = viewport

        setDataByTerm(i)
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

    private fun setDataByTerm(index: Int?) {
        val firstTermCode = termAndValueMap[1]
        val chosenTermCode = termAndValueMap[index]
        info { firstTermCode }
        info { chosenTermCode }
        if (firstTermCode != null && chosenTermCode != null){
            val year = ((chosenTermCode.toInt() + 99)/100) -  ((firstTermCode.toInt() + 99)/100)
            val term = when(chosenTermCode.toString().substring(2)){
                "10" -> ((year * 2) + 1).toString()
                "20" -> ((year * 2) + 2).toString()
                "30" -> (year * 2).toString() + " ( SP )"
                else -> "N/A"
            }
            gradesToolbar.title = "Semester " + term
        }
    }
}
