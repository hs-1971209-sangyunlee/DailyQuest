package com.example.dailyquest.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyquest.R
import com.example.dailyquest.adapter.QuestAdapter
import com.example.dailyquest.model.Quest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    val fragmentTitle: String get() = "Home"
    private var userId: String? = null
    private lateinit var questRecyclerView: RecyclerView
    private lateinit var questAdapter: QuestAdapter
    private val questList = mutableListOf<Quest>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false) // XML 연결
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid // ✅ Firebase UID 가져오기
            Log.d("FirebaseAuth", "현재 로그인한 사용자 UID: $userId")
        } else {
            Log.e("FirebaseAuth", "사용자가 로그인하지 않았습니다.")
        }
        userId?.let { uid ->
            Log.d("HomeFragment", "로그인한 사용자 ID: $uid")

            getQuests(userId = uid, category = "일일퀘스트") { quests ->
                quests.forEach {
                    Log.d("Firebase", "퀘스트 제목: ${it.title}, 경험치: ${it.xp}")
                }
                questList.clear()
                questList.addAll(quests)
                questAdapter.notifyDataSetChanged() // UI 업데이트
            }
        } ?: Log.e("HomeFragment", "userId가 null입니다.")

        questRecyclerView = view.findViewById(R.id.questRecyclerView)
        questRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 더미 데이터 추가 (Firebase 연동 가능)
        questList.addAll(
            listOf(
                Quest("일일 퀘스트", "물 마시기", "~2025.02.27", 500),
                Quest("주간 퀘스트", "운동하기", "~2025.03.03", 1000),
                Quest("월간 퀘스트", "책 3권 읽기", "~2025.03.31", 2000)
            )
        )

        questAdapter = QuestAdapter(questList)
        questRecyclerView.adapter = questAdapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString("USER_ID") // MainActivity에서 전달받은 userId
        }
    }

    fun getQuests(userId: String, category: String, onResult: (List<Quest>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("uid").document(userId)
            .collection(category)
            .get()
            .addOnSuccessListener { result ->
                val quests = result.documents.mapNotNull { it.toObject(Quest::class.java)!! }
                onResult(quests)
            }
            .addOnFailureListener { e -> Log.w("Firebase", "데이터 가져오기 실패", e) }
    }
}