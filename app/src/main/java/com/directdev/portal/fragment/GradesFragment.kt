package com.directdev.portal.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.model.CreditModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_grades.*
import org.jetbrains.anko.AnkoLogger
import java.util.*
import kotlin.properties.Delegates

class GradesFragment : Fragment(), AnkoLogger {
    private var realm: Realm by Delegates.notNull()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_grades, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        realm = Realm.getDefaultInstance()
        val grades = realm.where(CreditModel::class.java).findAll()
        val entries = grades.map {
            val gpa = it.gpaCummulative.toFloat()
            val term = it.term.toFloat()
            Entry(term, gpa)
        }
        Collections.sort(entries, EntryXComparator())
        val dataset = LineDataSet(entries, "Label")
        val data = LineData(dataset)
        testChart.data = data
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }
}
