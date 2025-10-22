// MemoItem.kt
package com.example.myapplication

data class MemoItem(
    val content: String,
    var isChecked: Boolean = false // 新增這個屬性來追蹤勾選狀態
)
