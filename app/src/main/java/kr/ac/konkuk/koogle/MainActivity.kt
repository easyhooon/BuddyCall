package kr.ac.konkuk.koogle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.ac.konkuk.koogle.Fragment.CommunityFragment
import kr.ac.konkuk.koogle.Fragment.GroupFragment
import kr.ac.konkuk.koogle.Fragment.TinderFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tinderFragment = TinderFragment()
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
}