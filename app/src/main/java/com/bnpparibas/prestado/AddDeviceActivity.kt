package com.bnpparibas.prestado

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_device.*
import kotlinx.android.synthetic.main.include_add_device_name.*
import kotlinx.android.synthetic.main.include_add_device_os.*
import kotlinx.android.synthetic.main.include_add_device_recap.*
import kotlinx.android.synthetic.main.include_add_device_recap_card.*

class AddDeviceActivity : AppCompatActivity(), AddPhoneView {

    private val presenter: AddPhonePresenter by lazy {
        AddPhonePresenter(AddPhoneRepository(), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)
        nameDeviceEditText.setOnEditorActionListener { textView, i, keyEvent ->
            nameDeviceRecapTextView.text = textView.text.toString()
            addDeviceViewFlipper.displayedChild = 1
            true
        }
        osEditText.setOnEditorActionListener { textView, i, keyEvent ->
            osRecapTextView.text = textView.text.toString()
            addDeviceViewFlipper.displayedChild = 2
            true
        }
        confirmDeviceButton.setOnClickListener {
            presenter.addDevice(nameDeviceRecapTextView.text.toString(),
                osRecapTextView.text.toString())
        }
    }

    override fun displaySuccess() {
        finish()
    }
}

class AddPhonePresenter(
    private val repository: AddPhoneRepository,
    private val view: AddPhoneView
) {
    fun addDevice(name: String, os: String) {
        repository.addPhone(name, os, ::displaySuccess)
    }

    private fun displaySuccess() {
        view.displaySuccess()
    }
}

interface AddPhoneView {
    fun displaySuccess()
}

class AddPhoneRepository {

    private val strore = FirebaseFirestore.getInstance()

    fun addPhone(name: String, os: String, callback: () -> Unit) {
        val collection = strore.collection("devices")
        val phone = mapOf(
            "name" to name,
            "os" to os,
            "free" to "true"
        )
        collection.document(name)
            .set(phone)
            .addOnCompleteListener { callback() }
            .addOnFailureListener { it.printStackTrace() }
    }

}
