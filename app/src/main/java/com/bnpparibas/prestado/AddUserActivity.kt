package com.bnpparibas.prestado

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_user.*
import kotlinx.android.synthetic.main.include_add_level.*
import kotlinx.android.synthetic.main.include_add_name.*
import kotlinx.android.synthetic.main.include_add_user_recap.*
import kotlinx.android.synthetic.main.include_add_user_recap_card.*
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager




class AddUserActivity : AppCompatActivity(), AddUserView {
    override fun displaySuccess() {
        finish()
    }

    private val presenter: AddUserPresenter by lazy {
        AddUserPresenter(this, AddUserRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
        nameEditText.setOnEditorActionListener { textView, i, keyEvent ->
            nameRecapTextView.text = textView.text.toString()
            addUserViewFlipper.displayedChild = 1
            closeKeyboard()
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

    private fun closeKeyboard() {
        val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = this.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

class AddUserPresenter(private val view: AddUserView, private val repository: AddUserRepository) {
    fun addUser(name: String, level: String) {
        when (level) {
            "Administrateur" -> repository.addUser(name, 1, ::displaySuccess)
            "Gestionnaire" -> repository.addUser(name, 2, ::displaySuccess)
            else -> repository.addUser(name, 3, ::displaySuccess)
        }
    }

    private fun displaySuccess() {
        view.displaySuccess()
    }
}

interface AddUserView {
    fun displaySuccess()
}

class AddUserRepository {
    private val strore = FirebaseFirestore.getInstance()

    fun addUser(name: String, level: Int, callback: () -> Unit) {
        val collection = strore.collection("users")
        val user = mapOf(
                "roles.id" to level,
                "name" to name
        )
        collection.document(name)
                .set(user)
                .addOnCompleteListener { callback() }
                .addOnFailureListener { it.printStackTrace() }
    }
}