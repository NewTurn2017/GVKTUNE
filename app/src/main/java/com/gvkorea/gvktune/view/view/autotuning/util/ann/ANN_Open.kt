package com.gvkorea.gvktune.view.view.autotuning.util.ann

import android.content.res.AssetManager
import org.tensorflow.contrib.android.TensorFlowInferenceInterface

class ANN_Open(assets: AssetManager, val target: FloatArray) {

    val results = FloatArray(31)
    private val MODEL_FILE_OPEN = "file:///android_asset/model/optimized_frozen_open_model_75.pb"
    private var inferenceInterface_open: TensorFlowInferenceInterface = TensorFlowInferenceInterface()
    private val INPUT_NODE = "input"
    private val INPUT_SHAPE_OPEN = intArrayOf(1, 31)
    private val OUTPUT_NODE = "out"
    init {
        inferenceInterface_open.initializeTensorFlow(assets, MODEL_FILE_OPEN)
    }

    fun getControlEQ_Open(): FloatArray {
        inferenceInterface_open.fillNodeFloat(INPUT_NODE, INPUT_SHAPE_OPEN, target)
        inferenceInterface_open.runInference(arrayOf(OUTPUT_NODE))
        inferenceInterface_open.readNodeFloat(OUTPUT_NODE, results)
        return results
    }

}