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
import kotlinx.android.synthetic.main.activity_devices.*

class DevicesActivity : AppCompatActivity(), PhonesView {

    private val presenter: PhonePresenter by lazy {
        PhonePresenter(PhoneRepository(), this)
    }

    private val recyclerView: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.phonesRecyclerView)
    }

    private val adapter = PhonesAdapter(::addPhoneClick)

    companion object {
        fun newIntent(context: Context) = Intent(context, DevicesActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        setTitle("Liste des devices")
        addDeviceButton.setOnClickListener { addPhoneClick() }
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_settings -> startActivity(UsersActivity.newIntent(this))
                //R.id.action_navigation -> startActivity(DevicesActivity.newIntent(this))
                R.id.action_scann -> onBackPressed()
            }
            false
        }
        presenter.load()
    }

    override fun displayPhones(phones: List<Phone>) {
        adapter.updateList(phones)
    }

    private fun addPhoneClick(){
        startActivity(Intent(this, AddDeviceActivity::class.java))
    }
}

class PhonePresenter(
    private val phoneRepository: PhoneRepository,
    private val view: PhonesView
) {
    fun load() {
        phoneRepository.getPhones(::displayPhones)
    }

    private fun displayPhones(phones: List<Phone>) {
        view.displayPhones(phones)

    }
}

interface PhonesView {
    fun displayPhones(phones: List<Phone>)
}

class PhonesAdapter(private val addPhoneClick: () -> Unit) : RecyclerView.Adapter<PhonesViewHolder>() {

    private var list = listOf<Phone>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhonesViewHolder {
        if (viewType == 25) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.button_add_device,
                parent,
                false)
            return PhonesViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_device, parent, false)
        return PhonesViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == list.size) return 25
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PhonesViewHolder, position: Int) {
        if (position == list.size) {
            holder.itemView.setOnClickListener { addPhoneClick() }
        } else {
            holder.itemView.findViewById<TextView>(R.id.deviceName).text = list[position].name
            holder.itemView.findViewById<TextView>(R.id.deviceOs).text = list[position].os
            holder.itemView.findViewById<TextView>(R.id.statusTextView).text = if (list[position].isBorrowed) "Déjà emprunté" else "libre"
        }
    }

    fun updateList(list: List<Phone>) {
        this.list = list
        notifyDataSetChanged()
    }
}

class PhonesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)