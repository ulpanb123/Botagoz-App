package com.example.visionin.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.visionin.R

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        supportActionBar?.hide()
    }
}