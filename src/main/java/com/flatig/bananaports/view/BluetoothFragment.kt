package com.flatig.bananaports.view

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flatig.bananaports.R
import com.flatig.bananaports.logic.tools.BluetoothDeviceInfo
import com.flatig.bananaports.logic.tools.BluetoothRecyclerAdapter
import kotlinx.coroutines.*

class BluetoothFragment: Fragment() {

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var bluetoothStateReceiver: BroadcastReceiver
    private val deviceList: MutableList<BluetoothDeviceInfo> = ArrayList()

    private lateinit var bluetoothThreads: BluetoothThreads

    private lateinit var textViewIsOn: TextView
    private lateinit var textViewIsDisc: TextView
    private lateinit var buttonSwitch: Button
    private lateinit var buttonDisc: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: BluetoothRecyclerAdapter
    private val STATE_ON: String = "ON"
    private val STATE_OFF: String = "OFF"
    private val SEARCHING: String = "Searching"
    private val NONSEARCH: String = "Null"
    private var STATE: String = STATE_OFF
    private var STATE_DISC: String = NONSEARCH

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_bluetooth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        setViewData()
        broadcastRegister()
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        bluetoothThreads = BluetoothThreads()
        bluetoothThreads.start()

    }
    override fun onPause() {
        super.onPause()
        bluetoothThreads.interrupt()
    }

    private fun initView(view: View) {
        textViewIsOn = view.findViewById(R.id.fragment_home_bluetooth_isOn)
        textViewIsDisc = view.findViewById(R.id.fragment_home_bluetooth_isDiscovering)
        buttonSwitch = view.findViewById(R.id.fragment_home_bluetooth_switch)
        buttonDisc = view.findViewById(R.id.fragment_home_bluetooth_discover)

        recyclerView = view.findViewById(R.id.bluetooth_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerViewAdapter = BluetoothRecyclerAdapter(deviceList)
        recyclerView.adapter = recyclerViewAdapter

        bluetoothManager = requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        textViewIsOn.text = STATE_OFF
        textViewIsDisc.text = NONSEARCH
    }

    @SuppressLint("MissingPermission")
    private fun setViewData() {

        buttonSwitch.setOnClickListener {
            when (STATE) {
                STATE_ON -> bluetoothAdapter.disable()

                STATE_OFF -> bluetoothAdapter.enable()
            }
        }

        buttonDisc.setOnClickListener {
            when (STATE_DISC) {
                STATE_ON -> {
                    bluetoothAdapter.cancelDiscovery()
                }
                STATE_OFF -> {
                    bluetoothAdapter.startDiscovery()
                }
            }

        }

    }

    private fun broadcastRegister() {
        bluetoothStateReceiver = object :BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                when (p1?.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                    BluetoothAdapter.STATE_ON -> STATE = STATE_ON
                    BluetoothAdapter.STATE_OFF -> STATE = STATE_OFF
                }
            }
        }
        broadcastReceiver = object : BroadcastReceiver() {
            @SuppressLint("MissingPermission", "NotifyDataSetChanged")
            override fun onReceive(p0: Context?, p1: Intent?) {
                val action = p1?.action
                if (BluetoothDevice.ACTION_FOUND == action) {
                    var isAdded = false
                    val device =
                        p1.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val deviceInfo =
                        BluetoothDeviceInfo(device?.name, device?.address)
                    for (devices in deviceList) {
                        if (devices.deviceAddress == deviceInfo.deviceAddress) {
                            isAdded = true
                            break
                        }
                    }
                    if (!isAdded) {
                        deviceList.add(deviceInfo)
                        recyclerViewAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        val filterState = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        requireActivity().registerReceiver(bluetoothStateReceiver,filterState)
        val filterDevice = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireActivity().registerReceiver(broadcastReceiver, filterDevice)
    }

    inner class BluetoothThreads: Thread() {
        @SuppressLint("MissingPermission")
        override fun run() {
            super.run()
            try {
                while (!isInterrupted) {
                    STATE = if (bluetoothAdapter.isEnabled) "ON" else "OFF"
                    STATE_DISC = if (bluetoothAdapter.isDiscovering) "ON" else "OFF"
                    try {
                        when (STATE) {
                            STATE_ON -> {
                                requireActivity().runOnUiThread {
                                    textViewIsOn.text = STATE_ON
                                    textViewIsOn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.teal_200))
                                    buttonSwitch.text = resources.getString(R.string.home_button_switch_off)
                                }
                            }
                            STATE_OFF -> {
                                requireActivity().runOnUiThread {
                                    textViewIsOn.text = STATE_OFF
                                    textViewIsOn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue_dai))
                                    buttonSwitch.text = resources.getString(R.string.home_button_switch_on)
                                }
                            }
                        }

                        when (STATE_DISC) {
                            STATE_ON -> {
                                requireActivity().runOnUiThread {
                                    textViewIsDisc.text = SEARCHING
                                    textViewIsDisc.setTextColor(ContextCompat.getColor(requireActivity(), R.color.teal_200))
                                    buttonDisc.text = resources.getString(R.string.home_button_discover_ing)
                                }
                            }
                            STATE_OFF -> {
                                requireActivity().runOnUiThread {
                                    textViewIsDisc.text = NONSEARCH
                                    textViewIsDisc.setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue_dai))
                                    buttonDisc.text = resources.getString(R.string.home_button_discover)
                                }
                            }
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

}