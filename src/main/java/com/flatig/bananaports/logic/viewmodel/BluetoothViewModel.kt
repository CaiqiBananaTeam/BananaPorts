package com.flatig.bananaports.logic.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BluetoothViewModel: ViewModel() {

    var bluetoothDeviceName = MutableLiveData<String>()
    val bluetoothDeviceAddress = MutableLiveData<String>()

    val onState = MutableLiveData<String>()
    val onSearch = MutableLiveData<String>()

    fun setName(string: String) {
        bluetoothDeviceAddress.value = string
    }
    fun getMessage(): String? {
        return bluetoothDeviceAddress.value
    }

}