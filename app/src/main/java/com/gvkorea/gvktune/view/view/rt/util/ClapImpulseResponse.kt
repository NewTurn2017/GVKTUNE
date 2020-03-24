package com.gvkorea.gvktune.view.view.rt.util

import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D

class ClapImpulseResponse : ClapAnalyzer {

    override fun getBaseLine(): Double {
        return baselineRMS
    }


    override fun getBaseLineV(): Double {
        return 20 * Math.log10(baselineRMS / Math.pow(2.0, 15.0))
    }
    val RT60s_10 = FloatArray(10)

    private var volumeThreshold: Double = 0.toDouble()

    private var numSamples = 0
    private var clapStart: Int = 0
    private val clapEnd: Int = 0
    private var clapTime: Int = 0

    override var status: StatusUpdate
    override var results: Results

    private var clap_heard: Boolean = false
    private var done: Boolean = false


    override var isListeningBase = true
        private set
    private var captureNextSecond = false
    internal var startTime = 0
    private var afterClap = 10

    private var baselineRMS: Double = 0.toDouble()


    override var data: ShortArray
    private var base: ShortArray
    private var baseIndex = 0
    override var cancel: Boolean = false

    private var recordhistory: Boolean = false
    private var currentIndex = 0

    private var mySurfaceView: MySurfaceView
    private var spectrumTime = .1
    private var factor = 1f

    override fun clap_heard(): Boolean {
        return clap_heard
    }

    override fun done(): Boolean {
        return done
    }


    constructor(volumeThreshold: Double, listeningtobase: Boolean, baselineRMS: Double, mySurfaceView: MySurfaceView) {
        this.numSamples = 0
        this.captureNextSecond = DEBUG
        this.startTime = 0
        this.afterClap = 10
        this.baseIndex = 0
        this.currentIndex = 0
        this.spectrumTime = 0.1
        this.factor = 1.0f

        this.volumeThreshold = volumeThreshold
        this.isListeningBase = listeningtobase
        this.baselineRMS = baselineRMS
        this.mySurfaceView = mySurfaceView
        this.clap_heard = false
        this.done = false
        this.cancel = false
        data = ShortArray(44100)

        for (i in data.indices) {
            data[i] = 0
        }

        base = ShortArray(44 * 1000)

        recordhistory = false

        status = StatusUpdate()
        results = Results()

        status.base = listeningtobase
        status.clap_heard = false
        status.done = false
        status.RT60 = false
        status.clapgraph = false
    }

    override fun dispHistory() {
        val chunk = ShortArray(44)
        var i = 0
        while (i < data.size - 44) {
            for (j in 0..43) {
                chunk[j] = data!![i + j]
            }
            i = i + 44
            //Log.d(TAG, "rms of chunk " + i/44 + "is " + rootMeanSquared(chunk));
        }
    }


    override fun heard(data: ShortArray, sampleRate: Int): Boolean {


        SampleRate = sampleRate
        copyIntoBuffer(data)
        results.sampleRate = sampleRate
        mySurfaceView.fromClap(data)
        var heard = false



        //use the first 1000 ms to obtain a baseline measurement for the room

        if (isListeningBase) {

            if (numSamples >= 1000) {
                baselineRMS = rootMeanSquared(base)
                status.base = false
                recordhistory = true
                isListeningBase = false
                volumeThreshold = baselineRMS * 50
            } else {
                for (i in baseIndex until (baseIndex + data.size)) {
                    base[i] = data[i % data.size]
                }
                baseIndex += data.size
            }
        } else {
            val currentRMS = rootMeanSquared(data)
            if (!clap_heard) {
                if (currentRMS > volumeThreshold) {
                    //                    Log.d(TAG, "heard a clap at rms: " + currentRMS);
                    clapStart = data.size * numSamples
                    clap_heard = true
                    status.clap_heard = true
                }
            } else {
                if (currentRMS < 2 * baselineRMS) {
                    clapTime = data.size * numSamples - clapStart
                    //Log.d(TAG, "clap lasted for: " + clapTime);
                    //                    			currentIndex -= chunkSize;
                    heard = true
                }
            }
        }


        /*		if (captureNextSecond) {
			currentIndex += chunkSize;
			if (numSamples >  startTime + AFTERCLAP) { //collect a bit more
			}
		}*/


        if (cancel) {
            //Log.d(TAG, "canceling record");
            status.done = true
            heard = true
        }
        numSamples++
        currentIndex += data.size
        return heard
    }

    fun copyIntoBuffer(data: ShortArray) {
        val chunkSize = data.size
        var breakout = false
        var wrap = 0
        for (i in currentIndex until currentIndex + chunkSize) {
            if (i >= this.data!!.size) {
                breakout = true
                wrap = i
                break
            }
            this.data[i] = data[i % chunkSize]
        }

        if (breakout) {
            currentIndex = 0
            wrap = wrap % chunkSize
            for (i in currentIndex until data.size - wrap) {
                this.data[i] = data[i + wrap]
            }
        }
    }

    override fun process() {
        status.done = true
        done = true
        val clapdata = FloatArray(clapTime)
        var j = 0

        var startIndex: Int
        val endIndex = currentIndex
        if (endIndex <= clapTime) {
            //Log.d(TAG, "in the weird case");
            startIndex = (endIndex - clapTime) % data.size
            startIndex += data.size
            //Log.d(TAG, startIndex + " to " + endIndex + " clapTime = " + clapTime);
            for (i in startIndex until data.size) {
                clapdata[j] = data[i].toFloat() / Math.pow(2.0, 15.0).toFloat()
                j++
            }
            for (i in 0 until endIndex) {
                clapdata[j] = data[i].toFloat() / Math.pow(2.0, 15.0).toFloat()
                j++
            }
        } else {
            //Log.d(TAG, "in the normal case");
            startIndex = endIndex - clapTime
            for (i in startIndex until endIndex) {
                clapdata[j] = data[i].toFloat() / Math.pow(2.0, 15.0).toFloat()
                j++
            }
        }

        val RT60 = calcReverb(clapdata, true)
        results.RT60 = RT60
        status.RT60 = true
        if (RT60 < 0) {
            //Log.d(TAG, "Something went wrong");
            return
        }
        status.clapgraph = true

        val hamm = Curve(spectrumResolution)
        hamm.hamm(false)

        val FFTlog = (Math.log(spectrumResolution.toDouble()) / Math.log(2.0)).toInt()

        val size = Math.floor(Math.log(clapdata.size.toDouble()) / Math.log(2.0)).toInt()
        val paddedSize = 1 shl size + 1

        val paddedClap = FloatArray(paddedSize)
        var i = 0
        //pad signal to nearest power of two above size
        while (i < clapdata.size) {
            paddedClap[i] = clapdata[i]
            i++
        }
        while (i < paddedClap.size) {
            paddedClap[i] = 0f
            i++
        }


        val depth = OVERLAP * paddedSize / spectrumResolution
        val spectrogram = Array(depth) { FloatArray(spectrumResolution) }
        val stepSize = spectrumResolution / OVERLAP
        /*
		FFT fft = new FFT();

		Complex[] current = new Complex[spectrumResolution];
		Complex[] spec = new Complex[spectrumResolution];
		int row = 0;

		for (int k = 0; k < paddedSize - spectrumResolution; k = k + stepSize) {
			for (int c = 0; c < spectrumResolution; c++) {
				current[c] = new Complex(hamm.array[c]*paddedClap[c+k], 0);
			}
			spec = fft.fft(current);
			for (int c = 0; c < spectrumResolution; c++) {
				spectrogram[row][c] = (float) spec[c].abs();
			}
			row++;
		}
		*/

        val current = FloatArray(2 * spectrumResolution)
        val fft = FloatFFT_1D(spectrumResolution)
        var row = 0

        run {
            var k = 0
            while (k < paddedSize - spectrumResolution) {
                for (c in 0 until spectrumResolution) {
                    current[c] = hamm.array[c] * paddedClap[c + k]
                }
                fft.realForwardFull(current)
                for (c in 0 until spectrumResolution - 1) {
                    spectrogram[row][c] = Math.hypot(current[2 * c].toDouble(), current[2 * c + 1].toDouble()).toFloat()
                }
                row++
                k += stepSize
            }
        }

        val RT60s = FloatArray(numFreqs)
        val freqs = specFreq()
        results.freqs = FloatArray(numFreqs)
        val freqSpec = FloatArray(depth)
        factor = (clapdata.size / stepSize * SampleRate / OVERLAP).toFloat()
        var freq: Int
        for (f in RT60s.indices) {
            freq = Math.floor((freqs[f] / (SampleRate / 2) * spectrumResolution).toDouble()).toInt()
            for (k in freqSpec.indices) {
                freqSpec[k] = spectrogram[k][freq]
            }
            RT60s[f] = calcReverb(freqSpec, false)
            if (RT60s[f] < 0) {
                RT60s[f] = 0f
            }
            results.freqs!![f] = freq.toFloat()
        }

        results.RT60s = RT60s


        status.RT60s = true

//        val dsSpectra = Curve(numFreqs)
//        var energy: Float
//        for (f in 0 until numFreqs) {
//            freq = Math.floor((freqs[f] / (SampleRate / 2) * spectrumResolution).toDouble()).toInt()
//            energy = 0f
//            var k = 0
//            while (k < Math.floor(.1 * freqSpec.size)) {
//                energy += spectrogram[k][freq]
//                k++
//            }
//            dsSpectra.array[f] = energy / Math.floor(.1 * freqSpec.size).toFloat()
//        }

//        val reference = baselineRMS.toFloat() / Math.pow(2.0, 15.0).toFloat()
//        dsSpectra.dbConvert(reference, true)
//        results.dsSpectra = dsSpectra.array
//        status.dsSpectra = true
//
//        val freqResp = Curve(numFreqs)
//        var reverbEnergy: Float
//
//        for (f in 0 until numFreqs) {
//            freq = Math.floor((freqs[f] / (SampleRate / 2) * spectrumResolution).toDouble()).toInt()
//            energy = 0f
//            for (k in Math.floor(.1 * freqSpec.size).toInt() until depth) {
//                energy += spectrogram[k][freq]
//            }
//            reverbEnergy = energy / (depth - Math.floor(.1 * freqSpec.size)).toFloat()
//            freqResp.array[f] = reverbEnergy / dsSpectra.array[f]
//        }
//
//        freqResp.dbConvert(reference, true)
//        results.freqResp = freqResp.array
//        status.freqResp = true
    }

    private fun specFreq(): FloatArray {
        val result = FloatArray(numFreqs)

        val sqrt2 = Math.sqrt(2.0)
        val sqrtsqrt2 = Math.sqrt(sqrt2)
        var x = 22.09708691207964 //1000/(sqrt(2)^11)
        for (i in 0 until numFreqs) {
            result[i] = x.toFloat()
            x *= sqrtsqrt2
        }
        return result
    }

    private fun calcReverb(c: FloatArray, overall: Boolean): Float {
        val curve = Curve(c)
        var claptime = c.size
        val result: Float
        var directSoundSamples: Int
        val minsec = .05f //seconds
        val minsamp = Math.ceil(minsec / .01).toInt()

        if (overall) {
            directSoundSamples = Math.floor(directSoundLength * SampleRate).toInt()
            val window = 44
            claptime = Math.floor((claptime / window).toDouble()).toInt()
            val reference = baselineRMS.toFloat() / Math.pow(2.0, 15.0).toFloat()
            curve.dbConvert(reference, true)
            val s = Smooth()
            curve.array = s.smooth(curve.array, window)
            curve.length = curve.array.size
            directSoundSamples = Math.floor((directSoundSamples / window).toDouble()).toInt()
            results.clapData = curve.array
            results.clapLength = curve.length
            results.smoothwindow = window
            factor = SampleRate / window.toFloat()
            if (directSoundSamples >= curve.length) {
                //Log.d(TAG, "Signal was too quiet, direct sound case.");
                return -1f
            }
        } else {
            val s = Smooth()
            curve.array = s.smooth(curve.array, 1)
            curve.length = curve.array.size
            directSoundSamples = Math.floor(curve.length * .2).toInt()
        }


        val directSoundSum = curve.sum(0, directSoundSamples)
        val tailSum = curve.sum(claptime - directSoundSamples, claptime)
        val decayEstimate = (directSoundSum - tailSum) / directSoundSamples
        if (overall) {
            if (decayEstimate < 10) {
                //Log.d(TAG, "Signal was too quiet, decayEstimate case.");
                return -1f
            }
        }

        if (tailSum == java.lang.Float.NEGATIVE_INFINITY) {
            //Log.d(TAG, "Signal was too quiet, NEGATIVE INFINITY case.");
            return -1f
        }

        var best = Knee()
        val rSound = curve.subset(directSoundSamples, claptime)
        best = findKnee(rSound, minsamp)

        var slope = best.fit!!.slope
        slope = slope * factor

        result = -60 / slope //calculate RT60
        return result
    }

    private fun rootMeanSquared(nums: ShortArray): Double {
        var ms = 0.0
        for (i in nums.indices) {
            ms += (nums[i] * nums[i]).toDouble()
        }
        ms /= nums.size.toDouble()
        return Math.sqrt(ms)
    }


    private inner class Curve {
        var array: FloatArray
        var length: Int = 0


        //Make a Curve of size length
        constructor(length: Int) {
            this.array = FloatArray(length)
            this.length = length
        }

        //Turn a float array into a Curve
        constructor(array: FloatArray) {
            this.array = FloatArray(array.size)
            for (i in array.indices) {
                this.array[i] = array[i]
            }
            this.length = array.size
        }

        //Create a float array of size length filled with floats a
        fun fill(a: Float) {
            for (i in 0 until length) {
                array[i] = a
            }
            this.length = length
        }

        //generates a hamming window for this curve.
        fun hamm(halfFlag: Boolean) {
            var end = this.length
            var item: Float
            if (halfFlag) {
                end = Math.ceil((this.length / 2).toDouble()).toInt()
            }
            for (i in 0 until end) {
                item = (.54 - .46 * Math.cos(Math.PI * i / end)).toFloat()
                this.array[i] = item
            }
        }

        fun ramp(init: Float, sign: Int, slope: Float) {
            var init = init
            for (i in 0 until length) {
                array[i] = init
                init += slope
            }
            this.length = length
        }

        fun square(size: Int) {
            for (i in 0 until size) {
                array[i] = array[i] * array[i]
            }
        }

        //DOES NOT CHANGE THIS OBJECT
        fun mult(B: Curve, size: Int): Curve {
            /*	if (this.length != B.length || this.length != result.length) {
				//throw some exception here
				return 0;
			}*/
            val result = Curve(size)
            for (i in 0 until size) {
                result.array[i] = this.array[i] * B.array[i]
            }
            return result
        }

        //DOES NOT CHANGE THIS OBJECT
        //computes this.array - B.array and returns as a Curve
        fun subtract(B: Curve, size: Int): Curve {
            val result = Curve(size)
            for (i in 0 until size) {
                result.array[i] = this.array[i] - B.array[i]
            }
            return result
        }

        fun sum(first: Int, last: Int): Float {
            var sum = 0f
            for (i in first until last) {
                sum = sum + array[i]
            }
            return sum
        }

        fun dbConvert(reference: Float, power: Boolean) {
            val alpha: Float
            val beta: Float
            if (power) {
                alpha = 10f
            } else {
                alpha = 20f
            }
             for (i in 0 until length) {
                array[i] = alpha * Math.log10((Math.abs(array[i]) / reference).toDouble()).toFloat()
            }
        }

        fun subset(start: Int, end: Int): Curve {
            val result = Curve(end - start)
            for (i in start until end) {
                result.array[i - start] = this.array[i]
            }
            return result
        }

    }

    private inner class Fit {
        internal var slope: Float = 0.toFloat()
        internal var yIntercept: Float = 0.toFloat()
    }

    private inner class Knee {
        internal var fit: Fit? = null
        internal var rmsError: Float = 0.toFloat()
        internal var prefix: Int = 0
    }

    private fun regression(curve: Curve, size: Int): Fit {
        val result = Fit()

        val meanX = ((size - 1) / 2).toFloat()
        val meanY = curve.sum(0, size) / size

        val xSq = Curve(size)
        xSq.ramp(0f, 1, 1f)
        val xy = curve.mult(xSq, size)
        val meanXY = xy.sum(0, size) / size

        xSq.square(size)
        val meanXsq = xSq.sum(0, size) / size

        val covarianceXY = meanXY - meanX * meanY
        val varianceXsq = meanXsq - meanX * meanY

        result.slope = covarianceXY / varianceXsq
        result.yIntercept = meanY - result.slope * meanX

        return result
    }

    private fun rmsError(curve: Curve, fit: Fit, size: Int): Float {
        var result: Float

        val fitLine = Curve(size)
        fitLine.ramp(fit.yIntercept, 1, fit.slope)
        val diff = fitLine.subtract(curve, size)
        diff.square(size)

        result = diff.sum(0, size) / size
        result = Math.sqrt(result.toDouble()).toFloat()
        return result
    }

    private fun findKnee(curve: Curve, min: Int): Knee {
        val result = Knee()
        result.rmsError = java.lang.Float.POSITIVE_INFINITY
        var error: Float

        var fit = Fit()
        for (i in min - 1 until curve.length) {
            fit = regression(curve, i)
            error = rmsError(curve, fit, curve.length)
            if (error < result.rmsError) {
                result.rmsError = error
                result.fit = fit
                result.prefix = i
            }
        }
        return result
    }

    companion object {
        private val TAG = "ClapImpulseResponse"
        var SampleRate: Int = 0

        val DEFAULT_LOUDNESS_THRESHOLD = 200000

        private val DEBUG = false

        private val numFreqs = 40
        private val spectrumResolution = 4096
        private val OVERLAP = 20
        private val directSoundLength = .01 //seconds



    }


}
