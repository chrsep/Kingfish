package com.directdev.portal.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.directdev.portal.R
import com.directdev.portal.fragment.FinancesFragment
import com.directdev.portal.fragment.GradesFragment
import com.directdev.portal.fragment.JournalFragment
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private var mFirebaseAnalytics : FirebaseAnalytics by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
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
            }
        }
    }
}
