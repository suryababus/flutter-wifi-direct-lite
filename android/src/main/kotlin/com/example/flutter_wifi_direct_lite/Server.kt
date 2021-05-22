package com.example.flutter_wifi_direct_lite


import android.util.Log
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

interface SocketConnectionListener{
    fun onSuccessfulConnection(socket: Socket)
}


class Server(
        private val socketConnectionListener: SocketConnectionListener
) : Thread(){
    private lateinit var socket:Socket
    private lateinit var serverSocket: ServerSocket
    override fun run() {
        try {
            serverSocket = ServerSocket(8888)
            Log.d("Server","serverSocket started")
            socket = serverSocket.accept()
            Log.d("Server","serverSocket accepted")
            socketConnectionListener.onSuccessfulConnection(socket)

        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    override fun interrupt() {
        super.interrupt()
        serverSocket.close()
    }
}