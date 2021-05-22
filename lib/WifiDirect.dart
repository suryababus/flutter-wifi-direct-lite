import 'dart:typed_data';

abstract class WifiDirect {
  // ignore: non_constant_identifier_names
  void onPeerChanged(List<Device> devices) {}
  void onConnectionChanged(ConnectionStatus status) {}
  void onMessageReceived(String message) {}
}

class Device {
  String name;
  String address;
  Device(String deviceName, String deviceAddress) {
    name = deviceName;
    address = deviceAddress;
  }
}

enum ConnectionStatus { CONNECTED, DISCONNECTED }

extension StringToConnectionStatus on ConnectionStatus {
  static var map = {
    "CONNECTED": ConnectionStatus.CONNECTED,
    "DISCONNECTED": ConnectionStatus.DISCONNECTED
  };
}
