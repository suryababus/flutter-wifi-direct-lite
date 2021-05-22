package com.example.flutter_wifi_direct_lite

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import io.flutter.plugin.common.MethodChannel

class ChannelToFlutter(private val channel: MethodChannel,private val activity: Activity) {
    enum class MessageCode {
        PEERS_CHANGED,
        MESSAGE_RECEIVED,
        CONNECTION_CHANGED
    }


    fun sendData(messageCode: MessageCode, data: Any) {
        activity.runOnUiThread {
            when (messageCode) {
                MessageCode.MESSAGE_RECEIVED -> {
                    // can be json string or file part
                    channel.invokeMethod("messageReceived", data)
                }
                MessageCode.PEERS_CHANGED -> {
                    // List<Hashmap<String,String>>
                    Log.d("Sending peers", data.toString())
                    channel.invokeMethod("peersChanged", data)
                }
                MessageCode.CONNECTION_CHANGED -> {
                    //String CONNECTED or DISCONNECTED
                    channel.invokeMethod("connectionChanged", data)
                }
            }
        }
    }

}