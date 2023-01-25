package com.aghogho.messengerappmuzz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.aghogho.messengerappmuzz.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (this::binding.isInitialized) {
            setSupportActionBar(binding.toolbarLogin)
//        }
        supportActionBar?.title = "Login"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarLogin.setNavigationOnClickListener {
            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()
        binding.loginBtn.setOnClickListener {
            loginUser()
        }

    }

    private fun loginUser() {
        Log.d("Login", "User Logged In")
        val email: String = binding.emailLogin.text.toString()
        val password: String = binding.passwordLogin.text.toString()

        if (email == "") {
            Toast.makeText(this@LoginActivity, "Pls Specify email", Toast.LENGTH_LONG).show()
        }
        else if (password == "") {
            Toast.makeText(this@LoginActivity, "Pls Specify Password", Toast.LENGTH_LONG).show()
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Login Failed " + task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
