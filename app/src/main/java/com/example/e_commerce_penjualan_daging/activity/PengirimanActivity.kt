package com.example.e_commerce_penjualan_daging.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_penjualan_daging.MainActivity
import com.example.e_commerce_penjualan_daging.R
import com.example.e_commerce_penjualan_daging.adapter.AdapterKurir
import com.example.e_commerce_penjualan_daging.app.ApiConfig
import com.example.e_commerce_penjualan_daging.app.ApiConfigAlamat
import com.example.e_commerce_penjualan_daging.helper.Helper
import com.example.e_commerce_penjualan_daging.helper.SharedPref
import com.example.e_commerce_penjualan_daging.model.ApiKey
import com.example.e_commerce_penjualan_daging.model.Checkout
import com.example.e_commerce_penjualan_daging.model.ResponModel
import com.example.e_commerce_penjualan_daging.model.rajaongkir.Costs
import com.example.e_commerce_penjualan_daging.model.rajaongkir.ResponOngkir

import com.example.e_commerce_penjualan_daging.room.MyDatabase
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengirimanActivity : AppCompatActivity() {

    lateinit var myDb : MyDatabase
     var totalHarga = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengiriman)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        Helper().setToolbar(this,toolbar, "Pengiriman")
        myDb = MyDatabase.getInstance(this)!!

        totalHarga = Integer.valueOf(intent.getStringExtra("extra")!!)
        val tv_totalBelanja = findViewById<TextView>(R.id.tv_totalBelanja)
        tv_totalBelanja.text = Helper().gantiRupiah(totalHarga)
        mainButton()
        setSpinner()

    }

    fun setSpinner(){
        val arrayString = ArrayList<String>()
        arrayString.add("JNE")
        arrayString.add("POS")
        arrayString.add("TIKI")

        val adapter = ArrayAdapter<Any>(this, R.layout.item_spinner,arrayString.toTypedArray())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spn_kurir = findViewById<Spinner>(R.id.spn_kurir)
        spn_kurir.adapter = adapter
        spn_kurir.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long,) {
                if (position != 0){
                  getOngkir(spn_kurir.selectedItem.toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun checkAlamat(){
        val div_alamat = findViewById<androidx.cardview.widget.CardView>(R.id.div_alamat)
        val div_kosong = findViewById<TextView>(R.id.div_kosong)
        val tv_nama = findViewById<TextView>(R.id.tv_nama)
        val tv_phone = findViewById<TextView>(R.id.tv_phone)
        val tv_alamat = findViewById<TextView>(R.id.tv_alamat)
        val btn_tambah_alamat = findViewById<Button>(R.id.btn_tambahAlamat)


       if (myDb.daoAlamat().getByStatus(true) != null){
        div_alamat.visibility = View.VISIBLE
        div_kosong.visibility = View.GONE
        val tv_kosong = findViewById<TextView>(R.id.tv_kosong)
        tv_kosong.visibility = View.GONE
        val div_metodePengiriman = findViewById<LinearLayout>(R.id.div_metodePengiriman)
        div_metodePengiriman.visibility = View.VISIBLE

        val a = myDb.daoAlamat().getByStatus(true)!!
        tv_nama.text = a.name
        tv_phone.text = a.phone
        tv_alamat.text = a.alamat + ", " +a.kota +", "+a.kecamatan + ", "+ a.kodepos + ", ("+a.type+ ")"
        btn_tambah_alamat.text = "Ubah Alamat"

           getOngkir("JNE")
       } else {
           div_alamat.visibility = View.GONE
           div_kosong.visibility = View.VISIBLE
           btn_tambah_alamat.text = "Tambah Alamat"

       }
    }

    private fun mainButton(){
        val btnTambahAlamat = findViewById<Button>(R.id.btn_tambahAlamat)
        btnTambahAlamat.setOnClickListener{
            startActivity(Intent(this, ListAlamatActivity::class.java))
        }

        val btn_bayar = findViewById<Button>(R.id.btn_bayar)
        btn_bayar.setOnClickListener{
            bayar()
        }
    }

    private fun bayar(){
        val user = SharedPref(this).getUser()
        val a = myDb.daoAlamat().getByStatus(true)!!

        val listProduk = myDb.daoKeranjang().getAll() as ArrayList
        var totalItem = 0
        var totalHarga = 0
        val produks = ArrayList<Checkout.Item>()
        for (p in listProduk){
            if (p.selected){
               totalItem += p.jumlah
                totalHarga += (p.jumlah * Integer.valueOf(p.harga))

                val produk = Checkout.Item()
                produk.id = ""+ p.id
                produk.total_item = ""+ p.jumlah
                produk.total_harga = ""+ (p.jumlah * Integer.valueOf(p.harga))
                produk.catatan = "catatan baru"
                produks.add(produk)
            }
        }

         val checkout = Checkout()
         checkout.user_id = "17"
        checkout.total_item = ""+ totalItem
        checkout.total_harga = "" + totalHarga
        checkout.name = a.name
        checkout.phone = a.phone
        checkout.jasa_pengiriaman = jasaKirim
        checkout.ongkir = ongkir
        checkout.kurir = kurir
        checkout.total_transfer = "" + (totalHarga + Integer.valueOf(ongkir))
        checkout.produks = produks

        val json = Gson().toJson(checkout, Checkout::class.java)
        Log.d("Respon:", "jseon:" + json)
        val intent = Intent(this, PembayaranActivity::class.java)
        intent.putExtra("extra", json)
        startActivity(intent)
    }

    private fun getOngkir(kurir: String){

        val alamat = myDb.daoAlamat().getByStatus(true)

        val origin = "501"
        val destination = "" + alamat!!.id_kota.toString()
        val berat = 1000

        ApiConfigAlamat.instanceRetrofit.ongkir(ApiKey.key, origin, destination, berat, kurir.toLowerCase()).enqueue(object :
            Callback<ResponOngkir> {
            override fun onFailure(call: Call<ResponOngkir>, t: Throwable) {
                Log.d("Error","gagal memuat data:" + t.message)
            }

            override fun onResponse(call: Call<ResponOngkir>, response: Response<ResponOngkir>) {

                if (response.isSuccessful){
                    Log.d("Succes","data berhasil ditampilkan")
                    val result = response.body()!!.rajaongkir.results
                    if (result.isNotEmpty()){
                        displayOngkir(result[0].code.toUpperCase(), result[0].costs)
                    }

                }else{
                    Log.d("Error","gagal memuat data:"+response.message())
                }
            }
        })
    }

    var ongkir = "0"
    var kurir = ""
    var jasaKirim = ""
    private fun displayOngkir(_kurir:String, arrayList : ArrayList<Costs>){

        var arrayOngkir = ArrayList<Costs>()
        for (i in arrayList.indices ){
            val ongkir = arrayList[i]
            if (i == 0){
               ongkir.isActive = true
            }
            arrayOngkir.add(ongkir)
        }
        setTotal(arrayOngkir[0].cost[0].value)

        val rv_metode = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_metode)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        var adapter:AdapterKurir? = null
         adapter = AdapterKurir(arrayOngkir,kurir, object : AdapterKurir.Listeners{
            override fun onClicked(data: Costs, adapterPosition: Int) {
                val newArrayOngkir = ArrayList<Costs>()
                for (ongkir in arrayOngkir){
                    ongkir.isActive = data.description == ongkir.description
                    newArrayOngkir.add(ongkir)
                }
                arrayOngkir = newArrayOngkir
                adapter!!.notifyDataSetChanged()
                setTotal(data.cost[0].value)

                ongkir = data.cost[0].value
                kurir = _kurir
                jasaKirim = data.service
            }

        })
        rv_metode.adapter = adapter

        rv_metode.layoutManager = layoutManager
    }

    fun setTotal(ongkir :String){
       val tv_ongkir = findViewById<TextView>(R.id.tv_ongkir)
        tv_ongkir.text = Helper().gantiRupiah(ongkir)
        val tv_total = findViewById<TextView>(R.id.tv_total)
        tv_total.text = Helper().gantiRupiah(Integer.valueOf(ongkir) + totalHarga)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        checkAlamat()
        super.onResume()
    }
}