package com.directdev.portal.features.assignment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import io.realm.Realm
import org.jetbrains.anko.AnkoLogger
import kotlin.properties.Delegates

class AssignmentFragment : Fragment(), AnkoLogger {
    private var realm: Realm by Delegates.notNull()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_assignments, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        realm = Realm.getDefaultInstance()
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }
}