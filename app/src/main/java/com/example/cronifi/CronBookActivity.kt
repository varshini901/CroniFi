package com.example.cronifi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CronBookActivity: AppCompatActivity() ,CronBookAdapter.OnItemSelectedListener{
    val contactList: MutableList<ContactDto> = ArrayList()
    lateinit var goCron : LinearLayout
    lateinit var adapter :CronBookAdapter
    lateinit var search : SearchView
    lateinit var sharedPreferences : SharedPreferences
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cron_book)
        sharedPreferences =getSharedPreferences("CroniFi",Context.MODE_PRIVATE)

//        val sharedPrefs = getSharedPreferences("CroniFi", Context.MODE_PRIVATE)
        val contactReyclerView = findViewById<RecyclerView>(R.id.contact_recycler_view)
        search =findViewById(R.id.search_bar)
        val backButton=findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener{
            finish()
        }
        goCron =findViewById(R.id.go_cron)
        contactReyclerView.layoutManager=LinearLayoutManager(this)

        adapter = CronBookAdapter(this,sharedPreferences,goCron)
        adapter.onItemSelectedListener=this
        adapter.setList(contactList)
        contactReyclerView.adapter=adapter
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                220
            )
        } else showContacts()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 220) {
            if ((grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("NIK","G")
                showContacts()
                // Permission is granted. Continue the action or workflow
                // in your app.
            } else {
                Log.d("NIK","f")
                Toast.makeText(this,"Please allow permissions!",Toast.LENGTH_SHORT).show()
                // Explain to the user that the feature is unavailable because
                // the feature requires a permission that the user has denied.
                // At the same time, respect the user's decision. Don't link to
                // system settings in an effort to convince the user to change
                // their decision.
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    @SuppressLint("Range")
    fun showContacts() {
        val contentResolver=contentResolver
        val uri=ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val cursor=contentResolver.query(uri,null,null,null,null)
        if (cursor != null) {
            if(cursor.count>0){
                while(cursor.moveToNext()){
                    val contactname =cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val contactnumber:String=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val obj = ContactDto()
                    obj.name = contactname
                    obj.number=contactnumber
                    contactList.add(obj)

                }
            }

        }
        adapter.setList(contactList)
        adapter.notifyDataSetChanged()


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
    }
    override fun onGoCronSelected() {
        finish()
    }

}