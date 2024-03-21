package com.example.musicservice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RvAdapterMusic(val list: List<Song>, val onMusicClick: RvInterface): RecyclerView.Adapter<RvAdapterMusic.MusicViewHolder>() {

    inner class MusicViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layoutitem, parent, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<TextView>(R.id.txtsinger).text = list[position].single
            findViewById<TextView>(R.id.txttitle).text = list[position].title

            holder.itemView.setOnClickListener {
                onMusicClick.onClickMusic(position)
            }
        }
//        Glide.with(holder.itemView.context)
//            .load(list[position].img)
//            .into(holder.itemView.findViewById(R.id.imgsong))
    }

    override fun getItemCount(): Int {
        return list.size
    }
}