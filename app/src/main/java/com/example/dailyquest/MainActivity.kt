package com.example.dailyquest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.dailyquest.databinding.ActivityMainBinding
import com.example.dailyquest.fragment.*

import com.google.android.material.bottomnavigation.BottomNavigationView
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { //뷰 바인딩 초기화
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var title: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setBottomNavigationView()
        if (savedInstanceState == null) { //앱 초기 실행기 기본화면 홈
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, HomeFragment()) // 프래그먼트를 명확하게 추가
                .commit()
            binding.bottomNavigationView.selectedItemId = R.id.fragment_home
        }
    }

    private fun setBottomNavigationView() {
        title = findViewById(R.id.main_title)
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.fragment_home -> HomeFragment()
                R.id.fragment_search -> SearchFragment()
                R.id.fragment_add -> AddFragment()
                R.id.fragment_calender -> CalenderFragment()
                else -> null
            }
            if (fragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .addToBackStack(null) //백 스택에 추가
                    .commit()

                title.text = when (fragment) {
                    is HomeFragment -> fragment.fragmentTitle
                    is SearchFragment -> fragment.fragmentTitle
                    is AddFragment -> fragment.fragmentTitle
                    is CalenderFragment -> fragment.fragmentTitle
                    else -> ""
                }
                true
            } else {
                false
            }
        }
    }
}