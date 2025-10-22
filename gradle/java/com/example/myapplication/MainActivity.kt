package com.example.myapplication  // ← 確保這裡是你的實際 package 名稱

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId


class MainActivity : AppCompatActivity() {

    val rates = mapOf(
        "USD" to 1.0,
        "TWD" to 31.0,
        "JPY" to 157.0,
        "KRW" to 1386.0,
        "EUR" to 0.92
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputAmount = findViewById<EditText>(R.id.editAmount)
        val spinnerFrom = findViewById<Spinner>(R.id.spinnerFrom)
        val spinnerTo = findViewById<Spinner>(R.id.spinnerTo)
        val btnConvert = findViewById<Button>(R.id.btnConvert)
        val textResult = findViewById<TextView>(R.id.textResult)

        val currencies = rates.keys.toTypedArray()

        spinnerFrom.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)
        spinnerTo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)

        btnConvert.setOnClickListener {

            val amount = inputAmount.text.toString().toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(this, "請輸入有效金額", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fromCurrency = spinnerFrom.selectedItem.toString()
            val toCurrency = spinnerTo.selectedItem.toString()

            val result = amount / rates[fromCurrency]!! * rates[toCurrency]!!
            textResult.text = String.format("%.2f %s = %.2f %s", amount, fromCurrency, result, toCurrency)

            // 幣別對應國家
            val currencyToCountry = mapOf(
                "TWD" to "台灣",
                "USD" to "美國",
                "JPY" to "日本",
                "KRW" to "韓國",
                "EUR" to "德國"
            )

            // 國家介紹
            fun getCountryInfo(country: String): String {
                val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
                val zoneMap = mapOf(
                    "台灣" to "Asia/Taipei",
                    "美國" to "America/New_York",  // 可改為其他時區，如 Los_Angeles
                    "日本" to "Asia/Tokyo",
                    "韓國" to "Asia/Seoul",
                    "德國" to "Europe/Berlin"  // 以德國為例
                )

                val descriptions = mapOf(
                    "台灣" to "🇹🇼 台灣",
                    "美國" to "🇺🇸 美國",
                    "日本" to "🇯🇵 日本",
                    "韓國" to "🇰🇷 韓國",
                    "德國" to "🇪🇺 德國"
                )

                val timeZone = zoneMap[country]
                val description = descriptions[country] ?: "找不到國家資料"

                return if (timeZone != null) {
                    val time = ZonedDateTime.now(ZoneId.of(timeZone)).format(formatter)
                    "$description\n當地時間（以首都為準）：$time"
                } else {
                    "$description\n當地時間未知"
                }
            }

            // 拿出國家名稱
            val fromCountry = currencyToCountry[fromCurrency] ?: "未知"
            val toCountry = currencyToCountry[toCurrency] ?: "未知"

            // 拿出說明文字
            val fromInfo = getCountryInfo(fromCountry)
            val toInfo = getCountryInfo(toCountry)

            // 顯示在畫面上1
            val textInfo = findViewById<TextView>(R.id.textInfo)
            textInfo.text = "出發地：\n$fromInfo\n\n目的地：\n$toInfo"
        }
        val ivAirplane = findViewById<ImageView>(R.id.ivAirplane)
        ivAirplane.setOnClickListener {
            val url = "https://www.taoyuan-airport.com/"  // 這裡放跳轉的航空業網站
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        val btnGo = findViewById<Button>(R.id.btnGo)

        btnGo.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
        val btnToThird = findViewById<Button>(R.id.btnToThird)
        btnToThird.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }
        //val btnTofor = findViewById<Button>(R.id.btnTofor)
        //btnTofor.setOnClickListener {
           // val intent = Intent(this, MainActivity4::class.java)
           // startActivity(intent)
       // }

    }
}
