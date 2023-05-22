package com.example.doan.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doan.Data.Search_result_Data
import com.example.doan.R
import java.util.*

class Search_Adapter(private val dataList: List<Search_result_Data>) :
    RecyclerView.Adapter<Search_Adapter.PlayListViewHolder>(), Filterable {

    private var filteredDataList: List<Search_result_Data> = dataList

    inner class PlayListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtBaiHat: TextView = itemView.findViewById(R.id.txtTenBaiHat)
        val txtCaSi: TextView = itemView.findViewById(R.id.txtCaSi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_search, parent, false)
        return PlayListViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        val data = filteredDataList[position]
        holder.itemView.apply {
            holder.txtBaiHat.text = data.song_name
            holder.txtCaSi.text = data.singer_name
        }
    }

    override fun getItemCount(): Int {
        return filteredDataList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Search_result_Data>()
                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(dataList)
                } else {
                    val filterPattern = constraint.toString().trim().toLowerCase(Locale.ROOT)
                    for (data in dataList) {
                        if (data.song_name.toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            data.singer_name.toLowerCase(Locale.ROOT).contains(filterPattern)
                        ) {
                            filteredList.add(data)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredDataList = results?.values as? List<Search_result_Data> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}
