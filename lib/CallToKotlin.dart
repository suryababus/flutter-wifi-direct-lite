import 'package:flutter/services.dart';
import 'package:flutter_wifi_direct_lite/WifiDirect.dart';

class CallToKotlin {
  static const CONNECT_TO_DEVICE = "connectToDevice";
  static const START_DISCOVERY = "startDiscovery";
  static const SEND_MESSAGE = "sendMessage";
  static const SEND_FILE = "sendFile";
  static const DISCONNECT = "disconnect";
  static const CONNECTION_STATUS = "connectionStatus";

  MethodChannel _methodChannel;
  CallToKotlin(MethodChannel methodChannel) {
    this._methodChannel = methodChannel;
  }

  Future<bool> startDiscovery() async {
    return Future.value(
        (await _methodChannel.invokeMethod(START_DISCOVERY)) as bool);
  }

  void connectToDevice(Device device) async {
    _methodChannel
        .invokeMethod(CONNECT_TO_DEVICE, {"deviceAddress": device.address});
  }

  void sendMessage(String message) async {
    _methodChannel.invokeMethod(SEND_MESSAGE, {"message": message});
  }

  void sendFile(String filePath) async {
    _methodChannel.invokeMethod(SEND_FILE, {"filePath": filePath});
  }

  Future<bool> connectionStatus() async {
    return Future.value(
        (await _methodChannel.invokeMethod(CONNECTION_STATUS)) as bool);
  }

  Future<bool> disconnect() async {
    return Future.value(
        (await _methodChannel.invokeMethod(DISCONNECT)) as bool);
  }
}
