package com.aghogho.messengerappmuzz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aghogho.messengerappmuzz.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (this::binding.isInitialized) {
            setSupportActionBar(binding.toolbarLogin)
        }
        supportActionBar?.title = "Login"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarLogin.setNavigationOnClickListener {
            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
