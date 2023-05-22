package com.example.doan.Adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doan.Data.SongData
import com.example.doan.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.NonDisposableHandle.parent

class SongAdapter (private val ds:List<SongData>): RecyclerView.Adapter<SongAdapter.SongViewHolder>(){
    private lateinit var listener: onItemClickListener

    interface  onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(clickListener: onItemClickListener){
        listener = clickListener
    }
    //    class viewholder
     class SongViewHolder(itemView: View,clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val txtBaiHat = itemView.findViewById<TextView>(R.id.txtTenBaiHat)
        val txtCaSi = itemView.findViewById<TextView>(R.id.txtCaSi)
        val imageView = itemView.findViewById<ImageView>(R.id.imgView)

        init {
            itemView.setOnClickListener {
                clickListener?.onItemClick(adapterPosition)


            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_song,parent,false)

        return SongViewHolder(view, listener)
    }


    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {

         holder.itemView.apply {
            holder.txtBaiHat.text = ds[position].song_name
            holder.txtCaSi.text = ds[position].singer_name
             Picasso.get()
                 .load(ds[position].urlImg)
                 .fit()
                 .into(holder.imageView)

        }
    }
    override fun getItemCount(): Int {
        return ds.size
    }
}

