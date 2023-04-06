package com.example.cronifi

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class CronEventsRequest (
    private val contactNumber: String,
    listener: Response.Listener<String>,
    errorListener: Response.ErrorListener
) : StringRequest(
    Request.Method.POST,
      "https://cronifi.app.v1.ngrok.dev/",
//    "https://cronifi.app.v1.ngrok.dev/api/CronReminder/CronEvents?contactNumber=${contactNumber}",
    listener,
    errorListener
) {
    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        headers["X-API-KEY"] = "schedioB.croniFi.91$contactNumber.api.key"
        headers["accept"] = "/"
        return headers
    }
}