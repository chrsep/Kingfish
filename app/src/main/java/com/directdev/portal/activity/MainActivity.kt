package com.directdev.portal.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.directdev.portal.R
import com.directdev.portal.fragment.FinancesFragment
import com.directdev.portal.fragment.GradesFragment
import com.directdev.portal.fragment.JournalFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
