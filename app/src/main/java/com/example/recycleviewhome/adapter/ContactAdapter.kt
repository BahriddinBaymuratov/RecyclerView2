package com.example.recycleviewhome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.recycleviewhome.R
import com.example.recycleviewhome.model.contact.Contact
import java.util.*
import kotlin.collections.ArrayList

class ContactAdapter(
     private val context: Context,
    private val contacts: List<Contact>
): RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    lateinit var onClick: (Contact) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent,false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    inner class ContactViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val textName: AppCompatTextView = view.findViewById(R.id.textName)
        private val view: View = view.findViewById(R.id.imageView)
        fun bind(contact: Contact){
            textName.text = contact.name
            view.setBackgroundColor(ContextCompat.getColor(context, randomColor()))

            itemView.setOnClickListener {
                onClick.invoke(contact)
            }
        }
        private fun randomColor(): Int{
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
}
