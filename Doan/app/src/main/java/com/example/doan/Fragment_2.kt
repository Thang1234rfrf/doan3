package com.example.doan

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.media.AudioManager
import android.media.MediaPlayer
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.os.Looper
import androidx.fragment.app.FragmentManager

import android.os.Handler
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.doan.Adapter.PlayListAdapter
import com.example.doan.Data.SongData
import com.example.doan.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.util.concurrent.TimeUnit
//import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayInputStream
import java.io.FileDescriptor
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Fragment_2: Fragment() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    lateinit var productList:MutableList<SongData>
    lateinit var dbRef : DatabaseReference
    private lateinit var handler: Handler
    private lateinit var thoigianhientai: TextView
    private lateinit var tenbaiha: TextView
    private lateinit var tencasi: TextView
    private lateinit var tongthoigianphat: TextView
    private lateinit var musicToggleImageView: ImageView
    private lateinit var nextsong: ImageView
    private lateinit var previous_song: ImageView
    private lateinit var repeat_button: ImageView
    private lateinit var shuffle_button: ImageView
    private lateinit var image_song: RoundedImageView
    private  lateinit var back :ImageView

    private var isMusicPlaying = false
    private var lap = false
    private var random = false
    private var abc = false

    private var mediaPlayerPosition:Int = 0

    private var param1: String? = null
    private var param2: String? = null
    private var getRate: String? = ""
    private var dem: String = ""
    private var getDesc: String = ""
    private var getName: String = ""
    private var anh: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {



        val view = inflater.inflate(R.layout.fragment_2, container, false)

        image_song = view.findViewById<RoundedImageView>(R.id.img_albart)
        tenbaiha = view.findViewById<TextView>(R.id.song_name)
        tencasi = view.findViewById<TextView>(R.id.singer_name)
        nextsong = view.findViewById<ImageView>(R.id.next_song)
        repeat_button = view.findViewById<ImageView>(R.id.repeat_button)
        back = view.findViewById<ImageView>(R.id.back)
        shuffle_button = view.findViewById<ImageView>(R.id.shuffle_button)
        previous_song = view.findViewById<ImageView>(R.id.previous_song)
        seekBar = view.findViewById<SeekBar>(R.id.player_seekbar)
        musicToggleImageView = view.findViewById<ImageView>(R.id.player_center_icon)
        thoigianhientai = view.findViewById<TextView>(R.id.player_current_position)
        tongthoigianphat = view.findViewById<TextView>(R.id.complete_position)


        back.setOnClickListener {
            val fragment = Fragment_1()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.container, fragment)
            transaction?.commit()
        }




        var data = this.arguments
        if(data == null) {


// Sử dụng mediaPlayer ở đây

            val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            getRate = sharedPref.getString("getRate", null).toString()
            getName = sharedPref.getString("getName", null).toString()
            getDesc = sharedPref.getString("getDesc", null).toString()
            dem = sharedPref.getString("dem", null).toString()
            anh = sharedPref.getString("anh", null).toString()
           abc= true

        }else{
                 getName = data?.get("name").toString()
                getDesc = data?.get("desc").toString()
                getRate = data?.get("rate").toString()
                dem = data?.get("dem").toString()
                 anh = data?.get("anh").toString()


                Picasso.get().load(anh).into(image_song)
            }
        tencasi.text =getName
tenbaiha.text = getDesc








        productList = mutableListOf()
        dbRef = FirebaseDatabase.getInstance().reference.child("DataSong")
        val currentDate = Calendar.getInstance()
        val tenDaysAgo = Calendar.getInstance()
        tenDaysAgo.add(Calendar.DATE, -9)
        val tenDaysAgoDateString = SimpleDateFormat("dd/MM/yyyy").format(tenDaysAgo.time)
        val nono = SimpleDateFormat("dd/MM/yyyy").format(currentDate.time)

        var c: Query
        if(dem=="1") {
            c =  dbRef.orderByChild("ngaydang").startAt(tenDaysAgoDateString).endAt(nono)
        }else if(dem=="2"){
            c =  dbRef.orderByChild("category").equalTo("dongque")
            Toast.makeText(context, "$dem", Toast.LENGTH_SHORT).show()

        }else if(dem=="3"){
            c =  dbRef.orderByChild("category").equalTo("pop")
        }else if(dem=="4"){
            c =  dbRef.orderByChild("category").equalTo("rock")
        }else if(dem=="5"){
            c =  dbRef.orderByChild("category").equalTo("remix")
        }else {
            c =dbRef
        }

       c.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()

                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val dataItem = data.getValue(SongData::class.java)
                        productList.add(dataItem!!)

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        // Khởi tạo đối tượng MediaPlayer
        mediaPlayer = MediaPlayer().apply {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
            setAudioAttributes(audioAttributes)
            setDataSource("$getRate")!!
//              setDataSource("https://storage.googleapis.com/laravel-test-625e8.appspot.com/Nhac/Buong-Doi-Tay-Nhau-Ra-Son-Tung-M-TP.mp3?GoogleAccessId=firebase-adminsdk-gcomm%40laravel-test-625e8.iam.gserviceaccount.com&Expires=1715529784&Signature=DhXyAbEwqSMkR7ocgwBOGhinlX39La3yMkoJr4ODJrXPYmdCs0fBOWKN8ItYS1u6jLIFJgL0PsVWO4g4nB9Zc%2F91DkVAf3OgVO9vxYBV6jmLPCVFgFf%2BKHRwJeYqXYNt%2BPy%2B3bez7LDinGDbJC3e4Ui3AaBtMsfScPKGHHhdAYj2CC4bzE7fEMQv2r2PVvk2aNih%2Bn9Uw%2BN%2FNqiu1t2LeCSxKoKy9%2FtopAuyA%2Bp8nZmg8ARQSeoPhOkKAHg6DUL52d%2Flk6WjHlZaXPzjdBR7im0ueA5KJpykqYEWVyUebF7aHEL33%2FkRJrkEvtGhGPijQXpIkh8yWa2r5JoGgsiS6Q%3D%3D")

                prepareAsync()}





        mediaPlayer.setOnPreparedListener {
            // Lấy thông tin về thời gian của bài hát
            val totalTimeInMillis = mediaPlayer.duration

            // Thiết lập đối tượng TextView để hiển thị thời gian hiện tại


            // Thiết lập handler để cập nhật thời gian hiện tại
            handler = Handler()
            activity?.runOnUiThread(object : Runnable {
                override fun run() {
                    if (mediaPlayer != null) {
                        val currentPosition = mediaPlayer.currentPosition
                        val currentTimeFormatted = String.format(
                            "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(currentPosition.toLong()),
                            TimeUnit.MILLISECONDS.toSeconds(currentPosition.toLong()) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(currentPosition.toLong())
                            )
                        )
                        thoigianhientai.text = currentTimeFormatted
                        val durationInMillis = mediaPlayer.duration
                        val durationFormatted = String.format(
                            "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(durationInMillis.toLong()),
                            TimeUnit.MILLISECONDS.toSeconds(durationInMillis.toLong()) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(durationInMillis.toLong())
                            )
                        )
                        // display the duration somewhere in your UI
                        tongthoigianphat.text = durationFormatted
                    }
                    handler.postDelayed(this, 1000)
                }
            })

            // listen for when the MediaPlayer is prepared


            // Thiết lập đối tượng SeekBar để thể hiện thời gian phát nhạc
            seekBar.max = mediaPlayer.duration
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })

            mediaPlayer.start()
            musicToggleImageView.setImageResource(R.drawable.dung)

            musicToggleImageView.setOnClickListener {
                if (isMusicPlaying) {
                    mediaPlayer.pause()
                    musicToggleImageView.setImageResource(R.drawable.play)
                } else {
                    mediaPlayer.start()
                    musicToggleImageView.setImageResource(R.drawable.dung)
                }
                isMusicPlaying = !isMusicPlaying
            }
            // Phát nhạc


        }





        nextsong.setOnClickListener {
            // Tìm vị trí của bài hát hiện tại trong productList
            val currentSongIndex = productList.indexOfFirst { it.song_name == getDesc }
            if (currentSongIndex < 0) {
                // Không tìm thấy bài hát hiện tại trong productList
                return@setOnClickListener
            }

            if (currentSongIndex >= productList.lastIndex) {
                // Bài hát hiện tại là bài hát cuối cùng trong productList, không chuyển bài hát
                return@setOnClickListener
            }

            // Lấy bài hát tiếp theo trong productList
            val nextSong = productList[currentSongIndex + 1]
            val nextSongTitle = nextSong.song_name ?: ""
            val nextSongUrl = nextSong.urlFile ?: ""
            val nextSongImg = nextSong.urlImg ?: ""
            val nextSong_Singer_Name = nextSong.singer_name ?: ""
            mediaPlayer.pause()
            mediaPlayer.setVolume(1.0f, 1.0f)

            // Cập nhật media player và thông tin bài hát mới
            mediaPlayer.apply {
                reset()
                setDataSource(nextSongUrl)
                prepare()
                start()
                seekBar.progress = 0

            }
            getDesc = nextSongTitle
            getRate=nextSongUrl
            tenbaiha.setText(nextSongTitle)
            tencasi.setText(nextSong_Singer_Name)
            Picasso.get().load(nextSongImg).into(image_song)
        }

        previous_song.setOnClickListener {
            // Tìm vị trí của bài hát hiện tại trong productList
            val currentSongIndex = productList.indexOfFirst { it.song_name == getDesc }

            if (currentSongIndex < 0) {
                // Không tìm thấy bài hát hiện tại trong productList
                return@setOnClickListener
            }

            if (currentSongIndex == 0) {
                // Bài hát hiện tại là bài hát cuối cùng trong productList, không chuyển bài hát
                return@setOnClickListener
            }

            // Lấy bài hát tiếp theo trong productList
            val lastSong = productList[currentSongIndex - 1]
            val nextSongTitle = lastSong.song_name ?: ""
            val nextSongUrl = lastSong.urlFile ?: ""
            val nextSongImg = lastSong.urlImg ?: ""
            val nextSong_Singer_Name = lastSong.singer_name ?: ""

            // Cập nhật media player và thông tin bài hát mới
            mediaPlayer.apply {
                reset()
                setDataSource(nextSongUrl)
                prepare()
                start()
                seekBar.progress = 0

            }
            getDesc = nextSongTitle
            getRate = nextSongUrl
            tenbaiha.setText(nextSongTitle)
            tencasi.setText(nextSong_Singer_Name)
            Picasso.get().load(nextSongImg).into(image_song)

        }

        repeat_button.setOnClickListener {
            if (lap == false) {
                random = false
                lap = true
                shuffle_button.setBackgroundColor(Color.TRANSPARENT)

                repeat_button.setBackgroundColor(Color.parseColor("#FF0000"))
                mediaPlayer.setOnCompletionListener {
                    mediaPlayer.seekTo(0)
                    mediaPlayer.start()
                    seekBar.progress = 0
                }

            } else {
                lap = false
                repeat_button.setBackgroundColor(Color.TRANSPARENT)

            }


        }



        shuffle_button.setOnClickListener {
            if (random == false) {
                lap = false
                random = true
                repeat_button.setBackgroundColor(Color.TRANSPARENT)
                shuffle_button.setBackgroundColor(Color.parseColor("#FF0000"))
                mediaPlayer.setOnCompletionListener {

                    val randomInRange = (1..productList.lastIndex).random()

                    // Lấy bài hát tiếp theo trong productList
                    val ngaunhien = productList[randomInRange]
                    val nextSongTitle = ngaunhien.song_name ?: ""
                    val nextSongImg = ngaunhien.urlImg ?: ""
                    val nextSong_Singer_Name = ngaunhien.singer_name ?: ""
                    val nextSongUrl = ngaunhien.urlFile ?: ""

                    // Cập nhật media player và thông tin bài hát mới
                    mediaPlayer.apply {
                        reset()
                        setDataSource(nextSongUrl)
                        prepare()
                        start()
                        seekBar.progress = 0

                    }
                    getRate= nextSongUrl
                    getDesc = nextSongTitle
                    tenbaiha.setText(nextSongTitle)
                    tencasi.setText(nextSong_Singer_Name)
                    Picasso.get().load(nextSongImg).into(image_song)


                }
            } else {
                random = false
                shuffle_button.setBackgroundColor(Color.TRANSPARENT)

            }
        }

        val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.remove("getDesc")
        editor.remove("anh")
        editor.remove("getRate")
        editor.remove("getName")
        editor.remove("dem")
        editor.apply()


        return view
    }






    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
        Toast.makeText(context, "adsd", Toast.LENGTH_SHORT).show()
    }


    override fun onResume() {
        super.onResume()
        val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("currentPosition", mediaPlayer.currentPosition)

        editor.putString("getDesc", getDesc)
        editor.putString("anh", anh)
        editor.putString("getRate", getRate)
        editor.putString("getName", getName)
        editor.putString("dem", dem)
        editor.apply()
        if(abc ==true) {
            val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

            tenbaiha.setText(getDesc.toString())
            val currentPosition = sharedPref.getInt("currentPosition", 0) ?: 0
            mediaPlayer.seekTo(currentPosition)
            seekBar.progress = currentPosition


        }
    }



//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString("musicLink", getRate )
//        outState.putString("nhac", getDesc )
//        // Lưu các dữ liệu khác vào Bundle outState nếu cần thiết
//    }
//
//        override fun onActivityCreated(savedInstanceState: Bundle?) {
//            super.onActivityCreated(savedInstanceState)
//            if (savedInstanceState != null) {
//                getRate = savedInstanceState.getString("musicLink", "")
//                // Restore other data if needed
//            }
//            // Start playing music with the restored musicLink
//        }




}
