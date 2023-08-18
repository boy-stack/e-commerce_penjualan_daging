package com.example.e_commerce_penjualan_daging.fargment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.e_commerce_penjualan_daging.R
import com.example.e_commerce_penjualan_daging.adapter.AdapterProduk
import com.example.e_commerce_penjualan_daging.app.ApiConfig
import com.example.e_commerce_penjualan_daging.model.Produk
import com.example.e_commerce_penjualan_daging.model.ResponModel
import com.inyongtisto.tutorial.adapter.AdapterSlider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    lateinit var dgSlider: ViewPager
    lateinit var rvProduk: RecyclerView
    lateinit var rvProdukTerkini: RecyclerView



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        init(view)
        getProduk()

        return view
    }

    private var listProduk: ArrayList<Produk> = ArrayList()
          fun getProduk(){
          ApiConfig.instanceRetrofit.getProduk().enqueue(object :Callback<ResponModel> {
              override fun onFailure(call: Call<ResponModel>, t: Throwable) {

              }

              override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                  val res= response.body()!!
                if (res.success == 1) {
                    val arrayProduk  = ArrayList<Produk>()
                    for (p in res.produks){
                        p.discount = 30000
                        arrayProduk.add(p)
                    }
                    listProduk = arrayProduk
                    displayProduk()
                }
              }
          })
      }

    fun init(view: View){
        dgSlider = view.findViewById(R.id.dg_slider)
        rvProduk = view.findViewById(R.id.pd_daging)
        rvProdukTerkini = view.findViewById(R.id.rv_produkTerkini)
    }

    fun displayProduk(){

        val arrSlider = ArrayList<Int>()
        arrSlider.add(R.drawable.daging_porterhouse)
        arrSlider.add(R.drawable.daging_ribeye)

        val adapterSlider = AdapterSlider(arrSlider, activity)
        dgSlider.adapter = adapterSlider

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        val layoutManager2 = LinearLayoutManager(activity)
        layoutManager2.orientation = LinearLayoutManager.HORIZONTAL



        rvProduk.adapter = AdapterProduk(requireActivity(), listProduk)
        rvProduk.layoutManager = layoutManager

        rvProdukTerkini.adapter = AdapterProduk(requireActivity(), listProduk)
        rvProdukTerkini.layoutManager = layoutManager2
    }

//   }

//    val arrProduk: ArrayList<Produk>get () {
//        val arr = ArrayList<Produk>()
//        val p1 = Produk()
//        p1.nama = "Daging Poterhouse"
//        p1.harga = "Rp. 67000"
//        p1.stok = "1"
//        p1.gambar = R.drawable.daging_poterhouse1

//        val p2 = Produk()
//        p2.nama = "Daging Poterhouse"
//        p2.harga = "Rp. 63000"
//        p2.stok = "1"
//        p2.gambar = R.drawable.daging_ribeye

//        arr.add(p1)
//        arr.add(p2)

//        return arr
//    }

//    val arrProdukTerkini: ArrayList<Produk>get () {
//       val arr = ArrayList<Produk>()
//        val p1 = Produk()
//        p1.nama = "Daging Poterhouse"
//        p1.harga = "Rp. 67000"
//        p1.stok = "1"
//        p1.gambar = R.drawable.daging_poterhouse1

//        val p2 = Produk()
//        p2.nama = "Daging Poterhouse"
//        p2.harga = "Rp. 63000"
//        p2.stok = "1"
//        p2.gambar = R.drawable.daging_porterhouse

//        arr.add(p1)
//        arr.add(p2)

//        return arr
//    }
}