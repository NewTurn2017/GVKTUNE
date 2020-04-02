package com.gvkorea.gvktune.view.view.rt

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.data.Entry

import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.rt.listener.CheckChangeListener
import com.gvkorea.gvktune.view.view.rt.listener.NoiseListener
import com.gvkorea.gvktune.view.view.rt.presenter.NoisePresenter
import com.gvkorea.gvktune.view.view.rt.util.chart.ChartLayoutLineChart
import kotlinx.android.synthetic.main.fragment_reverb.*

class ReverbFragment : Fragment() {

    private lateinit var presenter: NoisePresenter
    private val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reverb, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = NoisePresenter(this, handler)
        initListener()
        initChartLayout()
    }

    private fun initChartLayout() {
        chart = ChartLayoutLineChart(this.context!!, mLineChart)
        chart.initLineChartLayout()
    }

    private fun initListener() {
        btn_noise.setOnClickListener(NoiseListener(presenter))
        btn_noiseClap.setOnClickListener(NoiseListener(presenter))
        cb_repeat.setOnCheckedChangeListener(CheckChangeListener(presenter))
        sp_volume.setSelection(0)
    }

    companion object {
        lateinit var chart: ChartLayoutLineChart
        var isRepeat = false
        var repeatCount = 0
        lateinit var arrList: ArrayList<FloatArray?>
        lateinit var valuesArrays: ArrayList<ArrayList<Entry>>
        lateinit var labelList: ArrayList<String>
    }

}
