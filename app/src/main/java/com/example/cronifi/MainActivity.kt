package com.example.cronifi

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences=getSharedPreferences("CroniFi", Context.MODE_PRIVATE)
        val isActivityShownFirstTime=sharedPreferences.getBoolean("isActivityShownFirstTime",false)
        val mainActivity=sharedPreferences.getBoolean("CronTabActivity",false)
        if(mainActivity){
            val intent = Intent(this, CronTabActivity::class.java)
            startActivity(intent)
            finish()
        }
      else
        if(isActivityShownFirstTime){
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
            }
        else{
            setContentView(R.layout.activity_main)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isLoggedIn", true)
            editor.apply()
            val getStarted=findViewById<Button>(R.id.get_started)
            getStarted.setOnClickListener {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }

        }


    }

}