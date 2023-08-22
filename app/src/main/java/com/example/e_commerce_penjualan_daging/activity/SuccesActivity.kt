package com.example.e_commerce_penjualan_daging.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.e_commerce_penjualan_daging.MainActivity
import com.example.e_commerce_penjualan_daging.R
import com.example.e_commerce_penjualan_daging.helper.Helper
import com.example.e_commerce_penjualan_daging.model.Bank
import com.example.e_commerce_penjualan_daging.model.Checkout
import com.example.e_commerce_penjualan_daging.model.Transaksi
import com.example.e_commerce_penjualan_daging.room.MyDatabase
import com.google.gson.Gson

class SuccesActivity : AppCompatActivity() {

    var nominal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        Helper().setToolbar(this,toolbar, "Menunggu Konfirmasi")

      setValues()
      mainButton()
    }

    fun mainButton(){
        val tv_nomorRekening = findViewById<TextView>(R.id.tv_nomorRekening)
        val btn_copyNoRek = findViewById<ImageView>(R.id.btn_copyNoRek)
        btn_copyNoRek.setOnClickListener{
           copyText(tv_nomorRekening.text.toString())
        }

        val btn_copyNominal = findViewById<ImageView>(R.id.btn_copyNominal)
        btn_copyNominal.setOnClickListener{
        copyText(nominal.toString())
        }

        val btn_cekStatus = findViewById<Button>(R.id.btn_cekStatus)
        btn_cekStatus.setOnClickListener{
            startActivity(Intent(this, RiwayatActivity::class.java))
        }
    }

    fun copyText(text:String){
        val copyManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copyText = ClipData.newPlainText("text", text)
        copyManager.setPrimaryClip(copyText)

        Toast.makeText(this, "Text Berhasil di Copy", Toast.LENGTH_SHORT).show()
    }

    fun setValues(){
    val jsBank = intent.getStringExtra("bank")
    val jsTransaksi = intent.getStringExtra("transaksi")
    val jsCheckout = intent.getStringExtra("checkout")

        val bank = Gson().fromJson(jsBank, Bank::class.java)
        val transaksi =  Gson().fromJson(jsTransaksi, Transaksi::class.java)
        val checkout =  Gson().fromJson(jsCheckout, Checkout::class.java)


        //hapus keranjang
        val myDb = MyDatabase.getInstance(this)!!
        for (produk in checkout.produks){
            myDb.daoKeranjang().deleteById(produk.id)
        }

        val tv_nomorRekening = findViewById<TextView>(R.id.tv_nomorRekening)
        val tv_namaPenerima = findViewById<TextView>(R.id.tv_namaPenerima)
        val image_bank = findViewById<ImageView>(R.id.image_bank)
        val tv_nominal = findViewById<TextView>(R.id.tv_nominal)

        tv_nomorRekening.text = bank.rekening
        tv_namaPenerima.text = bank.penerima
        image_bank.setImageResource(bank.image)

        nominal = Integer.valueOf(transaksi.total_transfer) + transaksi.kode_unik
        tv_nominal.text = Helper().gantiRupiah(nominal)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        super.onBackPressed()
    }

}