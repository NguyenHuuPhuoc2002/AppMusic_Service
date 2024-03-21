package com.example.musicservice

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private lateinit var layoutBottom: RelativeLayout
    private lateinit var imgSong: ImageView
    private lateinit var imgPlayOrPause: ImageView
    private lateinit var imgClear: ImageView
    private lateinit var listSong: ArrayList<Song>
    private lateinit var txtTitleSong: TextView
    private lateinit var imgNextSong: ImageView
    private lateinit var imgPreSong: ImageView
    private lateinit var txtSingleSong: TextView
    private lateinit var mSong: ArrayList<Song>
    private var pos by Delegates.notNull<Int>()
    private var isPlaying by Delegates.notNull<Boolean>()

    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle = intent?.extras
            if(bundle == null){
                return
            }
            mSong = bundle.getParcelableArrayList<Song>("songList") as ArrayList<Song>
            pos = bundle.getInt("pos", -1)
            isPlaying = bundle.getBoolean("status_player")
            val actionMusic: Int = bundle.getInt("action_music")

            if(isPlaying && (actionMusic == 5 || actionMusic == 6)){
                imgPlayOrPause.setImageResource(R.drawable.ic_pause)
            }

            handleLayoutMusic(actionMusic)
            txtTitleSong.text = listSong[pos].title
            txtSingleSong.text = listSong[pos].single

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listSong = arrayListOf()
        mSong = arrayListOf()

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,  IntentFilter("send_data_to_activity"))
        addSong()

        imgNextSong = findViewById(R.id.img_next)
        imgPreSong = findViewById(R.id.img_pre)
        layoutBottom = findViewById(R.id.layout_bottom)
        imgSong = findViewById(R.id.img_song)
        imgPlayOrPause = findViewById(R.id.img_play_or_pause)
        imgClear = findViewById(R.id.img_clear)
        txtTitleSong = findViewById(R.id.txt_title_song)
        txtSingleSong = findViewById(R.id.txt_single_song)

        setAdapter()
    }

    private fun handleLayoutMusic(actionMusic: Int) {
        when(actionMusic){
            MyService.ACTION_START -> {
                layoutBottom.visibility = View.VISIBLE
                showInforSong()
                setStatusButtonPlayOrPause()
            }
            MyService.ACTION_PAUSE -> {
                setStatusButtonPlayOrPause()
            }
            MyService.ACTION_RESUME -> {
                setStatusButtonPlayOrPause()
            }
            MyService.ACTION_CLEAR -> {
                layoutBottom.visibility = View.GONE
            }
        }

    }

    private fun setStatusButtonPlayOrPause() {
        if(isPlaying){
            imgPlayOrPause.setImageResource(R.drawable.ic_pause)
        }else{
            imgPlayOrPause.setImageResource(R.drawable.ic_play)
        }
    }

    private fun showInforSong() {
        if(mSong == null){
            return
        }
        imgSong.setImageResource(mSong[pos].image)
        txtTitleSong.text = mSong[pos].title
        txtSingleSong.text = mSong[pos].single

        imgPlayOrPause.setOnClickListener {
            if(isPlaying){
                sendActionToService(MyService.ACTION_PAUSE)
            }else{
                sendActionToService(MyService.ACTION_RESUME)
            }
        }
        imgNextSong.setOnClickListener {
            if(isPlaying){
                sendActionToService(MyService.ACTION_NEXT)
            }else{
                imgPlayOrPause.setImageResource(R.drawable.ic_pause)
                sendActionToService(MyService.ACTION_NEXT)
            }
        }

        imgPreSong.setOnClickListener {
            if(isPlaying){
                sendActionToService(MyService.ACTION_PREVIOUS)
            }else{
                imgPlayOrPause.setImageResource(R.drawable.ic_pause)
                sendActionToService(MyService.ACTION_PREVIOUS)
            }
        }

        imgClear.setOnClickListener {
            sendActionToService(MyService.ACTION_CLEAR)
        }
    }

    private fun sendActionToService(action: Int){
        val intent = Intent(this, MyService::class.java)
        intent.putExtra("action_music_service", action)

        startService(intent)
    }
    @SuppressLint("CutPasteId")
    private fun setAdapter() {
        val adapter = RvAdapterMusic(listSong, object : RvInterface {
            override fun onClickMusic(pos: Int) {
                val intent = Intent(this@MainActivity, MyService::class.java)
                intent.putParcelableArrayListExtra("songList", ArrayList(listSong))
                intent.putExtra("pos", pos)
                startService(intent)

            }
        })

        findViewById<RecyclerView>(R.id.rv_song).adapter = adapter
        findViewById<RecyclerView>(R.id.rv_song).layoutManager =
            GridLayoutManager(this@MainActivity, 1, GridLayoutManager.VERTICAL, false)
    }
    private fun addSong(){
        listSong.add(Song("Em là hạnh phúc trong anh", "Hồ Quang Hiếu ",R.drawable.ic_music ,R.raw.emlahanhphuctronganh_hqh))
        listSong.add(Song("Cho tôi lang thang", "Đen Vâu",R.drawable.ic_music , R.raw.chotoilangthang_denvau))

    }
}