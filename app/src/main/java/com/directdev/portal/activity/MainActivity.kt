package com.directdev.portal.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.directdev.portal.R
import com.directdev.portal.fragment.FinancesFragment
import com.directdev.portal.fragment.GradesFragment
import com.directdev.portal.fragment.JournalFragment
import com.directdev.portal.fragment.ResourceFragment
import com.directdev.portal.utils.readPref
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alert
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), AnkoLogger {
    private var mFirebaseAnalytics : FirebaseAnalytics by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!(readPref(R.string.isLoggedIn, true) as Boolean)) finish()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setContentView(R.layout.activity_main)
        bottomBar.setOnTabSelectListener {
            val transaction = fragmentManager.beginTransaction()
            when (it) {
                R.id.tab_journal -> {
                    transaction.replace(R.id.fragmentContainer, JournalFragment()).commit()
                }
                R.id.tab_grades -> {
                    transaction.replace(R.id.fragmentContainer, GradesFragment()).commit()
                }
                R.id.tab_finances -> {
                    transaction.replace(R.id.fragmentContainer, FinancesFragment()).commit()
                }
//                R.id.tab_assignments -> {
//                    transaction.replace(R.id.fragmentContainer, AssignmentFragment()).commit()
//                }
                R.id.tab_resources -> {
                    transaction.replace(R.id.fragmentContainer, ResourceFragment()).commit()
                }
            }
        }
        getNotif()
    }

    private fun getNotif() {
        val notifyExtra = intent.getBundleExtra("Notify")
        if (notifyExtra != null && notifyExtra.getString("message") != null) {
            alert(notifyExtra.getString("message"), notifyExtra.getString("title")) {
                negativeButton("Ok, Got it")
            }.show()
        }
    }

    private fun showAddAssignment() {
        bottomSheet.showWithSheetView(layoutInflater.inflate(R.layout.bottomsheet_assignment, bottomSheet, false))
    }
}
