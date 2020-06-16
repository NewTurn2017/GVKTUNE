package com.gvkorea.gvktune.view.presenter

import android.content.Intent
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
                "data" -> {
                    mainActivity.replace(R.id.fragment_container, view.dataFragment)
                    nowFragment = view.dataFragment
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

    fun launchTuneApp() {
//        view.activity?.moveTaskToBack(true)
//        view.activity?.finishAndRemoveTask()
//        android.os.Process.killProcess(android.os.Process.myPid())
        view.mainActivity.disconnect()
        val intent = view.activity?.packageManager?.getLaunchIntentForPackage("com.gvkorea.gvs1000_dsp")
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        view.activity?.startActivity(intent)
    }
}