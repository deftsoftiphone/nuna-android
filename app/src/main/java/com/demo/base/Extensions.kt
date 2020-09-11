package com.demo.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

@Suppress("UNCHECKED_CAST")
fun <F : Fragment> AppCompatActivity.getCurrentFragment(fragmentClass: Class<F>): F? {
    if (this.supportFragmentManager.fragments.isEmpty()) return null
    val navHostFragment = this.supportFragmentManager.fragments.first() as? NavHostFragment

    navHostFragment?.childFragmentManager?.fragments?.forEach {
        if (fragmentClass.isAssignableFrom(it.javaClass)) {
            return it as F
        }
    }
    return null
}