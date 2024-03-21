package com.example.musicservice

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MyApplication: Application() {
    companion object {
        const val CHANNEL_ID: String = "channel_service_example"
    }
    override fun onCreate() {
        super.onCreate()
        createChannelNotification()
    }

    private fun createChannelNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID, "Channel Service Example", NotificationManager.IMPORTANCE_DEFAULT)
            val manager: NotificationManager = getSystemService(Notification::class.java) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}