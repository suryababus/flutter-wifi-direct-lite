package com.example.flutter_wifi_direct_lite


import android.util.Log
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class Client(
        private val hostAddress:String,
        private val socketConnectionListener: SocketConnectionListener
):Thread() {
    private lateinit var socket: Socket
    override fun run() {
        try {
            socket = Socket()
            while (!socket.isConnected){
                socket.connect(InetSocketAddress(hostAddress,8888),500)
                Log.d("Client", "trying")
            }
            Log.d("Client", "Client connected")
            socketConnectionListener.onSuccessfulConnection(socket)

        }catch (e:IOException){
            e.printStackTrace()
        }
    }
}