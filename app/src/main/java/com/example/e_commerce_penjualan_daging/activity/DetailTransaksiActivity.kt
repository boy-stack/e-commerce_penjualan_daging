package com.example.e_commerce_penjualan_daging.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_penjualan_daging.R
import com.example.e_commerce_penjualan_daging.adapter.AdapterProdukTransaksi
import com.example.e_commerce_penjualan_daging.helper.Helper
import com.example.e_commerce_penjualan_daging.model.DetailTransaksi
import com.example.e_commerce_penjualan_daging.model.Transaksi
import com.google.gson.Gson

class DetailTransaksiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_transaksi)

        val json = intent.getStringExtra("transaksi")
        val transaksi = Gson().fromJson(json, Transaksi::class.java)

        setData(transaksi)
        displayProduk(transaksi.details)
    }

    fun setData(t: Transaksi) {
        val tv_status = findViewById<TextView>(R.id.tv_status)
        val tv_tgl = findViewById<TextView>(R.id.tv_tgl)
        val tv_penerima = findViewById<TextView>(R.id.tv_penerima)
        val tv_alamat = findViewById<TextView>(R.id.tv_alamat)
        val tv_kodeUnik = findViewById<TextView>(R.id.tv_kodeUnik)
        val tv_tb = findViewById<TextView>(R.id.tv_totalBelanja)
        val tv_ongkir = findViewById<TextView>(R.id.tv_ongkir)
        val tv_total = findViewById<TextView>(R.id.tv_total)

        tv_status.text = t.status
        tv_tgl.text = t.created_at
        tv_penerima.text = t.name +" - "+t.phone
        tv_alamat.text = t.detail_lokasi
        tv_kodeUnik.text = Helper().gantiRupiah(t.kode_unik)
        tv_tb.text = Helper().gantiRupiah(t.total_harga)
        tv_ongkir.text = Helper().gantiRupiah(t.ongkir)
        tv_total.text = Helper().gantiRupiah(t.total_transfer)

    }

    fun displayProduk(transaksis: ArrayList<DetailTransaksi>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        val rv_produk = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_produk)
        rv_produk.adapter = AdapterProdukTransaksi(transaksis)
        rv_produk.layoutManager = layoutManager
    }


}