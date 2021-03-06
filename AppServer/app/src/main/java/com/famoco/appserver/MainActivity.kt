package com.famoco.appserver

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.famoco.appserver.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private var service: IRemoteProductService? = null
    private var serverAppUri = "com.famoco.appserver"
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (appInstalledOrNot(serverAppUri)) {
            binding.save.setOnClickListener{
                var name = binding.name.text.toString()
                var quantity = binding.quantity.text.toString().toInt()
                var cost = binding.cost.text.toString().toFloat()
                try {
                    service!!.addProduct(name, quantity, cost)
                    Toast.makeText(applicationContext, "Product added", Toast.LENGTH_SHORT)
                        .show();
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            binding.view.setOnClickListener{
                viewAllProducts()
            }
        } else {
            Toast.makeText(applicationContext, "Server App not installed", Toast.LENGTH_SHORT)
                .show();
        }
    }

    private fun initConnection() {
        val intent = Intent()
        intent.setAction("productservice")
        intent.setPackage(serverAppUri)
        bindService(intent, connection, BIND_AUTO_CREATE)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            service = IRemoteProductService.Stub.asInterface(iBinder)
            Log.d(TAG, "Service Connected")
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            service = null
            Log.d(TAG, "Service Disconnected")
        }
    }

    private fun viewAllProducts() {
        try {
            var result = ""
            val allProducts = service!!.getAllProducts()
            for (product in allProducts) {
                result += "Name : ${product.name} Quantity : ${product.quantity} Cost : ${product.cost} \n"
            }
            binding.result.setText(result)

        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if (service == null) {
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
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }
}