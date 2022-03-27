package com.example.orangepistate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.viewpager.widget.ViewPager

import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {


    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tabs = ArrayList<String>()
        tabs.add(Const.RASPPERRY_PI)
        tabs.add(Const.ORANGE_PI)
        title = "Informations about SBC's"
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        tabLayout.addTab(tabLayout.newTab().setText(Const.RASPPERRY_PI))
        tabLayout.addTab(tabLayout.newTab().setText(Const.ORANGE_PI))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = InformationAdapter(this, supportFragmentManager,
            tabLayout.tabCount,tabs
        )
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


    }
}