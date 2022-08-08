package com.flatig.bananaports

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.flatig.bananaports.view.BluetoothFragment
import com.flatig.bananaports.view.MeFragment
import com.flatig.bananaports.view.WifiFragment

class MainActivity : AppCompatActivity() {
    private val PERMISSION_REQUEST_CODE: Int = 0
    private lateinit var toolbar: Toolbar
    private lateinit var radioGroup: RadioGroup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        requestPermissions()

        toolbar.title = resources.getString(R.string.home_toolbar)
        setSupportActionBar(toolbar)
        fragmentReplace(BluetoothFragment(),resources.getString(R.string.text_home_bar_bluetooth))
        radioGroup.setOnCheckedChangeListener { _, checkedID ->
            when (checkedID) {
                R.id.bar_home_radio_bluetooth
                        -> fragmentReplace(BluetoothFragment(), resources.getString(R.string.text_home_bar_bluetooth))
                R.id.bar_home_radio_wifi
                        -> fragmentReplace(WifiFragment(), resources.getString(R.string.text_home_bar_wifi))
                R.id.bar_home_radio_me
                        -> fragmentReplace(MeFragment(), resources.getString(R.string.text_home_bar_me))
            }
        }
    }


    // Function to initial View
    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        radioGroup = findViewById(R.id.home_bar_radio_group)
    }

    // Function to request permissions
    private fun requestPermissions() {
        val permissionsArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
        for (permissionItem in permissionsArray) {
            if (checkSelfPermission(permissionItem) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permissionItem),PERMISSION_REQUEST_CODE)
            }
        }
    }

    // Function to replace fragments
    private fun fragmentReplace(
        fragment: Fragment,
        title: String = resources.getString(R.string.home_toolbar),
        isAddToBackStack: Boolean = false
    ) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.home_frame_layout,fragment)
        toolbar.title = title
        if (isAddToBackStack) fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}