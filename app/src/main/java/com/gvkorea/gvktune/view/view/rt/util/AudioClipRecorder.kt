package com.gvkorea.gvktune.view.view.rt.util

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioRecord.OnRecordPositionUpdateListener
import android.media.MediaRecorder.AudioSource
import android.os.AsyncTask
import android.util.Log

class AudioClipRecorder(private val clipListener: AudioClipListener) {
    private var recorder: AudioRecord? = null

    /**
     * state variable to control starting and stopping recording
     */
    var isRecording = false
        private set
    private var task: AsyncTask<Any,Any,Any>? = null
    private var heard = false

    constructor(clipListener: AudioClipListener, task: AsyncTask<Any,Any,Any>?) : this(clipListener) {
        this.task = task
    }

    /**
     * start recording: set the parameters that correspond to a buffer that
     * contains millisecondsPerAudioClip milliseconds of samples
     */
    fun startRecordingForTime(
        millisecondsPerAudioClip: Int,
        sampleRate: Int, encoding: Int
    ): Boolean {
        val percentOfASecond = millisecondsPerAudioClip.toFloat() / 1000.0f
        val numSamplesRequired = (sampleRate.toFloat() * percentOfASecond).toInt()
        val bufferSize = determineCalculatedBufferSize(
            sampleRate, encoding,
            numSamplesRequired
        )
        return doRecording(
            sampleRate, encoding, bufferSize,
            numSamplesRequired, DEFAULT_BUFFER_INCREASE_FACTOR
        )
    }
    /**
     * start recording: Use a minimum audio buffer and a read buffer of the same
     * size.
     */
    /**
     * records with some default parameters
     */
    @JvmOverloads
    fun startRecording(
        sampleRate: Int = RECORDER_SAMPLERATE_8000, encoding: Int =
            AudioFormat.ENCODING_PCM_16BIT
    ): Boolean {
        val bufferSize = determineMinimumBufferSize(sampleRate, encoding)
        return doRecording(
            sampleRate, encoding, bufferSize, bufferSize,
            DEFAULT_BUFFER_INCREASE_FACTOR
        )
    }

    private fun determineMinimumBufferSize(sampleRate: Int, encoding: Int): Int {
        return AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO, encoding
        )
    }

    /**
     * Calculate audio buffer size such that it holds numSamplesInBuffer and is
     * bigger than the minimum size<br></br>
     *
     * @param numSamplesInBuffer
     * Make the audio buffer size big enough to hold this many
     * samples
     */
    private fun determineCalculatedBufferSize(
        sampleRate: Int,
        encoding: Int, numSamplesInBuffer: Int
    ): Int {
        val minBufferSize = determineMinimumBufferSize(sampleRate, encoding)
        var bufferSize: Int
        // each sample takes two bytes, need a bigger buffer
        bufferSize = if (encoding == AudioFormat.ENCODING_PCM_16BIT) {
            numSamplesInBuffer * 2
        } else {
            numSamplesInBuffer
        }
        if (bufferSize < minBufferSize) {
            Log.w(
                TAG, "Increasing buffer to hold enough samples "
                        + minBufferSize + " was: " + bufferSize
            )
            bufferSize = minBufferSize
        }
        return bufferSize
    }

    /**
     * Records audio until stopped the [.task] is canceled,
     * [.continueRecording] is false, or [.clipListener] returns
     * true <br></br>
     * records audio to a short [readBufferSize] and passes it to
     * [.clipListener] <br></br>
     * uses an audio buffer of size bufferSize * bufferIncreaseFactor
     *
     * @param recordingBufferSize
     * minimum audio buffer size
     * @param readBufferSize
     * reads a buffer of this size
     * @param bufferIncreaseFactor
     * to increase recording buffer size beyond the minimum needed
     */
    private fun doRecording(
        sampleRate: Int, encoding: Int,
        recordingBufferSize: Int, readBufferSize: Int,
        bufferIncreaseFactor: Int
    ): Boolean {
        if (recordingBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "Bad encoding value, see logcat")
            return false
        } else if (recordingBufferSize == AudioRecord.ERROR) {
            Log.e(TAG, "Error creating buffer size")
            return false
        }

        // give it extra space to prevent overflow
        val increasedRecordingBufferSize = recordingBufferSize * bufferIncreaseFactor
        recorder = AudioRecord(
            AudioSource.MIC, sampleRate,
            AudioFormat.CHANNEL_IN_MONO, encoding,
            increasedRecordingBufferSize
        )
        val readBuffer = ShortArray(readBufferSize)
        isRecording = true
        Log.d(
            TAG, "start recording, " + "recording bufferSize: "
                    + increasedRecordingBufferSize
                    + " read buffer size: " + readBufferSize
        )

        //Note: possible IllegalStateException
        //if audio recording is already recording or otherwise not available
        //AudioRecord.getState() will be AudioRecord.STATE_UNINITIALIZED
        recorder!!.startRecording()
        while (isRecording) {
            val bufferResult = recorder!!.read(readBuffer, 0, readBufferSize)
            //in case external code stopped this while read was happening
            if (!isRecording || task != null && task!!.isCancelled) {
                break
            }
            // check for error conditions
            if (bufferResult == AudioRecord.ERROR_INVALID_OPERATION) {
                Log.e(
                    TAG,
                    "error reading: ERROR_INVALID_OPERATION"
                )
            } else if (bufferResult == AudioRecord.ERROR_BAD_VALUE) {
                Log.e(
                    TAG,
                    "error reading: ERROR_BAD_VALUE"
                )
            } else  // no errors, do processing
            {
                heard = clipListener.heard(readBuffer, sampleRate)
                if (heard) {
                    stopRecording()
                }
            }
        }
        done()
        return heard
    }

    fun stopRecording() {
        isRecording = false
    }

    /**
     * need to call this when completely done with recording
     */
    fun done() {
        Log.d(TAG, "shut down recorder")
        if (recorder != null) {
            recorder!!.stop()
            recorder!!.release()
            recorder = null
        }
    }

    /**
     * @param audioData
     * will be filled when reading the audio data
     */
    private fun setOnPositionUpdate(
        audioData: ShortArray,
        sampleRate: Int, numSamplesInBuffer: Int
    ) {
        // possibly do it that way
        // setOnNotification(audioData, sampleRate, numSamplesInBuffer);
        val positionUpdater: OnRecordPositionUpdateListener =
            object : OnRecordPositionUpdateListener {
                override fun onPeriodicNotification(recorder: AudioRecord) {
                    // no need to read the audioData again since it was just
                    // read
                    heard = clipListener.heard(audioData, sampleRate)
                    if (heard) {
                        Log.d(TAG, "heard audio")
                        stopRecording()
                    }
                }

                override fun onMarkerReached(recorder: AudioRecord) {
                    Log.d(TAG, "marker reached")
                }
            }
        // get notified after so many samples collected
        recorder!!.positionNotificationPeriod = numSamplesInBuffer
        recorder!!.setRecordPositionUpdateListener(positionUpdater)
    }

    companion object {
        private const val TAG = "AudioClipRecorder"
        const val RECORDER_SAMPLERATE_CD = 44100
        const val RECORDER_SAMPLERATE_8000 = 8000
        private const val DEFAULT_BUFFER_INCREASE_FACTOR = 3
    }

}