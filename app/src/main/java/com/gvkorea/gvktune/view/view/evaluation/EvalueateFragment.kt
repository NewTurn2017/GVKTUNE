package com.gvkorea.gvktune.view.view.evaluation

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.evaluation.listener.ButtonListener
import com.gvkorea.gvktune.view.view.evaluation.presenter.RtaPresenter
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta
import com.gvkorea.gvktune.view.view.evaluation.util.chart.ChartLayoutBarChartForRTA
import kotlinx.android.synthetic.main.fragment_rta.*


class EvalueateFragment : Fragment() {

    lateinit var presenter: RtaPresenter
    val handler = Handler()
    lateinit var recordAudioRta: RecordAudioRta

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = RtaPresenter(this, handler)
        initListener()
        init_ChartLayout()

    }


    private fun initListener() {
        btn_noiseOn.setOnClickListener(ButtonListener(presenter))
        btn_noiseOff.setOnClickListener(ButtonListener(presenter))
        btn_measureAvg.setOnClickListener(ButtonListener(presenter))
        btn_save.setOnClickListener(ButtonListener(presenter))
    }

    private fun init_ChartLayout() {
        val chartLayout = ChartLayoutBarChartForRTA(view?.context!!, mChart)
        chartLayout.initBarChartLayout(110f, 10f)
    }

    fun recordTaskStart() {
        handler.postDelayed({
            if (!isStarted) {
                isStarted = true
                mChart.clear()
                recordAudioRta = RecordAudioRta(mChart, this.view!!)
                recordAudioRta.execute()
            }
        }, 100)
    }

    fun recordTaskStop() {
        if (isStarted) {
            isStarted = false
            recordAudioRta.cancel(true)
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
        var isStarted = false
        var targetdB: Double = 100.0
        var isShow = false


    }

}
