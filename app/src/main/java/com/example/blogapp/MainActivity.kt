package com.example.blogapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        init()
    }

    private fun init() {
        val isLogged = this.applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
            .getBoolean("isLoggedIn", false)
        if (isLogged) {
            startActivity(Intent(this@MainActivity, AppActivity::class.java))
            finish()
        }

    }

}