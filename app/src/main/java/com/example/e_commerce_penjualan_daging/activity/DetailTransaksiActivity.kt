package com.example.e_commerce_penjualan_daging.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_penjualan_daging.MainActivity
import com.example.e_commerce_penjualan_daging.R
import com.example.e_commerce_penjualan_daging.adapter.AdapterProdukTransaksi
import com.example.e_commerce_penjualan_daging.app.ApiConfig
import com.example.e_commerce_penjualan_daging.helper.Helper
import com.example.e_commerce_penjualan_daging.model.DetailTransaksi
import com.example.e_commerce_penjualan_daging.model.ResponModel
import com.example.e_commerce_penjualan_daging.model.Transaksi
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.gson.Gson
import com.inyongtisto.myhelper.base.BaseActivity
import com.inyongtisto.myhelper.extension.showErrorDialog
import com.inyongtisto.myhelper.extension.showSuccessDialog
import com.inyongtisto.myhelper.extension.toGone
import com.inyongtisto.myhelper.extension.toMultipartBody
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DetailTransaksiActivity : BaseActivity() {

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
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Apakah anda yakin?")
                .setContentText("Transaksi akan di batalkan dan tidak bisa di kembalikan!")
                .setConfirmText("Yes,Batalkan")
                .setConfirmClickListener {
                    it.dismissWithAnimation()
                    batalTransaksi()
                }
                .setCancelText("Tutup")
                .setCancelClickListener {
                    it.dismissWithAnimation()
                }.show()
        }

        val btn_bayar = findViewById<Button>(R.id.btn_bayar)
        btn_bayar.setOnClickListener{
           imagePic()
        }
    }

    private fun imagePic(){
        ImagePicker.with(this)
            .crop()
            .maxResultSize(512, 512, true) //true: Keep Ratio
            .provider(ImageProvider.BOTH) //Or bothCameraGallery()
            .createIntentFromDialog { launcher.launch(it) }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                Log.d("TAG", "URI IMAGE: "+ uri)
                val fileUri: Uri = uri
                dialogUpload(File(fileUri.path!!))
            }
        }

    var alertDialog : AlertDialog? = null
    @SuppressLint("InflateParams")
    private fun dialogUpload(file: File){

        val view = layoutInflater
        val layout = view.inflate(R.layout.view_upload, null)

        val imageView :ImageView = layout.findViewById(R.id.image)
        val btn_upload: Button = layout.findViewById(R.id.btn_upload)
        val btn_image: Button = layout.findViewById(R.id.btn_image)

        Picasso.get()
            .load(file)
            .into(imageView)

        btn_upload.setOnClickListener{
           upload(file)
        }

        btn_image.setOnClickListener{
            imagePic()
        }

        alertDialog = AlertDialog.Builder(this).create()
        alertDialog!!.setView(layout)
        alertDialog!!.setCancelable(true)
        alertDialog!!.show()
    }

    private fun upload(file: File){

        progress.show()
        val fileImage = file.toMultipartBody()
        ApiConfig.instanceRetrofit.uploadBukti(transaksi.id, fileImage!!).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                progress.dismiss()
                showErrorDialog(t.message!!)
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                progress.dismiss()
                if (response.isSuccessful){
                    if (response.body()!!.success == 1){
                        showSuccessDialog("Upload Bukti Berhasil"){
                            alertDialog!!.dismiss()
                            val btn_bayar = findViewById<Button>(R.id.btn_bayar)
                            btn_bayar.toGone()
                            val tv_status = findViewById<TextView>(R.id.tv_status)
                            tv_status.text = "DIBAYAR"
                            onBackPressed()
                        }

                    } else {
                        showErrorDialog(response.body()!!.message)
                    }

                } else {
                    showErrorDialog(response.message())
                }
            }
        })
    }

    fun batalTransaksi(){
        val loading = SweetAlertDialog(this@DetailTransaksiActivity, SweetAlertDialog.PROGRESS_TYPE)
            loading.setTitleText("Loading...").show()

        ApiConfig.instanceRetrofit.batalCheckout(transaksi.id).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
             loading.dismiss()
                error(t.message.toString())
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
               loading.dismiss()
                val res = response.body()!!
                if (res.success == 1) {

                    SweetAlertDialog(this@DetailTransaksiActivity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Succes...")
                        .setContentText("Transaksi Berhasil di Batalkan!")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                            onBackPressed()
                        }
                        .show()
//                    Toast.makeText(this@DetailTransaksiActivity, "Transaksi berhasil di Batalkan", Toast.LENGTH_SHORT).show()
//                    onBackPressed()
//                    displayRiwayat(res.transaksis)
                }else{
                   error(res.message)
                }
            }
        })
    }

    fun error(pesan:String){
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Oops...")
            .setContentText(pesan)
            .show()
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