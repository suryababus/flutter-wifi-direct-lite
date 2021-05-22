package com.example.flutter_wifi_direct_lite

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

class WifiDirectBroadcastReceiver(
       private val wifiP2pManager: WifiP2pManager,
       private val wifiP2pChannel:WifiP2pManager.Channel
): BroadcastReceiver() {
    private lateinit var _peerListListener:WifiP2pManager.PeerListListener
    private lateinit var _connectionInfoListener:WifiP2pManager.ConnectionInfoListener

    fun setPeerListListener(peerListListener:WifiP2pManager.PeerListListener){
        _peerListListener = peerListListener
    }
    fun setConnectionInfoListener(connectionInfoListener:WifiP2pManager.ConnectionInfoListener){
        _connectionInfoListener = connectionInfoListener
    }

    companion object{
        fun getIntentFilter():IntentFilter{
             val intentFilter = IntentFilter()
            // Indicates a change in the Wi-Fi P2P status.

            intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)

            // Indicates a change in the list of available peers.
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)

            // Indicates the state of Wi-Fi P2P connectivity has changed.
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

            // Indicates this device's details have changed.
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
            return intentFilter
        }

    }





    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Determine if Wifi P2P mode is enabled or not, alert
                // the Activity.
//                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                Log.d("WIFI_P2P_STATE","WIFI_P2P_STATE_CHANGED_ACTION")
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // The peer list has changed! We should probably do something about
                // that.
                Log.d("Peers changed","Peers changed")
                if(!this::_peerListListener.isInitialized) return
                wifiP2pManager.requestPeers(wifiP2pChannel,_peerListListener)
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                // Connection state changed! We should probably do something about
                // that.
                if(!this::_connectionInfoListener.isInitialized) return
                wifiP2pManager.requestConnectionInfo(wifiP2pChannel,_connectionInfoListener)
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {

            }

        }
    }
}