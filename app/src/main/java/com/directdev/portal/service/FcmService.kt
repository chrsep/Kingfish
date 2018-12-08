package com.directdev.portal.service

import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import com.directdev.portal.R
import com.directdev.portal.features.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.notificationManager
import org.jetbrains.anko.singleTop

class FcmService : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage?) {
        val intent = intentFor<SplashActivity>("message" to p0?.notification?.body.toString(), "title" to p0?.notification?.title.toString()).singleTop()
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)
        val notificationBuilder = NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(p0?.notification?.title.toString())
                .setContentText(p0?.notification?.body.toString())
                .setContentIntent(pendingIntent)
        notificationManager.notify(0, notificationBuilder.build())
    }
}
