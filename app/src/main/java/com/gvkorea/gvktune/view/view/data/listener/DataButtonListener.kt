package com.gvkorea.gvktune.view.view.data.listener

import android.view.View
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.data.DataFragment
import com.gvkorea.gvktune.view.view.data.DataFragment.Companion.isOn
import com.gvkorea.gvktune.view.view.data.DataFragment.Companion.noiseVolume
import com.gvkorea.gvktune.view.view.data.presenter.DataPresenter
import kotlinx.android.synthetic.main.fragment_data.*

class DataButtonListener(val view: DataFragment, val presenter: DataPresenter): View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id){

            R.id.btn_RTA -> {

                if(isOn){
                    view.btn_RTA.text = "START"
                    isOn = false
                    view.recordTaskStop_31()
                }else{
                    view.btn_RTA.text = "STOP"
                    isOn = true
                    view.recordTaskStart_31()
                }
            }


            R.id.btn_noise_on -> {
                presenter.noise(presenter.PINK, noiseVolume)

            }

            R.id.btn_noise_off -> {
                presenter.noise(presenter.NOISE_OFF, noiseVolume)

            }

            R.id.btn_start -> {
                presenter.gainDataStart()
            }

            R.id.btn_stop -> {
                presenter.gainDataStop()

            }




        }
    }

}