import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_wifi_direct_lite/AppMethodCallHandler.dart';
import 'package:flutter_wifi_direct_lite/WifiDirect.dart';

import 'CallToKotlin.dart';

class FlutterWifiDirectLite {
  static const MethodChannel _channel =
      const MethodChannel('flutter_wifi_direct_lite');
  static var _appMethodCallHandler = AppMethodCallHandler();
  static var _callToKotlin = CallToKotlin(_channel);
  static void subscribeToWifiDirectEvents(WifiDirect wifiDirect) {
    _appMethodCallHandler.setWifiDirect(wifiDirect);
  }

  static Future<bool> startDiscovery(WifiDirect wifiDirect) {
    print("Setting onMethodChannel");
    _channel.setMethodCallHandler(_appMethodCallHandler.callHandler);
    _appMethodCallHandler.setWifiDirect(wifiDirect);
    return _callToKotlin.startDiscovery();
  }

  static void connectToDevice(Device device) {
    _callToKotlin.connectToDevice(device);
  }

  static void sendMessage(String message) {
    _callToKotlin.sendMessage(message);
  }

  static void sendFile(String filePath) {
    _callToKotlin.sendFile(filePath);
  }

  static Future<bool> connectionStatus() async {
    return _callToKotlin.connectionStatus();
  }

  static Future<bool> disconnect() async {
    return _callToKotlin.disconnect();
  }
}
