package com.flatig.bananaports.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.flatig.bananaports.R
import com.flatig.bananaports.logic.tools.StaticSingleData
import com.flatig.bananaports.logic.viewmodel.PortsViewModel

class PortsFragment : Fragment() {

    private lateinit var viewModel: PortsViewModel
    private lateinit var textBluetoothName: TextView
    private lateinit var textBluetoothAddress: TextView
    private lateinit var textWifiAddress: TextView
    private lateinit var textWifiPort: TextView
    private lateinit var textStateBluetooth: TextView
    private lateinit var textStateWifi: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ports, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        viewModel = ViewModelProvider(this)[PortsViewModel::class.java]
    }

    private fun initView(view: View) {
        textBluetoothName = view.findViewById(R.id.port_bluetooth_name)
        textBluetoothAddress = view.findViewById(R.id.port_bluetooth_address)
        textWifiAddress = view.findViewById(R.id.port_wifi_address)
        textWifiPort = view.findViewById(R.id.port_wifi_port)
        textStateBluetooth = view.findViewById(R.id.port_state_bluetooth)
        textStateWifi = view.findViewById(R.id.port_state_wifi)
    }

    private fun setViewData() {
        textBluetoothName.text = StaticSingleData.bluetoothDeviceName
        textBluetoothAddress.text = StaticSingleData.bluetoothDeviceAddress
        textWifiAddress.text = StaticSingleData.wifiIPAddress
        textWifiPort.text = StaticSingleData.wifiIPPort
    }


    override fun onResume() {
        super.onResume()
        setViewData()
    }
    override fun onPause() {
        super.onPause()
    }
}