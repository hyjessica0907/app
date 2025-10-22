package com.example.myapplication

data class TripItem(
    val id: String = java.util.UUID.randomUUID().toString(), // 為每個行程項目生成一個唯一 ID
    val location: String,
    val date: String,      // 格式建議 "YYYY-MM-DD"
    val time: String,      // 格式建議 "HH:MM"
    val activity: String,
    val mapLink: String?,  // 可選，如果沒有連結為 null
    val notes: String?     // 可選，如果沒有備註為 null
)