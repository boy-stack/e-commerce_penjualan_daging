package com.example.e_commerce_penjualan_daging.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toast
import com.example.e_commerce_penjualan_daging.R
import com.example.e_commerce_penjualan_daging.app.ApiConfig
import com.example.e_commerce_penjualan_daging.app.ApiConfigAlamat
import com.example.e_commerce_penjualan_daging.helper.Helper
import com.example.e_commerce_penjualan_daging.model.Alamat
import com.example.e_commerce_penjualan_daging.model.ApiKey
import com.example.e_commerce_penjualan_daging.model.ModelAlamat
import com.example.e_commerce_penjualan_daging.model.ResponModel
import com.example.e_commerce_penjualan_daging.room.MyDatabase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahAlamatActivity : AppCompatActivity() {

    var provinsi = ModelAlamat.Provinsi()
    var kota = ModelAlamat.Provinsi()
    var kecamatan = ModelAlamat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_alamat)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        Helper().setToolbar(this,toolbar, "Tambah Alamat")

        mainButton()
        getProvinsi()
    }

    private fun mainButton(){
        val simpan = findViewById<Button>(R.id.btn_simpan)
        simpan.setOnClickListener{
          simpan()
        }
    }

    private fun simpan(){
        val edt_nama = findViewById<EditText>(R.id.edt_nama)
        val edt_type = findViewById<EditText>(R.id.edt_type)
        val edt_phone = findViewById<EditText>(R.id.edt_phone)
        val edt_alamat = findViewById<EditText>(R.id.edt_alamat)
        val kd_pos = findViewById<EditText>(R.id.edt_kodePos)
        when {
            edt_nama.text.isEmpty() -> {
                error(edt_nama)
                return
            }
            edt_type.text.isEmpty() -> {
                error(edt_type)
                return
            }
            edt_phone.text.isEmpty() -> {
                error(edt_phone)
                return
            }
            edt_alamat.text.isEmpty() -> {
                error(edt_alamat)
                return
            }
            kd_pos.text.isEmpty() -> {
                error(kd_pos)
                return
            }

        }

        if (provinsi.province_id == "0"){
            toast("Silahkan Pilih Provinsi Terlebih Dahulu")
            return
        }

        if (kota.city_id == "0"){
            toast("Silahkan Pilih Kota")
            return
        }

 //       if (kecamatan.id == 0){
 //           toast("Silahkan Pilih Kecamatan")
 //           return
 //       }

        val alamat = Alamat()
        alamat.name = edt_nama.text.toString()
        alamat.type = edt_type.text.toString()
        alamat.phone = edt_phone.text.toString()
        alamat.alamat = edt_alamat.text.toString()
        alamat.kodepos = kd_pos.text.toString()

        alamat.id_provinsi = Integer.valueOf(provinsi.province_id)
        alamat.provinsi = provinsi.province
        alamat.id_kota = Integer.valueOf(kota.city_id)
        alamat.kota = kota.city_name
//        alamat.id_kecamatan = kecamatan.id
//        alamat.kecamatan = kecamatan.nama

        insert(alamat)

    }

    fun toast(string: String){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

    private fun error(editText: EditText){
        editText.error = "Kolom Tidak Boleh Kosong"
        editText.requestFocus()
    }

   private fun getProvinsi(){
       ApiConfigAlamat.instanceRetrofit.getProvinsi(ApiKey.key).enqueue(object : Callback<ResponModel> {
           override fun onFailure(call: Call<ResponModel>, t: Throwable) {

           }

           override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

               if (response.isSuccessful){

                   val pb = findViewById<ProgressBar>(R.id.pb)
                   pb.visibility = View.GONE
                   val div_pv = findViewById<RelativeLayout>(R.id.div_provinsi)
                   div_pv.visibility = View.VISIBLE

                   val res= response.body()!!
                   val arrayString = ArrayList<String>()
                   arrayString.add("Pilih Provinsi")

                   val listProvinsi = res.rajaongkir.results
                   for (prov in listProvinsi){
                       arrayString.add(prov.province)
                   }

                   val adapter = ArrayAdapter<Any>(this@TambahAlamatActivity, R.layout.item_spinner,arrayString.toTypedArray())
                   adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                   val spn_pv = findViewById<Spinner>(R.id.spn_provinsi)
                   spn_pv.adapter = adapter
                   spn_pv.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                       override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long,) {
                           if (position != 0){
                               provinsi = listProvinsi[position - 1]
                               val idProv = provinsi.province_id
                               getKota(idProv)
                           }
                       }

                       override fun onNothingSelected(parent: AdapterView<*>?) {
                           TODO("Not yet implemented")
                       }
                   }

               }else{
                   Log.d("Error","gagal memuat data:"+response.message())
               }
           }
       })
   }

   private fun getKota(id: String) {
       val pb = findViewById<ProgressBar>(R.id.pb)
       pb.visibility = View.VISIBLE
        ApiConfigAlamat.instanceRetrofit.getKota(ApiKey.key, id).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                if (response.isSuccessful){

                    val pb = findViewById<ProgressBar>(R.id.pb)
                    pb.visibility = View.GONE
                    val div_kt = findViewById<RelativeLayout>(R.id.div_kota)
                    div_kt.visibility = View.VISIBLE

                    val res= response.body()!!
                    val listArray = res.rajaongkir.results

                    val arrayString = ArrayList<String>()
                    arrayString.add("Pilih Kota")
                    for (kota in listArray){
                        arrayString.add(kota.type+ "" + kota.city_name)
                    }

                    val adapter = ArrayAdapter<Any>(this@TambahAlamatActivity, R.layout.item_spinner,arrayString.toTypedArray())
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    val spn_kt = findViewById<Spinner>(R.id.spn_kota)
                    spn_kt.adapter = adapter
                    spn_kt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long,) {
                            if (position != 0){
                                kota = listArray[position - 1]
                               val kodePos = kota.postal_code
                                val edt_kodepos = findViewById<EditText>(R.id.edt_kodePos)
                                edt_kodepos.setText(kodePos)
//                                getKecamatan(idKota)

                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }
                    }

                }else{
                    Log.d("Error","gagal memuat data:"+response.message())
                }
            }
        })
    }

   private fun getKecamatan(id: Int){
       val pb = findViewById<ProgressBar>(R.id.pb)
       pb.visibility = View.VISIBLE
        ApiConfigAlamat.instanceRetrofit.getKecamatan(id).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                if (response.isSuccessful){

                    val pb = findViewById<ProgressBar>(R.id.pb)
                    pb.visibility = View.GONE
                    val div_kc = findViewById<RelativeLayout>(R.id.div_kecamatan)
                    div_kc.visibility = View.VISIBLE

                    val res= response.body()!!
                    val listArray = res.kecamatan

                    val arrayString = ArrayList<String>()
                    arrayString.add("Pilih Kecamatan")
                    for (data in listArray){
                        arrayString.add(data.nama)
                    }

                    val adapter = ArrayAdapter<Any>(this@TambahAlamatActivity, R.layout.item_spinner,arrayString.toTypedArray())
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    val spn_kc = findViewById<Spinner>(R.id.spn_kecamatan)
                    spn_kc.adapter = adapter
                    spn_kc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long,) {
                            if (position != 0){
                                kecamatan = listArray[position - 1]


                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }

                }else{
                    Log.d("Error","gagal memuat data:"+response.message())
                }
            }
        })
    }

    private fun insert(data:Alamat){
        val myDb = MyDatabase.getInstance(this)!!
        if (myDb.daoAlamat().getByStatus(true) == null){
            data.isSelected = true
        }
        CompositeDisposable().add(Observable.fromCallable { myDb.daoAlamat().insert(data) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                toast("Insert Data Succes")
                onBackPressed()
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}