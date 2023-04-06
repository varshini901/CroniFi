package com.example.cronifi

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ReminderListItem: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reminder_list_item)
//    val editText = findViewById<EditText>(R.id.reminder_msg)
//        editText.isEnabled=true
//        val tick=findViewById<ImageView>(R.id.tick)
//    editText.addTextChangedListener(object : TextWatcher {
//        override fun afterTextChanged(s: Editable?) {}
//
//        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            val words = s?.trim()?.split("\\s+".toRegex()) // split the text into words
//            if (words?.size ?: 0 > 15) { // check the number of words and limit to 10
//                val lastSpace = s!!.lastIndexOf(" ")
//                editText.setText(s.subSequence(0, lastSpace))
//                editText.setSelection(lastSpace)
//            }
//        }
//    })
//        tick.setOnClickListener(){
//            editText.isEnabled=false
//        }
    }
}