package com.bnpparibas.prestado

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_users.*

class UsersActivity : AppCompatActivity(), UsersView {

    private val recyclerView: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.usersRecyclerView)
    }

    private val presenter: UsersPresenter by lazy {
        UsersPresenter(UsersRepository(), this)
    }

    private val adapter = UsersAdpter(::addUser)

    companion object {

        fun newIntent(context: Context) = Intent(context, UsersActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        setTitle("Liste des users")
        addUserButton.setOnClickListener { addUser() }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                //R.id.action_settings -> startActivity(UsersActivity.newIntent(this))
                R.id.action_navigation -> startActivity(DevicesActivity.newIntent(this))
                R.id.action_scann -> onBackPressed()
            }
            false
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.load()
    }

    override fun displayUsers(users: List<Users>) {
        adapter.updateList(users)
    }

    private fun addUser() {
        startActivity(Intent(this, AddUserActivity::class.java))
    }
}

class UsersPresenter(
        private val repository: UsersRepository,
        private val view: UsersView
) {
    fun load() {
        repository.getUsers(::displayUsers)
    }

    private fun displayUsers(users: List<Users>) {
        view.displayUsers(users)
    }
}

interface UsersView {
    fun displayUsers(users: List<Users>)
}

class UsersRepository {

    private val strore = FirebaseFirestore.getInstance()

    private fun transformToUser(data: Map<String, Any>, id: String): Users {
        val level = data.get("roles.id") as Long?
        return Users(id, data.get("name") as String, level.toLabel())
    }

    fun getUsers(callback: (List<Users>) -> Unit) {
        val collection = strore.collection("users")
        collection.get()
                .addOnSuccessListener {
                    callback(it.map {
                        transformToUser(it.data, it.id)
                    })
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
    }

}

private fun Long?.toLabel(): String {
    this?.toInt()?.let {
        return when (it) {
            1 -> "Administrateur"
            2 -> "Gestionnaire"
            else -> "Emprunteur"
        }
    }
    return "Emprunteur"
}

data class Users(
        val id: String,
        val name: String,
        val level: String
)

class UsersAdpter(private val addUserClick: () -> Unit) : RecyclerView.Adapter<UsersViewHolder>() {

    private var list = listOf<Users>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        if (viewType == 12) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.button_add_user, parent, false)
            return UsersViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_user_item, parent, false)
        return UsersViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == list.size) return 12
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        if (position == list.size) {
            holder.itemView.setOnClickListener { addUserClick() }
        } else {
            holder.itemView.findViewById<TextView>(R.id.textView2).text = list[position].name
            holder.itemView.findViewById<TextView>(R.id.textView3).text = list[position].level
        }
    }

    fun updateList(list: List<Users>) {
        this.list = list
        notifyDataSetChanged()
    }
}

class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)