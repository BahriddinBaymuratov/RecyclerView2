package com.example.recycleviewhome

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recycleviewhome.adapter.ContactAdapter
import com.example.recycleviewhome.model.contact.Contact
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    companion object {
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    private val contactList: MutableList<Contact> = ArrayList()
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnget: Button
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var btnGet: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        btnget = findViewById(R.id.btnGet)
        shimmerFrameLayout = findViewById(R.id.shimmerLayout)
        setupRv()
        requestPermission()

        contactAdapter.onClick = {
            showBottomSheet(it)
        }
    }

    private fun setupRv() = recyclerView.apply {
        contactAdapter = ContactAdapter(this@MainActivity, contactList)
        layoutManager = LinearLayoutManager(this@MainActivity)
        adapter = contactAdapter
    }

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
        } else {
            btnget.isVisible = true
            readContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            btnget.isVisible = true
            readContacts()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun readContacts() {
        btnget.setOnClickListener {
            shimmerFrameLayout.startShimmer()
            btnget.isVisible = false
            recyclerView.isVisible = false
            shimmerFrameLayout.isVisible = true

            Handler(Looper.myLooper()!!).postDelayed({
                recyclerView.isVisible = true
                shimmerFrameLayout.isVisible = false
                shimmerFrameLayout.stopShimmer()
            }, 3000)

            val contacts = contentResolver?.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            while (contacts!!.moveToNext()) {
                val name =
                    contacts.getString(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    contacts.getString(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactList.add(Contact(name, number))
                contactAdapter.notifyDataSetChanged()
            }
            contacts.close()

        }
    }

    private fun showBottomSheet(contact: Contact) {
        val view: View = layoutInflater.inflate(R.layout.person_layout, null)
        val name: TextView = view.findViewById(R.id.Name)
        val number: TextView = view.findViewById(R.id.number)
        val frameLayout: FrameLayout = view.findViewById(R.id.frameLayout)
        val btnCall: ImageView = view.findViewById(R.id.call)
        val btnMessage: ImageView = view.findViewById(R.id.sms)
        val bottomSheet = BottomSheetDialog(this)
        bottomSheet.setContentView(view)
        name.text = contact.name
        number.text = contact.number
        val color = randomColor()
        frameLayout.setBackgroundColor(ContextCompat.getColor(this, color))
        btnCall.setColorFilter(ContextCompat.getColor(this, color))
        btnMessage.setColorFilter(ContextCompat.getColor(this, color))
        bottomSheet.show()

        btnMessage.setOnClickListener {
            val intent = Intent(ACTION_VIEW, Uri.parse("sms:"))
            startActivity(intent)
        }
        btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${contact.number}"))
            startActivity(intent)
        }
    }

    private fun randomColor(): Int {
        val colorList = ArrayList<Int>()
        val random = Random()
        colorList.add(R.color.blue)
        colorList.add(R.color.gray)
        colorList.add(R.color.green)
        colorList.add(R.color.dark)
        colorList.add(R.color.red)
        return colorList[random.nextInt(colorList.size)]
    }

}

