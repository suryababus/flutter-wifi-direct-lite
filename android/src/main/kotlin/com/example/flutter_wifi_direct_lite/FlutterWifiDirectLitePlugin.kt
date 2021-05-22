package com.example.flutter_wifi_direct_lite

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File

/** FlutterWifiDirectLitePlugin */
class FlutterWifiDirectLitePlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var wifiP2pChannel: WifiP2pManager.Channel
    private lateinit var wifiDirectHandler: WifiDirectHandler
    private lateinit var activity: Activity

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        requestPermission()
    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {

    }

    override fun onDetachedFromActivity() {
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_wifi_direct_lite")
        context = flutterPluginBinding.applicationContext

        channel.setMethodCallHandler(this)

        //method call handler


    }


    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        Log.d("onMethodCall_Kotlin", call.method)
        when (call.method) {
            "connectToDevice" -> {
                val deviceAddress = call.argument<String>("deviceAddress")
                if (deviceAddress != null) {
                    wifiDirectHandler.connectToDevice(deviceAddress)
                }
            }
            "startDiscovery" -> {
                wifiDirectHandler.startDiscovery(object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        result.success(true)
                    }
                    override fun onFailure(reasonCode: Int) {
                        result.success(false)
//                        result.error("$reasonCode","Cannot start Start Discovery","Check wifi is turned on and hotspot turned off")
                    }
                })
            }
            "sendMessage" -> {
                val message = call.argument<String>("message")
                if (message != null) {
                    wifiDirectHandler.sendMessage(message)
                }
            }
            "sendFile" -> {
                val path = call.argument<String>("filePath")
                if (path != null) {
                    wifiDirectHandler.sendFile(File(path))
                }
            }
            "disconnect"->{
                wifiDirectHandler.disconnect(result)
            }
            "connectionStatus"->{
                wifiDirectHandler.connectionStatus(result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    private fun requestPermission() {
        when (ContextCompat.checkSelfPermission(
                context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )) {
            PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                wifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
                wifiP2pChannel = wifiP2pManager.initialize(context, Looper.getMainLooper(), null)
                wifiDirectHandler = WifiDirectHandler(wifiP2pManager, wifiP2pChannel, context, ChannelToFlutter(channel,activity))

            }


            else -> {
                // You can directly ask for the permission.
                ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE),
                        0)
            }
        }


    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        wifiDirectHandler.dispose()
    }

}
