package com.gvkorea.gvktune.view.view.calibration

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.calibration.audio.RecordAudioCalib
import com.gvkorea.gvktune.view.view.calibration.audio.RecordAudioCalib.Companion.started
import com.gvkorea.gvktune.view.view.calibration.chart.ChartLayoutBarChart
import com.gvkorea.gvktune.view.view.calibration.listener.CalibListener
import com.gvkorea.gvktune.view.view.calibration.presenter.CalibPresenter
import kotlinx.android.synthetic.main.fragment_calib.*


class CalibFragment : Fragment() {

    lateinit var recordTaskCalib : RecordAudioCalib
    var handler = Handler()
    lateinit var presenter : CalibPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calib, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_ChartLayout(view)
        init_Listener(view)
    }

    private fun init_ChartLayout(view: View) {
        val chartLayout = ChartLayoutBarChart(view.context, barChart_calib)
        chartLayout.initBarChartLayout(120f, 20f)
    }

    private fun init_Listener(view: View){
        btn_Calib.setOnClickListener(CalibListener(view))
        btn_CalibReset.setOnClickListener(CalibListener(view))
    }

    override fun onStop() {
        super.onStop()
        Log.d("!!!!", "test2onStop")
        recordTaskStop()
    }

    override fun onStart() {
        super.onStart()
        recordTaskStart()

    }


    private fun recordTaskStart() {
        handler.postDelayed({
            if(!started){
                started = true
                barChart_calib.clear()
                recordTaskCalib = RecordAudioCalib(this, barChart_calib)
                recordTaskCalib.execute()
            }
        }, 100)
    }


    private fun recordTaskStop() {
        if(started){
            started = false
            recordTaskCalib.cancel(true)
        }
    }

}
