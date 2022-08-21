package com.flatig.bananaports.view

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.flatig.bananaports.R
import com.flatig.bananaports.logic.tools.StaticSingleData
import com.flatig.bananaports.logic.viewmodel.PortsViewModel
import java.lang.Exception
import java.net.Socket
import java.util.*

class PortsFragment : Fragment() {

    private lateinit var textBluetoothName: TextView
    private lateinit var textBluetoothAddress: TextView
    private lateinit var textWifiAddress: TextView
    private lateinit var textWifiPort: TextView
    private lateinit var textStateBluetooth: TextView
    private lateinit var textStateWifi: TextView
    private lateinit var buttonConnect: Button

    private lateinit var updateStateData: UpdateStateData
    private val uuID = StaticSingleData.uuID

    private lateinit var bluetoothThread: BluetoothThread
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var wifiThread: WifiThread
    private lateinit var wifiSocket: Socket


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ports, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        textBluetoothName = view.findViewById(R.id.port_bluetooth_name)
        textBluetoothAddress = view.findViewById(R.id.port_bluetooth_address)
        textWifiAddress = view.findViewById(R.id.port_wifi_address)
        textWifiPort = view.findViewById(R.id.port_wifi_port)
        textStateBluetooth = view.findViewById(R.id.port_state_bluetooth)
        textStateWifi = view.findViewById(R.id.port_state_wifi)
        buttonConnect = view.findViewById(R.id.port_button_connect)
    }

    private fun setViewData() {
        textBluetoothName.text = StaticSingleData.bluetoothDeviceName
        textBluetoothAddress.text = StaticSingleData.bluetoothDeviceAddress
        textWifiAddress.text = StaticSingleData.wifiIPAddress
        textWifiPort.text = StaticSingleData.wifiIPPort
        textStateBluetooth.text = StaticSingleData.NULL
        textStateWifi.text = StaticSingleData.NULL

        buttonConnect.setOnClickListener {
            bluetoothThread.start()
            wifiThread.start()
        }
    }


    override fun onResume() {
        super.onResume()
        setViewData()
        updateStateData = UpdateStateData()
        updateStateData.start()
        bluetoothThread = BluetoothThread()
        wifiThread = WifiThread()
    }
    override fun onPause() {
        super.onPause()
        updateStateData.interrupt()
    }

    inner class UpdateStateData: Thread() {
        override fun run() {
            super.run()
            try {
                while (!isInterrupted) {
                    try {

                        requireActivity().runOnUiThread {
                            textBluetoothName.text = StaticSingleData.bluetoothDeviceName
                            textBluetoothAddress.text = StaticSingleData.bluetoothDeviceAddress
                            textWifiAddress.text = StaticSingleData.wifiIPAddress
                            textWifiPort.text = StaticSingleData.wifiIPPort
                        }

                        Thread.sleep(200)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                        break
                    }
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
    inner class BluetoothThread: Thread()  {
        @SuppressLint("MissingPermission")
        override fun run() {
            super.run()
            bluetoothManager = requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(StaticSingleData.bluetoothDeviceAddress)
            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuID)
                bluetoothSocket.connect()
                if (bluetoothSocket.isConnected) {

                }
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: IllegalThreadStateException) {
                e.printStackTrace()
            }
        }
    }
    inner class WifiThread: Thread() {
        override fun run() {
            super.run()
        }
    }


}