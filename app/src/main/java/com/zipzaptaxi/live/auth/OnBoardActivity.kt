package com.zipzaptaxi.live.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zipzaptaxi.live.databinding.ActivityOnBoardBinding

class OnBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnClicks()
    }

    private fun setOnClicks() {
        binding.btnSignUp.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java)
            intent.putExtra("loginfor","signup")
            startActivity(intent)

        }
        binding.btnSignIn.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }

}

