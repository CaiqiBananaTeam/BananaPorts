package com.flatig.bananaports.logic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.flatig.bananaports.logic.tools.BluetoothDeviceInfo
import androidx.lifecycle.LiveData

class FragmentViewModel : ViewModel() {
    private var deviceInfo: MutableLiveData<BluetoothDeviceInfo>? = null
    val info: LiveData<BluetoothDeviceInfo>
        get() {
            if (deviceInfo == null) {
                deviceInfo = MutableLiveData()
            }
            return deviceInfo!!
        }

    override fun onCleared() {
        super.onCleared()
        deviceInfo = null
    }
}