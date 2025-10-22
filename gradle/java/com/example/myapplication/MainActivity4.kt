package com.example.myapplication

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.myapplication.databinding.ActivityMainBinding
import android.widget.Button // 確保有匯入 Button
import android.widget.ImageView

class MainActivity4 : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var fgPermission = true
    lateinit var picURI: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //---------------- 檢查權限與要求權限  -------------------------------------
        val permissions = checkPermissions()  // 檢查權限
        val reqPermissions = registerForActivityResult(  // 權限要求回呼函式
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            it.forEach { s, b ->
                if (b == false)
                    fgPermission = false
            }
        }
        if (permissions.isNotEmpty())  // 要求缺少的權限
            reqPermissions.launch(permissions.toTypedArray())

        //----------------- 拍照後的回呼函式 -------------------
        val getPicture = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK)
                findViewById<ImageView>(R.id.imageView).setImageURI(picURI)  //載入拍照後的照片
        }

        // ----------------- 拍照按鈕點擊事件 -------------------
        findViewById<Button>(R.id.button).setOnClickListener {
            if (fgPermission) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "test1.png")
                }
                val imgURI = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                )
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, imgURI)  //加這行可以儲存拍照的照片，否則會在imageview上顯示的只是縮圖而已。
                }
                picURI = imgURI!!
                getPicture.launch(intent)
            } else
                Toast.makeText(
                    this, "需要取得照相機的權限",
                    Toast.LENGTH_SHORT
                ).show()
        }

        // --- 將回到主頁按鈕的程式碼移動到 onCreate 方法內部 ---
        val btnBack = findViewById<Button>(R.id.btnBack) // 這行現在在 onCreate 裡面
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        // -------------------------------------------------------------------
        // 注意：這裡原來多了一個 }，它錯誤地關閉了 onCreate 方法
        // 刪除多餘的 }
    } // <-- onCreate 方法的結束大括號

    fun checkPermissions(): ArrayList<String> {
        var permissions = ArrayList<String>() //儲存要求的權限
        val camera = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        val writestorage = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (camera != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.CAMERA)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)  // Android 11以下
            if (writestorage != PackageManager.PERMISSION_GRANTED)
                permissions.add(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

        return permissions
    }
}