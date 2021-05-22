import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/cupertino.dart';
import 'package:flutter_wifi_direct_lite/WifiDirect.dart';
import 'package:flutter_wifi_direct_lite/flutter_wifi_direct_lite.dart';

class WifiDirectHandler extends ChangeNotifier implements WifiDirect {
  List<Device> devices = [];
  bool connected = false;
  Device connectedDevice;
  Device _attemptConnectToDevice;

  // ignore: close_sinks
  var _onMessageReceivedStreamController = StreamController<String>();

  WifiDirectHandler() {
    FlutterWifiDirectLite.connectionStatus().then((value) {
      connected = value;
    });
  }

  Stream<String> get onMessageReceivedStream =>
      _onMessageReceivedStreamController.stream.asBroadcastStream();
  @override
  void onConnectionChanged(ConnectionStatus status) {
    if (status == ConnectionStatus.CONNECTED) {
      connected = true;
      connectedDevice = _attemptConnectToDevice;
    } else {
      connected = false;
      connectedDevice = null;
    }
    notifyListeners();
  }

  @override
  void onMessageReceived(String message) {
    print(message);
    _onMessageReceivedStreamController.sink.add(message);
  }

  @override
  void onPeerChanged(List<Device> devices) {
    print(devices);
    this.devices = devices;
    notifyListeners();
  }

  void connectToDevice(Device device) {
    _attemptConnectToDevice = device;
    FlutterWifiDirectLite.connectionStatus().then((value) {
      if (!value) {
        FlutterWifiDirectLite.connectToDevice(device);
      } else {
        FlutterWifiDirectLite.disconnect().then((value) {
          FlutterWifiDirectLite.connectToDevice(device);
        });
      }
    });
  }

  void sendMessage(String message) {
    FlutterWifiDirectLite.sendMessage(message);
  }

  void sendFile(String filePath) {
    FlutterWifiDirectLite.sendFile(filePath);
  }

  void disconnect() async {
    FlutterWifiDirectLite.disconnect().then((value) {
      connected = false;
      connectedDevice = null;
      notifyListeners();
    });
  }
}
