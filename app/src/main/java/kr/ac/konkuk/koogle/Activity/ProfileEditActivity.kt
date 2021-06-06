package kr.ac.konkuk.koogle.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityEditProfileBinding

class ProfileEditActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}