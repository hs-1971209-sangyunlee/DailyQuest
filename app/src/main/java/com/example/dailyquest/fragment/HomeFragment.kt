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

        // RecyclerView 설정
        questRecyclerView = view.findViewById(R.id.questRecyclerView)
        questRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        //어댑터 초기화
        questAdapter = QuestAdapter(questList) { selectedQuest ->
            showQuestActionDialog(selectedQuest) //아이템 클릭 시 다이얼로그 표시
        }
        questRecyclerView.adapter = questAdapter

        //Firestore에서 퀘스트 가져오기
        userId?.let { uid ->
            Log.d("HomeFragment", "로그인한 사용자 ID: $uid")
            getQuests(userId = uid, category = "일일퀘스트")
        } ?: Log.e("HomeFragment", "userId가 null입니다.")

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString("USER_ID") // MainActivity에서 전달받은 userId
        }
    }

    fun getQuests(userId: String, category: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("uid").document(userId)
            .collection(category)
            .get()
            .addOnSuccessListener { result ->
                val quests = result.documents.mapNotNull { it.toObject(Quest::class.java)!! }
                questList.clear()
                questList.addAll(quests)
                questAdapter.notifyDataSetChanged() // UI 업데이트
            }
            .addOnFailureListener { e -> Log.w("Firebase", "데이터 가져오기 실패", e) }
    }

    // ✅ 모달창 띄우는 함수
    private fun showQuestActionDialog(quest: Quest) {
        QuestActionDialogFragment(
            onEdit = { editQuest(quest) },
            onDelete = { deleteQuest(quest) },
            onComplete = { completeQuest(quest) }
        ).show(parentFragmentManager, "QuestActionDialog")
    }

    // ✅ 퀘스트 수정 기능 (Firestore 업데이트)
    private fun editQuest(quest: Quest) {
        val newTitle = "수정된 퀘스트 제목" // 🔹 수정된 제목 (예시)
        userId?.let { uid ->
            val db = FirebaseFirestore.getInstance()
            db.collection("uid").document(uid)
                .collection(quest.category).document(quest.id)
                .update("title", newTitle)
                .addOnSuccessListener {
                    Log.d("Firestore", "퀘스트 수정 완료!")
                    getQuests(userId = uid, category = "일일퀘스트")
                }
                .addOnFailureListener { e -> Log.e("Firestore", "퀘스트 수정 실패", e) }
        }
    }

    // ✅ 퀘스트 삭제 기능 (Firestore에서 제거)
    private fun deleteQuest(quest: Quest) {
        userId?.let { uid ->
            val db = FirebaseFirestore.getInstance()
            db.collection("uid").document(uid)
                .collection(quest.category).document(quest.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firestore", "퀘스트 삭제 완료!")
                    getQuests(userId = uid, category = "일일퀘스트")
                }
                .addOnFailureListener { e -> Log.e("Firestore", "퀘스트 삭제 실패", e) }
        }
    }

    // ✅ 퀘스트 완료 기능 (Firestore에 completed 업데이트)
    private fun completeQuest(quest: Quest) {
        userId?.let { uid ->
            val db = FirebaseFirestore.getInstance()
            db.collection("uid").document(uid)
                .collection(quest.category).document(quest.id)
                .update("completed", true)
                .addOnSuccessListener {
                    Log.d("Firestore", "퀘스트 완료!")
                    getQuests(userId = uid, category = "일일퀘스트")
                }
                .addOnFailureListener { e -> Log.e("Firestore", "퀘스트 완료 실패", e) }
        }
    }
}