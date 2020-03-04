package com.gvkorea.gvktune.view.view.rta

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.rta.listener.SourceCheckChangeListener
import com.gvkorea.gvktune.view.view.rta.listener.SourceSeekBarChangeListener
import com.gvkorea.gvktune.view.view.rta.presenter.NoisePresenter
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRTA
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRTA.Companion.isStartedAudio
import com.gvkorea.gvktune.view.view.rta.util.chart.ChartLayoutLineChartForRTA
import kotlinx.android.synthetic.main.fragment_rta.*


class RtaFragment : Fragment() {

    lateinit var presenter: NoisePresenter
    lateinit var recordAudio_rta: RecordAudioRTA
    val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = NoisePresenter(this)
        initListener()
        initchartLayout()
    }

    private fun initchartLayout() {
        val chartLayout = ChartLayoutLineChartForRTA(mChart)
        chartLayout.initLineChartLayout(110f, 0f)
    }

    private fun initListener() {
        rg_SorceSelect.setOnCheckedChangeListener(SourceCheckChangeListener(presenter))
        sb_source_gain.setOnSeekBarChangeListener(SourceSeekBarChangeListener(presenter))
    }

    override fun onStart() {
        super.onStart()
        recordAudio_recordStart()
    }

    private fun recordAudio_recordStart() {
        handler.postDelayed({
            isStartedAudio = true
            mChart.clear()
            recordAudio_rta = RecordAudioRTA(this, mChart)
            recordAudio_rta.execute()
        }, 100)
    }

    override fun onStop() {
        super.onStop()
        if (isStartedAudio){
            recordAudio_recordStop()
        }
    }

    private fun recordAudio_recordStop() {
        if (isStartedAudio) {
            isStartedAudio = false
            recordAudio_rta.cancel(true)
        }
    }

    companion object {
        var currentNoise = 0
        var isStarted = false
    }

}
