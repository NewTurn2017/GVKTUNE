package com.gvkorea.gvktune.view.view.data

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.gvkorea.gvktune.MainActivity.Companion.prefSettings
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.util.PrefSettings
import com.gvkorea.gvktune.view.view.data.listener.DataButtonListener
import com.gvkorea.gvktune.view.view.data.listener.TimerButtonListener
import com.gvkorea.gvktune.view.view.data.presenter.DataPresenter
import com.gvkorea.gvktune.view.view.data.presenter.TimerPresenter
import com.gvkorea.gvktune.view.view.data.util.PrefUtil
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31
import com.gvkorea.gvktune.view.view.data.util.chart.ChartLayoutBarChartForRTA
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.fragment_data.view.*
import kotlinx.android.synthetic.main.timer_main.*

class DataFragment : Fragment() {

    enum class TimerState {
        Stopped, Paused, Running
    }

    lateinit var recordAudioRTA_31: RecordAudioRTA_31
    private var handler = Handler()
    private var tx_buff = ByteArray(13)

    private lateinit var presenter: DataPresenter
    private lateinit var timerPresenter: TimerPresenter
    private lateinit var pref: SharedPreferences

    val MODEL_GVA200 = "GVA-200"
    val MODEL_GVA300 = "GVA-300"
    val MODEL_GVA500 = "GVA-500"
    val MODEL_GVA700 = "GVA-700"
    val MODEL_GVA900 = "GVA-900"
    val MODEL_GVS200A = "GVS-200A"
    val MODEL_GVS500A = "GVS-500A"
    val MODEL_GVS700A = "GVS-700A"
    val MODEL_GVS200B = "GVS-200B"
    val MODEL_GVS200BA = "GVS-200BA"
    val MODEL_GVS400B = "GVS-400B"
    val MODEL_GVS500B = "GVS-500B"
    val MODEL_GVS500BA = "GVS-500BA"
    val MODEL_GVAS50 = "GVAS-50"
    val MODEL_GVS200 = "GVS-200"
    val MODEL_GVS300 = "GVS-300"
    val MODEL_GVS400 = "GVS-400"
    val MODEL_GVS500 = "GVS-500"
    val MODEL_GVS700 = "GVS-700"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = PreferenceManager.getDefaultSharedPreferences(this.requireActivity())

        init_ChartLayout()
        presenter = DataPresenter(this, handler, tx_buff, pref)
        timerPresenter = TimerPresenter(this, presenter)
        init_listener()
        timerPresenter.initTimer()
        tv_reverbTime.text = prefSettings.getReverbTimePref() + " (sec)"

    }

    private fun registerAdapter(
        context: Context,
        list: ArrayList<String>,
        spinner: Spinner
    ) {
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown)
        adapter.notifyDataSetChanged()
        spinner.adapter = adapter
    }


    private fun init_ChartLayout() {
        val chartLayout = ChartLayoutBarChartForRTA(view?.context!!, mChart_RMS)
        chartLayout.initBarChartLayout_31(110f, 10f)
    }

    private val listModel = arrayListOf(
        MODEL_GVA200, MODEL_GVA300, MODEL_GVA500,
        MODEL_GVA700, MODEL_GVA900, MODEL_GVS200A,
        MODEL_GVS500A, MODEL_GVS700A, MODEL_GVS200B,
        MODEL_GVS200BA, MODEL_GVS400B, MODEL_GVS500B,
        MODEL_GVS500BA, MODEL_GVAS50, MODEL_GVS200,
        MODEL_GVS300, MODEL_GVS400, MODEL_GVS500, MODEL_GVS700
    )

    private fun init_listener() {
        btn_RTA.setOnClickListener(DataButtonListener(this, presenter))
        btn_noise_on.setOnClickListener(DataButtonListener(this, presenter))
        btn_noise_off.setOnClickListener(DataButtonListener(this, presenter))
        btn_start.setOnClickListener(DataButtonListener(this, presenter))
        btn_stop.setOnClickListener(DataButtonListener(this, presenter))
        btn_setDate.setOnClickListener(TimerButtonListener(this, timerPresenter))
        btn_setTime.setOnClickListener(TimerButtonListener(this, timerPresenter))
        btn_reservStart.setOnClickListener(TimerButtonListener(this, timerPresenter))
        btn_timerPlay.setOnClickListener(TimerButtonListener(this, timerPresenter))
        btn_timerPause.setOnClickListener(TimerButtonListener(this, timerPresenter))
        btn_timerStop.setOnClickListener(TimerButtonListener(this, timerPresenter))

        registerAdapter(this.requireContext(), listModel, sp_dataSpkModel)
    }

    fun recordTaskStart_31() {
        handler.postDelayed({
            if (!octaveStarted31) {
                octaveStarted31 = true
                mChart_RMS.clear()
                recordAudioRTA_31 = RecordAudioRTA_31(mChart_RMS, this.requireView())
                recordAudioRTA_31.execute()
            }
        }, 100)
    }

    fun recordTaskStop_31() {
        if (octaveStarted31) {
            octaveStarted31 = false
            recordAudioRTA_31.cancel(true)
        }
    }

    override fun onPause() {
        super.onPause()
        if (timerPresenter.timerState == TimerState.Running) {
            timerPresenter.timer.cancel()
        } else if (timerPresenter.timerState == TimerState.Paused) {
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerPresenter.timerLengthSeconds, this.requireContext())
        PrefUtil.setSecondsRemaining(timerPresenter.secondsRemaining, this.requireContext())
        PrefUtil.setTimerState(timerPresenter.timerState, this.requireContext())
    }

    override fun onStart() {
        super.onStart()
        recordTaskStart_31()
    }

    override fun onStop() {
        super.onStop()

        if (octaveStarted31) {
            recordTaskStop_31()
        }

    }
    companion object {
        var isStarted: Boolean = false
        var octaveStarted10 = false
        var octaveStarted31 = false
        var isOn = true
        var isOctave = false
        var averageCount = 1
        var remainingMin = 1
        var noiseVolume = -40

        var counter2 = 0
        var str = ""

    }
}