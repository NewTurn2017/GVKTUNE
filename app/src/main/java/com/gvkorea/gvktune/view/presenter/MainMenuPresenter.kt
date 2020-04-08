package com.gvkorea.gvktune.view.presenter

import android.widget.Toast
import com.gvkorea.gvktune.MainActivity
import com.gvkorea.gvktune.MainActivity.Companion.nowFragment
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.util.replace
import com.gvkorea.gvktune.view.MainFragment

class MainMenuPresenter(val view: MainFragment, val mainActivity: MainActivity) {

    fun selectFunction(function: String) {
        if (isSocketAlive()) {
            when(function) {
                "rta" -> {
                    mainActivity.replace(R.id.fragment_container, view.evaluateFragment)
                    nowFragment = view.evaluateFragment
                }
                "calib" -> {
                    mainActivity.replace(R.id.fragment_container, view.calibFragment)
                    nowFragment = view.calibFragment
                }
                "reverb" -> {
                    mainActivity.replace(R.id.fragment_container, view.reverbFragment)
                    nowFragment = view.reverbFragment
                }
                "tune" -> {
                    mainActivity.replace(R.id.fragment_container, view.tuneFragment)
                    nowFragment = view.tuneFragment
                }

            }
        } else {
            msg("연결된 스피커가 없습니다.")
        }
    }

    fun isSocketAlive(): Boolean {

//        if (spk1Client != null || spk2Client != null || spk3Client != null || spk4Client != null || otherClient.size > 0
//        ) {
//            return true
//        }
        return true
    }

    private fun msg(msg: String) {
        Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
    }
}