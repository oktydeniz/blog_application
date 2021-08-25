package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.blogapp.databinding.ActivityAppActiviytBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class AppActivity : AppCompatActivity() {
    private lateinit var navBottomView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_activiyt)
        init()
    }

    private fun init() {
        navBottomView = findViewById(R.id.bottom_nav)
        val navCont = Navigation.findNavController(this@AppActivity, R.id.fragmentContainerView2)
        NavigationUI.setupWithNavController(navBottomView, navCont)
    }
}