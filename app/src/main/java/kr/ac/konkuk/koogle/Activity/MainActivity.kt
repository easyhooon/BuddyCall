package kr.ac.konkuk.koogle.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kr.ac.konkuk.koogle.Fragment.CommunityFragment
import kr.ac.konkuk.koogle.Fragment.GroupFragment
import kr.ac.konkuk.koogle.Fragment.CardFragment
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var binding: ActivityMainBinding

    private var backBtnTime: Long = 0 // 뒤로가기 두번 눌러 종료 용 변수

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_short -> {
                    moveToFragment(CardFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.nav_community -> {
                    moveToFragment(CommunityFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.nav_group -> {
                    moveToFragment(GroupFragment())
                    return@OnNavigationItemSelectedListener true
                }

            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //프래그먼트 위에 있는 editText에 입력을 할때 키보드에 의해 가려질때 사용
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )

        initDrawerLayout()

//        //첫화면이 CardFragment 이므로
//        binding.bottomNavView.selectedItemId = R.id.nav_short

        //아직 CardFragment 가 미완성인 관계로
        binding.bottomNavView.selectedItemId = R.id.nav_community

        //바텀 네비게이션뷰 선택
        binding.bottomNavView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        //네비게이션 메뉴 아이템 클릭 시 속성 부여
        binding.navView.setNavigationItemSelectedListener(this)

//        moveToFragment(CardFragment()) //앱을 시작할때 디폴트로 오늘의 단어어프래그먼트가 켜지도록
        //아직 CardFragment 가 미완성인 관계로
        moveToFragment(CommunityFragment())

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.temp -> {
                Toast.makeText(applicationContext, "temp", Toast.LENGTH_SHORT).show()
            }
        }
        binding.drawerLayout.closeDrawers()

        return true
    }

    private fun initDrawerLayout() {
        binding.toolbar.option.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.toolbar.ProfileImage.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun moveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction() //fragement Transaction
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }

    //뒤로가기 두번 눌러 종료
    override fun onBackPressed() {
        val curTime = System.currentTimeMillis()
        val gapTime: Long = curTime - backBtnTime

        //뒤로가기를 한번 누른 후에 2초가 지나기전에 한번 더 눌렀을 경우 if문 진입
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            binding.drawerLayout.closeDrawers()
        }
        else{
            if (gapTime in 0..2000) {
                super.onBackPressed()
            } else {
                backBtnTime = curTime
                Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}