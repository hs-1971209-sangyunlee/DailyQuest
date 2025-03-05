package com.example.dailyquest.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dailyquest.R
import com.example.dailyquest.databinding.ActivityMainBinding

class CalenderFragment : Fragment() {
    val fragmentTitle: String get() = "Calender"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calender, container, false) // XML 연결
    }
}