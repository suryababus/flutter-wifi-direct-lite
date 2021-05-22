package com.example.flutter_wifi_direct_lite

import android.content.Context
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.util.Log
import io.flutter.plugin.common.MethodChannel
import java.io.File
import java.net.Socket

class WifiDirectHandler(
        private val wifiP2pManager: WifiP2pManager,
        private val wifiP2pChannel: WifiP2pManager.Channel,
        private val context: Context,
        private val channelToFlutter: ChannelToFlutter
) {
    private val receiver = WifiDirectBroadcastReceiver(wifiP2pManager, wifiP2pChannel)
    private var sendReceive: SendReceive? = null

    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->

        val refreshedPeers = peerList.deviceList
        val devices = mutableListOf<HashMap<String, String>>()
        refreshedPeers.forEach {
            devices.add(hashMapOf(
                "deviceName" to it.deviceName,
                "deviceAddress" to it.deviceAddress
            ))
        }
        Log.d("Peers changed","Peers changed Listener: ${peerList.deviceList.size}")
        Log.d("Peers changed","$devices")
        channelToFlutter.sendData(ChannelToFlutter.MessageCode.PEERS_CHANGED, devices)
    }

    private val connectionInfoListener = WifiP2pManager.ConnectionInfoListener {
        if (sendReceive?.socket?.isConnected == true) return@ConnectionInfoListener
        Log.d("Status","Connected")
        if (it.groupFormed && it.isGroupOwner) {
            // start server at port 8888
            Log.d("Status","Server")
            Server(object : SocketConnectionListener {
                override fun onSuccessfulConnection(socket: Socket) {
                    connectionEstablished(socket)
                }

            }).start()
        } else if (it.groupFormed) {
            // connect to server at port 8888 of group owner ip
            Log.d("Status","Client")

            Client(it.groupOwnerAddress.hostAddress, object : SocketConnectionListener {
                override fun onSuccessfulConnection(socket: Socket) {
                    connectionEstablished(socket)
                }

            }).start()
        }
    }


    init {
        receiver.setPeerListListener(peerListListener)
        receiver.setConnectionInfoListener(connectionInfoListener)
        context.registerReceiver(receiver, WifiDirectBroadcastReceiver.getIntentFilter())
    }



    private fun connectionEstablished(socket: Socket) {
        channelToFlutter.sendData(ChannelToFlutter.MessageCode.CONNECTION_CHANGED,"CONNECTED")

        sendReceive = SendReceive(socket, channelToFlutter,context)
        sendReceive?.start()
    }


    fun startDiscovery(listener: WifiP2pManager.ActionListener) {
        wifiP2pManager.discoverPeers(wifiP2pChannel, listener)
    }

    fun connectToDevice(deviceAddress: String) {
        val config = WifiP2pConfig()
        config.deviceAddress = deviceAddress
        config.wps.setup = WpsInfo.PBC
        config.groupOwnerIntent = 0
        wifiP2pManager.connect(wifiP2pChannel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                wifiP2pManager.requestConnectionInfo(wifiP2pChannel, connectionInfoListener)
            }
            override fun onFailure(reason: Int) {

            }
        })
    }

    fun sendFile(file: File) {
        if(file.exists()&&file.isFile) {
            sendReceive?.write(file)
        }
    }

    fun sendMessage(message: String) {
        sendReceive?.write(message)
    }


    fun dispose() {
        context.unregisterReceiver(receiver)
    }

    fun disconnect(result: MethodChannel.Result) {
        wifiP2pManager.requestConnectionInfo(wifiP2pChannel) {
            if(it.groupFormed){
                wifiP2pManager.removeGroup(wifiP2pChannel,object:WifiP2pManager.ActionListener{
                    override fun onSuccess() {
                        result.success(true)
                    }

                    override fun onFailure(p0: Int) {
                        result.success(false)
                    }

                })
            }
        }
    }

    fun connectionStatus(result: MethodChannel.Result) {
        wifiP2pManager.requestConnectionInfo(wifiP2pChannel) {
            if (it.groupFormed) {
                result.success(true)
            } else {
                result.success(false)
            }
        }
    }
}