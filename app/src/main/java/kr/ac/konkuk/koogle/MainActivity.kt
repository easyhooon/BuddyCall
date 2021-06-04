package kr.ac.konkuk.koogle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.ac.konkuk.koogle.Fragment.CommunityFragment
import kr.ac.konkuk.koogle.Fragment.GroupFragment
import kr.ac.konkuk.koogle.Fragment.CardFragment

class MainActivity : AppCompatActivity() {

    private var backBtnTime: Long = 0 // 뒤로가기 두번 눌러 종료 용 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tinderFragment = CardFragment()
        val communityFragment = CommunityFragment()
        val groupFragment = GroupFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        replaceFragment(tinderFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_short -> replaceFragment(tinderFragment)
                R.id.nav_search -> replaceFragment(communityFragment)
                R.id.nav_group_chat -> replaceFragment(groupFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        //activity에 붙어있는 fragment를 관리
        //transaction -> 작업을 시작한다고 알림 (~commit 까지는 이 작업만 하라고 명령)
        //commit <- transaction의 끝을 알림
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainer, fragment)
                commit()
            }
    }

    //뒤로가기 두번 눌러 종료
    override fun onBackPressed() {
        val curTime = System.currentTimeMillis()
        val gapTime: Long = curTime - backBtnTime

        //뒤로가기를 한번 누른 후에 2초가 지나기전에 한번 더 눌렀을 경우 if문 진입
        if (gapTime in 0..2000) {
            super.onBackPressed()
        } else {
            backBtnTime = curTime
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }
}