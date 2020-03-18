package com.gvkorea.gvktune.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.View
import com.gvkorea.gvktune.R
import kotlinx.android.synthetic.main.dialog_waiting.view.*

class WaitingDialog(val context: Context) {
    lateinit var dialog: AlertDialog
    var builder = AlertDialog.Builder(context)


    fun create(msg: String, time_sec : Long) {
        val view: View = View.inflate(context, R.layout.dialog_waiting, null)
        val handler: Handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                view.progressBar.incrementProgressBy(1)
            }
        }

        view.progressBar.max = 100
        builder.setView(view)
        builder.setTitle(msg)
        builder.setIcon(android.R.drawable.ic_menu_send)
        builder.setMessage("잠시만 기다려 주세요..")

        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()

        Thread {
            while(view.progressBar.progress <= view.progressBar.max){
                Thread.sleep((time_sec/1000 * 10))
                handler.sendMessage(handler.obtainMessage())
                if(view.progressBar.progress == view.progressBar.max){
                    dialog.dismiss()
                }
            }
        }.start()


    }



}