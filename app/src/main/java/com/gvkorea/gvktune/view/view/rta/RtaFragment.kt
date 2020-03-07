package com.gvkorea.gvktune.view.view.rta

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.rta.listener.ButtonListener
import com.gvkorea.gvktune.view.view.rta.listener.SourceCheckChangeListener
import com.gvkorea.gvktune.view.view.rta.listener.SourceSeekBarChangeListener
import com.gvkorea.gvktune.view.view.rta.presenter.NoisePresenter
import com.gvkorea.gvktune.view.view.rta.presenter.RtaPresenter
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRTA
import kotlinx.android.synthetic.main.fragment_rta.*
import java.text.DecimalFormatSymbols
import java.text.DecimalFormat
import java.util.*


class RtaFragment : Fragment() {

    lateinit var presenter: NoisePresenter
    lateinit var rtaPresenter: RtaPresenter
    lateinit var prefs: SharedPreferences

    val handler = Handler()
    lateinit var bitmap: Bitmap
    lateinit var canvas: Canvas
    lateinit var paint: Paint

    lateinit var bitmap2: Bitmap
    lateinit var canvas2: Canvas
    lateinit var paint2: Paint

    lateinit var canvas3: Canvas
    lateinit var paint3: Paint

    lateinit var canvas4: Canvas
    lateinit var paint4: Paint

    lateinit var canvas5: Canvas
    lateinit var paint5: Paint

    lateinit var canvas6: Canvas
    lateinit var paint6: Paint

    val blockSize_buffer = 1024
    val blockSize_fft = 2048

    var threshold_height = 7
    val NUM_harmonics = 6

    // Valores pordefecto para el estudio de los armonicos
    var THRESHOLD = 100.0                     // los armonicos, depende del tamaño de la FFT
    var FRAMELENGTH = 20

    var chartHeight = 200f
    var blockSize_graph = 724f
    val valid = DoubleArray(NUM_harmonics)
    val amplitudes = DoubleArray(NUM_harmonics)
    var factor =
        Math.round(blockSize_graph.toDouble() / chartHeight.toDouble()).toInt() //adaptive
    var TEXT_SIZE = 40
    var TEXT_SIZE1 = 10*factor
    var TEXT_SIZE2 = 5*factor
    var TEXT_SIZE3 = 7*factor

    lateinit var df1: DecimalFormat
    val symbols = java.text.DecimalFormatSymbols(Locale.KOREA)

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
        rtaPresenter = RtaPresenter(this)
        initListener()
        initchartLayout()
        initGraph()

        symbols.decimalSeparator = '.'
        df1 = DecimalFormat("#.#", symbols)

    }

    private fun initGraph() {
        bitmap = Bitmap.createBitmap(blockSize_buffer, blockSize_fft, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        paint = Paint()
        paint .color = Color.GREEN
        imageView01.setImageBitmap(bitmap)

        bitmap2 = Bitmap.createBitmap(blockSize_buffer, TEXT_SIZE1, Bitmap.Config.ARGB_8888)
        canvas2 = Canvas(bitmap2)
        paint2 = Paint()
        paint2.color = Color.WHITE
        imageView02.setImageBitmap(bitmap)

        //to draw the value of the SNR


        //to draw the value of the SNR
        canvas3 = Canvas(bitmap)
        paint3 = Paint()
        paint3.color = Color.MAGENTA

        // para dibujar texto (frecuencia) en el espectrograma

        // para dibujar texto (frecuencia) en el espectrograma
        canvas4 = Canvas(bitmap)
        paint4 = Paint()
        paint4.color = Color.YELLOW

        // para dibujar el promedio de la magnitud de los armonicos en el espectrograma

        // para dibujar el promedio de la magnitud de los armonicos en el espectrograma
        canvas5 = Canvas(bitmap)
        paint5 = Paint()
        paint5.color = Color.RED

        // para dibujar el umbral establecido por el usuario

        // para dibujar el umbral establecido por el usuario
        canvas6 = Canvas(bitmap)
        paint6 = Paint()
        paint6.color = Color.CYAN

        drawAxisFrequencies()
    }

    fun drawAxisFrequencies() {
        canvas2.drawColor(Color.BLACK)
        paint2.isAntiAlias = true
        paint2.isFilterBitmap = true

        //Values ​​to be displayed on the X axis

        //Values ​​to be displayed on the X axis
        val bands = intArrayOf(220, 440, 880, 1320, 1760, 2350)
        paint2.strokeWidth = 5f
        canvas2.drawLine(0f, 0f, blockSize_buffer.toFloat(), 0f, paint2)

        paint2.textSize = TEXT_SIZE3.toFloat()
        canvas2.drawText(
            bands[0].toString(),
            bands[0] / factor - TEXT_SIZE3 / 2.toFloat(),
            TEXT_SIZE3.toFloat(),
            paint2
        )
        canvas2.drawText(
            bands[1].toString(),
            bands[1] / factor - TEXT_SIZE3 / 2.toFloat(),
            TEXT_SIZE3.toFloat(),
            paint2
        )
        canvas2.drawText(
            bands[2].toString(),
            bands[2] / factor - TEXT_SIZE3 / 2.toFloat(),
            TEXT_SIZE3.toFloat(),
            paint2
        )
        canvas2.drawText(
            bands[3].toString(),
            bands[3] / factor - TEXT_SIZE3 / 2.toFloat(),
            TEXT_SIZE3.toFloat(),
            paint2
        )
        canvas2.drawText(
            bands[4].toString(),
            bands[4] / factor - TEXT_SIZE3 / 2.toFloat(),
            TEXT_SIZE3.toFloat(),
            paint2
        )
        canvas2.drawText(
            bands[5].toString(),
            bands[5] / factor - TEXT_SIZE3 / 2.toFloat(),
            TEXT_SIZE3.toFloat(),
            paint2
        )
        canvas2.drawText("Hz", blockSize_buffer - TEXT_SIZE1.toFloat(), TEXT_SIZE3.toFloat(), paint2)

        imageView02.invalidate()
    }

    private fun initchartLayout() {
    }

    private fun initListener() {

        startStopButton.setOnClickListener(ButtonListener(rtaPresenter))

        rg_SorceSelect.setOnCheckedChangeListener(SourceCheckChangeListener(presenter))
        sb_source_gain.setOnSeekBarChangeListener(SourceSeekBarChangeListener(presenter))
    }

    override fun onStart() {
        super.onStart()
        startStopButton.text = "ON"
        prefs = PreferenceManager.getDefaultSharedPreferences(this.context)
        AUTODETECCION = prefs.getBoolean("Default_Option", false)

        if(prefs.getInt("thresoldPref", 7)< 7){
            THRESHOLD = 80.0
        }else{
            THRESHOLD = (14*(prefs.getInt("thresoldPref", 7))).toDouble()
        }

        threshold_height = prefs.getInt("thresoldPref", 7)
    }

    override fun onPause() {
        super.onPause()
        if(isStartedAudio){
            isStartedAudio = false
        }
    }

    companion object {
        var currentNoise = 0
        var isStarted = false
        lateinit var recordAudio_rta: RecordAudioRTA
        /// PREFERENCIAS
        var AUTODETECCION = false
        var isStartedAudio = false

    }

}
