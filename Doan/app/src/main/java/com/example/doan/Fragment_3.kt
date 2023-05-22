package com.example.doan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doan.Adapter.PlayListAdapter
import com.example.doan.Adapter.RvAdapter
import com.example.doan.Adapter.SongAdapter
import com.example.doan.Data.PlayListData

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.doan.Data.SongData
//import com.bumptech.glide.Glide
import com.example.doan.R
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class Fragment_3 : Fragment() {
    private lateinit var recyclerView: RecyclerView
    lateinit var productList:MutableList<SongData>
    lateinit var dbRef : DatabaseReference
    private var param1: String? = null
    private var dem  = ""
    private var param2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1= it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_3, container, false)

        recyclerView = view.findViewById<RecyclerView>(R.id.rview1)

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
//        val items2 = mutableListOf<PlayListData>()
//        items2.add(PlayListData(R.drawable.chillhop_vol2_nl, "Anh Là Dân Chơi", "KHOA, Hải Ngân"))
//        items2.add(PlayListData(R.drawable.chillhop_vol2_nl, "Không Yêu Trả Dép Tôi Về ", "HuyR"))
//        items2.add(
//            PlayListData(
//                R.drawable.chillhop_vol2_nl,
//                "Ghệ Iu Dấu Của Em Ơi (Sped Up)",
//                "tlinh, 2pillz, WOKEUP"
//            )
//        )
//        items2.add(PlayListData(R.drawable.chillhop_vol2_nl, "Lệ Cay", "Du Thiên"))
//        items2.add(PlayListData(R.drawable.chillhop_vol2_nl, "Cầu Vồng Sau Mưa", "Trường An"))
//        items2.add(PlayListData(R.drawable.chillhop_vol2_nl, "Anh Là Dân Chơi", "KHOA, Hải Ngân"))
//        items2.add(PlayListData(R.drawable.chillhop_vol2_nl, "Không Yêu Trả Dép Tôi Về ", "HuyR"))
//        items2.add(
//            PlayListData(
//                R.drawable.chillhop_vol2_nl,
//                "Ghệ Iu Dấu Của Em Ơi (Sped Up)",
//                "tlinh, 2pillz, WOKEUP"
//            )
//        )
//        items2.add(PlayListData(R.drawable.chillhop_vol2_nl, "Lệ Cay", "Du Thiên"))
//        items2.add(PlayListData(R.drawable.chillhop_vol2_nl, "Cầu Vồng Sau Mưa", "Trường An"))
//
//
//
//
////        val adapter = PlayListAdapter(items2)
////        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(
//            context,
//            LinearLayoutManager.VERTICAL,
//            false
//        )

        adapter()

        return view
    }
    private fun adapter() {
        // set layout for adapter
//        recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
//        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.setHasFixedSize(true)

        productList =  mutableListOf()



        // get data person
        information()

    }




    private fun information() {
        val data = this.arguments
        dem = data?.get("dem").toString()
        if (data== null) {

        }


        // get data
        dbRef = FirebaseDatabase.getInstance().reference.child("DataSong")

        val currentDate = Calendar.getInstance()
        val tenDaysAgo = Calendar.getInstance()
        tenDaysAgo.add(Calendar.DATE, -9)
        val tenDaysAgoDateString = SimpleDateFormat("dd/MM/yyyy").format(tenDaysAgo.time)
        val nono = SimpleDateFormat("dd/MM/yyyy").format(currentDate.time)



        var c: Query

        c =dbRef
        if(dem=="1") {
          c =  dbRef.orderByChild("ngaydang").startAt(tenDaysAgoDateString).endAt(nono)
        }else if(dem=="2"){
            c =  dbRef.orderByChild("category").equalTo("dongque")
        }else if(dem=="3"){
            c =  dbRef.orderByChild("category").equalTo("pop")
        }else if(dem=="4"){
            c =  dbRef.orderByChild("category").equalTo("rock")
        }else if(dem=="5"){
            c =  dbRef.orderByChild("category").equalTo("remix")
        }

       c.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()

//                var cal= Calendar.getInstance()
//                var date=cal.time
//                var sdf= SimpleDateFormat("dd/MM/yyyy").format(date)
//                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//                val tenDaysAgoString = dateFormat.parse(sdf)
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val dataItem = data.getValue(SongData::class.java)
                        productList.add(dataItem!!)

                    }
                    // set data
                    val adapter = SongAdapter(productList)
                    recyclerView.adapter = adapter

                    // item click listener
//                    recyclerView.adapter = PlayListAdapter(productList, object : InterfaceItemClick{
//                        override fun OnClickItem(position: Int) {
                    adapter.setOnItemClickListener(object :SongAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val  fragment2= Fragment_2()

                            val bundle = Bundle().apply {
                                putString("name", productList[position].singer_name)
                                putString("desc", productList[position].song_name)
                                putString("anh", productList[position].urlImg)
                                putString("rate", productList[position].urlFile)

                                putString("dem", dem.toString())
                            }


                            fragment2.arguments = bundle
                            val transaction = requireActivity().supportFragmentManager.beginTransaction()

                            transaction.replace(R.id.f3, fragment2)
                            transaction.addToBackStack(null)
                            transaction.commit()
                        }
//                        }
                    })

                    // display data
//                    binding.rvPersonData.visibility = View.VISIBLE
//                    binding.txtTitle.visibility = View.VISIBLE
//                    binding.txtLoad.visibility = View.GONE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })}

    override fun onPause() {
        super.onPause()

        val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("dem", dem)
        editor.apply()
    }
}