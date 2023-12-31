package com.example.where42android

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton

class SettingPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_page)

        fun adjustTextViewSize(textView: TextView) {
            val displayMetrics = textView.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels

            val desiredWidth = screenWidth * 0.412 // 원하는 폭 (현재 레이아웃에서의 비율)
            val text = textView.text.toString()

            // 적절한 텍스트 크기 계산
            var textSize = 74 // 초기 텍스트 크기
            var paint = Paint()
            paint.textSize = textSize.toFloat()

            while (paint.measureText(text) > desiredWidth) {
                textSize--
                paint.textSize = textSize.toFloat()
            }

            // TextView에 적절한 텍스트 크기 설정
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        }
        val myTextView = findViewById<TextView>(R.id.text_setting)
        adjustTextViewSize(myTextView)


//
//        fun resizeTextViewToScreenRatio(textView: TextView, textSizeDP: Float) {
//            val displayMetrics = textView.context.resources.displayMetrics
//            val screenWidth = displayMetrics.widthPixels.toFloat()
//            val screenHeight = displayMetrics.heightPixels.toFloat()
//
//            val textSizeRatio = textSizeDP / screenWidth
//            val newTextSize = screenWidth * textSizeRatio
//
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize)
//        }
//
//        val myTextView = findViewById<TextView>(R.id.text_setting)
//        val textSizeInDP = 16.0f // 설정하려는 텍스트 크기 (DP 단위)
//
//        resizeTextViewToScreenRatio(myTextView, textSizeInDP)

        /*footer 홈, 검색 버튼*/
        val homeButton: ImageButton = this.findViewById(R.id.home_button)

        homeButton.setOnClickListener {
            try {
                val intent = Intent(this, MainPageActivity::class.java)
                startActivity(intent)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }


        }

        val searchButton: ImageButton = this.findViewById(R.id.search_button)

        searchButton.setOnClickListener {
            try {
                //Toast.makeText(this, "버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SearchPage::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        /* 코멘트 설정 버튼을 눌렀을 떄 기능 구현해야함. 주여이 팝업 기능 들고오기*/
        //val commentbutton: AppCompatButton = this.findViewById(R.id.comment_button)
        val commentButton: Button = this.findViewById(R.id.comment_button)

        commentButton.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.new_group_popup)

            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val title = dialog.findViewById<TextView>(R.id.title)
            title.text = "코멘트 설정"

            val editText = dialog.findViewById<EditText>(R.id.input)
            val btnCancel = dialog.findViewById<Button>(R.id.cancel)
            val btnSubmit = dialog.findViewById<Button>(R.id.submit)

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnSubmit.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}