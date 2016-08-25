package com.directdev.portal.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.directdev.portal.R
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
                R.id.tab_info -> {

                }
            }
        }
    }
}
