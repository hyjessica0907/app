package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import android.widget.TextView // 引入 TextView

// 實作 OnItemCheckedChangeListener 介面
class MainActivity2 : AppCompatActivity(), OnItemCheckedChangeListener {

    private lateinit var memoAdapter: MemoAdapter
    private val memoList = mutableListOf<MemoItem>()
    private lateinit var textViewMessage: TextView // 宣告 TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2) // 確保這是你第二個畫面的佈局檔案

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 找到顯示訊息的 TextView
        textViewMessage = findViewById(R.id.textViewMessage)
        textViewMessage.visibility = View.GONE // 預設隱藏

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 初始化 RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 將 this (MainActivity2) 作為監聽器傳入 MemoAdapter
        memoAdapter = MemoAdapter(memoList, this)
        recyclerView.adapter = memoAdapter

        // 加入初始備忘項目
        memoList.addAll(
            listOf(
                MemoItem("護照"),
                MemoItem("機票"),
                MemoItem("充電器"),
                MemoItem("現金與信用卡")
            )
        )
        memoAdapter.notifyDataSetChanged()

        // 檢查初始狀態（如果有項目預設是勾選的）
        checkAllItemsChecked()


        // 加入新增功能
        val editTextNewItem = findViewById<EditText>(R.id.editTextNewItem)
        val btnAddItem = findViewById<Button>(R.id.btnAddItem)

        btnAddItem.setOnClickListener {
            val newItemText = editTextNewItem.text.toString().trim()
            if (newItemText.isNotEmpty()) {
                memoList.add(MemoItem(newItemText))
                memoAdapter.notifyItemInserted(memoList.size - 1)
                editTextNewItem.text.clear()
                checkAllItemsChecked() // 新增項目後重新檢查
            }
        }
    }

    // 實作 OnItemCheckedChangeListener 介面的方法
    override fun onItemCheckedChanged() {
        checkAllItemsChecked()
    }

    /**
     * 檢查所有 MemoItem 是否都被勾選，並據此顯示或隱藏訊息。
     */
    private fun checkAllItemsChecked() {
        if (memoList.isEmpty()) { // 如果清單是空的，則不顯示訊息
            textViewMessage.visibility = View.GONE
            return
        }

        val allChecked = memoList.all { it.isChecked } // 使用 Kotlin 的 all 函數檢查所有項目

        if (allChecked) {
            textViewMessage.text = "行李收拾完畢，準備出國！✈️"
            textViewMessage.visibility = View.VISIBLE
        } else {
            textViewMessage.visibility = View.GONE
        }
    }
}