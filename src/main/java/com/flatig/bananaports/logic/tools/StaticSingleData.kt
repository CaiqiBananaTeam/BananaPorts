package com.flatig.bananaports.logic.tools

import java.util.*

//To save data in the whole applications
object StaticSingleData {

    const val CONNECT = "Connected!"
    const val NULL = "Null"
    val uuID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    var bluetoothDeviceName: String = "Null"
    var bluetoothDeviceAddress: String = "00:00:00:00"
    var wifiIPAddress: String = "192.168.4.1"
    var wifiIPPort: String = "8081"

    var portBluetoothState: String = "Null"
    var portWifiState: String = "Null"

}