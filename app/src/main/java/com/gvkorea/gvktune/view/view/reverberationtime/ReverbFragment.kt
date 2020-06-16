package com.gvkorea.gvktune.view.view.reverberationtime

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.reverberationtime.listener.ReverbListener
import com.gvkorea.gvktune.view.view.reverberationtime.presenter.ReverbPresenter
import kotlinx.android.synthetic.main.fragment_reverb.*

class ReverbFragment : Fragment() {

    private lateinit var presenter: ReverbPresenter
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

        presenter = ReverbPresenter(this, handler)
        initListener()
    }



    private fun initListener() {
        btn_noiseClap.setOnClickListener(ReverbListener(presenter))
        btn_testReset.setOnClickListener(ReverbListener(presenter))
    }

    companion object {
        var reverbCount = 0
    }

}
