package com.example.cronifi

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class CronTabActivity : AppCompatActivity() {
    private lateinit var received: TextView
    private lateinit var requestQueue: RequestQueue
    val messageList = mutableListOf<Reminder>()
    private val reminderList: ArrayList<Message> = ArrayList()
    private val reminderadapter = ReminderAdapter(reminderList, this)
    lateinit var dialog: Dialog
    var toochselectedDate: Date? = null
    var date: String = ""
    lateinit var calendar : ImageView
    val cal = Calendar.getInstance()
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val day = cal.get(Calendar.DAY_OF_MONTH)
    var tata: String = (year.toString() + "-" + (month + 1) + "-" + day.toString())
    lateinit var dateSet:TextView
    lateinit var reminder:RecyclerView
    lateinit var receiverName:String
    lateinit var receiverNumber:String
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cron_tab)
        requestQueue = Volley.newRequestQueue(this)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
//        progressBar.setOnClickListener {
        val sharedPrefs = getSharedPreferences("CroniFi", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("CronTabActivity", true).apply()
        val contactSelected = sharedPrefs.getBoolean("selected", false)
        val name = sharedPrefs.getString("name", "")
        Log.d("NIK","$name")
         val userNumber = "91" + sharedPrefs.getString("number", "")
        received = findViewById(R.id.username)
        calendar= findViewById(R.id.calendar)
        dateSet = findViewById(R.id.dateSet)
            val newReminder = findViewById<ImageView>(R.id.plus_icon)
             reminder = findViewById(R.id.reminder_list)
            val str: String
            val intent = intent
             receiverName = intent.getStringExtra("name").toString()
            receiverNumber = intent.getStringExtra("number").toString()

            val contactbook = findViewById<ImageView>(R.id.contact_book)

            val pro = findViewById<LinearLayout>(R.id.pro)
            if (contactSelected ) {
                str = "$receiverName's"
                var cleanedNumber = receiverNumber.replace("[^0-9+]".toRegex(), "")
                if (cleanedNumber.startsWith("91")) {
                    cleanedNumber = cleanedNumber.substring(2) // Remove "91" prefix
                } else if (cleanedNumber.startsWith("+91")) {
                    cleanedNumber = cleanedNumber.substring(3)
                }
                receiverNumber = "91$cleanedNumber"
                contactbook.visibility = View.INVISIBLE
                pro.visibility = View.INVISIBLE
                sharedPrefs.edit().putBoolean("selected",false).apply()

            } else {
                str = "$name's"
                receiverNumber = userNumber
                contactbook.visibility = View.VISIBLE
                pro.visibility = View.VISIBLE
            }
            received.text = str
        reminder.layoutManager = LinearLayoutManager(this)
        reminder.adapter = reminderadapter
            getRequest(userNumber, "$tata 00:00:00", receiverNumber, this)
            dateSet.text = tata

        var previousdate = (year.toString() + "-" + (month + 1) + "-" + day.toString())
        calendar.setOnClickListener {
            onDateSelected()
        }
            if (previousdate != tata) {
                getRequest(
                    userNumber,
                    "$tata 00:00:00",
                    receiverNumber,
                    this
                )
            }
            Log.d("TAG", "tata: $tata")
            contactbook.setOnClickListener {
                val inten = Intent(this, CronBookActivity::class.java)
                startActivity(inten)
            }

            newReminder.setOnClickListener {
                val calen = Calendar.getInstance()
                val hour = calen.get(Calendar.HOUR_OF_DAY)
                val minute = calen.get(Calendar.MINUTE)
                var time = ""
                val timeSetListener =
                    TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                        val selectedTime =
                            LocalTime.of(selectedHour, selectedMinute * 15).withSecond(0)
                                .withNano(0)
                        time = selectedTime.toString()
                        Log.d("NIK", tata)
                        val msgTime = "$tata $time:00"
                        val msgTimeInUTC = convertISTtoUTC(msgTime)
                        dialog = Dialog(this)
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.edit_text_dialog)
                        val layoutParams = WindowManager.LayoutParams()
                        layoutParams.copyFrom(dialog.window?.attributes)
                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                        dialog.window?.attributes = layoutParams
                        val edit = dialog.findViewById<EditText>(R.id.edit_text)
                        val ok = dialog.findViewById<Button>(R.id.OK_Button)
                        val cancel = dialog.findViewById<Button>(R.id.cancel_Button)
                        var msg = ""
                        ok.setOnClickListener {
                            msg = edit.text.toString()
                            dialog.dismiss()
                            val reminder1 =
                                Reminder(
                                    msg,
                                    userNumber,
                                    receiverNumber,
                                    msgTimeInUTC
                                )
                            sendRequest(reminder1, this@CronTabActivity)
                            getRequest(
                                userNumber,
                                "$tata 00:00:00",
                                receiverNumber,
                                this@CronTabActivity
                            )
                            reminderadapter.notifyDataSetChanged()

                        }
                        cancel.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                val timePickerDialog = CustomTimePickerDialog(
                    this,
                    timeSetListener,
                    hour,
                    minute
                )

                timePickerDialog.show()
            }
//        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getRequest(sender: String, time: String, receiver: String, context: Context) {
        val url =
            "https://cronifi.app.v1.ngrok.dev/api/CronReminder/CronEvents?fromContactNumber=%2B$sender&timeStamp=$time&toContactNumber=%2B$receiver"
        val stringRequest = object : StringRequest(Method.GET, url,
            Response.Listener { response ->
                messageList.clear()
                reminderList.clear()
                reminder.removeAllViews()
                reminderadapter.notifyDataSetChanged()
                val jsonArray = JSONArray(response)
                for (i in 0 until jsonArray.length()) {

                    val jsonObject = jsonArray.getJSONObject(i)
                    val message = jsonObject.getString("message")
                    val from = jsonObject.getString("from")
                    val to = jsonObject.getString("to")
                    val timeStamp = jsonObject.getString("timeStamp")
                    val reminder = Reminder(message, from, to, timeStamp)
                    messageList.add(reminder)

                }
                datatoRecylerView(messageList)
                reminderadapter.notifyDataSetChanged()

            },
            Response.ErrorListener { error ->
                Toast.makeText(context, "Fail to get response = $error", Toast.LENGTH_SHORT).show()
            }) {

            override fun getParams(): Map<String, String> {
                return HashMap()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["X-API-KEY"] = "schedioB.croniFi.9199171223.api.key"
                headers["accept"] = "/"
                return headers
            }
        }
        requestQueue.add(stringRequest)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendRequest(reminder: Reminder, context: Context) {
        val url =
            "https://cronifi.app.v1.ngrok.dev/api/CronReminder/CronEvent?TimeStamp=${reminder.time}&From=%2B${reminder.sender}&To=%2B${reminder.receiver}&Message=${reminder.message}"
        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener {
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, "Fail to get response = $error", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["variable1"] = reminder.message
                params["variable2"] = reminder.sender
                params["variable3"] = reminder.receiver
                params["variable4"] = reminder.time
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["X-API-KEY"] = "schedioB.croniFi.9199171223.api.key"
                headers["accept"] = "/"
                return headers
            }
        }
        requestQueue.add(stringRequest)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertISTtoUTC(timeInIST: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(timeInIST)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dat = outputFormat.format(date)
        return dat.format(formatter)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertUTCtoIST(timeInUTC: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val date = inputFormat.parse(timeInUTC)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dat = outputFormat.format(date)
        return dat.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun datatoRecylerView(items: MutableList<Reminder>) {
        for (i in items) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateTime = LocalDateTime.parse(i.time)
            val output=dateTime.format(formatter)
            val t=convertUTCtoIST(output)
            val time = t.slice(11..15)

            reminderList.add(0, Message(time, i.message))
            reminderadapter.notifyDataSetChanged()
        }
    }
    private fun onDateSelected() {
           val lastdate = cal.clone() as Calendar

            val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val tooch = Calendar.getInstance()
                tooch.set(Calendar.YEAR, year)
                tooch.set(Calendar.MONTH, month)
                tooch.set(Calendar.DAY_OF_MONTH, day)
                toochselectedDate = tooch.time
                this.tata = (year.toString() + "-" + (month + 1) + "-" + day.toString())
                dateSet.text=tata
            }

            lastdate.add(Calendar.DATE, 14)
            val datePickerDialog = DatePickerDialog(this, datePickerListener, year, month, day)
            datePickerDialog.datePicker.minDate = cal.timeInMillis
            datePickerDialog.datePicker.maxDate = lastdate.timeInMillis
            datePickerDialog.show()

            Log.d("TAG", "tooch value: $toochselectedDate")

    }

}
