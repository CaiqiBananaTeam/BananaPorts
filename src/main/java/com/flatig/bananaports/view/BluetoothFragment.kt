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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.flatig.bananaports.MainActivity
import com.flatig.bananaports.R
import com.flatig.bananaports.logic.tools.BluetoothArrayAdapter
import com.flatig.bananaports.logic.tools.BluetoothDeviceInfo
import com.flatig.bananaports.logic.tools.StaticSingleData
import com.flatig.bananaports.logic.viewmodel.BluetoothViewModel
import kotlinx.coroutines.*

class BluetoothFragment: Fragment() {
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var bluetoothStateReceiver: BroadcastReceiver
    private val deviceList: MutableList<BluetoothDeviceInfo> = ArrayList()

    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(coroutineJob)
    private lateinit var bluetoothViewModel: BluetoothViewModel
    private lateinit var bluetoothLiveData: MutableLiveData<String>

    private lateinit var textViewIsOn: TextView
    private lateinit var textViewIsDisc: TextView
    private lateinit var buttonSwitch: Button
    private lateinit var buttonDisc: Button
    private lateinit var listView: ListView
    private lateinit var listViewAdapter: BluetoothArrayAdapter
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

    //Override the FragmentLifeCycle : New in fragment:1.3.0-alpha02
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        setViewData()
        broadcastRegister()
    }

    private fun initView(view: View) {
        textViewIsOn = view.findViewById(R.id.fragment_home_bluetooth_isOn)
        textViewIsDisc = view.findViewById(R.id.fragment_home_bluetooth_isDiscovering)
        buttonSwitch = view.findViewById(R.id.fragment_home_bluetooth_switch)
        buttonDisc = view.findViewById(R.id.fragment_home_bluetooth_discover)
        listView = view.findViewById(R.id.home_listview)
        listViewAdapter = BluetoothArrayAdapter(deviceList, requireActivity())
        listView.adapter = listViewAdapter

        bluetoothManager = requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        textViewIsOn.text = STATE_OFF
        textViewIsDisc.text = NONSEARCH

        bluetoothViewModel = ViewModelProvider(this)[BluetoothViewModel::class.java]
        bluetoothLiveData = bluetoothViewModel.bluetoothDeviceName
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
        listView.setOnItemClickListener { _, _, position, _ ->
            val deviceInfo = deviceList[position]
            val intent = Intent(requireActivity(), BluetoothConnectionActivity::class.java)
            StaticSingleData.bluetoothDeviceName = deviceInfo.deviceName.toString()
            StaticSingleData.bluetoothDeviceAddress = deviceInfo.deviceAddress.toString()
            intent.putExtra("device", deviceInfo.deviceName)
            intent.putExtra("address",deviceInfo.deviceAddress)
            startActivity(intent)
        }

        bluetoothViewModel.onState.observe(viewLifecycleOwner, Observer { state ->

        })
        bluetoothViewModel.onSearch.observe(viewLifecycleOwner, Observer { search ->

        })
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
            @SuppressLint("MissingPermission")
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
                        listViewAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        val filterState = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        requireActivity().registerReceiver(bluetoothStateReceiver,filterState)
        val filterDevice = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireActivity().registerReceiver(broadcastReceiver, filterDevice)
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        //SwitchCoroutines
        coroutineScope.launch(Dispatchers.Main) {
            try {
                while (true) {
                    STATE = if (bluetoothAdapter.isEnabled) "ON" else "OFF"
                    when (STATE) {
                        STATE_ON -> {
                            textViewIsOn.text = STATE_ON
                            textViewIsOn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.teal_200))
                            buttonSwitch.text = resources.getString(R.string.home_button_switch_off)
                        }
                        STATE_OFF -> {
                            textViewIsOn.text = STATE_OFF
                            textViewIsOn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue_dai))
                            buttonSwitch.text = resources.getString(R.string.home_button_switch_on)
                        }
                    }
                    delay(200)
                }
            } catch (e: Exception) {
                coroutineJob.cancel()
                e.printStackTrace()
            }
        }
        //SearchCoroutines
        coroutineScope.launch(Dispatchers.Main) {
            try {
                while (true) {
                    STATE_DISC = if (bluetoothAdapter.isDiscovering) "ON" else "OFF"
                    when (STATE_DISC) {
                        STATE_ON -> {
                            textViewIsDisc.text = SEARCHING
                            textViewIsDisc.setTextColor(ContextCompat.getColor(requireActivity(), R.color.teal_200))
                            buttonDisc.text = resources.getString(R.string.home_button_discover_ing)
                        }
                        STATE_OFF -> {
                            textViewIsDisc.text = NONSEARCH
                            textViewIsDisc.setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue_dai))
                            buttonDisc.text = resources.getString(R.string.home_button_discover)
                        }
                    }
                    delay(500)
                }
            } catch (e: Exception) {
                coroutineJob.cancel()
                e.printStackTrace()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        coroutineJob.cancel()
    }
}

