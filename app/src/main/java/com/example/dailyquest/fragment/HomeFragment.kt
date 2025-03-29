package com.example.dailyquest.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyquest.R
import com.example.dailyquest.adapter.QuestAdapter
import com.example.dailyquest.model.Quest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {
    val fragmentTitle: String get() = "Home"
    private var userId: String? = null
    private lateinit var questRecyclerView: RecyclerView
    private lateinit var questAdapter: QuestAdapter
    private val questList = mutableListOf<Quest>()
    private var isQuestListCheck = false
    private lateinit var questListCheckBox: CheckBox

    //프래그먼트가 생성될 때 최초 한번 호출, View 생성 전이라 findViewById 등의 메서드 불가
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString("USER_ID") // MainActivity에서 전달받은 userId
        }
    }
    //프래그먼트가 View를 처음 그릴 때 호출, 레이아웃 XML을 연결함, 반드시 View를 반환해야함
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false) // XML 연결
    }

    //onCreateView()가 실행된 직후 호출됨, 뷰가 완전히 생성된 다음 실행,
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

        //체크박스 필터 설정
        questListCheckBox = view.findViewById<CheckBox>(R.id.quest_list_check_box)
        questListCheckBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            isQuestListCheck = isChecked
            userId?.let { uid ->
                Log.d("HomeFragment", "로그인한 사용자 ID: $uid")
                getQuests(userId = uid, category = "일일퀘스트")
            } ?: Log.e("HomeFragment", "userId가 null입니다.")
        }
    }

    fun getQuests(userId: String, category: String) {
        val db = FirebaseFirestore.getInstance()
        var query: Query = db.collection("uid").document(userId).collection(category)

        // 🔹 isChecking이 false일 때만 완료되지 않은 퀘스트 필터링
        if (!isQuestListCheck) {
            query = query.whereEqualTo("completed", false)
        }

        query.get()
            .addOnSuccessListener { result ->
                val quests = result.documents.mapNotNull { it.toObject(Quest::class.java)!! }
                questList.clear()
                questList.addAll(quests)
                questList.sort()
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

    // 퀘스트 완료 기능 (Firestore에 completed 업데이트)
    private fun completeQuest(quest: Quest) {
        userId?.let { uid ->
            val db = FirebaseFirestore.getInstance()
            val questRef = db.collection("uid").document(uid)
                .collection(quest.category).document(quest.id)

            val userRef = db.collection("uid").document(uid)

            // 퀘스트 완료 -> XP 증가 -> 리스트 갱신 순으로 체인 처리
            questRef.update("completed", true)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "퀘스트 성공! +${quest.xp}xp", Toast.LENGTH_SHORT).show()
                    getQuests(userId = uid, category = "일일퀘스트")
                }
                .continueWithTask {
                    db.runTransaction { transaction ->
                        val snapshot = transaction.get(userRef)
                        val currentXp = snapshot.getLong("xp") ?: 0
                        transaction.update(userRef, "xp", currentXp + quest.xp)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "퀘스트 완료 처리 중 오류 발생", e)
                }
        }
    }
}