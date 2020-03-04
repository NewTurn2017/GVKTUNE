package com.gvkorea.gvktune.view.view.rta.util.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.AsyncTask
import android.util.Log
import com.github.mikephil.charting.charts.LineChart
import com.gvkorea.gvktune.util.fft.RealDoubleFFT
import com.gvkorea.gvktune.view.view.rta.RtaFragment
import com.gvkorea.gvktune.view.view.rta.util.Maximum
import kotlinx.android.synthetic.main.fragment_rta.*
import java.lang.Math.*
import java.text.DecimalFormat
import kotlin.math.*
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class RecordAudioRTA(val view: RtaFragment, val lineChart: LineChart) : AsyncTask<Unit, ShortArray, Unit>() {

    private val frequency = 44100
    private val channelConfiguration = AudioFormat.CHANNEL_IN_MONO
    private val audioEncoding = AudioFormat.ENCODING_PCM_16BIT
    private val blockSize_buffer = 4096
    private val blockSize_fft = 8192
    lateinit var transformer : RealDoubleFFT
    var bufferReadResult = 0

    ////block size = 8192

    private val INDEX_20HZ = 7
    private val INDEX_25HZ = 9
    private val INDEX_32HZ = 11
    private val INDEX_40HZ = 14
    private val INDEX_50HZ = 18
    private val INDEX_63HZ = 23
    private val INDEX_80HZ = 29
    private val INDEX_100HZ = 37
    private val INDEX_125HZ = 46
    private val INDEX_160HZ = 59
    private val INDEX_200HZ = 74
    private val INDEX_250HZ = 92
    private val INDEX_315HZ = 117
    private val INDEX_400HZ = 148
    private val INDEX_500HZ = 185
    private val INDEX_630HZ = 234
    private val INDEX_800HZ = 297
    private val INDEX_1000HZ = 371
    private val INDEX_1250HZ = 464
    private val INDEX_1600HZ = 594
    private val INDEX_2000HZ = 743
    private val INDEX_2500HZ = 928
    private val INDEX_3150HZ = 1170
    private val INDEX_4000HZ = 1486
    private val INDEX_5000HZ = 1857
    private val INDEX_6300HZ = 2340
    private val INDEX_8000HZ = 2972
    private val INDEX_10000HZ = 3715
    private val INDEX_12500HZ = 4643
    private val INDEX_16000HZ = 5944
    private val INDEX_20000HZ = 7430

    lateinit var audioRecord: AudioRecord


    override fun doInBackground(vararg p0: Unit?): Unit? {
        try {
            val bufferSize = AudioRecord.getMinBufferSize(
                frequency,
                channelConfiguration, audioEncoding
            )

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION, frequency,
                channelConfiguration, audioEncoding, bufferSize
            )

            val buffer = ShortArray(blockSize_buffer)
            audioRecord.startRecording()

            while (isStartedAudio) {
                bufferReadResult = audioRecord.read(buffer, 0, blockSize_buffer)

                publishProgress(buffer)
            }
            audioRecord.stop()
        } catch (t: Throwable) {
            Log.e("AudioRecord", "Recording Failed")
        }
        return null
    }

    override fun onProgressUpdate(vararg toTransform: ShortArray) {

        var maximum: Double = 0.0
        var variance: Double = 0.0
        var plot = DoubleArray(blockSize_fft)
        transformer = RealDoubleFFT(blockSize_fft)

        for (i in 0 until bufferReadResult){
            plot[i * 2] = toTransform[0][i].toDouble()
            plot[i * 2 + 1] = 0.0
        }

        maximum = max(plot, 0, plot.size).value

        plot = normalize(plot)

        variance = calculateVariance(plot)

        if (AUTODETECCION){
            if(validos[1]!=0){ // Si han aprecido armonicos
                if((maximum>=800)&&(variance>0.04)){

                    isStartedAudio = false;
                    view.btn_audioStart.text = "ON";
                    this.cancel(true)

                }
            }
        }

        // 여기서 부터 이어서

    }

    private fun calculateVariance(plot: DoubleArray): Double {
        val N = plot.size
        val mean = calculateMean(plot)
        var variance = 0.0
        for (k in 0 until N) {
            variance += (plot[k] - mean).pow(2.0)
        }
        variance /= (N - 1)
        return variance

    }

    private fun calculateMean(plot: DoubleArray): Double {
        val N: Int = plot.size
        var mean = 0.0
        for (k in 0 until N) {
            mean += plot[k]
        }
        mean /= N
        return mean

    }

    private fun normalize(plot: DoubleArray): DoubleArray {

        var max = 0.0

        for (i in plot.indices) {
            if(abs(plot[i]) > max) {
                max = abs(plot[i])
            }
        }
        for (i in plot.indices){
            plot[i] = plot[i] / max
        }
        return plot
    }

    fun max(x: DoubleArray, first: Int, end: Int): Maximum {
        val mMaxsimum = Maximum()
        for (i in first until end){
            if(abs(x[i]) >= mMaxsimum.value) {
                mMaxsimum.value = abs(x[i])
                mMaxsimum.pos = i
            }
        }
        return mMaxsimum
    }




    fun freq_value(i: Int, toTransform: DoubleArray, arrayNum: Int) {
        val dbfsFinal = freqAverageCalc(i, toTransform)
        rmsValues31[arrayNum] = dbfsFinal
    }

    private fun freqAverageCalc(i: Int, toTransform: DoubleArray): Double {
        var dbfs = 0.0
        var dbfsFinal = 0.0

        if (i == INDEX_20HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_25HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_32HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_40HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_50HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_63HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_80HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_100HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_125HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_160HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_200HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_250HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_315HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_400HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_500HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_630HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_800HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_1000HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_1250HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_1600HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_2000HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_2500HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_3150HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_4000HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_5000HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_6300HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_8000HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_10000HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_12500HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_16000HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_20000HZ) {
            dbfs += toTransfromTodbfs(toTransform, i)
            dbfsFinal = dbfsAvg(dbfs)
        }
        return dbfsFinal
    }

    private fun toTransfromTodbfs(toTransform: DoubleArray, i: Int): Double {

        return pow(
            10.0,
            ((20.0 * log10(toTransform[i]) + 50.5) * 100.0).roundToInt() / 100.0 / 10
        ) // calib 수정

    }

    private fun dbfsAvg(dbfsSum: Double): Double {
        return (10.0 * log10(dbfsSum) * 100.0).roundToLong() / 100.0
    }

    private fun doubleToString(value: Double): String {
        val format = DecimalFormat("##.#")
        return format.format(value)
    }

    companion object {
        var isStartedAudio = false
        var isMeasure = false
        var avgStart = false
        var averageCount = 1

        var freq1Sum = ArrayList<Double>()
        var freq2Sum = ArrayList<Double>()
        var freq3Sum = ArrayList<Double>()
        var freq4Sum = ArrayList<Double>()
        var freq5Sum = ArrayList<Double>()
        var freq6Sum = ArrayList<Double>()
        var freq7Sum = ArrayList<Double>()
        var freq8Sum = ArrayList<Double>()
        var freq9Sum = ArrayList<Double>()
        var freq10Sum = ArrayList<Double>()
        var freq11Sum = ArrayList<Double>()
        var freq12Sum = ArrayList<Double>()
        var freq13Sum = ArrayList<Double>()
        var freq14Sum = ArrayList<Double>()
        var freq15Sum = ArrayList<Double>()
        var freq16Sum = ArrayList<Double>()
        var freq17Sum = ArrayList<Double>()
        var freq18Sum = ArrayList<Double>()
        var freq19Sum = ArrayList<Double>()
        var freq20Sum = ArrayList<Double>()
        var freq21Sum = ArrayList<Double>()
        var freq22Sum = ArrayList<Double>()
        var freq23Sum = ArrayList<Double>()
        var freq24Sum = ArrayList<Double>()
        var freq25Sum = ArrayList<Double>()
        var freq26Sum = ArrayList<Double>()
        var freq27Sum = ArrayList<Double>()
        var freq28Sum = ArrayList<Double>()
        var freq29Sum = ArrayList<Double>()
        var freq30Sum = ArrayList<Double>()
        var freq31Sum = ArrayList<Double>()
        var freqSum = ArrayList<String>()
    }
}