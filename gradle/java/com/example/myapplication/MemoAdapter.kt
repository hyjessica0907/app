// MemoAdapter.kt
package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView // 如果你的 item_memo.xml 裡有 TextView，則需要

import androidx.recyclerview.widget.RecyclerView

// 新增一個介面來通知 Activity 狀態改變
interface OnItemCheckedChangeListener {
    fun onItemCheckedChanged()
}

class MemoAdapter(
    private val memoList: MutableList<MemoItem>,
    private val listener: OnItemCheckedChangeListener? // 傳入監聽器
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBoxItem: CheckBox = itemView.findViewById(R.id.checkboxItem)
        // 如果你的 item_memo.xml 裡有 TextView 顯示內容，也可以在這裡初始化
        // val textViewContent: TextView = itemView.findViewById(R.id.textViewContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        // 確保這裡的佈局檔案包含 CheckBox
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_memo, parent, false) // 假設你的單個項目佈局檔案是 item_memo.xml
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val currentItem = memoList[position]
        holder.checkBoxItem.text = currentItem.content // 設定 CheckBox 的文字
        holder.checkBoxItem.isChecked = currentItem.isChecked // 設定 CheckBox 的勾選狀態

        // 移除舊的監聽器以避免重複觸發，因為 RecyclerView 會重複利用 ViewHolder
        holder.checkBoxItem.setOnCheckedChangeListener(null)

        // 設定監聽器來處理勾選狀態的變化
        holder.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->
            currentItem.isChecked = isChecked // 更新 MemoItem 的勾選狀態
            listener?.onItemCheckedChanged() // 通知 Activity 狀態已改變
        }
    }

    override fun getItemCount(): Int {
        return memoList.size
    }
}