package com.gvkorea.gvktune.view.view.autotuning

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gvkorea.gvktune.MainActivity

import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.autotuning.listener.TuneButtonListener
import com.gvkorea.gvktune.view.view.autotuning.presenter.TunePresenter
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune
import com.gvkorea.gvktune.view.view.autotuning.util.chart.ChartLayoutBarChartForEQGraph
import com.gvkorea.gvktune.view.view.autotuning.util.chart.ChartLayoutLineChartForTune
import kotlinx.android.synthetic.main.fragment_tune.*

class TuneFragment : Fragment() {

    lateinit var presenter: TunePresenter
    val handler = Handler()
    lateinit var recordAudioTune: RecordAudioTune
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tune, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter =
            TunePresenter(
                this,
                handler
            )
        initListener()
        init_ChartLayout()
        tv_reverb_time.text = MainActivity.prefSettings.getReverbTimePref() + " sec"
    }

    private fun initListener() {
        btn_tune_start.setOnClickListener(TuneButtonListener(presenter))
        btn_tune_stop.setOnClickListener(TuneButtonListener(presenter))
        btn_showTable.setOnClickListener(TuneButtonListener(presenter))
        btn_showEQ.setOnClickListener(TuneButtonListener(presenter))

    }

    private fun init_ChartLayout() {
        init_lineChart()
        init_barChart()
    }



    fun init_lineChart() {
        lineChart = ChartLayoutLineChartForTune(this.context!!, chart_tune_line)
        targetValues = presenter.setTarget(targetdB.toInt()-15)
        lineChart.initLineChartLayout(100f, 20f)
        lineChart.initGraph(targetValues, "Target(dB)", Color.BLUE)
    }

    fun init_barChart(){
        barChart = ChartLayoutBarChartForEQGraph(this.context!!, chart_tune_bar)
        barChart.initLineChartLayout(15f, -15f)
        val curEQ = FloatArray(31)
        barChart.initGraph(curEQ)
    }

    fun recordTaskStart() {
        handler.postDelayed({
            if (!isStarted) {
                isStarted = true
                recordAudioTune = RecordAudioTune(this.view!!)
                recordAudioTune.execute()
            }
        }, 100)
    }

    fun recordTaskStop() {
        if (isStarted) {
            isStarted = false
            recordAudioTune.cancel(true)
        }
    }

    override fun onStart() {
        super.onStart()
        recordTaskStart()
    }

    override fun onStop() {
        super.onStop()
        recordTaskStop()
    }

    companion object {
        var averageCount = 1
        var currentNoise = 0
        var isStarted = false
        var noiseVolume = -40
        var targetdB: Double = 85.0
        lateinit var lineChart: ChartLayoutLineChartForTune
        lateinit var barChart: ChartLayoutBarChartForEQGraph
        var targetValues:FloatArray? = null
        var isShowTable = false
        var isShowEQ = false
    }

}
