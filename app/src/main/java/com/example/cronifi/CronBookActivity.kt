package com.example.cronifi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CronBookActivity: AppCompatActivity() ,CronBookAdapter.OnItemSelectedListener{
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contactList: MutableList<ContactDto> = ArrayList()
        setContentView(R.layout.activity_cron_book)
        val sharedPreferences=getSharedPreferences("CroniFi",Context.MODE_PRIVATE)

//        val sharedPrefs = getSharedPreferences("CroniFi", Context.MODE_PRIVATE)
        val contactReyclerView = findViewById<RecyclerView>(R.id.contact_recycler_view)
        val search=findViewById<SearchView>(R.id.search_bar)
        val backButton=findViewById<ImageView>(R.id.back_button)
        val goCron:LinearLayout=findViewById(R.id.go_cron)
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_CONTACTS),0)
            return
        }
        val contentResolver=contentResolver
        val uri=ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val cursor=contentResolver.query(uri,null,null,null,null)
        if (cursor != null) {
            if(cursor.count>0){
                while(cursor.moveToNext()){
                    val contactname:String=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val contactnumber:String=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val obj = ContactDto()
                    obj.name = contactname
                    obj.number=contactnumber
                    contactList.add(obj)

                }
            }

        }
        contactReyclerView.layoutManager=LinearLayoutManager(this)

        val adapter = CronBookAdapter(this,sharedPreferences,goCron)
        adapter.onItemSelectedListener=this
        adapter.setList(contactList)
        contactReyclerView.adapter=adapter
        cursor?.close()
        search.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
               return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                val filteredContacts = ArrayList<ContactDto>()
                p0?.let {
                    for (contact in contactList) {
                        if (contact.name.contains(it, true)) {
                            filteredContacts.add(contact)
                        }
                    }
                }
                adapter.updateList(filteredContacts)
                return true
            }
        })
        goCron.setOnClickListener(){
            adapter.selectedItem?.let{
                selectedItem ->
                val i = Intent(this, CronTabActivity::class.java)
                i.putExtra("name",selectedItem.name)
                i.putExtra("number",selectedItem.number)
                sharedPreferences.edit().putBoolean("selected",true).apply()
                startActivity(i)
            }
            finish()

        }
        backButton.setOnClickListener(){
            finish()
        }
    }



    override fun onGoCronSelected() {
        finish()
    }

}