package com.flatig.bananaports.logic.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.flatig.bananaports.R

class WifiArrayAdapter(private val device: List<WifiDeviceInfo>, val context: Context):
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

    override fun getView(int: Int, view: View, viewGroup: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewHere: View = view ?: inflater.inflate(R.layout.listview_wifi, viewGroup, false)
        val deviceName: TextView = view.findViewById(R.id.listview_wifi_text_devices)
        val wifiDeviceInfo: WifiDeviceInfo = device[int]
        deviceName.text = wifiDeviceInfo.deviceName
        return viewHere
    }
}