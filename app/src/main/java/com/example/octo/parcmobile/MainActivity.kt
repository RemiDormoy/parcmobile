package com.example.octo.parcmobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class MainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var scannerView: ZXingScannerView

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
            Toast.makeText(this, "bim : ${it.text}", Toast.LENGTH_LONG).show()
        }
        scannerView.resumeCameraPreview(this)
    }
}
