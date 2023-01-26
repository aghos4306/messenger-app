package com.aghogho.messengerappmuzz.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghogho.messengerappmuzz.R
import com.aghogho.messengerappmuzz.adapterclass.UserAdapter
import com.aghogho.messengerappmuzz.modelclass.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var recyclerView: RecyclerView? = null
    private var searchUsersEditText: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        searchUsersEditText = view.findViewById(R.id.searchUsersET)

        mUsers = ArrayList()
        retrieveAllUsers()

        searchUsersEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Before Text Changed", "Before Users")
            }

            override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(cs.toString().lowercase(Locale.ROOT))
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("After text Changed", "After Users")
            }

        })

        return view
    }

    private fun retrieveAllUsers() {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        //The reference is up to the Users node to get all the users from fb db
        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshots: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                if (searchUsersEditText!!.text.toString() == "") {
                    //get all users from Users node
                    for (snapshot in snapshots.children) {
                        val user: Users? = snapshot.getValue(Users::class.java)
                        //check so that you only retrieve users other than yourself
                        if (!(user!!.getUID()).equals(firebaseUserID)) {
                            (mUsers as ArrayList<Users>).add(user)
                        }
                    }
                    userAdapter = UserAdapter(context!!, mUsers!!, false)
                    recyclerView!!.adapter = userAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun searchForUsers(str: String) {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser
        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshots: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snapshot in snapshots.children) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (!(user!!.getUID())!!.equals(firebaseUserID)) {
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!, mUsers!!, false)
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

}
