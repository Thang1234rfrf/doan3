package com.example.doan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.doan.Data.dangnhap
import com.example.doan.databinding.ActivityDangNhapBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class DangNhapActivity : AppCompatActivity() {
    // Khai báo biến để lưu trữ thông tin đăng nhập của người dùng
    private lateinit var dbdangnhap: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var txtUserName: EditText
    lateinit var binding: ActivityDangNhapBinding
    private lateinit var currentUser: FirebaseUser


    private lateinit var txtUserEmail: EditText

    // Hằng số RC_SIGN_IN được sử dụng để xác định mã yêu cầu khi gửi yêu cầu đăng nhập
    private val RC_SIGN_IN = 123
    private val TAG = "MainActivity"
    private val ten = "MainActivity"
    private val emaill = "MainActivity"
    private lateinit var firebaseAuth: FirebaseAuth

    // Phương thức onCreate() được gọi khi activity được tạo ra

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dang_nhap)
        val btnLogin = findViewById<Button>(R.id.btnLogIn)
        val btnLogout = findViewById<Button>(R.id.btnLogOut)




        dbdangnhap = FirebaseDatabase.getInstance().getReference("dangnhap")
        // Khởi tạo GoogleSignInOptions để cấu hình yêu cầu đăng nhập của người dùng

        // Khởi tạo GoogleSignInClient để đăng nhập với tài khoản Google

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, options)
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser


        // Bắt đầu quá trình đăng nhập khi người dùng nhấn vào nút đăng nhập

        btnLogin.setOnClickListener {
            // window infor login
            googleSignInClient.signOut().addOnCompleteListener {
                val intent = googleSignInClient.signInIntent
                startActivityForResult(intent, RC_SIGN_IN)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    // Xử lý kết quả đăng nhập
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken)


        } catch (e: ApiException) {
            // Xử lý lỗi
            Log.e(TAG,  e.toString())
        }
    }

    // Đăng nhập vào Firebase với token từ Google
    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Đăng nhập thành công
                    val user = firebaseAuth.currentUser
                    Log.d(TAG, "Đăng nhập thành công: ${user?.displayName}")
                    val txtName = findViewById<TextView>(R.id.txtName)
                    val image = findViewById<CircleImageView>(R.id.profile_image)
                    val profileImage = user?.photoUrl.toString()
                    txtName.text = "${user?.displayName}"


                    Log.e(TAG, "Đăng nhập thất bại: ${user?.displayName}")

                    Picasso.get().load(profileImage).into(image)
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()

                    if (user?.displayName!!.isNotEmpty()){
                        val btnLogout = findViewById<Button>(R.id.btnLogOut)
                        val btnLogin = findViewById<Button>(R.id.btnLogIn)


                        btnLogin.visibility = View.GONE
                        btnLogout.visibility = View.VISIBLE
                        btnLogout.setOnClickListener {
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("Đăng Xuất")
                            builder.setMessage("Bạn có chắc chắn muốn đăng xuất?")
                            builder.setPositiveButton("Đồng ý") { dialog, which ->
                                FirebaseAuth.getInstance().signOut()
                                txtName.text = "Bạn Chưa Đăng Nhập"
                                image.setImageResource(R.drawable.google)
                                btnLogin.visibility = View.VISIBLE
                                btnLogout.visibility = View.GONE
                                Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
                            }
                            builder.setNegativeButton("Hủy") { dialog, which ->
                                // Hủy bỏ hoặc không làm gì khi người dùng hủy thông báo
                            }
                            val dialog = builder.create()
                            dialog.show()
                        }

                    }

                } else {
                    // Đăng nhập thất bại
                    Log.e(TAG, "Đăng nhập thất bại: ${task.exception?.message}")
                }
            })
    }
}



