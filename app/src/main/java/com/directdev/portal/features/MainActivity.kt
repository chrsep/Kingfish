package com.directdev.portal.features

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import com.directdev.portal.R
import com.directdev.portal.features.finance.FinancesFragment
import com.directdev.portal.features.grades.GradesFragment
import com.directdev.portal.features.journal.JournalFragment
import com.directdev.portal.utils.readPref
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alert
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 *
 * This is NOT the activity that always gets loaded first when Portal starts (#SplashActivity is).
 * MainActivity acts as the container for the fragments. It's shows the Bottom bar and control
 * which fragments to be displayed.
 *
 *------------------------------------------------------------------------------------------------*/

class MainActivity : Activity(), AnkoLogger, HasFragmentInjector {
    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var realm: Realm

    override fun fragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomBar.setOnTabSelectListener {

            // TODO: Fragment is aalways recreated, this causes multiple presenter to be created
            // and multiple sync request created.
            val fragment = fragmentManager.findFragmentByTag(it.toString()) ?: when (it) {
                R.id.tab_journal -> JournalFragment()
                R.id.tab_grades -> GradesFragment()
                R.id.tab_finances -> FinancesFragment()
            // R.id.tab_resources -> ResourceFragment()
                else -> JournalFragment()
            }
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment, it.toString())
                    .addToBackStack(it.toString())
                    .commit()
        }
        handleNotification()
    }

    // TODO: REFACTOR | Not sure what this function is for, further investigation needed
    override fun onResume() {
        super.onResume()
        if (!readPref(R.string.isLoggedIn, false)) finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    /**---------------------------------------------------------------------------------------------
     * Handles notification from Firebase Cloud Messaging. When the notification is clicked, it
     * will start MainActivity with an 'extra'. So we check if the extra is empty or not, and choose
     * whether to show the alert containing the message or not.
     *--------------------------------------------------------------------------------------------*/

    private fun handleNotification() {
        val extra = intent.getBundleExtra("Notify")
        if (extra?.getString("message") != null) {
            alert(extra.getString("message"), extra.getString("title")) {
                negativeButton("Ok, Got it") {}
            }.show()
        }
    }
}
