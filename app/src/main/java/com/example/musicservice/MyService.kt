package com.example.musicservice

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat

import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.musicservice.MyApplication.Companion.CHANNEL_ID
import kotlin.properties.Delegates


class MyService: Service() {
    companion object {
        const val ACTION_PAUSE: Int = 1
        const val ACTION_RESUME: Int = 2
        const val ACTION_CLEAR: Int = 3
        const val ACTION_START: Int = 4
        const val ACTION_NEXT: Int = 5
        const val ACTION_PREVIOUS: Int = 6
    }
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying: Boolean = false
    private lateinit var mList: ArrayList<Song>
    private var pos by Delegates.notNull<Int>()
    override fun onCreate() {
        super.onCreate()

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //Nhận và gửi dữ liệu lên push notification
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       /* val bundle = intent?.extras
        if(bundle != null){
            val song = bundle.get("object_song") as? Song
            if (song != null) {
                msong = song
                startMusic(song)
                sendNotificationMedia(song)
            }
        }*/
        if (intent != null && intent.hasExtra("songList")) {
            val receivedSongList = intent.getParcelableArrayListExtra<Song>("songList")
            pos = intent.getIntExtra("pos", -1)
            if (receivedSongList != null) {
                mList = receivedSongList
                startMusic(receivedSongList)
                sendNotificationMedia(receivedSongList)
            }
        }

        val actionMusic = intent?.getIntExtra("action_music_service", 0)
        if (actionMusic != null) {
            handleActionMusic(actionMusic)
        }
        return START_NOT_STICKY
    }
    private fun startMusic(song: java.util.ArrayList<Song>?) {
        var currentPosition = -1 // Lưu vị trí của bài hát hiện tại
        // Dừng bài hát hiện tại nếu có
        if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        val isSameSong = currentPosition == pos
        mediaPlayer = MediaPlayer.create(applicationContext, song?.get(pos)?.resoure!!)
        currentPosition = pos
        if (!isSameSong) {
            mediaPlayer?.start()
            isPlaying = true
        }else{
            mediaPlayer?.start()
            isPlaying = true
        }

        sendActionToActivity(ACTION_START)
//        if(mediaPlayer == null){
//            mediaPlayer = song?.get(pos)?.let { MediaPlayer.create(applicationContext, it.resoure) }
//        }
//        mediaPlayer?.start()
//        isPlaying = true
    }
    private fun handleActionMusic(action: Int) {
        when (action) {
            ACTION_PAUSE -> {
                pauseMusic()
            }
            ACTION_RESUME -> {
                resumeMusic()
            }
            ACTION_NEXT -> {
                nextSong()
            }
            ACTION_PREVIOUS -> {
                preSong()
            }
            ACTION_CLEAR -> {
                stopSelf()
                sendActionToActivity(ACTION_CLEAR)
            }else -> {

            }
        }
    }

    private fun preSong() {
        if(pos > mList.size - 1){
            pos--
        }else{
            pos = mList.size - 1
        }
        mediaPlayer?.stop()
        mediaPlayer = MediaPlayer.create(applicationContext, mList[pos].resoure)
        mediaPlayer?.start()
        isPlaying = true
        sendNotificationMedia(mList)
        sendActionToActivity(ACTION_PREVIOUS)
    }

    private fun nextSong() {
        if(pos < mList.size - 1){
            pos++
        }else{
            pos = 0
        }
        mediaPlayer?.stop()
        mediaPlayer = MediaPlayer.create(applicationContext, mList[pos].resoure)
        mediaPlayer?.start()
        isPlaying = true
        sendNotificationMedia(mList)
        sendActionToActivity(ACTION_NEXT)
    }

    private fun resumeMusic() {
        if(mediaPlayer != null && !isPlaying){
            mediaPlayer!!.start()
            isPlaying = true
            sendNotificationMedia(mList)
            sendActionToActivity(ACTION_RESUME)
        }
    }

    private fun pauseMusic(){
        if(mediaPlayer != null && isPlaying){
            mediaPlayer!!.pause()
            isPlaying = false
            sendNotificationMedia(mList)
            sendActionToActivity(ACTION_PAUSE)
        }
    }

    //Gửi dữ liệu lên notification Custom
    private fun sendNotificationMedia(song: java.util.ArrayList<Song>?){
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_music)
        val mediaSessionCompat = MediaSessionCompat(this, "tag")
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)

            .setSmallIcon(R.drawable.ic_small_music)
            .setSubText("HuuPhuoc")
            .setContentTitle(song?.get(pos)?.title)
            .setContentText(song?.get(pos)?.single)
            .setLargeIcon(bitmap)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(3)
                .setMediaSession(mediaSessionCompat.sessionToken))

        if(isPlaying){
            notificationBuilder
                .addAction(R.drawable.ic_previous, "Previous", getPendingIntent(this, ACTION_PREVIOUS))
                .addAction(R.drawable.ic_pause, "Pause", getPendingIntent(this, ACTION_PAUSE))
                .addAction(R.drawable.ic_next, "Next", getPendingIntent(this, ACTION_NEXT))
                .addAction(R.drawable.ic_clear, "Clear", getPendingIntent(this, ACTION_CLEAR))
        }else{
            notificationBuilder
                .addAction(R.drawable.ic_previous, "Previous", getPendingIntent(this, ACTION_PREVIOUS))
                .addAction(R.drawable.ic_play, "Pause", getPendingIntent(this, ACTION_RESUME))
                .addAction(R.drawable.ic_next, "Next", getPendingIntent(this, ACTION_NEXT))
                .addAction(R.drawable.ic_clear, "Clear", getPendingIntent(this, ACTION_CLEAR))
        }
        val notification = notificationBuilder.build()
        startForeground(1, notification)
    }

    //Gửi dữ liệu qua MyReceiver
    private fun getPendingIntent(context: Context, action: Int): PendingIntent? {
        val intent = Intent(this, MyReceiver::class.java)
        intent.putExtra("action_music", action)

        return PendingIntent.getBroadcast(context.applicationContext, action, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mediaPlayer != null){
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    //Gửi data đến MainActivity
    private fun sendActionToActivity(action: Int){
        val intent = Intent("send_data_to_activity")
        val bundle = Bundle()
        bundle.putParcelableArrayList("songList", ArrayList(mList))
        bundle.putInt("pos", pos)
        bundle.putBoolean("status_player", isPlaying)
        bundle.putInt("action_music", action)

        intent.putExtras(bundle)

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}