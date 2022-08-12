package com.flatig.bananaports.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.flatig.bananaports.R

class AboutFragment: Fragment() {
    private lateinit var buttonOpenSourceAddress: Button
    private lateinit var buttonOpenSourceLicense: Button
    private lateinit var buttonPrivacyPolicy: Button
    private lateinit var buttonUserPolicy: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
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
        setData()
    }

    private fun initView(view: View) {
        buttonOpenSourceAddress = view.findViewById(R.id.about_opensource_address)
        buttonOpenSourceLicense = view.findViewById(R.id.about_opensource_license)
        buttonPrivacyPolicy = view.findViewById(R.id.about_privacy_policy)
        buttonUserPolicy = view.findViewById(R.id.about_using_license)
    }
    private fun setData() {
        val intent = Intent(Intent.ACTION_VIEW)
        buttonOpenSourceAddress.setOnClickListener {
            intent.data = Uri.parse("https://github.com/CaiqiBananaTeam/BananaPorts")
            startActivity(intent)
        }
        buttonOpenSourceLicense.setOnClickListener {
            intent.data = Uri.parse("https://flatig.vip/assets/b-license.html")
            startActivity(intent)
        }
        buttonPrivacyPolicy.setOnClickListener {
            intent.data = Uri.parse("https://flatig.vip/assets/b-privacy.html")
            startActivity(intent)
        }
        buttonUserPolicy.setOnClickListener {
            intent.data = Uri.parse("https://flatig.vip/assets/bu-privacy.html")
            startActivity(intent)
        }

    }
}