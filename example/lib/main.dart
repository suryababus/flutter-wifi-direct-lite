import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:flutter_wifi_direct_lite/WifiDirect.dart';
import 'package:flutter_wifi_direct_lite/flutter_wifi_direct_lite.dart';
import 'package:flutter_wifi_direct_lite_example/WifiDirectHandler.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(ChangeNotifierProvider(
    create: (context) => WifiDirectHandler(),
    child: MyApp(),
  ));
}

// ignore: must_be_immutable
class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
  bool connected = false;
  List<Device> devices = [];
}

class _MyAppState extends State<MyApp> {
  List<String> messages;
  @override
  void initState() {
    super.initState();
    var wifiDirectHandler =
        Provider.of<WifiDirectHandler>(context, listen: false);
    FlutterWifiDirectLite.startDiscovery(wifiDirectHandler)
        .then((value) => print(value));
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        home: Scaffold(
            appBar: AppBar(
              title: Text('Plugin example app'),
            ),
            body: Consumer<WifiDirectHandler>(
              builder: (context, _wifiDirectHandler, child) {
                return Column(children: [
                  Text(
                      'ConnectionStatus on: ${_wifiDirectHandler.connected}\n'),
                  _wifiDirectHandler.connected
                      ? SendAndReceiveMessage(_wifiDirectHandler)
                      : ListDevices(_wifiDirectHandler)
                ]);
              },
            )));
  }
}

// ignore: must_be_immutable
class SendAndReceiveMessage extends StatefulWidget {
  WifiDirectHandler _wifiDirectHandler;

  SendAndReceiveMessage(WifiDirectHandler wifiDirectHandler) {
    _wifiDirectHandler = wifiDirectHandler;
  }

  @override
  _SendAndReceiveMessageState createState() => _SendAndReceiveMessageState();
}

class _SendAndReceiveMessageState extends State<SendAndReceiveMessage> {
  List<String> messages = [];
  final _picker = ImagePicker();
  var _textEditingController = new TextEditingController();

  @override
  void initState() {
    super.initState();
    messages.add("messages");
    var callBack =
        widget._wifiDirectHandler.onMessageReceivedStream.listen((message) {
      print(message);
      setState(() {
        messages.add(message);
      });
    });
  }

  void sendFile() async {
    PickedFile image = await _picker.getImage(source: ImageSource.gallery);
    widget._wifiDirectHandler.sendFile(image.path);
  }

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Column(
        children: [
          Column(
            children: [
              TextField(
                controller: _textEditingController,
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  GestureDetector(
                    onTap: () => widget._wifiDirectHandler
                        .sendMessage(_textEditingController.text),
                    child: Container(
                      margin: EdgeInsets.all(8.0),
                      color: Colors.lightGreen,
                      padding: EdgeInsets.all(8.0),
                      child: Text('Send'),
                    ),
                  ),
                  GestureDetector(
                    onTap: widget._wifiDirectHandler.disconnect,
                    child: Container(
                      margin: EdgeInsets.all(8.0),
                      color: Colors.red,
                      padding: EdgeInsets.all(8.0),
                      child: Text("Disconnect"),
                    ),
                  ),
                  GestureDetector(
                    onTap: sendFile,
                    child: Container(
                      margin: EdgeInsets.all(8.0),
                      color: Colors.blue,
                      padding: EdgeInsets.all(8.0),
                      child: Text("Send file"),
                    ),
                  ),
                ],
              ),
            ],
          ),
          Expanded(
            flex: 1,
            child: ListView.builder(
              itemBuilder: (context, index) {
                return Center(
                  child: Text(messages[index]),
                );
              },
              itemCount: messages.length,
            ),
          )
        ],
      ),
    );
  }
}

// ignore: must_be_immutable
class ListDevices extends StatelessWidget {
  WifiDirectHandler _wifiDirectHandler;
  ListDevices(WifiDirectHandler wifiDirectHandler) {
    _wifiDirectHandler = wifiDirectHandler;
  }

  @override
  Widget build(BuildContext context) {
    return Expanded(
        flex: 1,
        child: ListView.builder(
          itemBuilder: (context, index) {
            return GestureDetector(
              onTap: () => _wifiDirectHandler
                  .connectToDevice(_wifiDirectHandler.devices[index]),
              child: Container(
                height: 50,
                color: Colors.blueGrey,
                alignment: Alignment.center,
                child: Text(_wifiDirectHandler.devices[index].name),
              ),
            );
          },
          itemCount: _wifiDirectHandler.devices.length,
        ));
  }
}
