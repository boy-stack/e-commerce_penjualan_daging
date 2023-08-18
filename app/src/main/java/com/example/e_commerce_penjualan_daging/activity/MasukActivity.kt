package com.example.e_commerce_penjualan_daging.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.e_commerce_penjualan_daging.R
import com.example.e_commerce_penjualan_daging.helper.SharedPref


class MasukActivity : AppCompatActivity() {

    lateinit var s:SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masuk)

         s = SharedPref(this)

        val btnprosesLogin = findViewById<Button>(R.id.btn_prosesLogin)
        btnprosesLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))

        }


        val btn_register = findViewById<Button>(R.id.btn_register)
        btn_register.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

}

