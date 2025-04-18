package com.example.vitaran

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ReturnThis
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        val splashLogo = findViewById<ImageView>(R.id.SplashLogo)
        val sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        val isLoggedin = sharedPreferences.getBoolean("isLoggedIn", false)

        splashLogo.alpha = 0f
        splashLogo.animate().setDuration(1500).alpha(1f).withEndAction {
            if (isLoggedin){
                startActivity(Intent(this, HomescreenActivity::class.java))
                finish()
            }
            else{
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }


    }
}