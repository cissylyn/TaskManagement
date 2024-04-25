package com.example.taskmanagement

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class WelcomeActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val welcomeText = findViewById<TextView>(R.id.welcome_text)
        welcomeText.text = "Your Welcome Message Here"

        // Delay for 3 seconds and then start MainActivity
        Handler().postDelayed({
            val intent = Intent(
                this@WelcomeActivity,
                MainActivity::class.java
            )
            startActivity(intent)
            finish()
        }, 3000) // 3 seconds delay
    }
}

