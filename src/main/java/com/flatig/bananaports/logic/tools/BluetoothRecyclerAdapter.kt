package com.flatig.bananaports.logic.tools

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatig.bananaports.R
import com.flatig.bananaports.view.BluetoothConnectionActivity

class BluetoothRecyclerAdapter(private val deviceList: List<BluetoothDeviceInfo>):
    RecyclerView.Adapter<BluetoothRecyclerAdapter.ViewHolder>(){

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val devices: TextView = view.findViewById(R.id.recycler_bluetooth_text_devices)
        val address: TextView = view.findViewById(R.id.recycler_bluetooth_text_address)
        val save: Button = view.findViewById(R.id.recycler_bluetooth_button_save)
        val enter: Button = view.findViewById(R.id.recycler_bluetooth_button_enter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_bluetooth, parent, false)
        val viewHolder = ViewHolder(view)

        viewHolder.itemView.setOnClickListener {
            val deviceInfo = deviceList[viewHolder.adapterPosition]
            val intent = Intent(parent.context, BluetoothConnectionActivity::class.java)
            StaticSingleData.bluetoothDeviceName = deviceInfo.deviceName.toString()
            StaticSingleData.bluetoothDeviceAddress = deviceInfo.deviceAddress.toString()
            parent.context.startActivity(intent)
        }
        viewHolder.save.setOnClickListener {
            val deviceInfo = deviceList[viewHolder.adapterPosition]
            StaticSingleData.bluetoothDeviceName = deviceInfo.deviceName.toString()
            StaticSingleData.bluetoothDeviceAddress = deviceInfo.deviceAddress.toString()
        }
        viewHolder.enter.setOnClickListener {
            val deviceInfo = deviceList[viewHolder.adapterPosition]
            val intent = Intent(parent.context, BluetoothConnectionActivity::class.java)
            StaticSingleData.bluetoothDeviceName = deviceInfo.deviceName.toString()
            StaticSingleData.bluetoothDeviceAddress = deviceInfo.deviceAddress.toString()
            parent.context.startActivity(intent)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bluetoothDeviceInfo = deviceList[position]
        holder.devices.text = bluetoothDeviceInfo.deviceName
        holder.address.text = bluetoothDeviceInfo.deviceAddress
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

}