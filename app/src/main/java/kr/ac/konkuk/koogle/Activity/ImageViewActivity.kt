package kr.ac.konkuk.koogle.Activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityImageViewBinding

class ImageViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityImageViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri = intent.getSerializableExtra("uri") as Uri
        binding.imageView.setImageURI(uri)
    }
}