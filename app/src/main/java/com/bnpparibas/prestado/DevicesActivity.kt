package com.bnpparibas.prestado

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DevicesActivity : AppCompatActivity(), PhonesView {

    private val presenter: PhonePresenter by lazy {
        PhonePresenter(PhoneRepository(), this)
    }

    private val recyclerView: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.phonesRecyclerView)
    }

    private val adapter = PhonesAdapter()

    companion object {
        fun newIntent(context: Context) = Intent(context, DevicesActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)
        presenter.load()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun displayPhones(phones: List<Phone>) {
        adapter.updateList(phones)
    }
}

class PhonePresenter(
    private val phoneRepository: PhoneRepository,
    private val view: PhonesView
) {
    fun load() {
        val phones = phoneRepository.getPhones(::displayPhones)
    }

    private fun displayPhones(phones: List<Phone>) {
        view.displayPhones(phones)

    }
}

interface PhonesView {
    fun displayPhones(phones: List<Phone>)
}

class PhonesAdapter : RecyclerView.Adapter<PhonesViewHolder>() {

    private var list = listOf<Phone>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhonesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_device, parent)
        return PhonesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PhonesViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.deviceName).text = list[position].name
        holder.itemView.findViewById<TextView>(R.id.deviceOs).text = list[position].os
    }

    fun updateList(list: List<Phone>) {
        this.list = list
        notifyDataSetChanged()
    }
}

class PhonesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)