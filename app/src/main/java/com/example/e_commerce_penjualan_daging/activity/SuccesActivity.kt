package com.example.e_commerce_penjualan_daging.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.e_commerce_penjualan_daging.R
import com.example.e_commerce_penjualan_daging.helper.Helper
import com.example.e_commerce_penjualan_daging.model.Bank
import com.example.e_commerce_penjualan_daging.model.Transaksi
import com.google.gson.Gson

class SuccesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        Helper().setToolbar(this,toolbar, "Pembayaran")

      setValues()
    }

    fun setValues(){
    val jsBank = intent.getStringExtra("bank")
    val jsTransaksi = intent.getStringExtra("transaksi")

        val bank = Gson().fromJson(jsBank, Bank::class.java)
        val transaksi =  Gson().fromJson(jsTransaksi, Transaksi::class.java)

        val tv_nomorRekening = findViewById<TextView>(R.id.tv_nomorRekening)
        val tv_namaPenerima = findViewById<TextView>(R.id.tv_namaPenerima)
        val image_bank = findViewById<ImageView>(R.id.image_bank)
        val tv_nominal = findViewById<TextView>(R.id.tv_nominal)

        tv_nomorRekening.text = bank.rekening
        tv_namaPenerima.text = bank.penerima
        image_bank.setImageResource(bank.image)

        tv_nominal.text = Helper().gantiRupiah(Integer.valueOf(transaksi.total_transfer) + transaksi.kode_unik)
    }

}