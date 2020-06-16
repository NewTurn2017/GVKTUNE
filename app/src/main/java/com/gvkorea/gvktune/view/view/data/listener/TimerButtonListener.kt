package com.gvkorea.gvktune.view.view.data.listener

import android.view.View
import android.widget.Toast
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.data.DataFragment
import com.gvkorea.gvktune.view.view.data.presenter.TimerPresenter
import kotlinx.android.synthetic.main.fragment_data.*

class TimerButtonListener(val view: DataFragment, val presenter: TimerPresenter) :
    View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_setDate -> {
                stopRTA()
                presenter.createDateDialog()
            }
            R.id.btn_setTime -> {
                stopRTA()
                presenter.createTimeDialog()

            }

            R.id.btn_reservStart -> {
                startRTA()
                Toast.makeText(view.context, "Play 버튼을 누르면 시작합니다.", Toast.LENGTH_SHORT).show()
                presenter.start()
            }

            R.id.btn_timerPlay -> {
                presenter.startTimer()
                presenter.timerState = DataFragment.TimerState.Running
                presenter.updateButtons()

            }

            R.id.btn_timerPause -> {
                presenter.timer.cancel()
                presenter.timerState = DataFragment.TimerState.Paused
                presenter.updateButtons()
            }

            R.id.btn_timerStop -> {
                presenter.timer.cancel()
                presenter.onTimerFinished()
            }
        }
    }

    fun stopRTA() {

        view.btn_RTA.text = "START"
        view.recordTaskStop_31()

    }

    fun startRTA() {
        view.btn_RTA.text = "STOP"
        view.recordTaskStart_31()
    }
}