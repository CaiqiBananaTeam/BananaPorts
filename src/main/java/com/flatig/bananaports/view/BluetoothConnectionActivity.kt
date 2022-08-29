package com.flatig.bananaports.view

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.flatig.bananaports.R
import com.flatig.bananaports.logic.tools.StaticSingleData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.*

class BluetoothConnectionActivity : AppCompatActivity() {
    private lateinit var deviceName: String
    private lateinit var deviceAddress: String
    private var numInt01: Int = 0
    private var numInt02: Int = 0
    private var message: String = ""
    private val uuID = StaticSingleData.uuID
    private var sendDataThread: SendDataThread? = null
    private var receiveDataThread: ReceiveDataThread? = null

    private lateinit var textViewAddress: TextView
    private lateinit var textViewDevice: TextView
    private lateinit var textView01: TextView
    private lateinit var textView02: TextView
    private lateinit var seekBar1: SeekBar
    private lateinit var seekBar2: SeekBar
    private lateinit var textViewContent: EditText

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var device: BluetoothDevice
    private lateinit var bluetoothSocket: BluetoothSocket
    companion object {
        private var outputStream: OutputStream? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_connection)
        initView()
        connectDevices()
    }

    // Initial the view to make code clean
    private fun initView() {

        deviceName = StaticSingleData.bluetoothDeviceName
        deviceAddress = StaticSingleData.bluetoothDeviceAddress

        textViewContent = findViewById(R.id.connection_text_receive)
        textViewDevice = findViewById(R.id.connection_text_devices)
        textViewAddress = findViewById(R.id.connection_text_address)
        textViewDevice.text = deviceName
        textViewAddress.text = deviceAddress
        textView01 = findViewById(R.id.textView01)
        textView02 = findViewById(R.id.textView02)
        textView01.text = numInt01.toString()
        textView02.text = numInt01.toString()
        seekBar1 = findViewById(R.id.seekBar1)
        seekBar2 = findViewById(R.id.seekBar2)


        seekBar1.setOnSeekBarChangeListener(object :
        SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                numInt01 = seekBar1.progress
                textView01.text = numInt01.toString()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                if ((-32 < seekBar1.progress) and (seekBar1.progress < 32)) {
                    seekBar1.progress = 0
                }
                numInt01 = seekBar1.progress
                textView01.text = numInt01.toString()
            }

        })
        seekBar2.setOnSeekBarChangeListener(object :
        SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                numInt02 = seekBar2.progress
                textView02.text = numInt02.toString()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                if ((-40 < seekBar2.progress) and (seekBar2.progress < 40)) {
                    seekBar2.progress = 0
                }
                numInt02 = seekBar2.progress
                textView02.text = numInt02.toString()
            }

        })

    }


    override fun onDestroy() {
        super.onDestroy()
        sendDataThread?.interrupt()
        receiveDataThread?.interrupt()
    }

    @SuppressLint("MissingPermission")
    private fun connectDevices() {
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        device = bluetoothAdapter.getRemoteDevice(deviceAddress)
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuID)
            bluetoothSocket.connect()
            if (bluetoothSocket.isConnected) {
                Toast.makeText(this, "connected", Toast.LENGTH_LONG).show()
                sendDataThread = SendDataThread()
                receiveDataThread = ReceiveDataThread()
                sendDataThread!!.start()
                receiveDataThread!!.start()

            } else {
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    private fun sendMessage(content: String) {
        if (bluetoothSocket.isConnected) {
            try {
                outputStream = bluetoothSocket.outputStream
                if (outputStream != null) {
                    outputStream!!.write(content.toByteArray(charset("US-ASCII")))
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "未连接成功", Toast.LENGTH_SHORT).show()
        }
    }

    inner class SendDataThread: Thread() {
        override fun run() {
            super.run()
            var stringT01 = ""
            var stringT02 = ""
            val intNum = 128
            try {
                while (!isInterrupted) {
                    val intNum01 = numInt01 + intNum
                    val intNum02 = numInt02 + intNum
                    when (intNum01.toString().length) {
                        1 -> stringT01 = "00$intNum01"
                        2 -> stringT01 = "0$intNum01"
                        3 -> stringT01 = "$intNum01"
                    }
                    when (intNum02.toString().length) {
                        1 -> stringT02 = "00$intNum02"
                        2 -> stringT02 = "0$intNum02"
                        3 -> stringT02 = "$intNum02"
                    }
                    try {
                        message = "$stringT01:$stringT02"
                        sendMessage(message)
                        Thread.sleep(50)
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
    inner class ReceiveDataThread: Thread() {
        private var inputStream = bluetoothSocket.inputStream
        override fun run() {
            super.run()
            try {
                while (!isInterrupted) {
                    try {
                        val buffer = ByteArray(128)
                        inputStream!!.read(buffer)
                        val a = String(buffer, 0, buffer.size, charset("US-ASCII"))
                        runOnUiThread { textViewContent.append(a) }
                        Thread.sleep(1000)
                        runOnUiThread { textViewContent.setText("") }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                        break
                    }
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }

        init {
            try {
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
