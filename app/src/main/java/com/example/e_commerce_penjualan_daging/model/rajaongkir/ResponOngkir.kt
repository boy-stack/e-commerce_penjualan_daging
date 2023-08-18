package com.example.e_commerce_penjualan_daging.model.rajaongkir

import com.example.e_commerce_penjualan_daging.model.ModelAlamat

class ResponOngkir {
    val rajaongkir = Rajaongkir()

    class Rajaongkir {
        val results = ArrayList<Result>()
    }


}