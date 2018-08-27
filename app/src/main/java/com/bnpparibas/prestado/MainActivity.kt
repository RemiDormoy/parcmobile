package com.bnpparibas.prestado

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView


class MainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler, MainView {

    private lateinit var scannerView: ZXingScannerView
    private val presenter: MainPresenter by lazy {
        MainPresenter(this, PhoneRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scannerView = ZXingScannerView(this)
        val container = findViewById<FrameLayout>(R.id.scanViewContainer)
        container.removeAllViews()
        container.addView(scannerView)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_settings -> startActivity(UsersActivity.newIntent(this))
                R.id.action_navigation -> startActivity(DevicesActivity.newIntent(this))
            }
            true
        }
    }

    public override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this) // Register ourselves as a handler for scan results.
        scannerView.startCamera()
        presenter.init()// Start camera on resume
    }

    public override fun onPause() {
        scannerView.stopCamera()           // Stop camera on pause
        super.onPause()
    }

    override fun handleResult(p0: Result?) {
        p0?.let {
            presenter.changePhoneStatus(it.text)
        }
    }

    override fun displayTelNotFound() {
        AlertDialog.Builder(this)
            .setTitle("Désolé")
            .setMessage("Nous ne trouvons pas de téléphone associé à ce code barre")
            .setPositiveButton("Fermer") { dialogInterface, _ ->
                dialogInterface.dismiss()
                scannerView.resumeCameraPreview(this)
            }
            .show()
    }

    override fun displayGiveBackPop(id: String, name: String) {
        AlertDialog.Builder(this)
            .setTitle("Téléphone emprunté")
            .setMessage("Voulez vous rendre ce téléphone : $name")
            .setPositiveButton("Valider") { dialogInterface, _ ->
                presenter.giveBack(id)
                dialogInterface.dismiss()
                scannerView.resumeCameraPreview(this)
            }
            .setNegativeButton("Annuler") { dialogInterface, _ ->
                dialogInterface.dismiss()
                scannerView.resumeCameraPreview(this)
            }
            .show()
    }

    override fun displayBorrow(id: String, name: String) {
        AlertDialog.Builder(this)
            .setTitle("Téléphone libre")
            .setMessage("Voulez vous emprunter ce téléphone : $name")
            .setPositiveButton("Valider") { dialogInterface, _ ->
                presenter.borrowPhone(id)
                dialogInterface.dismiss()
                scannerView.resumeCameraPreview(this)
            }
            .setNegativeButton("Annuler") { dialogInterface, _ ->
                dialogInterface.dismiss()
                scannerView.resumeCameraPreview(this)
            }
            .show()
    }
}

class MainPresenter(
    private val view: MainView,
    private val phoneRepository: PhoneRepository) {

    fun changePhoneStatus(id: String) {
        try {
            val phone = phoneRepository.getPhones().first { it.id == id }
            if (phone.isBorrowed) {
                view.displayGiveBackPop(phone.id, phone.name)
            } else {
                view.displayBorrow(phone.id, phone.name)
            }
        } catch (e: NoSuchElementException) {
            view.displayTelNotFound()
        }
    }

    fun giveBack(id: String) {

    }

    fun borrowPhone(id: String) {

    }

    fun init() {
    }
}

interface MainView {
    fun displayTelNotFound()
    fun displayGiveBackPop(id: String, name: String)
    fun displayBorrow(id: String, name: String)
}

class PhoneRepository {

    private val strore = FirebaseFirestore.getInstance()
    private lateinit var values: List<Phone>

    init {
        val collection = strore.collection("devices")
        collection.get()
            .addOnSuccessListener {
                values = it.map {
                    transformToPhone(it.data, it.id)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun transformToPhone(data: Map<String, Any>, id: String): Phone {
        return Phone(id, data.get("free") as Boolean, data.get("device") as String, data.get("os") as String)
    }

    fun getPhones(): List<Phone> {
        return values
    }

}

data class Phone(
    val id: String,
    val isBorrowed: Boolean,
        val name: String,
        val os: String
)
