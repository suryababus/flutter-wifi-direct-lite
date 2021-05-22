import 'dart:typed_data';

import 'package:flutter/services.dart';
import 'package:flutter_wifi_direct_lite/WifiDirect.dart';

enum MessageCode { PEERS_CHANGED, MESSAGE_RECEIVED, CONNECTION_CHANGED }

extension StringToMessageCode on MessageCode {
  static var map = {
    "peersChanged": MessageCode.PEERS_CHANGED,
    "messageReceived": MessageCode.MESSAGE_RECEIVED,
    "connectionChanged": MessageCode.CONNECTION_CHANGED
  };
}

class AppMethodCallHandler {
  WifiDirect _wifiDirect;
  void setWifiDirect(WifiDirect wifiDirect) {
    this._wifiDirect = wifiDirect;
  }

  Future<dynamic> callHandler(MethodCall call) {
    print("Called");
    switch (StringToMessageCode.map[call.method]) {
      case MessageCode.PEERS_CHANGED:
        {
          // print(call.arguments<List<dynamic>>());
          var _ = call.arguments as List<dynamic>;
          List<Device> devices = [];
          _.forEach((element) {
            devices
                .add(Device(element["deviceName"], element["deviceAddress"]));
          });
          if (_wifiDirect != null) {
            _wifiDirect.onPeerChanged(devices);
          }
        }
        break;
      case MessageCode.MESSAGE_RECEIVED:
        {
          var message = call.arguments as String;

          if (_wifiDirect != null) {
            _wifiDirect.onMessageReceived(message);
          }
        }
        break;
      case MessageCode.CONNECTION_CHANGED:
        {
          var connectionStatusString = call.arguments as String;
          if (_wifiDirect != null) {
            _wifiDirect.onConnectionChanged(
                StringToConnectionStatus.map[connectionStatusString]);
          }
        }
        break;
    }
    return null;
  }
}
