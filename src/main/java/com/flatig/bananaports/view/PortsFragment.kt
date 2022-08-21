package com.flatig.bananaports.view

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.flatig.bananaports.R
import com.flatig.bananaports.logic.tools.StaticSingleData
import com.flatig.bananaports.logic.viewmodel.PortsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.NoRouteToHostException
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
    private lateinit var textWifiData: TextView
    private lateinit var textBluetoothData1: TextView
    private lateinit var textBluetoothData2: TextView
    private lateinit var textBluetoothMsgText: TextView
    private lateinit var buttonWifiSend: Button
    private lateinit var buttonBluetoothSend: Button
    private lateinit var buttonBluetoothStop: Button
    private lateinit var pickerNum1: NumberPicker
    private lateinit var pickerNum2: NumberPicker
    private lateinit var pickerNum3: NumberPicker
    private lateinit var seekBar1: SeekBar
    private lateinit var seekBar2: SeekBar

    private lateinit var updateStateData: UpdateStateData
    private val uuID = StaticSingleData.uuID

    private lateinit var bluetoothThread: BluetoothThread
    private var bluetoothSocket: BluetoothSocket? = null
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var wifiThread: WifiThread
    private var wifiSocket = Socket()
    private lateinit var outputStream: OutputStream
    private lateinit var outputBlueStream: OutputStream

    private var wifiMessage = "01:01:01"
    private var wifiMsg01 = "01"
    private var wifiMsg02 = "01"
    private var wifiMsg03 = "01"
    private var bluetoothMessage = "000:000"
    private var bluetoothMsg01 = "000"
    private var bluetoothMsg02 = "000"
    private val pickerValue: Array<String> =
        arrayOf("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10" )


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

        textWifiData = view.findViewById(R.id.port_text_wifi_data)
        textBluetoothData1 = view.findViewById(R.id.port_seektext1)
        textBluetoothData2 = view.findViewById(R.id.port_seektext2)
        textBluetoothMsgText = view.findViewById(R.id.port_bluetooth_msgtext)
        buttonWifiSend = view.findViewById(R.id.port_button_wifi_send)
        buttonBluetoothSend = view.findViewById(R.id.port_button_bluetooth_send)
        buttonBluetoothStop = view.findViewById(R.id.port_button_bluetooth_stop)
        pickerNum1 = view.findViewById(R.id.port_picker1)
        pickerNum2 = view.findViewById(R.id.port_picker2)
        pickerNum3 = view.findViewById(R.id.port_picker3)
        seekBar1 = view.findViewById(R.id.port_seekbar1)
        seekBar2 = view.findViewById(R.id.port_seekbar2)

    }

    private fun setViewData() {
        textBluetoothName.text = StaticSingleData.bluetoothDeviceName
        textBluetoothAddress.text = StaticSingleData.bluetoothDeviceAddress
        textWifiAddress.text = StaticSingleData.wifiIPAddress
        textWifiPort.text = StaticSingleData.wifiIPPort
        textStateBluetooth.text = StaticSingleData.NULL
        textStateWifi.text = StaticSingleData.NULL
        textBluetoothData1.text = bluetoothMsg01
        textBluetoothData2.text = bluetoothMsg02
        textBluetoothMsgText.text = bluetoothMessage
        textWifiData.text = wifiMessage

        pickerNum1.displayedValues = pickerValue
        pickerNum2.displayedValues = pickerValue
        pickerNum3.displayedValues = pickerValue
        pickerNum1.maxValue = pickerValue.size - 1
        pickerNum2.maxValue = pickerValue.size - 1
        pickerNum3.maxValue = pickerValue.size - 1
        pickerNum1.value = 1
        pickerNum2.value = 1
        pickerNum3.value = 1
        pickerNum1.wrapSelectorWheel = false
        pickerNum2.wrapSelectorWheel = false
        pickerNum3.wrapSelectorWheel = false
        pickerNum1.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        pickerNum2.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        pickerNum3.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        pickerNum1.setOnValueChangedListener { _, _, newVal ->
            wifiMsg01 = if (newVal.toString().length == 1) "0$newVal" else newVal.toString()
            wifiMessage = "$wifiMsg01:$wifiMsg02:$wifiMsg03"
            textWifiData.text = wifiMessage
        }
        pickerNum2.setOnValueChangedListener{ _, _, newVal ->
            wifiMsg02 = if (newVal.toString().length == 1) "0$newVal" else newVal.toString()
            wifiMessage = "$wifiMsg01:$wifiMsg02:$wifiMsg03"
            textWifiData.text = wifiMessage
        }
        pickerNum3.setOnValueChangedListener{ _, _, newVal ->
            wifiMsg03 = if (newVal.toString().length == 1) "0$newVal" else newVal.toString()
            wifiMessage = "$wifiMsg01:$wifiMsg02:$wifiMsg03"
            textWifiData.text = wifiMessage
        }

        seekBar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when((seekBar1.progress + 128).toString().length) {
                    1 -> bluetoothMsg01 = "00${seekBar1.progress + 128}"
                    2 -> bluetoothMsg01 = "0${seekBar1.progress + 128}"
                    3 -> bluetoothMsg01 = "${seekBar1.progress +128}"
                }
                bluetoothMessage = "$bluetoothMsg01:$bluetoothMsg02"
                textBluetoothData1.text = seekBar1.progress.toString()
                textBluetoothMsgText.text = bluetoothMessage
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if ((-20 < seekBar1.progress) and (seekBar1.progress < 20)) {
                    seekBar1.progress = 0
                }
                when((seekBar1.progress + 128).toString().length) {
                    1 -> bluetoothMsg01 = "00${seekBar1.progress + 128}"
                    2 -> bluetoothMsg01 = "0${seekBar1.progress + 128}"
                    3 -> bluetoothMsg01 = "${seekBar1.progress +128}"
                }
                bluetoothMessage = "$bluetoothMsg01:$bluetoothMsg02"
                textBluetoothData1.text = seekBar1.progress.toString()
                textBluetoothMsgText.text = bluetoothMessage
            }

        })
        seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when((seekBar2.progress + 128).toString().length) {
                    1 -> bluetoothMsg02 = "00${seekBar2.progress + 128}"
                    2 -> bluetoothMsg02 = "0${seekBar2.progress + 128}"
                    3 -> bluetoothMsg02 = "${seekBar2.progress +128}"
                }
                bluetoothMessage = "$bluetoothMsg01:$bluetoothMsg02"
                textBluetoothData2.text = seekBar2.progress.toString()
                textBluetoothMsgText.text = bluetoothMessage
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if ((-20 < seekBar2.progress) and (seekBar2.progress < 20)) {
                    seekBar2.progress = 0
                }
                when((seekBar2.progress + 128).toString().length) {
                    1 -> bluetoothMsg02 = "00${seekBar2.progress + 128}"
                    2 -> bluetoothMsg02 = "0${seekBar2.progress + 128}"
                    3 -> bluetoothMsg02 = "${seekBar2.progress + 128}"
                }
                bluetoothMessage = "$bluetoothMsg01:$bluetoothMsg02"
                textBluetoothData2.text = seekBar2.progress.toString()
                textBluetoothMsgText.text = bluetoothMessage
            }
        })

        buttonConnect.setOnClickListener {
            try {
                bluetoothThread.start()
                wifiThread.start()
            } catch (e: IllegalThreadStateException) {
             e.printStackTrace()
                Toast.makeText(requireActivity(), "Connecting", Toast.LENGTH_SHORT).show()
            }
        }
        buttonWifiSend.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Default) {
                sendMessage(wifiMessage)
            }
        }
        buttonBluetoothSend.setOnClickListener {  }
        buttonBluetoothStop.setOnClickListener {  }
    }


    override fun onResume() {
        super.onResume()
        setViewData()

        updateStateData = UpdateStateData()
        bluetoothThread = BluetoothThread()
        wifiThread = WifiThread()
        updateStateData.start()
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
                        if (bluetoothSocket != null) {
                            if (bluetoothSocket!!.isConnected) {
                                StaticSingleData.portBluetoothState = StaticSingleData.CONNECT
                            } else {
                                StaticSingleData.portBluetoothState = StaticSingleData.NULL
                            }
                        }
                        if (wifiSocket.isConnected) {
                            StaticSingleData.portWifiState = StaticSingleData.CONNECT
                        } else {
                            StaticSingleData.portWifiState = StaticSingleData.NULL
                        }


                        requireActivity().runOnUiThread {
                            textBluetoothName.text = StaticSingleData.bluetoothDeviceName
                            textBluetoothAddress.text = StaticSingleData.bluetoothDeviceAddress
                            textWifiAddress.text = StaticSingleData.wifiIPAddress
                            textWifiPort.text = StaticSingleData.wifiIPPort
                            textStateBluetooth.text = StaticSingleData.portBluetoothState
                            textStateWifi.text = StaticSingleData.portWifiState
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
            try {
                bluetoothDevice = bluetoothAdapter.getRemoteDevice(StaticSingleData.bluetoothDeviceAddress)
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuID)
                bluetoothSocket?.connect()
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: IllegalThreadStateException) {
                e.printStackTrace()
                Looper.prepare()
                Toast.makeText(requireActivity(), "Connecting", Toast.LENGTH_SHORT).show()
                Looper.loop()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                Looper.prepare()
                Toast.makeText(requireActivity(), "Check Value", Toast.LENGTH_SHORT).show()
                Looper.loop()
            }
        }
    }
    inner class WifiThread: Thread() {
        override fun run() {
            super.run()
            try {
                wifiSocket.connect(InetSocketAddress(StaticSingleData.wifiIPAddress, StaticSingleData.wifiIPPort.toInt()))
            } catch (e: ConnectException) {
                e.printStackTrace()
                Looper.prepare()
                Toast.makeText(requireActivity(), "Connect Failed", Toast.LENGTH_SHORT).show()
                Looper.loop()
            } catch (e: IllegalThreadStateException) {
                e.printStackTrace()
                Looper.prepare()
                Toast.makeText(requireActivity(), "Connecting!!! Please Wait", Toast.LENGTH_SHORT).show()
                Looper.loop()
            } catch (e: NoRouteToHostException) {
                e.printStackTrace()
                Looper.prepare()
                Toast.makeText(requireActivity(), "Wifi OFF!!!", Toast.LENGTH_SHORT).show()
                Looper.loop()
            }
        }
    }
    inner class BluetoothSendThread: Thread() {
        override fun run() {
            super.run()
            try {
                while (!isInterrupted) {
                    try {
                        sendBlueMessage(bluetoothMessage)
                        Thread.sleep(75)
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

    private fun sendMessage(content: String) {
        if (wifiSocket.isConnected) {
            try {
                outputStream = wifiSocket.getOutputStream()
                outputStream.write(content.toByteArray(charset("US-ASCII")))
                outputStream.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun sendBlueMessage(content: String) {
        if (bluetoothSocket?.isConnected == true) {
            try {
                outputBlueStream = bluetoothSocket!!.outputStream
                outputBlueStream.write(content.toByteArray(charset("US-ASCII")))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(requireActivity(), "未连接成功", Toast.LENGTH_SHORT).show()
        }
    }
}