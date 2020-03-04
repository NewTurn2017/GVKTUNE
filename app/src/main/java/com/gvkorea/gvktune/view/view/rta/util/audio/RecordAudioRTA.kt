package com.gvkorea.gvktune.view.view.rta.util.audio

import android.graphics.Color
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.AsyncTask
import android.util.Log
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.gvkorea.gvktune.util.fft.RealDoubleFFT
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class RecordAudioRTA(val lineChart: LineChart) : AsyncTask<Unit, DoubleArray, Unit>() {

    private var valSum = DoubleArray(31)
    private var toTransFormCount = 0
    private val frequency = 44100
    private val channelConfiguration = AudioFormat.CHANNEL_IN_MONO
    private val audioEncoding = AudioFormat.ENCODING_PCM_16BIT
    private val blockSize = 8192
    private val transformer = RealDoubleFFT(blockSize)
    private var toTransformAvg = DoubleArray(blockSize)
    private var lineValues = ArrayList<Entry>()
    private var lineDataSet = LineDataSet(lineValues, null)

    private var rmsValues31 = DoubleArray(31)
    private var rmsValues = DoubleArray(blockSize)

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


    override fun doInBackground(vararg p0: Unit?): Unit? {
        try {
            val bufferSize = AudioRecord.getMinBufferSize(
                frequency,
                channelConfiguration, audioEncoding
            )

            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION, frequency,
                channelConfiguration, audioEncoding, bufferSize
            )

            val buffer = ShortArray(blockSize)
            val toTransform = DoubleArray(blockSize)


            audioRecord.startRecording()

            while (isStartedAudio) {
                val bufferReadResult = audioRecord.read(buffer, 0, blockSize)

                var i = 0
                while (i < blockSize && i < bufferReadResult) {
                    toTransform[i] = buffer[i].toDouble() / java.lang.Short.MAX_VALUE // 32,768
                    i++
                }
                transformer.ft(toTransform)
                publishProgress(toTransform)
//                counter1++
            }
            audioRecord.stop()
            audioRecord.release()
        } catch (t: Throwable) {
            Log.e("AudioRecord", "Recording Failed")
        }
        return null
    }

    override fun onProgressUpdate(vararg values: DoubleArray) {

        lineValues = ArrayList()
        for (i in 0 until blockSize) {
            if (values[0][i] < 0) {
                toTransformAvg[i] = -values[0][i]
            } else {
                toTransformAvg[i] = values[0][i]
            }
        }

        var arrayNum = 0

        for (i in 1 until toTransformAvg.size) {

            if (i == INDEX_20HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_25HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_32HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_40HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_50HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_63HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_80HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_100HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_125HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_160HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_200HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_250HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_315HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_400HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_500HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_630HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_800HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_1000HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_1250HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_1600HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_2000HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_2500HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_3150HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_4000HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_5000HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_6300HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_8000HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_10000HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_12500HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_16000HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_20000HZ) {
                freq_value(i, toTransformAvg, arrayNum)
                arrayNum++
            }

            if (arrayNum == 31) {

                if (avgStart && isMeasure) {

                    freq1Sum.add(rmsValues31[0])
                    freq2Sum.add(rmsValues31[1])
                    freq3Sum.add(rmsValues31[2])
                    freq4Sum.add(rmsValues31[3])
                    freq5Sum.add(rmsValues31[4])
                    freq6Sum.add(rmsValues31[5])
                    freq7Sum.add(rmsValues31[6])
                    freq8Sum.add(rmsValues31[7])
                    freq9Sum.add(rmsValues31[8])
                    freq10Sum.add(rmsValues31[9])
                    freq11Sum.add(rmsValues31[10])
                    freq12Sum.add(rmsValues31[11])
                    freq13Sum.add(rmsValues31[12])
                    freq14Sum.add(rmsValues31[13])
                    freq15Sum.add(rmsValues31[14])
                    freq16Sum.add(rmsValues31[15])
                    freq17Sum.add(rmsValues31[16])
                    freq18Sum.add(rmsValues31[17])
                    freq19Sum.add(rmsValues31[18])
                    freq20Sum.add(rmsValues31[19])
                    freq21Sum.add(rmsValues31[20])
                    freq22Sum.add(rmsValues31[21])
                    freq23Sum.add(rmsValues31[22])
                    freq24Sum.add(rmsValues31[23])
                    freq25Sum.add(rmsValues31[24])
                    freq26Sum.add(rmsValues31[25])
                    freq27Sum.add(rmsValues31[26])
                    freq28Sum.add(rmsValues31[27])
                    freq29Sum.add(rmsValues31[28])
                    freq30Sum.add(rmsValues31[29])
                    freq31Sum.add(rmsValues31[30])
                }
                if (avgStart && !isMeasure) {

                    freqSum.add(doubleToString(freq1Sum.average()))
                    freqSum.add(doubleToString(freq2Sum.average()))
                    freqSum.add(doubleToString(freq3Sum.average()))
                    freqSum.add(doubleToString(freq4Sum.average()))
                    freqSum.add(doubleToString(freq5Sum.average()))
                    freqSum.add(doubleToString(freq6Sum.average()))
                    freqSum.add(doubleToString(freq7Sum.average()))
                    freqSum.add(doubleToString(freq8Sum.average()))
                    freqSum.add(doubleToString(freq9Sum.average()))
                    freqSum.add(doubleToString(freq10Sum.average()))
                    freqSum.add(doubleToString(freq11Sum.average()))
                    freqSum.add(doubleToString(freq12Sum.average()))
                    freqSum.add(doubleToString(freq13Sum.average()))
                    freqSum.add(doubleToString(freq14Sum.average()))
                    freqSum.add(doubleToString(freq15Sum.average()))
                    freqSum.add(doubleToString(freq16Sum.average()))
                    freqSum.add(doubleToString(freq17Sum.average()))
                    freqSum.add(doubleToString(freq18Sum.average()))
                    freqSum.add(doubleToString(freq19Sum.average()))
                    freqSum.add(doubleToString(freq20Sum.average()))
                    freqSum.add(doubleToString(freq21Sum.average()))
                    freqSum.add(doubleToString(freq22Sum.average()))
                    freqSum.add(doubleToString(freq23Sum.average()))
                    freqSum.add(doubleToString(freq24Sum.average()))
                    freqSum.add(doubleToString(freq25Sum.average()))
                    freqSum.add(doubleToString(freq26Sum.average()))
                    freqSum.add(doubleToString(freq27Sum.average()))
                    freqSum.add(doubleToString(freq28Sum.average()))
                    freqSum.add(doubleToString(freq29Sum.average()))
                    freqSum.add(doubleToString(freq30Sum.average()))
                    freqSum.add(doubleToString(freq31Sum.average()))
                    avgStart = false
                    isMeasure = false
                }
            }
        }

        if (lineChart.data != null && lineChart.data.dataSetCount > 0) {
            if (toTransFormCount < averageCount) {
                for (i in rmsValues31.indices) {
                    valSum[i] += rmsValues31[i]
                }
                toTransFormCount++

            }
            else {
                for (i in rmsValues31.indices) {
                    valSum[i] /= averageCount.toDouble()
                    lineValues.add(Entry(i.toFloat(), valSum[i].toFloat()))
                }
                lineDataSet.values = lineValues
                lineDataSet.setDrawValues(false)
                lineDataSet.valueTextColor = Color.RED
                lineDataSet.valueTextSize = 8.0f
                lineDataSet.mode = LineDataSet.Mode.LINEAR
                lineChart.data.notifyDataChanged()
                lineChart.notifyDataSetChanged()
                lineChart.invalidate()
                toTransFormCount = 0
                valSum = DoubleArray(31)
            }

        } else {

            lineDataSet = LineDataSet(lineValues, "RMS")
            lineDataSet.setDrawFilled(false)
            lineDataSet.setDrawCircles(false)

            lineDataSet.formLineWidth = 1f
            val data = LineData(lineDataSet)
            // set data
           lineChart.data = data
           lineChart.invalidate()
        }

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

        return Math.pow(
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