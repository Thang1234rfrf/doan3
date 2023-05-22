package com.example.doan

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.doan.Adapter.PlayListAdapter
import com.example.doan.Adapter.Search_Adapter
import com.example.doan.Adapter.SongAdapter
import com.example.doan.Data.*
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.NonDisposableHandle.parent


class Fragment_1 : Fragment() {

    lateinit var dbRef : DatabaseReference

    lateinit var productList:MutableList<SongData>
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView3: RecyclerView
    private lateinit var nhacmoi: ImageView
    private lateinit var theloai: ImageView
    private lateinit var imageSlider: ImageSlider
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var rcv: RecyclerView

    private var items2 = mutableListOf<SongData>()
    val items = mutableListOf<PlayListData>()
    val searchDATA = mutableListOf<Search_result_Data>()


    private lateinit var searchResultLayout: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_1, container, false)




        autoCompleteTextView = view.findViewById(R.id.search_bar)
        recyclerView = view.findViewById<RecyclerView>(R.id.rview1)
        recyclerView3 = view.findViewById<RecyclerView>(R.id.rview3)
        nhacmoi = view.findViewById<ImageView>(R.id.nhacmoi)
        theloai = view.findViewById<ImageView>(R.id.theloai)
        productList = mutableListOf()






        recyclerView.layoutManager = LinearLayoutManager(context)

        theloai.setOnClickListener {
            openUpdateDialog()
        }

        nhacmoi.setOnClickListener {
            val fragment3 = Fragment_3()
            val dem = "1"
            val bundle = Bundle().apply {
                putString("dem", dem.toString())
            }
            fragment3.arguments = bundle

            // Thực hiện thay thế Fragment hiện tại bằng Fragment đích
            fragment3.arguments = bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            transaction.replace(R.id.f1, fragment3)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        // get data
        val database = FirebaseDatabase.getInstance()
        val databaseReference = database.reference.child("DataSong")


        databaseReference.limitToFirst(20).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (songSnapshot in dataSnapshot.children) {
                    val singer_name =
                        songSnapshot.child("song_name").getValue(String::class.java).toString()
                    val casi =
                        songSnapshot.child("singer_name").getValue(String::class.java).toString()
                    val img = songSnapshot.child("urlImg").getValue(String::class.java).toString()
                    val video = songSnapshot.child("urlFile").getValue(String::class.java).toString()

                    items.add(PlayListData(img, singer_name, casi,video))
                    val adapter = PlayListAdapter(items)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(
                        context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter.setOnItemClickListener(object :PlayListAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val  fragment2= Fragment_2()

                            val bundle = Bundle().apply {
                                putString("name", items[position].tencasi)
                                putString("desc", items[position].tenbaihat)
                                putString("anh", items[position].image)
                                putString("rate", items[position].urlFile)

                            }


                            fragment2.arguments = bundle
                            val transaction = requireActivity().supportFragmentManager.beginTransaction()

                            transaction.replace(R.id.container, fragment2)
                            transaction.addToBackStack(null)
                            transaction.commit()
                        }
//                        }
                    })
                }

                // Hiển thị dữ liệu
                // ...

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })







        recyclerView3.layoutManager = LinearLayoutManager(context)

        // get data


        databaseReference.limitToFirst(20).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (songSnapshot in dataSnapshot.children) {
                    val tenbaihat =
                        songSnapshot.child("song_name").getValue(String::class.java).toString()
                    val tencasi =
                        songSnapshot.child("singer_name").getValue(String::class.java).toString()
                    val urlImg =
                        songSnapshot.child("urlImg").getValue(String::class.java).toString()
                    items2.add(SongData(urlImg, tenbaihat, tencasi))

                    val adapter2 = SongAdapter(items2)
                    recyclerView3.adapter = adapter2
                    recyclerView3.layoutManager = LinearLayoutManager(
                        context,
                        LinearLayoutManager.VERTICAL,
                        false
                    )

                    adapter2.setOnItemClickListener(object : SongAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {

                        }
//                        }
                    })


                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })







        imageSlider = view.findViewById<ImageSlider>(R.id.imageSlider)

        val imageList = ArrayList<SlideModel>()

        imageList.add(
            SlideModel(
                "https://i.pinimg.com/564x/de/bb/1a/debb1a8f2ea4cd66e3593b0fe5230adc.jpg",
                "1"
            )
        )
        imageList.add(
            SlideModel(
                "https://i.pinimg.com/564x/72/ce/42/72ce429a45ba61ba2e4c660746638de1.jpg",
                "2"
            )
        )
        imageList.add(
            SlideModel(
                "https://i.pinimg.com/564x/07/21/f4/0721f4854764d5e39530456c97ca7739.jpg",
                "3"
            )
        )


        imageSlider.setImageList(imageList, ScaleTypes.FIT)




        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val searchDATA = mutableListOf<String>()
                for (songSnapshot in dataSnapshot.children) {
                    val song_name =
                        songSnapshot.child("song_name").getValue(String::class.java).toString()
                    val singer_name =
                        songSnapshot.child("singer_name").getValue(String::class.java).toString()
                    val item = "$song_name - $singer_name"
                    searchDATA.add(item)
                }

                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    searchDATA
                )
                autoCompleteTextView.setAdapter(adapter)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })


        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, searchDATA)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position) as String
                val ten = selectedItem.substringBefore(" -")
                if (ten != null) {
                    val database = FirebaseDatabase.getInstance()
                    val db =
                        database.getReference("DataSong").orderByChild("song_name").equalTo("$ten")
                    db.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val dem = "6"
                            for (songSnapshot in dataSnapshot.children) {
                                val getDesc =
                                    songSnapshot.child("song_name").getValue(String::class.java)
                                        .toString()


                                val tencasi =
                                    songSnapshot.child("singer_name").getValue(String::class.java)
                                        .toString()
                                var anh =
                                    songSnapshot.child("urlImg").getValue(String::class.java)
                                        .toString()


                                var abc = songSnapshot.child("urlFile").getValue(String::class.java)
                                    .toString()
                                val fragment2 = Fragment_2()

                                val bundle = Bundle().apply {
                                    putString("name", "")
                                    putString("desc", tencasi)
                                    putString("anh", anh)
                                    putString("rate", abc)
                                    putString("dem", dem.toString())
                                }


                                fragment2.arguments = bundle
                                val transaction =
                                    requireActivity().supportFragmentManager.beginTransaction()

                                transaction.replace(R.id.container, fragment2)
                                transaction.addToBackStack(null)
                                transaction.commit()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }


                    })


                }


            }








        return view

    }

    private fun openUpdateDialog() {
        val mDialog = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.theloai, null)
        mDialog.setView(mDialogView)
        //update thông tin vào dialog
        val spinner = mDialogView.findViewById<Spinner>(R.id.spinner_options)
//        val pop = mDialogView.findViewById<Button>(R.id.pop)
//        val rock = mDialogView.findViewById<Button>(R.id.rock)
//        val remix = mDialogView.findViewById<Button>(R.id.remix)
        rcv =  mDialogView.findViewById<RecyclerView>(R.id.abc)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                dbRef = FirebaseDatabase.getInstance().reference.child("DataSong")




                val c: Query = when (position) {
                    4 -> dbRef.orderByChild("category").equalTo("dongque")
                    0 -> dbRef.orderByChild("category").equalTo("pop")
                    3 -> dbRef.orderByChild("category").equalTo("rock")
                    1 -> dbRef.orderByChild("category").equalTo("nhactre")
                    2 -> dbRef.orderByChild("category").equalTo("remix")
                    5 -> dbRef.orderByChild("category").equalTo("Acoustic")
                    6 -> dbRef.orderByChild("category").equalTo("khac")
                    else -> dbRef  // Default query
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
                            rcv.adapter = adapter

                            rcv.layoutManager = LinearLayoutManager(
                                context,
                                LinearLayoutManager.VERTICAL,
                                false
                            )


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

                                    }


                                    fragment2.arguments = bundle
                                    val transaction = requireActivity().supportFragmentManager.beginTransaction()

                                    transaction.replace(R.id.container, fragment2)
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

                })

                // Xử lý giá trị được chọn
                Toast.makeText(context, "Bạn đã chọn: $position", Toast.LENGTH_SHORT).show()
            }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Xử lý khi không có mục nào được chọn
                }
            }

        mDialog.setTitle("Updating  Record")
        val alertDialog = mDialog.create()
        alertDialog.show()

//        dongque.setOnClickListener{
//            val fragment3 = Fragment_3()
//            val dem = 2
//            val bundle = Bundle().apply {
//                putString("dem", dem.toString())
//            }
//            fragment3.arguments = bundle
//
//            // Thực hiện thay thế Fragment hiện tại bằng Fragment đích
//            fragment3.arguments = bundle
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//
//            transaction.replace(R.id.f1, fragment3)
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }
//
//        pop.setOnClickListener {
//            val fragment3 = Fragment_3()
//            val dem = 3
//            val bundle = Bundle().apply {
//                putString("dem", dem.toString())
//            }
//            fragment3.arguments = bundle
//
//            // Thực hiện thay thế Fragment hiện tại bằng Fragment đích
//            fragment3.arguments = bundle
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//
//            transaction.replace(R.id.f1, fragment3)
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }
//
//        rock.setOnClickListener {
//            val fragment3 = Fragment_3()
//            val dem = 4
//            val bundle = Bundle().apply {
//                putString("dem", dem.toString())
//            }
//            fragment3.arguments = bundle
//
//            // Thực hiện thay thế Fragment hiện tại bằng Fragment đích
//            fragment3.arguments = bundle
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//
//            transaction.replace(R.id.f1, fragment3)
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }
//        remix.setOnClickListener {
//            val fragment3 = Fragment_3()
//            val dem = 5
//            val bundle = Bundle().apply {
//                putString("dem", dem.toString())
//            }
//            fragment3.arguments = bundle
//
//            // Thực hiện thay thế Fragment hiện tại bằng Fragment đích
//            fragment3.arguments = bundle
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//
//            transaction.replace(R.id.f1, fragment3)
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }




    }
}
