package com.example.cronifi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class SignInActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val signIn:Button=findViewById(R.id.sign_in)
        val user:EditText=findViewById(R.id.name)
        val mobileNumber:EditText=findViewById(R.id.number)
        val sharedPrefs=getSharedPreferences("CroniFi",Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("isActivityShownFirstTime",true).apply()

            signIn.setOnClickListener {
                val name = user.text.toString();
                val number = mobileNumber.text.toString()
                if (TextUtils.isEmpty(mobileNumber.text.toString())) {
                    Toast.makeText(
                        applicationContext,
                        "Please enter a valid phone number.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val phone = mobileNumber.text.toString()
                    if (isPhoneNumberValid(phone)) {
                        val intent = Intent(this, OtpActivity::class.java)
                        intent.putExtra("number", number)
                        sharedPrefs.edit().putString("number",number).apply()
                        sharedPrefs.edit().putString("name",name).apply()
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Please enter a Valid Phone Number",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
    fun isPhoneNumberValid(phoneNumber: String): Boolean {
        val pattern = Regex("^\\d{10}$") // pattern to match a 10-digit phone number

        return pattern.matches(phoneNumber)
    }





}

