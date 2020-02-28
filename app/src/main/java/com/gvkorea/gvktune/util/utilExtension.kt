package com.gvkorea.gvktune.util

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gvkorea.gvktune.R

fun AppCompatActivity.replace(@IdRes frameID: Int, fragment: Fragment, tag:String? = null){
    val transaction = supportFragmentManager.beginTransaction()
    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
    transaction.detach(fragment)
    transaction.attach(fragment)
    transaction.replace(frameID, fragment, tag).commit()
}