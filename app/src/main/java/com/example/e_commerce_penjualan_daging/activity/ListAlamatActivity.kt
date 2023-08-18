package com.example.e_commerce_penjualan_daging.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_penjualan_daging.R
import com.example.e_commerce_penjualan_daging.adapter.AdapterAlamat
import com.example.e_commerce_penjualan_daging.adapter.AdapterProduk
import com.example.e_commerce_penjualan_daging.helper.Helper
import com.example.e_commerce_penjualan_daging.model.Alamat
import com.example.e_commerce_penjualan_daging.model.Produk
import com.example.e_commerce_penjualan_daging.room.MyDatabase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ListAlamatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_alamat)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        Helper().setToolbar(this,toolbar, "Pilih Alamat")

        mainButton()
    }

    private fun displayAlamat(){
        val MyDb = MyDatabase.getInstance(this)!!
        val arrayList = MyDb.daoAlamat().getAll() as ArrayList

        val div_kosong = findViewById<LinearLayout>(R.id.div_kosong)
        val rv_alamat = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_alamat)
        if (arrayList.isEmpty())div_kosong.visibility = View.VISIBLE
        else div_kosong.visibility = View.GONE

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_alamat.adapter = AdapterAlamat(arrayList, object : AdapterAlamat.Listeners{
            override fun onClicked(data: Alamat) {
                for (alamat in arrayList){
                    alamat.isSelected = false
                    update(alamat)
                }
                data.isSelected = true
                update(data)
                onBackPressed()

            }

        })
        rv_alamat.layoutManager = layoutManager
    }

    private fun update(data: Alamat) {
        val myDb = MyDatabase.getInstance(this)
        CompositeDisposable().add(Observable.fromCallable { myDb!!.daoAlamat().update(data) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
            })
    }

    override fun onResume() {
        displayAlamat()
        super.onResume()
    }

    private fun mainButton(){
        val BTNtambahAlamat = findViewById<Button>(R.id.btn_tambahAlamat)
        BTNtambahAlamat.setOnClickListener{
            startActivity(Intent(this, TambahAlamatActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
    }
}