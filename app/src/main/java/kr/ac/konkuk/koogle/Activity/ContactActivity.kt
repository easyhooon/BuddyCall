package kr.ac.konkuk.koogle.Activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kr.ac.konkuk.koogle.R
import kr.ac.konkuk.koogle.databinding.ActivityContactBinding

class ContactActivity : AppCompatActivity() {
    lateinit var binding: ActivityContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        val actionBar = supportActionBar!!
        actionBar.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false) //기본 제목을 없애줌
            setDisplayHomeAsUpEnabled(true) // 자동으로 뒤로가기 버튼 만들어줌
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.default_option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            else -> {
                //뒤로가기
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}