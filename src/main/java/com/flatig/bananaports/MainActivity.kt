package com.flatig.bananaports

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.flatig.bananaports.view.BluetoothFragment
import com.flatig.bananaports.view.AboutFragment
import com.flatig.bananaports.view.WifiFragment
import com.permissionx.guolindev.PermissionX

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var radioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

        toolbar.title = resources.getString(R.string.home_toolbar)
        setSupportActionBar(toolbar)
        fragmentReplace(BluetoothFragment(),resources.getString(R.string.text_home_bar_bluetooth))
        radioGroup.setOnCheckedChangeListener { _, checkedID ->
            when (checkedID) {
                R.id.bar_home_radio_bluetooth
                        -> fragmentReplace(BluetoothFragment(), resources.getString(R.string.text_home_bar_bluetooth)
                )
                R.id.bar_home_radio_wifi
                        -> fragmentReplace(WifiFragment(), resources.getString(R.string.text_home_bar_wifi))
                R.id.bar_home_radio_me
                        -> fragmentReplace(AboutFragment(), resources.getString(R.string.text_home_bar_me))
            }
        }
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
        fragmentReplace(BluetoothFragment(),resources.getString(R.string.text_home_bar_bluetooth))
        super.onResume()
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
    // Function to initial View
    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        radioGroup = findViewById(R.id.home_bar_radio_group)
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