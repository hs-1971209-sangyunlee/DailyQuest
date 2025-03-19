package com.example.dailyquest.fragment
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.app.AlertDialog
import android.content.Context

class QuestActionDialogFragment(
    private val onEdit:()->Unit,
    private val onDelete:()->Unit,
    private val onComplete:()->Unit) : DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("퀘스트 관리")
            .setItems(arrayOf("수정", "삭제", "완료")) { _, which ->
                when (which) {
                    0 -> onEdit()
                    1 -> onDelete()
                    2 -> onComplete()
                }
        }.setNegativeButton("취소", null).create()
    }
}
