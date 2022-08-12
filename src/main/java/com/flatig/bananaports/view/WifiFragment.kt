package com.flatig.bananaports.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.flatig.bananaports.R
import com.flatig.bananaports.logic.model.IsStringIPv4
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket

class WifiFragment: Fragment() {
    private lateinit var textViewState: TextView
    private lateinit var editTextIPAddress: EditText
    private lateinit var editTextPort: EditText
    private lateinit var buttonSubmit: Button
    private lateinit var textViewInstantIP: TextView
    private lateinit var textViewInstantPort: TextView
    private lateinit var textConnectStatue: TextView
    private lateinit var buttonConnectDevice: Button
    private lateinit var wifiEditData01: EditText
    private lateinit var wifiEditData02: EditText
    private lateinit var wifiEditData03: EditText
    private lateinit var wifiTextDataView: TextView
    private lateinit var wifiButtonDataSave: Button
    private lateinit var wifiButtonDataSend: Button

    private var socket = Socket()
    private lateinit var wifiManager: WifiManager
    private lateinit var outputStream: OutputStream
    private lateinit var wifiSendDataThread: WifiSendDataThread
    private lateinit var wifiConnectThread: WifiConnect
    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(coroutineJob)

    private val defaultIP = "192.168.4.1"
    private val defaultPort = 8081
    private var figureIP: String = defaultIP
    private var figurePort: Int = defaultPort
    private var wifiState: Int = WifiManager.WIFI_STATE_DISABLED
    private val stateOn = "Wifi ON"
    private val stateOff = "Wifi OFF"
    private val wifiNull = "NULL"
    private var connectState = false
    private var connectOn = "Connected!"
    private var connectOff = "Null"

    private var stringText01 = "01"
    private var stringText02 = "01"
    private var stringText03 = "01"
    private var message = "01:01:01"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wifi, container, false)
    }

    //Override the FragmentLifeCycle : New in fragment:1.3.0-alpha02
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        setViewData()
    }

    private fun initView(view: View) {
        textViewState = view.findViewById(R.id.fragment_wifi_state)
        editTextIPAddress = view.findViewById(R.id.fragment_wifi_edit_ip)
        editTextPort = view.findViewById(R.id.fragment_wifi_edit_port)
        buttonSubmit = view.findViewById(R.id.wifi_submit_button)
        textViewInstantIP = view.findViewById(R.id.fragment_wifi_instantIP)
        textViewInstantPort = view.findViewById(R.id.fragment_wifi_instantPort)
        textConnectStatue = view.findViewById(R.id.fragment_wifi_connectStatue)
        buttonConnectDevice = view.findViewById(R.id.fragment_wifi_connectDevice)
        wifiEditData01 = view.findViewById(R.id.wifi_edit_data01)
        wifiEditData02 = view.findViewById(R.id.wifi_edit_data02)
        wifiEditData03 = view.findViewById(R.id.wifi_edit_data03)
        wifiTextDataView = view.findViewById(R.id.wifi_text_data_view)
        wifiButtonDataSend = view.findViewById(R.id.wifi_button_data_send)
        wifiButtonDataSave = view.findViewById(R.id.wifi_button_data_save)

        wifiManager = requireActivity().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiState = wifiManager.wifiState
    }
    @SuppressLint("SetTextI18n")
    private fun setViewData() {
        textViewState.text = stateOff
        textViewInstantIP.text = defaultIP
        textViewInstantPort.text = defaultPort.toString()
        editTextIPAddress.setText(defaultIP)
        editTextPort.setText(defaultPort.toString())

        wifiEditData01.setText("01")
        wifiEditData02.setText("01")
        wifiEditData03.setText("01")
        wifiTextDataView.text = message

        textConnectStatue.text = connectOff
        socket = Socket()

        if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            textViewState.text = stateOn
        } else {
            textViewState.text = stateOff
        }

        buttonSubmit.setOnClickListener {
            if (IsStringIPv4().isIP(editTextIPAddress.text.toString())) {
                figureIP = editTextIPAddress.text.toString()
                editTextIPAddress.setText(figureIP)
                editTextIPAddress.clearFocus()
                textViewInstantIP.text = figureIP
            } else {
                Toast.makeText(requireActivity(), "Check IP", Toast.LENGTH_SHORT).show()
            }
            if (IsStringIPv4.isNum(editTextPort.text.toString())) {
                figurePort = editTextPort.text.toString().toInt()
                editTextPort.setText(figurePort.toString())
                editTextPort.clearFocus()
                textViewInstantPort.text = figurePort.toString()
            } else {
                Toast.makeText(requireActivity(), "Check Port", Toast.LENGTH_SHORT).show()
            }
        }
        buttonConnectDevice.setOnClickListener {
            if (!connectState) {
                try {
                    wifiConnectThread.start()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        wifiButtonDataSave.setOnClickListener {
            val strTmp01 = wifiEditData01.text.toString()
            val strTmp02 = wifiEditData02.text.toString()
            val strTmp03 = wifiEditData03.text.toString()
            try {
                when(strTmp01.length) {
                    1 -> stringText01 = "0$strTmp01"
                    2 -> stringText01 = strTmp01
                }
                when(strTmp02.length) {
                    1 -> stringText02 = "0$strTmp02"
                    2 -> stringText02 = strTmp02
                }
                when(strTmp03.length) {
                    1 -> stringText03 = "0$strTmp03"
                    2 -> stringText03 = strTmp03
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireActivity(),"Null Value!", Toast.LENGTH_SHORT).show()
            }
            wifiEditData01.setText(stringText01)
            wifiEditData02.setText(stringText02)
            wifiEditData03.setText(stringText03)

            message = "$stringText01:$stringText02:$stringText03"

            wifiTextDataView.text = message

            wifiEditData01.clearFocus()
            wifiEditData02.clearFocus()
            wifiEditData03.clearFocus()
        }
        wifiButtonDataSend.setOnClickListener {
            wifiSendDataThread.start()
            Thread.sleep(100)
            wifiSendDataThread.interrupt()
        }
    }

    private fun sendMessage(content: String) {
        if (connectState) {
            try {
                outputStream = socket.getOutputStream()
                outputStream.write(content.toByteArray(charset("US-ASCII")))
                outputStream.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    inner class WifiConnect: Thread() {
        override fun run() {
            super.run()
            try {
                socket.connect(InetSocketAddress(figureIP, figurePort))
            } catch (e: InterruptedException) {
                e.printStackTrace()
                socket.close()
            }
        }
    }
    inner class WifiSendDataThread: Thread() {
        override fun run() {
            super.run()
            try {
                    sendMessage(message)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    socket.close()
                }
        }
    }

    override fun onStart() {
        //WifiStateCheckCoroutines
        coroutineScope.launch(Dispatchers.Main) {
            try {
                while (true) {
                    wifiState = wifiManager.wifiState
                    if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                        textViewState.text = stateOn
                    } else {
                        textViewState.text = stateOff
                    }
                    connectState = socket.isConnected
                    if (connectState) {
                        textConnectStatue.text = connectOn
                    } else {
                        textConnectStatue.text = connectOff
                    }
                    delay(200)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        super.onStart()
        wifiSendDataThread = WifiSendDataThread()
        wifiConnectThread = WifiConnect()
    }
    override fun onStop() {
        coroutineJob.cancel()
        super.onStop()
    }

}