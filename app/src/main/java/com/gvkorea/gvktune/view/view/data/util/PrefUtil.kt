package com.gvkorea.gvktune.view.view.data.util

import android.content.Context
import android.preference.PreferenceManager
import com.gvkorea.gvktune.view.view.data.DataFragment
import com.gvkorea.gvktune.view.view.data.DataFragment.Companion.remainingMin

class PrefUtil {

    companion object{
        fun getTimerLength(context: Context): Int {
            //placeholder
            return remainingMin
        }

        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.gvkorea.timer.previous_timer_leng"

        fun getPreviousTimerLengthSeconds(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        private const val TIMER_STATE_ID = "com.gvkorea.timer.timer_state"

        fun getTimerState(context: Context): DataFragment.TimerState {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return DataFragment.TimerState.values()[ordinal]
        }

        fun setTimerState(state: DataFragment.TimerState, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        private const val SECONDS_REMAINING_ID = "com.gvkorea.timer.seconds_remaining"

        fun getSecondsRemaining(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }

    }
}