package com.example.e_commerce_penjualan_daging

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.e_commerce_penjualan_daging.activity.MasukActivity
import com.example.e_commerce_penjualan_daging.fargment.AkunFragment
import com.example.e_commerce_penjualan_daging.fargment.HomeFragment
import com.example.e_commerce_penjualan_daging.fargment.KeranjangFragment
import com.example.e_commerce_penjualan_daging.helper.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val fargmentHome: Fragment = HomeFragment()
    private val fargmentAkun: Fragment = AkunFragment()
    private val fargmentKeranjang: Fragment = KeranjangFragment()
    private val fm: FragmentManager = supportFragmentManager
    private var active:Fragment = fargmentHome

       private lateinit var menu: Menu
       private lateinit var menuItem: MenuItem
       private lateinit var bottomNavigationView: BottomNavigationView

       private var statusLogin = false

       private lateinit var s:SharedPref

       private var dariDetail :Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        s = SharedPref(this)

        setUpBottomNav()

        LocalBroadcastManager.getInstance(this).registerReceiver(mmessage, IntentFilter("event:keranjang"))
    }

      val mmessage : BroadcastReceiver = object :BroadcastReceiver(){
          override fun onReceive(context: Context?, intent: Intent?) {
          dariDetail = true
          }
      }

    fun setUpBottomNav(){
        fm.beginTransaction().add(R.id.container, fargmentHome).show(fargmentHome).commit()
        fm.beginTransaction().add(R.id.container, fargmentAkun).hide(fargmentAkun).commit()
        fm.beginTransaction().add(R.id.container, fargmentKeranjang).hide(fargmentKeranjang).commit()

        bottomNavigationView = findViewById(R.id.nav_view)
        menu = bottomNavigationView.menu
        menuItem = menu.getItem( 0)
        menuItem.isChecked = true

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            when(item.itemId) {
                R.id.navigation_home->{
                    callFargment(0, fargmentHome)
                }
                R.id.navigation_keranjang->{
                    callFargment(1, fargmentKeranjang)
                }
                R.id.navigation_akun->{
                    if (s.getStatusLogin()){
                        callFargment(2, fargmentAkun)
                    } else {
                        startActivity(Intent(this, MasukActivity::class.java))
                    }

                }
            }

            false
        }
    }

    fun callFargment(int : Int, fragment: Fragment) {
        menuItem = menu.getItem(int)
        menuItem.isChecked = true
        fm.beginTransaction().hide(active).show(fragment).commit()
        active =fragment
    }

    override fun onResume() {
        if (dariDetail)  {
            dariDetail = false
            callFargment(1, fargmentKeranjang)
        }
        super.onResume()
    }
}