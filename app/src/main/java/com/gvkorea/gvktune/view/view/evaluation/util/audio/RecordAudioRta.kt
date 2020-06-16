package com.gvkorea.gvktune.view.view.evaluation.util.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.gvkorea.gvktune.MainActivity
import com.gvkorea.gvktune.util.CSVRead
import com.gvkorea.gvktune.util.fft.RealDoubleFFT
import com.gvkorea.gvktune.view.view.evaluation.EvaluateFragment.Companion.isStarted
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

class RecordAudioRta(val view: View) :
    AsyncTask<Unit, DoubleArray, Unit>() {


    private var valSum = DoubleArray(31)
    private var toTransFormCount = 0
    private val frequency = 44100
    private val channelConfiguration = AudioFormat.CHANNEL_IN_MONO
    private val audioEncoding = AudioFormat.ENCODING_PCM_16BIT
    private val blockSize = 8192
    private val transformer = RealDoubleFFT(blockSize)
    private var toTransformAvg = DoubleArray(blockSize)
    private var chartValue = ArrayList<BarEntry>()
    private var barDataSet = BarDataSet(chartValue, null)
    lateinit var barValues: ArrayList<BarEntry>
    private val calib_data =
        CSVRead().readCalibCsv(view.context.assets, MainActivity.selectedMicName)
    private var counter1 = 0


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


    private val LOW_20HZ = 6
    private val LOW_25HZ = 8
    private val LOW_32HZ = 10
    private val LOW_40HZ = 13
    private val LOW_50HZ = 16
    private val LOW_63HZ = 20
    private val LOW_80HZ = 26
    private val LOW_100HZ = 32
    private val LOW_125HZ = 41
    private val LOW_160HZ = 52
    private val LOW_200HZ = 65
    private val LOW_250HZ = 82
    private val LOW_315HZ = 104
    private val LOW_400HZ = 131
    private val LOW_500HZ = 165
    private val LOW_630HZ = 208
    private val LOW_800HZ = 262
    private val LOW_1000HZ = 330
    private val LOW_1250HZ = 417
    private val LOW_1600HZ = 525
    private val LOW_2000HZ = 661
    private val LOW_2500HZ = 834
    private val LOW_3150HZ = 1050
    private val LOW_4000HZ = 1323
    private val LOW_5000HZ = 1668
    private val LOW_6300HZ = 2101
    private val LOW_8000HZ = 2647
    private val LOW_10000HZ = 3336
    private val LOW_12500HZ = 4203
    private val LOW_16000HZ = 5295
    private val LOW_20000HZ = 6672

    private val HIGH_20HZ = 8
    private val HIGH_25HZ = 10
    private val HIGH_32HZ = 13
    private val HIGH_40HZ = 16
    private val HIGH_50HZ = 20
    private val HIGH_63HZ = 26
    private val HIGH_80HZ = 32
    private val HIGH_100HZ = 41
    private val HIGH_125HZ = 52
    private val HIGH_160HZ = 65
    private val HIGH_200HZ = 82
    private val HIGH_250HZ = 104
    private val HIGH_315HZ = 131
    private val HIGH_400HZ = 165
    private val HIGH_500HZ = 208
    private val HIGH_630HZ = 262
    private val HIGH_800HZ = 330
    private val HIGH_1000HZ = 417
    private val HIGH_1250HZ = 525
    private val HIGH_1600HZ = 661
    private val HIGH_2000HZ = 834
    private val HIGH_2500HZ = 1050
    private val HIGH_3150HZ = 1323
    private val HIGH_4000HZ = 1668
    private val HIGH_5000HZ = 2101
    private val HIGH_6300HZ = 2647
    private val HIGH_8000HZ = 3336
    private val HIGH_10000HZ = 4203
    private val HIGH_12500HZ = 5295
    private val HIGH_16000HZ = 6672
    private val HIGH_20000HZ = 8191


    ////block size = 4096

//    private val INDEX_20HZ      = 3
//    private val INDEX_25HZ      = 4
//    private val INDEX_32HZ      = 5
//    private val INDEX_40HZ      = 7
//    private val INDEX_50HZ      = 9
//    private val INDEX_63HZ      = 11
//    private val INDEX_80HZ      = 14
//    private val INDEX_100HZ     = 18
//    private val INDEX_125HZ     = 23
//    private val INDEX_160HZ     = 29
//    private val INDEX_200HZ     = 37
//    private val INDEX_250HZ     = 46
//    private val INDEX_315HZ     = 58
//    private val INDEX_400HZ     = 74
//    private val INDEX_500HZ     = 92
//    private val INDEX_630HZ     = 117
//    private val INDEX_800HZ     = 148
//    private val INDEX_1000HZ    = 185
//    private val INDEX_1250HZ    = 232
//    private val INDEX_1600HZ    = 297
//    private val INDEX_2000HZ    = 371
//    private val INDEX_2500HZ    = 464
//    private val INDEX_3150HZ    = 585
//    private val INDEX_4000HZ    = 743
//    private val INDEX_5000HZ    = 928
//    private val INDEX_6300HZ    = 1170
//    private val INDEX_8000HZ    = 1486
//    private val INDEX_10000HZ   = 1857
//    private val INDEX_12500HZ   = 2321
//    private val INDEX_16000HZ   = 2972
//    private val INDEX_20000HZ   = 3715
//
//
//    private val LOW_20HZ     = 3
//    private val LOW_25HZ     = 4
//    private val LOW_32HZ     = 5
//    private val LOW_40HZ     = 6
//    private val LOW_50HZ     = 8
//    private val LOW_63HZ     = 10
//    private val LOW_80HZ     = 13
//    private val LOW_100HZ    = 16
//    private val LOW_125HZ    = 20
//    private val LOW_160HZ    = 26
//    private val LOW_200HZ    = 33
//    private val LOW_250HZ    = 41
//    private val LOW_315HZ    = 52
//    private val LOW_400HZ    = 65
//    private val LOW_500HZ    = 83
//    private val LOW_630HZ    = 104
//    private val LOW_800HZ    = 131
//    private val LOW_1000HZ   = 165
//    private val LOW_1250HZ   = 208
//    private val LOW_1600HZ   = 261
//    private val LOW_2000HZ   = 330
//    private val LOW_2500HZ   = 416
//    private val LOW_3150HZ   = 523
//    private val LOW_4000HZ   = 659
//    private val LOW_5000HZ   = 830
//    private val LOW_6300HZ   = 1043
//    private val LOW_8000HZ   = 1315
//    private val LOW_10000HZ  = 1655
//    private val LOW_12500HZ  = 2080
//    private val LOW_16000HZ  = 2619
//    private val LOW_20000HZ  = 3306
//
//    private val HIGH_20HZ     = 4
//    private val HIGH_25HZ     = 5
//    private val HIGH_32HZ     = 6
//    private val HIGH_40HZ     = 8
//    private val HIGH_50HZ     = 10
//    private val HIGH_63HZ     = 13
//    private val HIGH_80HZ     = 16
//    private val HIGH_100HZ    = 20
//    private val HIGH_125HZ    = 26
//    private val HIGH_160HZ    = 33
//    private val HIGH_200HZ    = 41
//    private val HIGH_250HZ    = 52
//    private val HIGH_315HZ    = 65
//    private val HIGH_400HZ    = 83
//    private val HIGH_500HZ    = 104
//    private val HIGH_630HZ    = 131
//    private val HIGH_800HZ    = 165
//    private val HIGH_1000HZ   = 208
//    private val HIGH_1250HZ   = 261
//    private val HIGH_1600HZ   = 330
//    private val HIGH_2000HZ   = 416
//    private val HIGH_2500HZ   = 523
//    private val HIGH_3150HZ   = 659
//    private val HIGH_4000HZ   = 830
//    private val HIGH_5000HZ   = 1043
//    private val HIGH_6300HZ   = 1315
//    private val HIGH_8000HZ   = 1655
//    private val HIGH_10000HZ  = 2080
//    private val HIGH_12500HZ  = 2619
//    private val HIGH_16000HZ  = 3306
//    private val HIGH_20000HZ  = 4095


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

            while (isStarted) {
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

//        chartValue = ArrayList()
//        barValues = ArrayList()
//
        for (i in 0 until blockSize) {
            if (values[0][i] < 0) {
                toTransformAvg[i] = -values[0][i]
            } else {
                toTransformAvg[i] = values[0][i]
            }
        }

        var arrayNum = 0

        for (i in 1 until toTransformAvg.size) {

//            toTransformAvg[i] = abs(values[0][i])
            if (i == INDEX_20HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_25HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_32HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_40HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_50HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_63HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_80HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_100HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_125HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_160HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_200HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_250HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_315HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_400HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_500HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_630HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_800HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_1000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_1250HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_1600HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_2000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_2500HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_3150HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_4000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_5000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_6300HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_8000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_10000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_12500HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_16000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_20000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            }

            if (arrayNum == 31) {

                if (avgStart && isMeasure) {

                    freq1Sum.add(rmsValues[0])
                    freq2Sum.add(rmsValues[1])
                    freq3Sum.add(rmsValues[2])
                    freq4Sum.add(rmsValues[3])
                    freq5Sum.add(rmsValues[4])
                    freq6Sum.add(rmsValues[5])
                    freq7Sum.add(rmsValues[6])
                    freq8Sum.add(rmsValues[7])
                    freq9Sum.add(rmsValues[8])
                    freq10Sum.add(rmsValues[9])
                    freq11Sum.add(rmsValues[10])
                    freq12Sum.add(rmsValues[11])
                    freq13Sum.add(rmsValues[12])
                    freq14Sum.add(rmsValues[13])
                    freq15Sum.add(rmsValues[14])
                    freq16Sum.add(rmsValues[15])
                    freq17Sum.add(rmsValues[16])
                    freq18Sum.add(rmsValues[17])
                    freq19Sum.add(rmsValues[18])
                    freq20Sum.add(rmsValues[19])
                    freq21Sum.add(rmsValues[20])
                    freq22Sum.add(rmsValues[21])
                    freq23Sum.add(rmsValues[22])
                    freq24Sum.add(rmsValues[23])
                    freq25Sum.add(rmsValues[24])
                    freq26Sum.add(rmsValues[25])
                    freq27Sum.add(rmsValues[26])
                    freq28Sum.add(rmsValues[27])
                    freq29Sum.add(rmsValues[28])
                    freq30Sum.add(rmsValues[29])
                    freq31Sum.add(rmsValues[30])
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

        //31band bar chart RTA
//        if (barChart.data != null && barChart.data.dataSetCount > 0) {
//            if (toTransFormCount < averageCount) {
//                for (i in rmsValues.indices) {
//                    valSum[i] += rmsValues[i]
//                }
//                toTransFormCount++
//
//            } else {
//
//                for (i in rmsValues.indices) {
//                    valSum[i] /= averageCount.toDouble()
//                    barValues.add(BarEntry(i.toFloat(), valSum[i].toFloat()))
//                }
//                barDataSet.values = barValues
//                barDataSet.setDrawValues(true)
//                barDataSet.valueTextColor = Color.RED
//                barDataSet.valueTextSize = 8.0f
//                barChart.data.notifyDataChanged()
//
//                barChart.notifyDataSetChanged()
//
//                barChart.invalidate()
//                toTransFormCount = 0
//                valSum = DoubleArray(31)
//            }
//        } else {
//
//            barDataSet = BarDataSet(barValues, "RMS")
//            barDataSet.setDrawIcons(false)
//            barDataSet.setGradientColor(Color.GREEN, Color.RED)
//            barDataSet.setDrawValues(false)
//            barDataSet.formLineWidth = 1f
//            val data = BarData(barDataSet)
//            // set data
//            barChart.data = data
//            barChart.invalidate()
//        }
    }

    private fun calculate_SPL(rmsValues: DoubleArray): Double {
        var sum = 0.0
        for (i in rmsValues) {
            sum += 10.0.pow((i / 10))
        }
        return 10 + 10 * log10(sum / 10)
    }

    private fun doubleToString(value: Double): String {
        val format = DecimalFormat("##.#")
        return format.format(value)
    }

    private fun readMicCalib(i: Int): Double {


        val delta = calib_data[i - 1][1]
        return delta.toDouble()
    }


    fun freq_value_31(i: Int, toTransform: DoubleArray, arrayNum: Int) {
        val dbfsFinal = freqAverageCalcForOneOctave_31(i, toTransform)
        rmsValues[arrayNum] = dbfsFinal
    }

    private fun freqAverageCalcForOneOctave_31(i: Int, toTransform: DoubleArray): Double {
        var dbfs = 0.0
        var dbfsFinal = 0.0

        if (i == INDEX_20HZ) {
            for (j in LOW_20HZ..HIGH_20HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_25HZ) {
            for (j in LOW_25HZ..HIGH_25HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_32HZ) {
            for (j in LOW_32HZ..HIGH_32HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_40HZ) {
            for (j in LOW_40HZ..HIGH_40HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_50HZ) {
            for (j in LOW_50HZ..HIGH_50HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_63HZ) {
            for (j in LOW_63HZ..HIGH_63HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_80HZ) {
            for (j in LOW_80HZ..HIGH_80HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_100HZ) {
            for (j in LOW_100HZ..HIGH_100HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_125HZ) {
            for (j in LOW_125HZ..HIGH_125HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_160HZ) {
            for (j in LOW_160HZ..HIGH_160HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_200HZ) {
            for (j in LOW_200HZ..HIGH_200HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_250HZ) {
            for (j in LOW_250HZ..HIGH_250HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_315HZ) {
            for (j in LOW_315HZ..HIGH_315HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_400HZ) {
            for (j in LOW_400HZ..HIGH_400HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_500HZ) {
            for (j in LOW_500HZ..HIGH_500HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_630HZ) {
            for (j in LOW_630HZ..HIGH_630HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_800HZ) {
            for (j in LOW_800HZ..HIGH_800HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_1000HZ) {
            for (j in LOW_1000HZ..HIGH_1000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_1250HZ) {
            for (j in LOW_1250HZ..HIGH_1250HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_1600HZ) {
            for (j in LOW_1600HZ..HIGH_1600HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_2000HZ) {
            for (j in LOW_2000HZ..HIGH_2000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_2500HZ) {
            for (j in LOW_2500HZ..HIGH_2500HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_3150HZ) {
            for (j in LOW_3150HZ..HIGH_3150HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_4000HZ) {
            for (j in LOW_4000HZ..HIGH_4000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_5000HZ) {
            for (j in LOW_5000HZ..HIGH_5000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_6300HZ) {
            for (j in LOW_6300HZ..HIGH_6300HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_8000HZ) {
            for (j in LOW_8000HZ..HIGH_8000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_10000HZ) {
            for (j in LOW_10000HZ..HIGH_10000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_12500HZ) {
            for (j in LOW_12500HZ..HIGH_12500HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_16000HZ) {
            for (j in LOW_16000HZ..HIGH_16000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_20000HZ) {
            for (j in LOW_20000HZ..HIGH_20000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        }
        return dbfsFinal
    }

    private fun toTransfromTodbfs(toTransform: DoubleArray, i: Int): Double {
            val delta: Double = readMicCalib(i)

        return Math.pow(
            10.0,
            Math.round((20.0 * Math.log10(toTransform[i]) + 50.5 + MainActivity.CALIBRATION + delta) * 100.0) / 100.0 / 10
        ) // calib 수정

    }

    private fun dbfsAvg(dbfsSum: Double): Double {
        return Math.round(10.0 * log10(dbfsSum) * 100.0) / 100.0
    }

    companion object {
        var rmsValues = DoubleArray(31)

        var isMeasure = false
        var avgStart = false
        var spldB = 0.0

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