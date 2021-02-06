package com.merttutsak.indicatortutorial

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

class CustomPagerAdapter(private val fm: FragmentManager, private val itemCount: Int) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = Integer.MAX_VALUE

    override fun getItem(position: Int): Fragment = TextFragment.newInstance(position.rem(itemCount).toString())
}