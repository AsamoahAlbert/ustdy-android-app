package com.example.ustdytake2.model

data class GamificationData(
    val streak: Int = 0,
    val lastStudyDate: Long = 0L,
    val badges: List<String> = emptyList()
)
