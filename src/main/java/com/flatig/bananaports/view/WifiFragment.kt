package com.flatig.bananaports.view

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.flatig.bananaports.R
import com.flatig.bananaports.logic.model.IsStringIPv4
import java.io.IOException
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

class WifiFragment: Fragment() {
    private lateinit var textViewState: TextView
    private lateinit var textViewSSID: TextView
    private lateinit var editTextIPAddress: EditText
    private lateinit var editTextPort: EditText
    private lateinit var buttonSubmit: Button
    private lateinit var textViewInstantIP: TextView
    private lateinit var textViewInstantPort: TextView
    private lateinit var textConnectStatue: TextView
    private lateinit var buttonConnectDevice: Button

    private var socket = Socket()
    private lateinit var wifiManager: WifiManager
    private lateinit var outputStream: OutputStream
    private lateinit var wifiStateCheck: WifiStateCheck
    private lateinit var wifiSendDataThread: WifiSendDataThread
    private lateinit var wifiConnectThread: WifiConnect

    private val defaultIP = "192.168.1.1"
    private val defaultPort = 80
    private var figureIP: String = defaultIP
    private var figurePort: Int = defaultPort
    private var wifiState: Int = WifiManager.WIFI_STATE_DISABLED
    private val stateOn = "Wifi ON"
    private val stateOff = "Wifi OFF"
    private val wifiNull = "NULL"
    private var connectState = false
    private var connectOn = "Connected!"
    private var connectOff = "Null"

    private var stringText01 = ""
    private var stringText02 = ""
    private var stringText03 = ""
    private var message = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wifi, container, false)
    }

    //Create Lifecycle Observer to initial when : Activity onCreate
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event.targetState == Lifecycle.State.CREATED) {
                    lifecycle.removeObserver(this)
                }
            }
        })
    }

    //Override the FragmentLifeCycle : New in fragment:1.3.0-alpha02
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        setViewData()
    }


    private fun initView(view: View) {
        textViewState = view.findViewById(R.id.fragment_wifi_state)
        textViewSSID = view.findViewById(R.id.fragment_wifi_ssid)
        editTextIPAddress = view.findViewById(R.id.fragment_wifi_edit_ip)
        editTextPort = view.findViewById(R.id.fragment_wifi_edit_port)
        buttonSubmit = view.findViewById(R.id.wifi_submit_button)
        textViewInstantIP = view.findViewById(R.id.fragment_wifi_instantIP)
        textViewInstantPort = view.findViewById(R.id.fragment_wifi_instantPort)
        textConnectStatue = view.findViewById(R.id.fragment_wifi_connectStatue)
        buttonConnectDevice = view.findViewById(R.id.fragment_wifi_connectDevice)

        wifiManager = requireActivity().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiState = wifiManager.wifiState

    }
    private fun setViewData() {
        textViewState.text = stateOff
        textViewSSID.text = wifiNull
        textViewInstantIP.text = defaultIP
        textViewInstantPort.text = defaultPort.toString()
        editTextIPAddress.setText(defaultIP)
        editTextPort.setText(defaultPort.toString())
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
                wifiConnectThread.start()
            }
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
    inner class WifiStateCheck: Thread() {
        override fun run() {
            super.run()
            while (!isInterrupted) {
                try {
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
                    Thread.sleep(200)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    break
                }
            }
        }
    }
    inner class WifiSendDataThread: Thread() {
        override fun run() {
            super.run()
            while (!isInterrupted) {
                try {
                    sendMessage(message)
                    Thread.sleep(50)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    socket.close()
                    break
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        wifiStateCheck = WifiStateCheck()
        wifiSendDataThread = WifiSendDataThread()
        wifiConnectThread = WifiConnect()
        wifiStateCheck.start()
    }
    override fun onStop() {
        super.onStop()
        wifiStateCheck.interrupt()
        wifiSendDataThread.interrupt()
        wifiConnectThread.interrupt()
    }

}