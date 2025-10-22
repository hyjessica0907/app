package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.* // For Locale (import this if not auto-imported)

class TripAdapter(
    private val tripList: MutableList<TripItem>, // 可變列表以支持新增
    private val listener: OnTripItemClickListener // 傳入點擊監聽器
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // 定義不同的 ViewType
    companion object {
        private const val VIEW_TYPE_DATE_HEADER = 0
        private const val VIEW_TYPE_TRIP_ITEM = 1
    }

    // 內部類別用於日期標題
    data class DateHeader(val date: String)

    // 實際用於顯示的列表，包含日期標題和行程項目
    private val displayList = mutableListOf<Any>()

    init {
        updateDisplayList() // 初始化時更新顯示列表
    }

    // 外部調用以更新資料
    fun updateData(newTripList: List<TripItem>) {
        tripList.clear()
        tripList.addAll(newTripList)
        updateDisplayList()
    }

    // 根據日期和時間排序並分組
    private fun updateDisplayList() {
        displayList.clear()
        if (tripList.isEmpty()) {
            notifyDataSetChanged()
            return
        }

        // 嘗試解析日期並排序
        // 確保在較低 API 版本上兼容，可以使用 SimpleDateFormat
        val sortedTrips = tripList.sortedWith(compareBy<TripItem> {
            try {
                LocalDate.parse(it.date)
            } catch (e: DateTimeParseException) {
                // 如果日期格式錯誤，放在列表末尾，或者你可以選擇過濾掉這些錯誤數據
                LocalDate.MAX
            }
        }.thenBy { it.time })

        var currentDate = ""
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        // 顯示格式，包含星期幾 (星期幾的顯示可能需要API 26+)
        val displayDateFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日 EEEE", Locale.getDefault())

        for (trip in sortedTrips) {
            if (trip.date != currentDate) {
                // 添加日期標題
                try {
                    val dateObj = LocalDate.parse(trip.date, dateFormatter)
                    displayList.add(DateHeader(dateObj.format(displayDateFormatter)))
                } catch (e: DateTimeParseException) {
                    displayList.add(DateHeader(trip.date)) // 如果解析失敗，直接用原始字串
                }
                currentDate = trip.date
            }
            displayList.add(trip)
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (displayList[position]) {
            is DateHeader -> VIEW_TYPE_DATE_HEADER
            is TripItem -> VIEW_TYPE_TRIP_ITEM
            else -> throw IllegalArgumentException("Unknown view type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false)
                DateHeaderViewHolder(view)
            }
            VIEW_TYPE_TRIP_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
                TripViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_DATE_HEADER -> {
                val dateHeaderHolder = holder as DateHeaderViewHolder
                val dateHeader = displayList[position] as DateHeader
                dateHeaderHolder.dateTextView.text = dateHeader.date
            }
            VIEW_TYPE_TRIP_ITEM -> {
                val tripViewHolder = holder as TripViewHolder
                val tripItem = displayList[position] as TripItem

                tripViewHolder.locationTextView.text = tripItem.location
                tripViewHolder.timeTextView.text = tripItem.time
                tripViewHolder.activityTextView.text = tripItem.activity
                // 如果備註為 null 或空，則隱藏備註 TextView
                tripViewHolder.notesTextView.text = tripItem.notes ?: "無備註"
                tripViewHolder.notesTextView.visibility = if (tripItem.notes.isNullOrEmpty()) View.GONE else View.VISIBLE

                // 處理地圖連結的顯示與點擊
                if (!tripItem.mapLink.isNullOrEmpty()) {
                    tripViewHolder.mapLinkTextView.text = "開啟地圖" // 顯示可點擊文字
                    tripViewHolder.mapLinkTextView.visibility = View.VISIBLE
                    tripViewHolder.mapLinkTextView.setOnClickListener {
                        listener.onMapLinkClick(tripItem.mapLink)
                    }
                } else {
                    tripViewHolder.mapLinkTextView.visibility = View.GONE
                    tripViewHolder.mapLinkTextView.setOnClickListener(null) // 清除點擊監聽器
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return displayList.size
    }

    // ViewHolder for Trip Item
    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val activityTextView: TextView = itemView.findViewById(R.id.activityTextView)
        val mapLinkTextView: TextView = itemView.findViewById(R.id.mapLinkTextView)
        val notesTextView: TextView = itemView.findViewById(R.id.notesTextView)
    }

    // ViewHolder for Date Header
    class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateHeaderTextView)
    }
}