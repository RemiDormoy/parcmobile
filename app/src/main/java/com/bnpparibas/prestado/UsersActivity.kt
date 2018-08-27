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
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.reflect.KFunction1

class UsersActivity : AppCompatActivity(), UsersView {

    private val recyclerView: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.usersRecyclerView)
    }

    private val presenter: UsersPresenter by lazy {
        UsersPresenter(UsersRepository(), this)
    }

    private val adapter = UsersAdpter()

    companion object {

        fun newIntent(context: Context) = Intent(context, UsersActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        presenter.load()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun displayUsers(users: List<Users>) {
        adapter.updateList(users)
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
        return Users(id, data.get("name") as String, data.get("roles.id") as String)
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

data class Users(
        val id: String,
        val name: String,
        val level: String
)

class UsersAdpter : RecyclerView.Adapter<UsersViewHolder>() {

    private var list = listOf<Users>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_user_item, parent)
        return UsersViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.textView2).text = list[position].name
        holder.itemView.findViewById<TextView>(R.id.textView3).text = list[position].level
    }

    fun updateList(list: List<Users>) {
        this.list = list
        notifyDataSetChanged()
    }
}

class UsersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)