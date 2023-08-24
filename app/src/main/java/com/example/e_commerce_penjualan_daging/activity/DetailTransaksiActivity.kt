package com.example.e_commerce_penjualan_daging.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_penjualan_daging.R
import com.example.e_commerce_penjualan_daging.adapter.AdapterProdukTransaksi
import com.example.e_commerce_penjualan_daging.app.ApiConfig
import com.example.e_commerce_penjualan_daging.helper.Helper
import com.example.e_commerce_penjualan_daging.model.DetailTransaksi
import com.example.e_commerce_penjualan_daging.model.ResponModel
import com.example.e_commerce_penjualan_daging.model.Transaksi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailTransaksiActivity : AppCompatActivity() {

    var transaksi = Transaksi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_transaksi)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        Helper().setToolbar(this,toolbar, "Pembayaran")

        val json = intent.getStringExtra("transaksi")
        transaksi = Gson().fromJson(json, Transaksi::class.java)

        setData(transaksi)
        displayProduk(transaksi.details)
        mainButton()
    }

    private fun mainButton(){
        val btn_batal = findViewById<Button>(R.id.btn_batal)
        btn_batal.setOnClickListener{
             batalTransaksi()
        }
    }

    fun batalTransaksi(){
        ApiConfig.instanceRetrofit.batalCheckout(transaksi.id).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.success == 1) {
                    Toast.makeText(this@DetailTransaksiActivity, "Transaksi berhasil di Batalkan", Toast.LENGTH_SHORT).show()
                    onBackPressed()
//                    displayRiwayat(res.transaksis)
                }
            }
        })
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
        val div_footer = findViewById<LinearLayout>(R.id.div_footer)

        tv_status.text = t.status
        val formatBaru = "dd MMMM yyyy, kk:mm:ss"
        tv_tgl.text = Helper().convertTanggal(t.created_at, formatBaru)


        tv_penerima.text = t.name +" - "+t.phone
        tv_alamat.text = t.detail_lokasi
        tv_kodeUnik.text = Helper().gantiRupiah(t.kode_unik)
        tv_tb.text = Helper().gantiRupiah(t.total_harga)
        tv_ongkir.text = Helper().gantiRupiah(t.ongkir)
        tv_total.text = Helper().gantiRupiah(t.total_transfer)

        if (t.status != "MENUNGGU") div_footer.visibility = View.GONE

        var color = getColor(R.color.menunggu)
        if (t.status == "SELESAI") color = getColor(R.color.selesai)
        else if (t.status == "BATAL") color = getColor(R.color.batal)

        tv_status.setTextColor(color)
    }

    fun displayProduk(transaksis: ArrayList<DetailTransaksi>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        val rv_produk = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_produk)
        rv_produk.adapter = AdapterProdukTransaksi(transaksis)
        rv_produk.layoutManager = layoutManager
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}