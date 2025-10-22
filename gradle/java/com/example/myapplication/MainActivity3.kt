package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView // 匯入 TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
// import androidx.recyclerview.widget.LinearLayoutManager // 註釋掉：不再需要
// import androidx.recyclerview.widget.RecyclerView // 註釋掉：不再需要
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Locale // 為了日期格式化

// MainActivity3 不再需要實作 OnTripItemClickListener 介面 (因為沒有Adapter了)
class MainActivity3 : AppCompatActivity() { // 移除了 OnTripItemClickListener 介面實作

    // private lateinit var tripAdapter: TripAdapter // 註釋掉：不再需要
    private lateinit var tripListTextView: TextView // 新增 TextView 引用

    // 記憶體中儲存行程資料，應用程式關閉時會消失。
    private val tripList = mutableListOf<TripItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // --- 旅遊行程規劃部分初始化 ---
        // 註釋掉所有 RecyclerView 相關的初始化程式碼
        // val recyclerViewTrip = findViewById<RecyclerView>(R.id.recyclerViewTrip)
        // recyclerViewTrip.layoutManager = LinearLayoutManager(this)
        // tripAdapter = TripAdapter(tripList, this)
        // recyclerViewTrip.adapter = tripAdapter

        tripListTextView = findViewById(R.id.tripListTextView) // 初始化 TextView

        val btnAddTrip = findViewById<Button>(R.id.btnAddTrip)
        btnAddTrip.setOnClickListener {
            Log.d("TripDebug", "點擊了 '新增行程' 按鈕，準備顯示對話框。")
            showAddTripDialog()
        }

        Log.d("TripDebug", "正在呼叫 addSampleTripItems()。")
        addSampleTripItems() // 載入範例行程
        updateTripListDisplay() // 初始化時也更新 TextView 顯示
    }

    private fun showAddTripDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_trip, null)
        val etLocation = dialogView.findViewById<EditText>(R.id.etLocation)
        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
        val etTime = dialogView.findViewById<EditText>(R.id.etTime)
        val etActivity = dialogView.findViewById<EditText>(R.id.etActivity)
        val etMapLink = dialogView.findViewById<EditText>(R.id.etMapLink)
        val etNotes = dialogView.findViewById<EditText>(R.id.etNotes)

        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)
                etDate.setText("$selectedYear-$formattedMonth-$formattedDay")
                Log.d("TripDebug", "選擇了日期: $selectedYear-$formattedMonth-$formattedDay")
            }, year, month, day).show()
        }

        etTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val formattedHour = String.format("%02d", selectedHour)
                val formattedMinute = String.format("%02d", selectedMinute)
                etTime.setText("$formattedHour:$formattedMinute")
                Log.d("TripDebug", "選擇了時間: $formattedHour:$formattedMinute")
            }, hour, minute, true).show()
        }

        AlertDialog.Builder(this)
            .setTitle("新增旅遊行程")
            .setView(dialogView)
            .setPositiveButton("新增") { dialog, _ ->
                val location = etLocation.text.toString().trim()
                val date = etDate.text.toString().trim()
                val time = etTime.text.toString().trim()
                val activity = etActivity.text.toString().trim()
                val mapLink = etMapLink.text.toString().trim().takeIf { it.isNotEmpty() }
                val notes = etNotes.text.toString().trim().takeIf { it.isNotEmpty() }

                Log.d("TripDebug", "對話框 '新增' 按鈕點擊。")
                Log.d("TripDebug", "取得輸入: 地點=$location, 日期=$date, 時間=$time, 活動=$activity, 地圖=$mapLink, 備註=$notes")

                if (location.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty() && activity.isNotEmpty()) {
                    if (!isValidDate(date)) {
                        Toast.makeText(this, "日期格式無效，請使用YYYY-MM-DD", Toast.LENGTH_SHORT).show()
                        Log.w("TripDebug", "日期格式驗證失敗: $date")
                        return@setPositiveButton
                    }
                    if (!isValidTime(time)) {
                        Toast.makeText(this, "時間格式無效，請使用 HH:MM (24小時制)", Toast.LENGTH_SHORT).show()
                        Log.w("TripDebug", "時間格式驗證失敗: $time")
                        return@setPositiveButton
                    }

                    val newTripItem = TripItem(
                        location = location,
                        date = date,
                        time = time,
                        activity = activity,
                        mapLink = mapLink,
                        notes = notes
                    )
                    tripList.add(newTripItem)
                    Log.d("TripDebug", "新行程物件已加入 tripList。目前 tripList 大小: ${tripList.size}")

                    // 更新 TextView 顯示
                    updateTripListDisplay() // <--- 呼叫新的顯示方法

                    Toast.makeText(this, "行程已新增！", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "請填寫所有必填欄位 (地點、日期、時間、活動內容)", Toast.LENGTH_LONG).show()
                    Log.w("TripDebug", "必填欄位未填寫。")
                }
                dialog.dismiss()
            }
            .setNegativeButton("取消") { dialog, _ ->
                Log.d("TripDebug", "對話框 '取消' 按鈕點擊。")
                dialog.cancel()
            }
            .show()
    }

    private fun updateTripListDisplay() {
        val stringBuilder = StringBuilder()
        val sortedTrips = tripList.sortedWith(compareBy<TripItem> {
            try {
                LocalDate.parse(it.date)
            } catch (e: DateTimeParseException) {
                LocalDate.MAX
            }
        }.thenBy { it.time })

        var currentDate = ""
        val displayDateFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日 EEEE", Locale.getDefault())

        for (trip in sortedTrips) {
            if (trip.date != currentDate) {
                try {
                    val dateObj = LocalDate.parse(trip.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    stringBuilder.append("\n=== ${dateObj.format(displayDateFormatter)} ===\n")
                } catch (e: DateTimeParseException) {
                    stringBuilder.append("\n=== ${trip.date} ===\n")
                }
                currentDate = trip.date
            }
            stringBuilder.append("地點: ${trip.location}\n")
            stringBuilder.append("時間: ${trip.time}\n")
            stringBuilder.append("活動: ${trip.activity}\n")
            if (!trip.mapLink.isNullOrEmpty()) {
                // 注意: 這裡只是顯示地圖連結文字，不是可點擊的連結
                stringBuilder.append("地圖: ${trip.mapLink}\n")
            }
            if (!trip.notes.isNullOrEmpty()) {
                stringBuilder.append("備註: ${trip.notes}\n")
            }
            stringBuilder.append("--------------------\n")
        }
        tripListTextView.text = stringBuilder.toString()
        Log.d("TripDebug", "TextView 已更新顯示，內容長度: ${stringBuilder.length}")
    }

    private fun isValidDate(dateString: String): Boolean {
        return try {
            LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            true
        } catch (e: DateTimeParseException) {
            Log.e("TripDebug", "isValidDate: 日期 '$dateString' 解析失敗.", e)
            false
        }
    }

    private fun isValidTime(timeString: String): Boolean {
        return try {
            LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
            true
        } catch (e: DateTimeParseException) {
            Log.e("TripDebug", "isValidTime: 時間 '$timeString' 解析失敗.", e)
            false
        }
    }

    // 註釋掉：因為現在沒有 Adapter 會呼叫這個方法
    // override fun onMapLinkClick(mapLink: String) {
    //     Log.d("TripDebug", "點擊地圖連結: $mapLink")
    //     if (mapLink.isNotEmpty()) {
    //         try {
    //             val gmmIntentUri = Uri.parse(mapLink)
    //             val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    //             mapIntent.setPackage("com.google.android.apps.maps")
    //
    //             if (mapIntent.resolveActivity(packageManager) != null) {
    //                 startActivity(mapIntent)
    //                 Log.i("TripDebug", "已成功啟動 Google 地圖應用程式。")
    //             } else {
    //                 val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapLink))
    //                 startActivity(webIntent)
    //                 Toast.makeText(this, "未找到 Google 地圖應用程式，嘗試用瀏覽器開啟", Toast.LENGTH_SHORT).show()
    //                 Log.w("TripDebug", "未找到 Google 地圖應用程式，嘗試用瀏覽器開啟連結。")
    //             }
    //         } catch (e: Exception) {
    //             Toast.makeText(this, "地圖連結無效或無法開啟地圖應用程式", Toast.LENGTH_SHORT).show()
    //             Log.e("TripDebug", "開啟地圖連結時發生錯誤。", e)
    //         }
    //     } else {
    //         Toast.makeText(this, "沒有提供地圖連結", Toast.LENGTH_SHORT).show()
    //         Log.w("TripDebug", "地圖連結為空，無法開啟。")
    //     }
    // }

    private fun addSampleTripItems() {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val dayAfterTomorrow = today.plusDays(2)

        tripList.addAll(
            listOf(
                TripItem(
                    location = "桃園國際機場",
                    date = today.toString(),
                    time = "08:00",
                    activity = "搭乘航班前往東京",
                    mapLink = "https://maps.app.goo.gl/YourActualLink1",
                    notes = "航班編號JL802，提前兩小時抵達"
                ),
                TripItem(
                    location = "東京晴空塔",
                    date = today.toString(),
                    time = "16:00",
                    activity = "參觀晴空塔觀景台",
                    mapLink = "https://maps.app.goo.gl/YourActualLink2",
                    notes = "已線上預約門票"
                ),
                TripItem(
                    location = "淺草寺",
                    date = tomorrow.toString(),
                    time = "09:30",
                    activity = "參拜淺草寺，逛仲見世商店街",
                    mapLink = "https://maps.app.goo.gl/YourActualLink3",
                    notes = null
                ),
                TripItem(
                    location = "新宿御苑",
                    date = tomorrow.toString(),
                    time = "14:00",
                    activity = "漫步日式庭園",
                    mapLink = "https://maps.app.goo.gl/YourActualLink4",
                    notes = "注意閉園時間"
                ),
                TripItem(
                    location = "富士山河口湖",
                    date = dayAfterTomorrow.toString(),
                    time = "08:00",
                    activity = "一日遊：搭乘纜車、遊船",
                    mapLink = "https://maps.app.goo.gl/YourActualLink5",
                    notes = "確認天氣狀況，攜帶外套"
                )
            )
        )
        Log.d("TripDebug", "範例行程已加入 tripList。目前 tripList 大小: ${tripList.size}")
        // 註釋掉：不需要再呼叫 tripAdapter.updateData
        // tripAdapter.updateData(tripList)
        Log.d("TripDebug", "已呼叫 updateTripListDisplay() 載入範例行程。")
    }
}