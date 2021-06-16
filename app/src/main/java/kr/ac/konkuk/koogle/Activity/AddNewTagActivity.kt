package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.ac.konkuk.koogle.databinding.ActivityAddTagBinding

class AddNewTagActivity:AppCompatActivity() {

    //Firebase Auth를 initialize 해주는 코드
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    lateinit var binding: ActivityAddTagBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTagBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init() {
        initButton()
    }

    private fun initButton() {
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}