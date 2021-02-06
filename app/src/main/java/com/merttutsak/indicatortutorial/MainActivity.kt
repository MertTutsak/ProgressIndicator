package com.merttutsak.indicatortutorial

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.merttutsak.indicator.ProgressIndicator


class MainActivity : AppCompatActivity() {

    val itemCount = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        viewPager.adapter = CustomPagerAdapter(this.supportFragmentManager, itemCount)

        val indicator = findViewById<View>(R.id.progressIndicator) as ProgressIndicator
        indicator.setupInfiniteViewPager(viewPager,itemCount)
    }
}