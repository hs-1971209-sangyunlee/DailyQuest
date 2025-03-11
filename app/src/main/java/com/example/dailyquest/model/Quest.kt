package com.example.dailyquest.model

data class Quest(
    val category: String = "",  // 퀘스트 카테고리 (일일, 주간 등)
    val title: String = "",  // 퀘스트 제목
    val period: String = "",  // 퀘스트 기간 (YYYY-MM-DD)
    val xp: Int = 0,  // 경험치
    val completed: Boolean = false,  // 완료 여부
)
