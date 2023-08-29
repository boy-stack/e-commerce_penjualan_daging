package com.example.e_commerce_penjualan_daging.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.e_commerce_penjualan_daging.R
import com.example.e_commerce_penjualan_daging.helper.Helper
import com.example.e_commerce_penjualan_daging.model.Produk
import com.example.e_commerce_penjualan_daging.room.MyDatabase
import com.example.e_commerce_penjualan_daging.util.Config
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class DetailProdukActivity : AppCompatActivity() {
    lateinit var myDb: MyDatabase
    lateinit var produk: Produk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produk)
        myDb = MyDatabase.getInstance(this)!! // call database

        getInfo()
        mainButton()
        checkKeranjang()
    }

    private fun mainButton(){
        val btn_keranjang = findViewById<RelativeLayout>(R.id.btn_keranjang)
        btn_keranjang.setOnClickListener{
           val data = myDb.daoKeranjang().getProduk(produk.id)

            if (data == null){
                insert()
            } else {
                data.jumlah += 1
                update(data)
            }


        }

        val btn_favorit = findViewById<RelativeLayout>(R.id.btn_favorit)
        btn_favorit.setOnClickListener{

            val listData = myDb.daoKeranjang().getAll() // get All data
            for(note :Produk in listData){
                println("-----------------------")
                println(note.name)
                println(note.harga)
            }
        }

        val btn_toKeranjang = findViewById<RelativeLayout>(R.id.btn_toKeranjang)
        btn_toKeranjang.setOnClickListener{
           val intent = Intent("event:keranjang")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            onBackPressed()
        }
    }

    private fun insert(){
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().insert(produk) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                checkKeranjang()
                if (myDb.daoKeranjang().getAll().isEmpty()){
                }
                Log.d("respons", "data inserted")
                Toast.makeText(this, "Berhasil menambah kekeranjang", Toast.LENGTH_SHORT).show()
            })
    }

    private fun update(data: Produk){
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().update(data) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                checkKeranjang()
                if (myDb.daoKeranjang().getAll().isEmpty()){
                }
                Log.d("respons", "data inserted")
                Toast.makeText(this, "Berhasil menambah kekeranjang", Toast.LENGTH_SHORT).show()
            })
    }

    private fun checkKeranjang(){
        val dataKeranjang = myDb.daoKeranjang().getAll()

        if(dataKeranjang.isNotEmpty()) {
            val div_angka = findViewById<RelativeLayout>(R.id.div_angka)
            div_angka.visibility = View.VISIBLE
            val tv_angka = findViewById<TextView>(R.id.tv_angka)
            tv_angka.text = dataKeranjang.size.toString()
        }else{
            val div_angka = findViewById<RelativeLayout>(R.id.div_angka)
            div_angka.visibility + View.GONE
        }
    }

    private fun getInfo(){
        val data = intent.getStringExtra("extra")
        produk = Gson().fromJson(data, Produk::class.java)

        // set value
        val tv_nama = findViewById<TextView>(R.id.tv_nama)
        val tv_harga = findViewById<TextView>(R.id.tv_harga)
        val tv_deskripsi = findViewById<TextView>(R.id.tv_deskripsi)
        val image = findViewById<ImageView>(R.id.image)
        val jumlah_stok = findViewById<TextView>(R.id.jumlah_stok)
        val tv_berat = findViewById<TextView>(R.id.tv_berat)
        tv_nama.text = produk.name
        tv_harga.text = Helper().gantiRupiah(produk.harga)
        tv_deskripsi.text = produk.deskripsi
        jumlah_stok.text = produk.stok.toString()
        tv_berat.text = produk.berat


        val img = Config.productUrl + produk.image
        Picasso.get()
            .load(img)
            .placeholder(R.drawable.produk)
            .error(R.drawable.produk)
            .resize(400,400)
            .into(image)

        // setToolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        Helper().setToolbar(this,toolbar, produk.name)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}