package com.example.dailyquest.model

import java.text.SimpleDateFormat
import java.util.Locale

data class Quest (
    val id: String = "",
    val category: String = "",  // 퀘스트 카테고리 (일일, 주간 등)
    val title: String = "",  // 퀘스트 제목
    val period: String = "",  // 퀘스트 기간 (yyyy/MM/DD HH:mm)
    val xp: Int = 0,  // 경험치
    val completed: Boolean = false,  // 완료 여부
) : Comparable<Quest> {
    override fun compareTo(other: Quest): Int {
        val format = SimpleDateFormat("yyyy/MM/DD HH:mm", Locale.getDefault())
        val thisDate = try {
            format.parse(this.period)
        } catch (e: Exception) {
            null
        }

        val otherDate = try {
            format.parse(other.period)
        } catch (e: Exception) {
            null
        }

        return when{
            thisDate == null || otherDate == null -> 0
            else -> thisDate.compareTo(otherDate)
        }
    }
}
