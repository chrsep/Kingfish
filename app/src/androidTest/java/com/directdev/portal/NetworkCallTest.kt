package com.directdev.portal

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.directdev.portal.features.SplashActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NetworkCallTest {
    @Rule @JvmField
    var activityRule = ActivityTestRule<SplashActivity>(SplashActivity::class.java)

    @Test
    fun serviceTest() {

    }
}
