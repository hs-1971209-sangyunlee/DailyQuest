package com.example.dailyquest.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.dailyquest.R
import com.example.dailyquest.databinding.ActivityMainBinding
import io.grpc.Deadline
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddFragment : Fragment() {
    val fragmentTitle: String get() = "Add"
    private lateinit var deadline: TextView
    private var selectedCalendar: Calendar = Calendar.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false) // XML 연결

        deadline = view.findViewById<TextView>(R.id.deadlineTextView)

        //초기값: 현재시각 +24시간
        selectedCalendar.add(Calendar.DAY_OF_YEAR, 1)
        updateDeadlineText()

        deadline.setOnClickListener{
            showDateTimePicker()
        }

        return view
    }

    //날짜 & 시간 선택 다이얼로그
    private fun showDateTimePicker() {
        val context = requireContext()

        // 1️⃣ 날짜 선택 다이얼로그
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedCalendar.set(year, month, dayOfMonth)

                // 2️⃣ 시간 선택 다이얼로그
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        selectedCalendar.set(Calendar.MINUTE, minute)

                        updateDeadlineText() // 선택한 날짜 & 시간 반영
                    },
                    selectedCalendar.get(Calendar.HOUR_OF_DAY),
                    selectedCalendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            selectedCalendar.get(Calendar.YEAR),
            selectedCalendar.get(Calendar.MONTH),
            selectedCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // TextView에 날짜 업데이트 (형식: YYYY/MM/DD HH:mm)
    private fun updateDeadlineText() {
        val format = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        deadline.text = format.format(selectedCalendar.time)
    }
}