package com.example.e_commerce_penjualan_daging.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.e_commerce_penjualan_daging.R
import com.example.e_commerce_penjualan_daging.app.ApiConfig
import com.example.e_commerce_penjualan_daging.model.Checkout
import com.example.e_commerce_penjualan_daging.model.ResponModel
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PembayaranActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)

        val btn_bca = findViewById<TextView>(R.id.btn_bca)
        val btn_bri = findViewById<TextView>(R.id.btn_bri)
        val btn_mandiri = findViewById<TextView>(R.id.btn_mandiri)

        btn_bca.setOnClickListener{
            bayar("bca")
        }
        btn_bri.setOnClickListener{
         bayar("bri")
        }
        btn_mandiri.setOnClickListener{
            bayar("mandiri")
        }
    }

    fun bayar(bank: String){

        val json = intent.getStringExtra("extra")!!.toString()
        val checkout = Gson().fromJson(json, Checkout::class.java)
        checkout.bank = bank

        ApiConfig.instanceRetrofit.checkout(checkout).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
 //               Toast.makeText(this@PengirimanActivity, "Error:" + t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val respon = response.body()!!
                if (respon.success == 1) {
                    Toast.makeText(this@PembayaranActivity, "Data Produk Berhasil Dikirim Ke Server", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@PembayaranActivity,  "Error"+respon.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}