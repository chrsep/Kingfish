package com.directdev.portal.features

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.directdev.portal.R
import com.directdev.portal.features.finance.FinancesFragment
import com.directdev.portal.features.grades.GradesFragment
import com.directdev.portal.features.journal.JournalFragment
import com.directdev.portal.features.resources.ResourceFragment
import com.directdev.portal.utils.readPref
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alert
import kotlin.properties.Delegates

/**-------------------------------------------------------------------------------------------------
 *
 * This is NOT the activity that always gets loaded first when Portal starts (#SplashActivity is).
 * MainActivity acts as the container for the fragments. It's shows the Bottom bar and control
 * which fragments to be displayed.
 *
 *------------------------------------------------------------------------------------------------*/

class MainActivity : AppCompatActivity(), AnkoLogger {
    private var mFirebaseAnalytics: FirebaseAnalytics by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        bottomBar.setOnTabSelectListener {
            val fragment = when (it) {
                R.id.tab_journal -> JournalFragment()
                R.id.tab_grades -> GradesFragment()
                R.id.tab_finances -> FinancesFragment()
                R.id.tab_resources -> ResourceFragment()
                else -> JournalFragment()
            }
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit()
        }
        handleNotification()
    }

    // TODO: REFACTOR | Not sure what this function is for, further investigation needed
    override fun onResume() {
        super.onResume()
        if (!readPref(R.string.isLoggedIn, false)) finishAffinity()
    }

    /**-------------------------------------------------------------------------------------------------
     *
     * Handles notification from Firebase Cloud Messaging. When a notification is clicked, it will start
     * MainActivity with an 'extra'. So we check if the extra is empty or not, and choose whether to
     * show the alert containing the message or not.
     *
     *------------------------------------------------------------------------------------------------*/

    private fun handleNotification() {
        val extra = intent.getBundleExtra("Notify")
        if (extra != null && extra.getString("message") != null) {
            alert(extra.getString("message"), extra.getString("title")) {
                negativeButton("Ok, Got it")
            }.show()
        }
    }
}
