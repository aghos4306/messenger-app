package com.aghogho.messengerappmuzz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.aghogho.messengerappmuzz.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refDatabaseUsers: DatabaseReference
    private var firebaseUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (this::binding.isInitialized) {
            setSupportActionBar(binding.toolbarRegister)
//        }
        supportActionBar?.title = "Register"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarRegister.setNavigationOnClickListener {
            val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()
        binding.registerBtn.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        Log.d("Register", "User Registered")
        val userName: String = binding.usernameRegister.text.toString()
        val email: String = binding.emailRegister.text.toString()
        val password: String = binding.passwordRegister.text.toString()

        if (userName == "") {
            Toast.makeText(this@RegisterActivity, "Pls Specify Username", Toast.LENGTH_LONG).show()
        }
        else if (email == "") {
            Toast.makeText(this@RegisterActivity, "Pls Specify Email", Toast.LENGTH_LONG).show()
        }
        else if (password == "") {
            Toast.makeText(this@RegisterActivity, "Pls Specify Password", Toast.LENGTH_LONG).show()
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseUserId = mAuth.currentUser!!.uid
                    refDatabaseUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserId)

                    val userHashMap = HashMap<String, Any>()
                    userHashMap["uid"] = firebaseUserId
                    userHashMap["userName"] = userName
                    userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/messenger-app-muzz.appspot.com/o/default_profile.png?alt=media&token=391d38c3-d4ff-433b-b2fb-a76cbd4d5faf"
                    userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/messenger-app-muzz.appspot.com/o/default_cover.jpeg?alt=media&token=00fc90f3-3c57-4fe4-8707-03269d711cf1"
                    userHashMap["status"] = "offline"
                    userHashMap["search"] = userName.lowercase(Locale.ROOT)
                    userHashMap["facebook"] = "https://m.facebook.com"
                    userHashMap["instagram"] = "https://m.instagram.com"
                    userHashMap["website"] = "https://m.google.com"

                    refDatabaseUsers.updateChildren(userHashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }

                } else {
                    Toast.makeText(this@RegisterActivity, "Couldnt create user at this time: " + task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
