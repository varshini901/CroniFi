package com.example.cronifi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReminderAdapter(val messages:ArrayList<Message>,val context:Context):RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.reminder_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.TimeText.setText(messages[position].time)
        holder.MsgText.setText(messages[position].reminder)
    }

    override fun getItemCount(): Int {
       return messages.size
    }


 class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
    var Linearlayout: LinearLayout
    var TimeText: TextView
    var MsgText: TextView

    init {
        Linearlayout = view.findViewById(R.id.LinearLayoutRecyclerView) as LinearLayout
        TimeText = view.findViewById(R.id.TimeTextView) as TextView
        MsgText = view.findViewById(R.id.reminder_msg) as TextView

    }
}
}
