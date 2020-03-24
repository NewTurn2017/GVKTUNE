package com.gvkorea.gvktune.view.view.reverb

import android.animation.*
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.media.AudioFormat
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView

import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.rt.graphview.GraphView
import com.gvkorea.gvktune.view.view.rt.graphview.GraphView.*
import com.gvkorea.gvktune.view.view.rt.graphview.GraphViewSeries
import com.gvkorea.gvktune.view.view.rt.graphview.LineGraphView
import com.gvkorea.gvktune.view.view.rt.util.*
import kotlinx.android.synthetic.main.fragment_reverb.*
import java.math.BigDecimal


class ReverbFragment : Fragment() {

    private var isrecording: Boolean = false
    private lateinit var audioLogger: ClapAnalyzer
    private lateinit var audioRecorder: AudioClipRecorder
    private val TAG = "MainActivity"
    private var numberofclaps = 0
    private lateinit var robotoType: Typeface
    private lateinit var mySurfaceView: MySurfaceView
    private lateinit var mySurfaceHolder: SurfaceHolder


    lateinit var recordTask: ClapRecord
    lateinit var analyzeTask: ClapAnalyze
    private var baseline = 0.0
    private var listenBase = true

    private lateinit var claps: Claps

    private lateinit var mTransition: LayoutTransition
    private lateinit var customAppearingAnim: Animator
    private lateinit var customDisappearingAnim: Animator

    private lateinit var customChangingAppearingAnim: Animator
    private lateinit var customChangingDisappearingAnim: Animator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reverb, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isrecording = false

        claps = Claps()
        robotoType = Typeface.createFromAsset(view.context.assets, "Roboto-Light.ttf")
        val txt = view.findViewById<TextView>(R.id.infoHeading)
        txt.typeface = robotoType
        mySurfaceView = view.findViewById(R.id.waveform)
        mySurfaceHolder = mySurfaceView.holder

        mTransition = LayoutTransition()
        mainLayout.layoutTransition = mTransition
        AnimationUtils.loadAnimation(view.context, R.anim.slide_up_right)
        customAppearingAnim = mTransition.getAnimator(LayoutTransition.APPEARING)
        customDisappearingAnim = mTransition.getAnimator(LayoutTransition.DISAPPEARING)
        customChangingAppearingAnim = mTransition.getAnimator(LayoutTransition.CHANGE_APPEARING)
        customChangingDisappearingAnim = mTransition.getAnimator(LayoutTransition.CHANGE_DISAPPEARING)

        createCustomAnimations(mTransition)
        setupCustomAnimations(mTransition)


        micButton.setOnClickListener {
            startRecord()
        }
        cancelButton.setOnClickListener {
            cancelRecord()
            reStart()
        }
    }

    private fun setupCustomAnimations(mTransition: LayoutTransition) {
        mTransition.setAnimator(LayoutTransition.APPEARING, customAppearingAnim)
        mTransition.setAnimator(LayoutTransition.DISAPPEARING, customDisappearingAnim)
        mTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, customChangingAppearingAnim)
        mTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, customChangingDisappearingAnim)
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun createCustomAnimations(transition: LayoutTransition) {

        // Changing while Adding
        @SuppressLint("ObjectAnimatorBinding") val pvhLeft = PropertyValuesHolder.ofInt("left", 0, 1)
        @SuppressLint("ObjectAnimatorBinding") val pvhTop = PropertyValuesHolder.ofInt("top", 0, 1)
        @SuppressLint("ObjectAnimatorBinding") val pvhRight = PropertyValuesHolder.ofInt("right", 0, 1)
        @SuppressLint("ObjectAnimatorBinding") val pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 1)
        @SuppressLint("ObjectAnimatorBinding") val pvhScaleX = PropertyValuesHolder.ofFloat("translationX", 1f, 0f, 1f)
        @SuppressLint("ObjectAnimatorBinding") val pvhScaleY = PropertyValuesHolder.ofFloat("translationY", 1f, 0f, 1f)
        customChangingAppearingAnim = ObjectAnimator.ofPropertyValuesHolder(
            this, pvhLeft, pvhTop, pvhRight, pvhBottom, pvhScaleX, pvhScaleY).setDuration(transition.getDuration(LayoutTransition.CHANGE_APPEARING))
        customChangingAppearingAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(anim: Animator) {
                val view = (anim as ObjectAnimator).target as View
                view.scaleX = 1f
                view.scaleY = 1f
            }
        })

        customChangingAppearingAnim = transition.getAnimator(LayoutTransition.CHANGE_APPEARING)

        // Changing while Removing
        val kf0 = Keyframe.ofFloat(0f, 0f)
        val kf1 = Keyframe.ofFloat(.9999f, 360f)
        val kf2 = Keyframe.ofFloat(1f, 0f)
        val pvhRotation = PropertyValuesHolder.ofKeyframe("translationY", kf0, kf1, kf2)
        customChangingDisappearingAnim = ObjectAnimator.ofPropertyValuesHolder(
            this, pvhLeft, pvhTop, pvhRight, pvhBottom, pvhRotation).setDuration(transition.getDuration(LayoutTransition.CHANGE_DISAPPEARING))
        customChangingDisappearingAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(anim: Animator) {
                val view = (anim as ObjectAnimator).target as View
                view.rotation = 0f
            }
        })

        // Adding
        @SuppressLint("ObjectAnimatorBinding") val pvhTranslateY = PropertyValuesHolder.ofFloat("translationY", 300f, 0f)
        @SuppressLint("ObjectAnimatorBinding") val pvhAlpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)

        customAppearingAnim = ObjectAnimator.ofPropertyValuesHolder(this, pvhTranslateY, pvhAlpha).setDuration(transition.getDuration(LayoutTransition.APPEARING))
        customAppearingAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(anim: Animator) {
                val view = (anim as ObjectAnimator).target as View
                view.rotationY = 0f
            }
        })

        // Removing
        customDisappearingAnim = ObjectAnimator.ofFloat(null, "alpha", 1f, 0f).setDuration(transition.getDuration(LayoutTransition.DISAPPEARING) / 2)
        customDisappearingAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(anim: Animator) {
                val view = (anim as ObjectAnimator).target as View
                view.rotationX = 0f
            }
        })


    }

    override fun onDestroy() {
        super.onDestroy()
        if (isrecording) {
            audioLogger.cancel = true
        }
        numberofclaps = 0
        listenBase = false
        baseline = 0.0
    }

    fun reStart() {
        if (isrecording) {
            audioLogger.cancel = true
        }
        numberofclaps = 0
        listenBase = true
        baseline = 0.0
    }

    override fun onPause() {
        super.onPause()
        if (isrecording) {
            audioLogger.cancel = true
        }
        mySurfaceView.terminateThread()
    }

    override fun onResume() {
        super.onResume()
        if (mySurfaceHolder.surface.isValid) {
            mySurfaceView.startThread()
        }
    }

    fun cancelRecord() {
        /*
				int num = 150;
				GraphViewData[] data = new GraphViewData[num];
				float st = 0;
				for (int i = 0; i < num; i++) {
					st+=0.2;
					data[i] = new GraphViewData(st, Math.sin(st));
				}
				displayGraph(R.layout.frcard, R.id.frText, R.id.frGraph, true, data, st, R.id.frGraphView);*/
        if (isrecording) {
            audioLogger.cancel = true
        }
    }

    fun startRecord() {
        if (!isrecording) {
            //Log.d(TAG, "in startRecord");
            val but = view?.findViewById<ImageButton>(R.id.micButton)
            if (listenBase) {
                but?.setColorFilter(Color.BLUE)
                val txt = view?.findViewById<TextView>(R.id.infoDescription)
                val txt2 = view?.findViewById<TextView>(R.id.infoHeading)
                txt2?.text = "Baseline 측정."
                txt?.text = "마이크 버튼을 누르면 박수소리나 충격음을 내 주세요"

            } else {
                but?.setColorFilter(Color.RED)
            }
            mySurfaceView.setBaseline(listenBase)

            audioLogger = ClapImpulseResponse(50.0 * baseline, listenBase, baseline, mySurfaceView)
            audioRecorder = AudioClipRecorder(audioLogger)
            recordTask = ClapRecord()
            analyzeTask = ClapAnalyze()
            mySurfaceView.setRecording()
            isrecording = true
            analyzeTask.execute()
        }
    }

    fun addCard(resource: Int, root: ViewGroup?) {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val child = inflater!!.inflate(resource, root) as LinearLayout
        //		Animation anim;
        //			if (inversed) {
        //				anim = AnimationUtils.loadAnimation(this, R.anim.slide_up_right);
        //			}
        //			else {
        //				 anim = AnimationUtils.loadAnimation(this, R.anim.slide_up_left);
        //			}
        //			inversed = !inversed;
        mainLayout.addView(child)
        //			child.startAnimation(anim);

    }

    fun removeCard(resource: Int) {
        val deadChild = view?.findViewById<LinearLayout>(resource)

        mainLayout.removeView(deadChild)

    }

    fun displayNumClaps() {
        val current = view?.findViewById<View>(R.id.countCard)
        if (current == null) {
            addCard(R.layout.clapcount, null)
            val txt = view?.findViewById<TextView>(R.id.numclaps)
            txt?.typeface = robotoType
        } else {
            val txt = view?.findViewById<TextView>(R.id.numclaps)
            txt?.text = "$numberofclaps claps."
        }
        //Log.d(TAG, "displaying clap");
    }

    fun displayGraph(card: Int, resource: Int, graph: Int, which: Int, end: Float, graphview: Int) {

        val c = view?.findViewById<View>(resource)
        var graphView = view?.findViewById<GraphView>(graphview)
        if (c == null) {
            graphView = LineGraphView(view?.context!!, "")
            when (which) {
                0 -> claps.root?.clapCurve?.let { graphView.addSeries(it) }
                1 -> claps.root?.freqDecay?.let { graphView.addSeries(it) }
//                2 -> claps.root?.clapSpectra?.let { graphView.addSeries(it) }
//                3 -> claps.root?.freqResponse?.let { graphView.addSeries(it) }
            }

            graphView.setScalable(true)
            graphView.isScrollable = true
            graphView.setViewPort(0.0, end.toDouble())
            addCard(card, null)
            val txt = view?.findViewById<TextView>(resource)
            txt?.typeface = robotoType

            val g = view?.findViewById<LinearLayout>(graph)
            g?.addView(graphView)
            graphView.id = graphview
        } else {
            val current = claps.root
            val next = current?.next
            val avg: Array<GraphViewData?>
            val avgseries: GraphViewSeries
            val avgs: FloatArray

            when (which) {
                0 -> {
                    current?.clapCurve?.setStyle(Color.GRAY, 3)
                    graphView?.run { addSeries(current!!.clapCurve) }
                    next?.clapCurve!!.setStyle(Color.LTGRAY, 3)
                    next.clapCurve.setDescription("이전 샘플")

                    avg = arrayOfNulls(current.clapData.size)
                    avgs = claps.getAverage(0)

                    for (i in avg.indices) {
                        avg[i] = GraphViewData(
                            current.clapData[i]!!.valueX,
                            avgs[i].toDouble()
                        )
                    }

                    if (numberofclaps > 1)
                        claps.clapCurve?.let { graphView?.removeSeries(it) }

                    avgseries = GraphViewSeries("평균", null, avg)
                    claps.clapCurve = avgseries
                    graphView?.addSeries(avgseries)


                }
                1 -> {
                    current?.freqDecay?.setStyle(Color.GRAY, 3)
                    graphView?.addSeries(current?.freqDecay!!)
                    next?.freqDecay?.setStyle(Color.LTGRAY, 3)
                    next?.freqDecay?.setDescription("이전 샘플")

                    avg = arrayOfNulls(current?.freqDecayData!!.size)
                    avgs = claps.getAverage(1)

                    for (i in avg.indices) {
                        avg[i] = GraphViewData(current.freqDecayData[i]!!.valueX, avgs[i].toDouble())
                    }

                    if (numberofclaps > 1)
                        claps.freqDecay?.let { graphView?.removeSeries(it) }

                    avgseries = GraphViewSeries("평균", null, avg)
                    claps.freqDecay = avgseries

                    graphView?.addSeries(avgseries)
                }
//                2 -> {
//                    current?.clapSpectra?.setStyle(Color.GRAY, 3)
//                    graphView?.addSeries(current?.clapSpectra!!)
//                    next?.clapSpectra?.setStyle(Color.LTGRAY, 3)
//                    next?.clapSpectra?.setDescription("이전 샘플")
//
//                    avg = arrayOfNulls(current?.clapSpecData!!.size)
//                    avgs = claps.getAverage(2)
//
//                    for (i in avg.indices) {
//                        avg[i] = GraphViewData(current.clapSpecData[i]!!.valueX, avgs[i].toDouble())
//                    }
//                    if (numberofclaps > 1)
//                        claps.clapSpectra?.let { graphView?.removeSeries(it) }
//
//                    avgseries = GraphViewSeries("평균", null, avg)
//                    claps.clapSpectra = avgseries
//
//                    graphView?.addSeries(avgseries)
//                }
//                3 -> {
//                    current?.freqResponse?.setStyle(Color.GRAY, 3)
//                    graphView?.addSeries(current?.freqResponse!!)
//                    next?.freqResponse?.setStyle(Color.LTGRAY, 3)
//                    next?.freqResponse?.setDescription("이전 샘플")
//                    avg = arrayOfNulls(current?.freqRespData!!.size)
//                    avgs = claps.getAverage(3)
//
//                    for (i in avg.indices) {
//                        avg[i] = GraphViewData(current.freqRespData[i]!!.valueX, avgs[i].toDouble())
//                    }
//
//                    if (numberofclaps > 1)
//                        claps.freqResponse?.let { graphView?.removeSeries(it) }
//
//                    avgseries = GraphViewSeries("평균", null, avg)
//                    claps.freqResponse = avgseries
//
//                    graphView?.addSeries(avgseries)
//                }
            }


        }

        graphView?.isShowLegend = true
        graphView?.legendAlign = LegendAlign.TOP
        graphView?.legendWidth = 130f
        graphView?.setViewPort(0.0, end.toDouble())
        graphView?.redrawAll()
        view?.invalidate()

    }

    @SuppressLint("StaticFieldLeak")
    inner class ClapAnalyze : AsyncTask<Void, Void, Int>() {
        private var record: StatusUpdate? = null
        private var clap: Clap? = null

        override fun onPreExecute() {
            super.onPreExecute()
            record = StatusUpdate()
            clap = Clap()

            record!!.base = !listenBase
            record!!.done = true
            record!!.clap_heard = true
            record!!.RT60 = true
            record!!.clapgraph = true

            record!!.RT60s = true
//            record!!.dsSpectra = true
//            record!!.freqResp = true
            recordTask.execute()
        }

        override fun onPostExecute(i: Int?) {}

        override fun onProgressUpdate(vararg v: Void) {
            super.onProgressUpdate(*v)
            val status = audioLogger.status
            val results = audioLogger.results

            if (!status.base && !record!!.base) {
                //Log.d(TAG, "false");
                micButton.setColorFilter(Color.RED)
                addCard(R.layout.noiselevelcard, null)
                val txt = view?.findViewById<TextView>(R.id.noiseLevel)
                txt?.typeface = robotoType
                record!!.base = !record!!.base

                listenBase = false
                baseline = audioLogger.getBaseLine()
                var bl = audioLogger.getBaseLineV()
                val roundOff = 2

                var bd = bl.toBigDecimal()
                bd = bd.setScale(roundOff, BigDecimal.ROUND_HALF_UP)
                bl = bd.toDouble()
                txt?.text = "$bl dB."
                mySurfaceView.setBaseline(listenBase)
            }


            if (status.RT60 && record!!.RT60) {
                val current = view?.findViewById<View>(R.id.reverbCard)
                if (current == null) {
                    addCard(R.layout.reverbcard, null)
                }
                val txtR = view?.findViewById<TextView>(R.id.reverbTimeRecent)
                txtR?.typeface = robotoType


                var rt = results.RT60.toBigDecimal()
                rt = rt.setScale(2, BigDecimal.ROUND_HALF_UP)
                val rtd = rt.toDouble()

                val txt = view?.findViewById<TextView>(R.id.reverbTimeDescription)
                val txtA = view?.findViewById<TextView>(R.id.reverbTimeAverage)
                if (results.RT60 == -1f) {
                    txtR?.text = "소리가 너무 작습니다.."
                    txt?.text = "충격음을 더 크게 내 주세요"
                } else if (results.RT60 < 0) {
                    txtR?.text = "$rtd 초."
                    txt?.text = "Overall reverberation time. Something has gone horribly wrong."
                } else {
                    var rtavg: Float
                    numberofclaps++
                    clap!!.RT60 = results.RT60
                    clap!!.clapNumber = numberofclaps
                    claps.insert(clap!!)
                    clap!!.inserted = true
                    val currentAvg = claps.averageRT60
                    var std = claps.std()
                    if (currentAvg < 0) {
                        rtavg = rtd.toFloat()
                    } else {
                        rtavg = currentAvg
                        var rtav = BigDecimal(rtavg.toDouble())
                        rtav = rtav.setScale(2, BigDecimal.ROUND_HALF_UP)
                        rtavg = rtav.toDouble().toFloat()
                        var st = BigDecimal(std.toDouble())
                        st = st.setScale(2, BigDecimal.ROUND_HALF_UP)
                        std = st.toDouble().toFloat()
                    }

                    txtR?.text = "$rtd 초."
                    txt?.text = "전체 잔향 시간"
                    txtA?.text = "Mean: $rtavg, Variance: $std"
                }

                displayNumClaps()
                record!!.clap_heard = false
                removeCard(R.id.infoCard)
                record!!.RT60 = false
            }

            if (status.clapgraph && record!!.clapgraph) {
                if (clap!!.inserted) {
                    val window = 6
                    val data = arrayOfNulls<GraphViewData>(results.clapData?.size!! / window)
                    var x = 0f
                    for (i in data.indices) {
                        x = (window * i * results.smoothwindow).toFloat() / results.sampleRate
                        data[i] = GraphViewData(x.toDouble(), results.clapData!![window * i].toDouble())
                    }
                    clap!!.clapCurve = GraphViewSeries("최근 샘플", null, data)
                    clap!!.clapData = data
                    displayGraph(R.layout.clapcard, R.id.clapText, R.id.clapGraph, 0, x, R.id.clapGraphView)
                    record!!.clapgraph = false
                }
            }

            if (status.RT60s && record!!.RT60s) {
                val data = arrayOfNulls<GraphViewData>(results.RT60s?.size!!)
                for (i in data.indices) {
                    data[i] = GraphViewData(results.freqs!![i].toDouble(), results.RT60s!![i].toDouble())
                }
                clap!!.freqDecay = GraphViewSeries("최근 샘플", null, data)
                clap!!.freqDecayData = data
                displayGraph(R.layout.fdecay, R.id.fdText, R.id.fdGraph, 1, results.freqs!![results.freqs?.size!! - 1], R.id.fdGraphView)
                record!!.RT60s = false
//                val RT60s_10 = FloatArray(10)
//                var j = 0
//
//                for (i in RT60s_10.indices) {
//                    if (i < 1) {
//                        j += 2
//                        RT60s_10[i] = results.RT60s?.get(j)!!
//                    } else {
//                        j += 4
//                        RT60s_10[i] = results.RT60s?.get(j)!!
//                    }
//                }
//                val RT60s_10_String = "[${RT60s_10[0]}, ${RT60s_10[1]},${RT60s_10[2]},${RT60s_10[3]},${RT60s_10[4]}," +
//                        "${RT60s_10[5]},${RT60s_10[6]},${RT60s_10[7]},${RT60s_10[8]},${RT60s_10[9]}]"
//                tv_rt60s.text = RT60s_10_String
            }

//            if (status.dsSpectra && record!!.dsSpectra) {
//                val data = arrayOfNulls<GraphViewData>(results.dsSpectra?.size!!)
//                for (i in data.indices) {
//                    data[i] = GraphViewData(results.freqs!![i].toDouble(), results.dsSpectra!![i].toDouble())
//                }
//                clap!!.clapSpectra = GraphViewSeries("최근 샘플", null, data)
//                clap!!.clapSpecData = data
//                displayGraph(R.layout.clapspec, R.id.csText, R.id.csGraph, 2, results.freqs!![results.freqs?.size!! - 1], R.id.csGraphView)
//                record!!.dsSpectra = false
//            }
//
//            if (status.freqResp && record!!.freqResp) {
//                val data = arrayOfNulls<GraphViewData>(results.freqResp?.size!!)
//                for (i in data.indices) {
//                    data[i] = GraphViewData(results.freqs!![i].toDouble(), results.freqResp!![i].toDouble())
//                }
//                clap!!.freqResponse = GraphViewSeries("최근 샘플", null, data)
//                clap!!.freqRespData = data
//                displayGraph(R.layout.frcard, R.id.frText, R.id.frGraph, 3, results.freqs!![results.freqs?.size!! - 1], R.id.frGraphView)
//                record!!.freqResp = false
//            }

            if (status.done && record!!.done) {
                val but = view?.findViewById<ImageButton>(R.id.micButton)
                but?.setColorFilter(Color.DKGRAY)
                mySurfaceView.stopRecording()
                record!!.done = false
            }
        }


        override fun doInBackground(vararg v: Void): Int? {
            while (isrecording) {
                publishProgress()
            }
            publishProgress()
            return 0
        }

    }


    inner class Claps {
        var root: Clap? = null
        var clapCurve: GraphViewSeries? = null
        var clapSpectra: GraphViewSeries? = null
        var freqResponse: GraphViewSeries? = null
        var freqDecay: GraphViewSeries? = null

        //Log.d(TAG, "no claps yet");
        val averageRT60: Float
            get() {
                if (root == null) {
                    return -1f
                }
                var current = root
                var sum = 0f
                var num = 0
                while (current != null) {
                    sum += current.RT60
                    num++
                    current = current.next
                }
                return sum / num
            }

        init {
            this.root = null
        }

        fun getAverage(which: Int): FloatArray {
            var current = root
            var num = 0

            var summed = FloatArray(2)

            when (which) {
                0 -> {
                    var longest = root
                    var max = 0
                    while (longest != null) {
                        if (longest.clapData.size > max) {
                            max = longest.clapData.size
                        }
                        longest = longest.next
                    }
                    summed = FloatArray(max)
                }
                1 -> summed = FloatArray(current!!.freqDecayData.size)
                2 -> summed = FloatArray(current!!.clapSpecData.size)
                3 -> summed = FloatArray(current!!.freqRespData.size)
            }

            for (i in summed.indices) {
                summed[i] = 0f
            }

            var c: Float
            while (current != null) {
                when (which) {
                    0 -> for (i in summed.indices) {
                        if (i >= current.clapData.size) {
                            c = 0f
                        } else {
                            c = current.clapData[i]!!.valueY.toFloat()
                        }
                        summed[i] += c
                    }
                    1 -> for (i in summed.indices) {
                        c = current.freqDecayData[i]!!.valueY.toFloat()
                        summed[i] += c
                    }
                    2 -> for (i in summed.indices) {
                        c = current.clapSpecData[i]!!.valueY.toFloat()
                        summed[i] += c
                    }
                    3 -> for (i in summed.indices) {
                        c = current.freqRespData[i]!!.valueY.toFloat()
                        summed[i] += c
                    }
                }
                current = current.next
                num++
            }

            for (i in summed.indices) {
                summed[i] = summed[i] / num
            }
            return summed
        }

        fun std(): Float {
            if (root == null) {
                //Log.d(TAG, "no claps yet");
                return -1f
            }
            var current = root
            var sum = 0f
            var sqrs = 0f
            var num = 0
            while (current != null) {
                sum += current.RT60
                sqrs += current.RT60 * current.RT60
                num++
                current = current.next
            }
            val lower = sum * sum / num
            return Math.sqrt(((sqrs - lower) / num).toDouble()).toFloat()
        }

        fun display() {
            var current = root
            while (current != null) {
                //Log.d(TAG, current.clapNumber + " -> ");
                current = current.next
            }
        }

        fun insert(ins: Clap) {
            ins.next = root
            root = ins
        }

    }

    inner class Clap {
        lateinit var freqDecay: GraphViewSeries
        lateinit var freqDecayData: Array<GraphViewData?>

        lateinit var freqResponse: GraphViewSeries
        lateinit var freqRespData: Array<GraphViewData?>

        lateinit var clapCurve: GraphViewSeries
        lateinit var clapData: Array<GraphViewData?>

        lateinit var clapSpectra: GraphViewSeries
        lateinit var clapSpecData: Array<GraphViewData?>

        var RT60: Float = 0.toFloat()
        var clapNumber: Int = 0
        var next: Clap? = null
        var inserted: Boolean = false

        init {
            this.inserted = false
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class ClapRecord : AsyncTask<Void, Void, Int>() {

        override fun doInBackground(vararg v: Void): Int? {
            val heard = audioRecorder.startRecordingForTime(1,
                AudioClipRecorder.RECORDER_SAMPLERATE_CD,
                AudioFormat.ENCODING_PCM_16BIT)
            if (heard) {
                if (!audioLogger.cancel) {
                    audioLogger.process()
                }
            }
            isrecording = false
            return 0
        }
    }

}
