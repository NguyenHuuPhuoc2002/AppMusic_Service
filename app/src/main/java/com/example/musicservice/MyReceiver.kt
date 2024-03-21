package com.example.musicservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyReceiver : BroadcastReceiver() {

    //Nhận dữ liệu
    override fun onReceive(context: Context?, intent: Intent?) {
        val actionMusic: Int? = intent?.getIntExtra("action_music", 0)

        // Tiếp tục gửi lại dữ liệu sang MyService
        val intentService = Intent(context, MyService::class.java)
        intentService.putExtra("action_music_service", actionMusic) // Sửa đổi này

        context?.startService(intentService)
    }
}
