package com.gvkorea.gvktune.view.view.rt.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class MySurfaceView(context: Context, attributeSet: AttributeSet) : SurfaceView(context, attributeSet), SurfaceHolder.Callback {
    internal var mSurfaceHolder: SurfaceHolder
    private var mThread: DrawingThread
    private var recording: Boolean = false
    private val history: IntArray
    private val historyIndex: Int
    private var w: Int = 0
    private var h: Int = 0
    private var validhistory: Boolean = false
    private var baseline: Boolean = false
    private val surfaceCreated: Boolean = false

    internal var s: Smooth

    init {
        s = Smooth()
        mSurfaceHolder = holder
        holder.addCallback(this)
        mThread = DrawingThread()
        history = IntArray(44)
        historyIndex = 0
        for (i in history.indices) {
            history[i] = height / 2
        }
        validhistory = false
        recording = false
    }


    fun setRecording() {
        recording = true
    }

    fun stopRecording() {
        recording = false
        for (i in history.indices) {
            history[i] = height / 2
        }
    }

    fun fromClap(data: ShortArray) {
        for (i in data.indices) {
            history[i] = data[i].toInt()
        }
        validhistory = true
    }

    fun setBaseline(bl: Boolean) {
        baseline = bl
    }


    fun drawData(c: Canvas, p: Paint) {

        var mean: Float
        var current: Float
        mean = 0f
        val thresh = 60f
        for (i in history.indices) {
            current = 20 * Math.log10(Math.abs(history[i]).toFloat() / Math.pow(2.0, 15.0)).toFloat()
            if (current < -1 * thresh) {
                current = -1 * thresh
            }
            mean += current
        }

        mean /= history.size.toFloat()
        mean += thresh

        val p2 = Paint()
        p2.strokeWidth = 3f
        p2.color = Color.LTGRAY

        val wcolor = mean / thresh * width

        c.drawLine(0f, height.toFloat() / 2, wcolor, height.toFloat() / 2, p)
        c.drawLine(wcolor, height.toFloat() / 2, width.toFloat(), height.toFloat() / 2, p2)


        /*
			float max = 0;
			float[] data = new float[history.length];
			float current;
			for (int i = 0; i < history.length; i++) {
				current = Math.abs(history[i]);
				if (current > max) {
					max = current;
				}
			}

			for (int i = 0; i < history.length; i++) {
				data[i] = ((float)history[i]) / max;
			}

			int skip = 10;

			float[] smooth = s.smooth(data, skip);

			float[] points = new float[2*smooth.length/skip];
			for (int i = 0; i < points.length-1; i = i + 2) {
				points[i] = width*((float) i)/points.length;
				points[i+1] = height*(smooth[i]/2)+height/2;
			}

			c.drawLines(points, 0, points.length, p);*/
    }

    override fun onDraw(canvas: Canvas?) {
        @SuppressLint("DrawAllocation")
        val p = Paint()
        p.strokeWidth = 3f
        canvas!!.drawColor(Color.WHITE)
        if (recording && validhistory) {
            if (baseline) {
                p.color = Color.BLUE
            } else {
                p.color = Color.RED
            }
            drawData(canvas, p)
        } else {
            p.color = Color.LTGRAY
            canvas.drawLine(0f, height.toFloat() / 2, width.toFloat(), height.toFloat() / 2, p)
        }
    }

    fun startThread() {

        if (mThread.state == Thread.State.TERMINATED) {
            mThread = DrawingThread()
        }
        mThread.keepRunning = true
        mThread.start()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        //Log.d(TAG, "creating surface");
        val c = getHolder().lockCanvas()
        w = c.width
        h = c.height
        getHolder().unlockCanvasAndPost(c)

        startThread()

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        //Log.d(TAG, "destroying surface");
        terminateThread()
    }

    fun terminateThread() {

        mThread.keepRunning = false
        var retry = true
        while (retry) {
            try {
                mThread.join()
                retry = false
            } catch (e: InterruptedException) {
            }

        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        //Log.d(TAG, "changing surface");
    }

    private inner class DrawingThread : Thread() {
        internal var keepRunning = true

        @SuppressLint("WrongCall")
        override fun run() {
            var c: Canvas?
            while (keepRunning) {
                c = null
                try {
                    c = mSurfaceHolder.lockCanvas()
                    synchronized(mSurfaceHolder) {
                        onDraw(c)
                    }
                } finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c)
                    }
                }

                try {
                    sleep(10)
                } catch (e: InterruptedException) {
                }

            }
        }
    }

    companion object {
        internal val ALPHA = 0.15f

        private val TAG = "MySurfaceView"
    }
}