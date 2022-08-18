package com.flatig.bananaports.logic.tools

//To save data in the whole applications
object StaticSingleData {

    const val ON = "ON"
    const val OFF = "OFF"
    const val NULL = "Null"

    var bluetoothDeviceName: String = "Null"
    var bluetoothDeviceAddress: String = "00-00-00-00"
    var wifiIPAddress: String = "192.168.4.1"
    var wifiIPPort: String = "8081"

}