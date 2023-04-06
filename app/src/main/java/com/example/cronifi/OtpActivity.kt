package com.example.cronifi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class OtpActivity:AppCompatActivity() {
    private lateinit var  verificationId:String
    lateinit var otp:EditText

    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        val goCron = findViewById<Button>(R.id.go_cron)
        otp = findViewById(R.id.otp_box)
        val sharedPrefs=getSharedPreferences("CroniFi", Context.MODE_PRIVATE)
        val number=sharedPrefs.getString("number","")
        val phone = "+91$number"
        sendVerificationCode(phone)
        goCron.setOnClickListener {
            if (TextUtils.isEmpty(otp.text.toString())) {
                Toast.makeText(applicationContext, "Please enter OTP", Toast.LENGTH_SHORT).show()
            } else {
                verifyCode(otp.text.toString())
            }

        }
    }
    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val i = Intent(this, CronTabActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
        private fun sendVerificationCode(number:String){
            val options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(number) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this) // Activity (for callback binding)
                .setCallbacks(mCallBack) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

        }

        private val mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                val code: String? =p0.smsCode
                if(code!=null){
                    otp.setText(code)
//                    verifyCode(code)
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(applicationContext, p0.message, Toast.LENGTH_LONG).show();
            }

            override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
            }

        }
    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithCredential(credential)
    }
}