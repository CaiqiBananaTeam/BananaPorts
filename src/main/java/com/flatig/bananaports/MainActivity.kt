package com.flatig.bananaports

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.flatig.bananaports.view.BluetoothFragment
import com.flatig.bananaports.view.AboutFragment
import com.flatig.bananaports.view.PortsFragment
import com.flatig.bananaports.view.WifiFragment
import com.permissionx.guolindev.PermissionX

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var radioGroup: RadioGroup

    private lateinit var bluetoothFragment: BluetoothFragment
    private lateinit var wifiFragment: WifiFragment
    private lateinit var portsFragment: PortsFragment
    private lateinit var aboutFragment: AboutFragment
    private lateinit var showingFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        fragmentReplace(bluetoothFragment,resources.getString(R.string.text_home_bar_bluetooth))
        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        if (isFirstRun(this)) {
            AlertDialog.Builder(this).apply {
                setTitle(resources.getString(R.string.main_dialog_policy))
                setMessage(resources.getString(R.string.main_dialog_message))
                setCancelable(false)
                setPositiveButton(resources.getString(R.string.main_dialog_agree)) { _, _ ->
                    alreadyRan(this.context)
                    permissionRequest()
                }
                setNegativeButton(resources.getString(R.string.main_dialog_exit)) { _, _ ->
                    finish()
                }
                setNeutralButton(resources.getString(R.string.main_dialog_user_policy)) { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://flatig.vip/assets/privacy.html")
                    startActivity(intent)
                }
                show()
            }
        } else {
            permissionRequest()
        }
        super.onResume()
    }

    // Function to initial View
    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        radioGroup = findViewById(R.id.home_bar_radio_group)

        bluetoothFragment = BluetoothFragment()
        wifiFragment = WifiFragment()
        portsFragment = PortsFragment()
        aboutFragment = AboutFragment()
        showingFragment = bluetoothFragment

        radioGroup.setOnCheckedChangeListener { _, checkedID ->
            when (checkedID) {
                R.id.bar_home_radio_bluetooth
                -> fragmentReplace(bluetoothFragment, resources.getString(R.string.text_home_bar_bluetooth))
                R.id.bar_home_radio_wifi
                -> fragmentReplace(wifiFragment, resources.getString(R.string.text_home_bar_wifi))
                R.id.bar_home_radio_ports
                -> fragmentReplace(portsFragment,resources.getString(R.string.text_home_bar_ports))
                R.id.bar_home_radio_about
                -> fragmentReplace(aboutFragment, resources.getString(R.string.text_home_bar_about))
            }
        }
    }
    // Function to request permissions
    private fun permissionRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PermissionX.init(this)
                .permissions(Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_ADVERTISE)
                .onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(deniedList, "Core fundamental are based on these permissions", "OK", "Cancel")
                }
                .onForwardToSettings { scope, deniedList ->
                    scope.showForwardToSettingsDialog(deniedList, "You need to allow necessary permissions in Settings manually", "OK", "Cancel")
                }
                .explainReasonBeforeRequest()
                .request { allGranted, _, deniedList ->
                    if (!allGranted) {
                        Toast.makeText(this, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            PermissionX.init(this)
                .permissions(Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                .onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(deniedList, "Core fundamental are based on these permissions", "OK", "Cancel")
                }
                .onForwardToSettings { scope, deniedList ->
                    scope.showForwardToSettingsDialog(deniedList, "You need to allow necessary permissions in Settings manually", "OK", "Cancel")
                }
                .explainReasonBeforeRequest()
                .request { allGranted, _, deniedList ->
                    if (!allGranted) {
                        Toast.makeText(this, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
    // Function to replace fragments
    private fun fragmentReplace(fragment: Fragment, title: String = resources.getString(R.string.home_toolbar), isAddToBackStack: Boolean = false) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (!wifiFragment.isAdded) {
            fragmentTransaction.add(R.id.home_frame_layout, bluetoothFragment)
            fragmentTransaction.add(R.id.home_frame_layout, wifiFragment)
            fragmentTransaction.add(R.id.home_frame_layout, portsFragment)
            fragmentTransaction.add(R.id.home_frame_layout, aboutFragment)
            fragmentTransaction.hide(bluetoothFragment)
            fragmentTransaction.hide(wifiFragment)
            fragmentTransaction.hide(portsFragment)
            fragmentTransaction.hide(aboutFragment)
        }
        fragmentTransaction.hide(showingFragment)
        fragmentTransaction.show(fragment)

        if (isAddToBackStack) fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        Log.e("WHY",fragment.isAdded.toString())

        showingFragment = fragment
        toolbar.title = title
    }

    // Function First Run
    private fun isFirstRun(context: Context): Boolean {
        val checkRunVar = context.getSharedPreferences("runNote", Context.MODE_PRIVATE)
        return checkRunVar.getInt("runFirst", 0) == 0
    }
    // Function Remember Run Times
    private fun alreadyRan(context: Context) {
        val runVar = context.getSharedPreferences("runNote", Context.MODE_PRIVATE).edit()
        runVar.putInt("runFirst", 6)
        runVar.apply()
    }
    // Function to manage and control coroutines
    private fun coroutinesControl() {

    }
}