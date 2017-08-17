package com.directdev.portal.features

import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceFragment
import com.directdev.portal.R
import kotlinx.android.synthetic.main.activity_settings.*

/**-------------------------------------------------------------------------------------------------
 *
 * This activity handles the 'Setting page', it's just about setting up the toolbar and layout for
 * the 'Setting page'.
 *
 *------------------------------------------------------------------------------------------------*/

class SettingsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        fragmentManager.beginTransaction().replace(R.id.settingsContent, SettingFragment()).commit()
        settingToolbar.title = "Settings"
        settingToolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        settingToolbar.setNavigationOnClickListener { finish() }
    }

    class SettingFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_data)
        }
    }
}