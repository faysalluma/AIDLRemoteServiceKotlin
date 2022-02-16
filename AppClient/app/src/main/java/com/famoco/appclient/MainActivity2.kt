package com.famoco.appclient

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.Toast
import com.famoco.appclient.databinding.ActivityMain2Binding
import com.famoco.appserver.IAdd

class MainActivity2 : AppCompatActivity(), View.OnClickListener {

    private var serverAppUri = "com.famoco.appserver"
    private val TAG = MainActivity2::class.java.simpleName
    private lateinit var binding : ActivityMain2Binding
    private var additionService : IAdd?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialse connection with server
        initConnection()

        binding.btnAdd.setOnClickListener(this)
        binding.btnNonPremitive.setOnClickListener(this)

    }

    private fun initConnection() {
        val intent = Intent()
        intent.setAction("addservice")
        intent.setPackage(serverAppUri)
        bindService(intent, connection, BIND_AUTO_CREATE)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            additionService = IAdd.Stub.asInterface(iBinder)
            Log.d(TAG, "Service Connected")
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            additionService=null
            Log.d(TAG, "Service Disconnected")
        }
    }

    override fun onClick(v: View?) {
        if (appInstalledOrNot(serverAppUri)) {
            when (v!!.id) {
                R.id.btnAdd -> {
                    var number1 = binding.num1.text.toString().toInt()
                    var number2 = binding.num2.text.toString().toInt()
                    try {
                        val result=additionService!!.addNumbers(number1, number2)
                        binding.total.setText("Result $result")
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }

                }
                R.id.btnNonPremitive -> {
                    var result =""
                    val lists = additionService!!.stringList
                    for (item in lists){
                        result+="${item} , "
                    }
                    binding.total.setText(result)
                }
            }
        }else{
            Toast.makeText(applicationContext, "Server App not installed", Toast.LENGTH_SHORT).show();
        }
    }

    override fun onResume() {
        super.onResume()
        if (additionService == null) {
            initConnection()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = packageManager
        val app_installed: Boolean
        app_installed = try {
            val a = pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            val b = a.toString()
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }
}