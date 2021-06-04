package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import kr.ac.konkuk.koogle.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            //1초 뒤에 일어날 액션을 구현
            val intent = Intent(this@SplashActivity, LogInActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티를 파괴 (다음에 쓰지 않기 때문에)
        }, 1000)
    }
}