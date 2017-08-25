package com.directdev.portal

import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Created by chris on 8/16/17.
 */
interface BaseView<T> {
    var presenter: T
    var fbAnalytics: FirebaseAnalytics
}