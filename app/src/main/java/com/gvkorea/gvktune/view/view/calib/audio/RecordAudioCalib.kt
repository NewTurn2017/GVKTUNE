package com.gvkorea.gvktune.view.view.calib.audio

import android.graphics.Color
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.AsyncTask
import android.util.Log
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.gvkorea.gvktune.MainActivity
import com.gvkorea.gvktune.MainActivity.Companion.CALIBRATION
import com.gvkorea.gvktune.util.CSVRead
import com.gvkorea.gvktune.util.fft.RealDoubleFFT
import com.gvkorea.gvktune.view.view.calib.CalibFragment

class RecordAudioCalib(val view: CalibFragment, var barChart: BarChart) : AsyncTask<Unit, DoubleArray, Unit>() {

    private val frequency = 44100
    private val channelConfiguration = AudioFormat.CHANNEL_IN_MONO
    private val audioEncoding = AudioFormat.ENCODING_PCM_16BIT
    private val blockSize = 8192
    private val transformer = RealDoubleFFT(blockSize)
    private var toTransformAvg = DoubleArray(blockSize)
    private var chartValue = ArrayList<BarEntry>()
    private var barDataSet = BarDataSet(chartValue, null)
    private val calib_data = CSVRead().readCalibCsv(view.context?.assets!!, MainActivity.selectedMicName)

    private val INDEX_1000HZ    = 371
    private val LOW_1000HZ   = 330
    private val HIGH_1000HZ   = 417

    override fun doInBackground(vararg p0: Unit?): Unit? {
        try {
            val bufferSize = AudioRecord.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding)

            val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.VOICE_RECOGNITION, frequency,
                    channelConfiguration, audioEncoding, bufferSize)

            val buffer = ShortArray(blockSize)
            val toTransform = DoubleArray(blockSize)


            audioRecord.startRecording()

            while (started) {
                val bufferReadResult = audioRecord.read(buffer, 0, blockSize)

                var i = 0
                while (i < blockSize && i < bufferReadResult) {
                    toTransform[i] = buffer[i].toDouble() / java.lang.Short.MAX_VALUE // 32,768
                    i++
                }
                transformer.ft(toTransform)
                publishProgress(toTransform)
            }
            audioRecord.stop()
            audioRecord.release()
        } catch (t: Throwable) {
            Log.e("AudioRecord", "Recording Failed")
        }
        return null
    }

    override fun onProgressUpdate(vararg values: DoubleArray) {

        chartValue = ArrayList()

        for (i in 0 until blockSize){
            if(values[0][i] < 0){
                toTransformAvg[i] = -values[0][i]
            }else{
                toTransformAvg[i] = values[0][i]
            }
        }
        for (i in 1 until toTransformAvg.size){
            if (i == INDEX_1000HZ) {
                freq_value(i, toTransformAvg)
            }
        }

        if(barChart.data != null && barChart.data.dataSetCount > 0){
            chartValue.add(BarEntry(0f, rmsValue.toFloat()))
            barDataSet.values = chartValue
            barDataSet.setDrawValues(true)
            barDataSet.valueTextColor = Color.RED
            barDataSet.valueTextSize = 12.0f
            barChart.data.notifyDataChanged()
            barChart.notifyDataSetChanged()
            barChart.invalidate()

        }else{
            barDataSet = BarDataSet(chartValue, "1 khz 음압")
            barDataSet.setDrawIcons(false)
            barDataSet.setGradientColor(Color.GREEN, Color.RED)
            barDataSet.setDrawValues(false)
            barDataSet.formLineWidth = 1f
            val data = BarData(barDataSet)
            barChart.data = data
            barChart.invalidate()
        }
    }

    private fun readMicCalib(i: Int): Double {


        val delta = calib_data[i-4][1]
        return delta.toDouble()
    }


    fun freq_value(i: Int, toTransform: DoubleArray){

        rmsValue = freqAverageCalc(i, toTransform)
    }

    private fun freqAverageCalc(i: Int, toTransform: DoubleArray): Double {
        var dbfs = 0.0
        var dbfsFinal = 0.0

        if(i == INDEX_1000HZ){
            for (j in LOW_1000HZ..HIGH_1000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        }
        return dbfsFinal
    }

    private fun toTransfromTodbfs(toTransform: DoubleArray, i: Int): Double {

        val delta: Double = readMicCalib(i)
        return Math.pow(10.0, Math.round((20.0 * Math.log10(toTransform[i]) + 50.5 + CALIBRATION + delta) * 100.0) / 100.0 / 10) // calib 수정

    }

    private fun dbfsAvg(dbfsSum: Double): Double {
        return Math.round(10.0 * Math.log10(dbfsSum) * 100.0) / 100.0
    }

    companion object {
        var started = false
        var rmsValue = 0.0
    }

}