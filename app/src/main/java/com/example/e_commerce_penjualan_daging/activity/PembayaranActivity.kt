package com.example.e_commerce_penjualan_daging.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_penjualan_daging.R
import com.example.e_commerce_penjualan_daging.adapter.AdapterBank
import com.example.e_commerce_penjualan_daging.app.ApiConfig
import com.example.e_commerce_penjualan_daging.helper.Helper
import com.example.e_commerce_penjualan_daging.model.Bank
import com.example.e_commerce_penjualan_daging.model.Checkout
import com.example.e_commerce_penjualan_daging.model.ResponModel
import com.example.e_commerce_penjualan_daging.model.Transaksi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PembayaranActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        Helper().setToolbar(this,toolbar, "Pembayaran")

       displayBank()
    }

    fun displayBank(){
        val arrBank = ArrayList<Bank>()
        arrBank.add(Bank("BCA", "092093871237", "Reza", R.drawable.logo_bca))
        arrBank.add(Bank("BRI", "86721349128", "Reza", R.drawable.logo_bri))
        arrBank.add(Bank("Mandiri", "02394870329", "Reza", R.drawable.logo_madiri))
        arrBank.add(Bank("Dana", "082384897258", "Riando", R.drawable.logo_dana))
        arrBank.add(Bank("OVO", "082384897258", "Riando", R.drawable.logo_ovo))

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        val rv_data = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_data)
        rv_data.layoutManager = layoutManager
        rv_data.adapter = AdapterBank(arrBank, object : AdapterBank.Listeners {
            override fun onClicked(data: Bank, index: Int) {
                bayar(data)
            }
        })
    }

    fun bayar(bank: Bank){

        val json = intent.getStringExtra("extra")!!.toString()
        val checkout = Gson().fromJson(json, Checkout::class.java)
        checkout.bank = bank.nama

        ApiConfig.instanceRetrofit.checkout(checkout).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
 //               Toast.makeText(this@PengirimanActivity, "Error:" + t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val respon = response.body()!!
                if (respon.success == 1) {

                    val jsBank = Gson().toJson(bank, Bank::class.java)
                    val jsTransaksi = Gson().toJson(respon.transaksi, Transaksi::class.java)

                    val intent = Intent(this@PembayaranActivity, SuccesActivity::class.java)
                    intent.putExtra("bank",jsBank)
                    intent.putExtra("transaksi",jsTransaksi)
                    startActivity(intent)

                } else {
                    Toast.makeText(this@PembayaranActivity,  "Error"+respon.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}