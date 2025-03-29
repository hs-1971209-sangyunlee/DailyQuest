package com.example.dailyquest.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dailyquest.R
import com.example.dailyquest.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.grpc.Deadline
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddFragment : Fragment() {
    val fragmentTitle: String get() = "Add"
    private lateinit var deadline: TextView
    private var userId: String? = null
    private var selectedCalendar: Calendar = Calendar.getInstance()
    private lateinit var categorySpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false) // XML 연결

        //현재 로그인한 사용자 UID 가져오기
        userId = FirebaseAuth.getInstance().currentUser?.uid

        //퀘스트 카테고리 설정
        val categories = listOf("일일퀘스트", "주간퀘스트", "한정퀘스트")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner = view.findViewById<Spinner>(R.id.category_spinner)
        categorySpinner.adapter = categoryAdapter
        categorySpinner.setSelection(0)

        //퀘스트 기한 설정, 초기값: 현재시각 +24시간
        deadline = view.findViewById<TextView>(R.id.deadlineTextView)
        deadline.setOnClickListener {
            showDateTimePicker()
        }
        selectedCalendar.add(Calendar.DAY_OF_YEAR, 1)
        updateDeadlineText()

        //퀘스트 추가 버튼
        val addButton = view.findViewById<Button>(R.id.addQuestButton)
        addButton.setOnClickListener {
            savaQuestToFireStore()
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

    private fun savaQuestToFireStore() {
        val uniqueId = UUID.randomUUID().toString()
        val title = view?.findViewById<EditText>(R.id.add_quest_title)?.text.toString()
        val category = categorySpinner.selectedItem.toString()
        val xp = 500

        if(title.isEmpty()){
            Toast.makeText(requireContext(),"일정이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        // ✅ Firestore에 저장할 데이터 객체
        val questData = hashMapOf(
            "id" to uniqueId,
            "category" to category,
            "title" to title,
            "xp" to xp,
            "period" to deadline.text,
            "completed" to false
        )


        val db = FirebaseFirestore.getInstance()
        // ✅ Firestore 저장 (uid > 사용자id > 일일퀘스트 > title)
        userId?.let {
            db.collection("uid").document(it)
                .collection(category).document(uniqueId)
                .set(questData)
                .addOnSuccessListener {
                    Log.d("Firestore", "퀘스트 저장 성공!")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "퀘스트 저장 실패", e)
                }
        }
    }
}