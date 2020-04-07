package com.gvkorea.gvktune.view.view.autotuning.util.ann

import android.content.res.AssetManager
import org.tensorflow.contrib.android.TensorFlowInferenceInterface
import kotlin.math.roundToInt

class ANN_Closed(var curEQ: IntArray, var curRMS: FloatArray, var targetValues: FloatArray, assets: AssetManager) {

    private val MODEL_FILE_CLOSE = "file:///android_asset/model/optimized_frozen_closed_model_75.pb"
    private var inferenceInterface_close: TensorFlowInferenceInterface = TensorFlowInferenceInterface()
    private val INPUT_SHAPE_CLOSE = intArrayOf(1, 62)

    private val INPUT_NODE = "input"
    private val OUTPUT_NODE = "out"

    init {
        inferenceInterface_close.initializeTensorFlow(assets, MODEL_FILE_CLOSE)
    }

    fun getControlEQ_Closed(): IntArray {

        val results = FloatArray(31)
        val input = FloatArray(62)


        for (i in input.indices){
            if(i < 31){
                input[i] = curRMS[i]
            }else{
                input[i] = targetValues[i-31]
            }
        }

        inferenceInterface_close.fillNodeFloat(INPUT_NODE, INPUT_SHAPE_CLOSE, input)
        inferenceInterface_close.runInference(arrayOf(OUTPUT_NODE))
        inferenceInterface_close.readNodeFloat(OUTPUT_NODE, results)

        val resultsToInt = FloatToInt(results)

        for (i in resultsToInt.indices) {

            deltaEQ[i] = resultsToInt[i]
            curEQ[i] += deltaEQ[i]
            if (curEQ[i] < 0) {
                curEQ[i] = 0
            } else if (curEQ[i] > 60) {
                curEQ[i] = 60
            }
        }

        return curEQ
    }

    private fun FloatToInt(results: FloatArray): IntArray {
        val resultsToInt = IntArray(31)
        for (i in results.indices){
            resultsToInt[i] = results[i].roundToInt()
        }
        return resultsToInt
    }

    companion object{
        val deltaEQ = IntArray(31)
    }
}