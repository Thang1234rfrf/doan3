package com.example.doan


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    lateinit var bottomNav : BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        loadFragment(Fragment_1())
        bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
             when (it.itemId) {
                 R.id.mot -> loadFragment(Fragment_1())
                 R.id.hai-> loadFragment(Fragment_2())
                 R.id.ba -> loadFragment(Fragment_3())
                 R.id.bon -> {
                     val intent = Intent(this, DangNhapActivity::class.java)
                     startActivity(intent)
                 }

             }
            true
        }

    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }


}