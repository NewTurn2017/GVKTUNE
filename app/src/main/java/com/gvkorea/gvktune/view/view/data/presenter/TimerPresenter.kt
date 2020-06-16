package com.gvkorea.gvktune.view.view.data.presenter

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.CountDownTimer
import android.text.format.DateFormat
import android.widget.Toast
import com.gvkorea.gvktune.view.view.data.DataFragment
import com.gvkorea.gvktune.view.view.data.DataFragment.Companion.remainingMin
import com.gvkorea.gvktune.view.view.data.util.PrefUtil
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.timer_main.*
import java.util.*

class TimerPresenter(val view: DataFragment, val presenter: DataPresenter) {

    var mYear: Int = 0
    var mMonth: Int = 0
    var mDate: Int = 0
    var mHour: Int = 0
    var mMinute: Int = 0

    var timerState = DataFragment.TimerState.Stopped
    lateinit var timer: CountDownTimer
    var timerLengthSeconds: Long = 0
    var secondsRemaining = 0L


    fun initTimer() {
        timerState = PrefUtil.getTimerState(view.requireContext())

        if (timerState == DataFragment.TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()

        secondsRemaining =
            if (timerState == DataFragment.TimerState.Running || timerState == DataFragment.TimerState.Paused)
                PrefUtil.getSecondsRemaining(view.requireContext())
            else
                timerLengthSeconds

        if (timerState == DataFragment.TimerState.Running)
            startTimer()

        updateButtons()
        updateCountdownUI()
    }

    fun updateButtons() {
        when (timerState) {
            DataFragment.TimerState.Running -> {
                view.btn_timerPlay.isEnabled = false
                view.btn_timerPause.isEnabled = true
                view.btn_timerStop.isEnabled = true
            }
            DataFragment.TimerState.Stopped -> {
                view.btn_timerPlay.isEnabled = true
                view.btn_timerPause.isEnabled = false
                view.btn_timerStop.isEnabled = false
            }
            DataFragment.TimerState.Paused -> {
                view.btn_timerPlay.isEnabled = true
                view.btn_timerPause.isEnabled = false
                view.btn_timerStop.isEnabled = true
            }
        }
    }

    fun startTimer() {
        timerState = DataFragment.TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {

            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength() {
        val lengthInMinutes = PrefUtil.getTimerLength(view.requireContext())
        timerLengthSeconds = (lengthInMinutes * 60L)
        view.progress_countdown.max = timerLengthSeconds.toInt()
    }

    fun updateCountdownUI() {

        val countDown = splitToComponentTimes(secondsRemaining)

        val hour = if (countDown[0].toString().length == 2)
            countDown[0].toString()
        else
            "0${countDown[0]}"
        val minute = if (countDown[1].toString().length == 2)
            countDown[1].toString()
        else
            "0${countDown[1]}"
        val second = if (countDown[2].toString().length == 2)
            countDown[2].toString()
        else
            "0${countDown[2]}"


        val countDownStr = "$hour:$minute:$second"
        view.tv_countdown.text = countDownStr
        view.progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    fun splitToComponentTimes(secondsRemaining: Long): IntArray {
        val value = secondsRemaining
        val hour = (value / 3600).toInt()
        var remainder = (value - hour * 3600).toInt()
        var min = remainder / 60
        remainder -= min * 60
        val sec = remainder
        return intArrayOf(hour, min, sec)
    }

    fun onTimerFinished() {
        timerState = DataFragment.TimerState.Stopped

        setNewTimerLength()

        view.progress_countdown.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds, view.requireContext())
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()
        presenter.gainDataStart()



    }

    fun onTimerFinishedForReservStart() {
        timerState = DataFragment.TimerState.Stopped

        setNewTimerLength()

        view.progress_countdown.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds, view.requireContext())
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()

    }

    private fun setPreviousTimerLength() {
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(view.requireContext())
        view.progress_countdown.max = timerLengthSeconds.toInt()
    }


    fun createDateDialog() {
        val calendar = Calendar.getInstance()

        val YEAR = calendar.get(Calendar.YEAR)
        val MONTH = calendar.get(Calendar.MONTH)
        val DATE = calendar.get(Calendar.DATE)
        val datePickerDialog = view.context?.let {
            DatePickerDialog(
                it,
                DatePickerDialog.OnDateSetListener { v, year, month, date ->
                    mYear = year
                    mMonth = month
                    mDate = date
                    val dateString = "$year 년 ${month + 1} 월 $date 일"
                    view.tv_reservDate.text = dateString
                },
                YEAR,
                MONTH,
                DATE
            )
        }
        datePickerDialog!!.show()
    }

    fun createTimeDialog() {
        val calendar = Calendar.getInstance()
        val HOUR = calendar.get(Calendar.HOUR_OF_DAY)
        val MINUTE = calendar.get(Calendar.MINUTE)

        val is24HourFormat = DateFormat.is24HourFormat(view.context)

        val timePickerDialog =
            TimePickerDialog(view.context, TimePickerDialog.OnTimeSetListener { v, hour, minute ->
                mHour = hour
                mMinute = minute
                val timeString = "$hour 시 $minute 분"
                view.tv_reservTime.text = timeString
            }, HOUR, MINUTE, is24HourFormat)

        timePickerDialog.show()

    }

    fun start() {
        val calendar = Calendar.getInstance()
        calendar.set(mYear, mMonth, mDate, mHour, mMinute, 0)
        val setTime = calendar.timeInMillis
        val currentTime = System.currentTimeMillis()
        val remainingTime = setTime - currentTime
        val remainingMinute = remainingTime / 1000 / 60

        remainingMin = remainingMinute.toInt()
        if(remainingMin < 0){
            Toast.makeText(view.context, "예약 설정 시간이 현재시간 이전입니다. 다시 설정하세요.", Toast.LENGTH_SHORT).show()
        }else{
            if(timerState == DataFragment.TimerState.Paused || timerState == DataFragment.TimerState.Running){
                timer.cancel()
            }
            onTimerFinishedForReservStart()
            initTimer()
        }


    }

}