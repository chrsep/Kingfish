package com.directdev.portal.service

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class InstanceIdService : FirebaseInstanceIdService(){
    override fun onTokenRefresh() {
        val refreshToken = FirebaseInstanceId.getInstance().token

    }
}
