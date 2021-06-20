package kr.ac.konkuk.koogle.Activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityImageViewBinding
import java.net.URLEncoder

class ImageViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityImageViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(binding.imageView)
            .load(intent.getStringExtra("url"))
            .into(binding.imageView)

        binding.cancelImageView.setOnClickListener {
            finish()
        }
    }
}