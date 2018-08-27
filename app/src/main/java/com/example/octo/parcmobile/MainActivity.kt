package com.example.octo.parcmobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    }

    public override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this) // Register ourselves as a handler for scan results.
        scannerView.startCamera()          // Start camera on resume
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
                }
                .show()
        scannerView.resumeCameraPreview(this)
    }

    override fun displayGiveBackPop(id: String, name: String) {
        AlertDialog.Builder(this)
                .setTitle("Téléphone emprunté")
                .setMessage("Voulez vous rendre ce téléphone : $name")
                .setPositiveButton("Valider") { dialogInterface, _ ->
                    presenter.giveBack(id)
                    dialogInterface.dismiss()
                }
                .setNegativeButton("Annuler") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .show()
        scannerView.resumeCameraPreview(this)
    }

    override fun displayBorrow(id: String, name: String) {
        AlertDialog.Builder(this)
                .setTitle("Téléphone libre")
                .setMessage("Voulez vous emprunter ce téléphone : $name")
                .setPositiveButton("Valider") { dialogInterface, _ ->
                    presenter.borrowPhone(id)
                    dialogInterface.dismiss()
                }
                .setNegativeButton("Annuler") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .show()
        scannerView.resumeCameraPreview(this)
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun borrowPhone(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

interface MainView {
    fun displayTelNotFound()
    fun displayGiveBackPop(id: String, name: String)
    fun displayBorrow(id: String, name: String)
}

class PhoneRepository {
    fun getPhones() = listOf(
            Phone("vçozbuberiv", false, "tel de Rémi"),
            Phone("qlzneofbqzbe", true, "tel de Marta"),
            Phone("ahahrephau", false, "tel de Amael")
    )
}

data class Phone(
        val id: String,
        val isBorrowed: Boolean,
        val name: String
)
