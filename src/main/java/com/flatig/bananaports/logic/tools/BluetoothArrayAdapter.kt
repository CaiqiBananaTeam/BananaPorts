package com.flatig.bananaports.logic.tools

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.flatig.bananaports.R

class BluetoothArrayAdapter(private val device: List<BluetoothDeviceInfo>, private val context: Context):
    BaseAdapter() {

    override fun getCount(): Int {
        return device.size
    }

    override fun getItem(p0: Int): Any {
        return device[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(int: Int,view: View? , viewGroup: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewHere: View = view ?: inflater.inflate(R.layout.listview_bluetooth, viewGroup, false)
        val devices: TextView = viewHere.findViewById(R.id.listview_bluetooth_text_devices)
        val address: TextView = viewHere.findViewById(R.id.listview_bluetooth_text_address)
        val bluetoothDeviceInfo: BluetoothDeviceInfo = device[int]
        devices.text = bluetoothDeviceInfo.deviceName
        address.text = bluetoothDeviceInfo.deviceAddress
        return viewHere
    }
}