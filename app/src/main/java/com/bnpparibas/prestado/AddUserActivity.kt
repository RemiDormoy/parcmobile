package com.bnpparibas.prestado

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_user.*
import kotlinx.android.synthetic.main.include_add_level.*
import kotlinx.android.synthetic.main.include_add_name.*
import kotlinx.android.synthetic.main.include_add_user_recap.*
import kotlinx.android.synthetic.main.include_add_user_recap_card.*

class AddUserActivity : AppCompatActivity() {

    private val presenter: AddUserPresenter by lazy {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
        nameEditText.setOnEditorActionListener { textView, i, keyEvent ->
            nameRecapTextView.text = textView.text.toString()
            addUserViewFlipper.displayedChild = 1
            true
        }
        button1.setOnClickListener {
            levelRecapTextView.text = "Administrateur"
            addUserViewFlipper.displayedChild = 2
        }
        button2.setOnClickListener {
            levelRecapTextView.text = "Gestionnaire"
            addUserViewFlipper.displayedChild = 2
        }
        button3.setOnClickListener {
            levelRecapTextView.text = "Emprunteur"
            addUserViewFlipper.displayedChild = 2
        }
        confirmButton.setOnClickListener {
            presenter.addUser(nameRecapTextView.text.toString(), levelRecapTextView.text.toString())
        }

    }
}

class AddUserPresenter(private val view: AddUserView) {

}

interface AddUserView

class AddUserRepository