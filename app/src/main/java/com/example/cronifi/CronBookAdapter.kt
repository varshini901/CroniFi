package com.example.cronifi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat.createDeviceProtectedStorageContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView


class CronBookAdapter(val context: Context,val sharedPreferences: SharedPreferences,val goCron:View):RecyclerView.Adapter<CronBookAdapter.ViewHolder>() {
    private var items:List<ContactDto> = ArrayList()
    private var selectedItemPosition = RecyclerView.NO_POSITION
    var onItemSelectedListener: OnItemSelectedListener? = null
    var selectedItem:ContactDto? = null
    interface OnItemSelectedListener {
        fun onGoCronSelected()

    }
    fun setList(items:List<ContactDto>) {
        this.items = items
    }
    fun updateList(newContacts: List<ContactDto>) {
        this.items = newContacts
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: CronBookAdapter.ViewHolder, position: Int) {
        holder.name.text = items[position].name
        holder.bind()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CronBookAdapter.ViewHolder {
         return ViewHolder(
             LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item, parent,false)
         )
    }
    override fun getItemCount()=items.size
     inner class ViewHolder(v: View):RecyclerView.ViewHolder(v){
            val name=v.findViewById(R.id.tv_name) as TextView
            val radioButton: RadioButton =v.findViewById(R.id.radio_button_check)
    init{
        radioButton.setOnClickListener{
//            onItemSelectedListener?.onItemSelected()
            selectedItemPosition=adapterPosition
            notifyDataSetChanged()
            goCron.visibility=View.VISIBLE
            goCron.setOnClickListener(){
           selectedItem=items[selectedItemPosition]
            val i = Intent(context, CronTabActivity::class.java)
            i.putExtra("name", selectedItem!!.name)
            i.putExtra("number", selectedItem!!.number)
            sharedPreferences.edit().putBoolean("selected",true).apply()

//                context.finish()
                context.startActivity(i)

                onItemSelectedListener?.onGoCronSelected()

            }
        }
    }
    fun bind(){
        radioButton.isChecked=adapterPosition==selectedItemPosition
    }
    }

}
