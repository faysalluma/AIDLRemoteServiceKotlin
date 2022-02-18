package com.famoco.appclient

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
import com.famoco.appclient.databinding.ActivityMainBinding
import com.famoco.appserver.IRemoteCallBack
import com.famoco.appserver.IRemoteProductService
import com.famoco.appserver.models.Product
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var service : IRemoteProductService?=null
    private var serverAppUri = "com.famoco.appserver"
    private val TAG = MainActivity::class.java.simpleName
    private val dispatcher = Executors.newSingleThreadExecutor()
        .asCoroutineDispatcher()

    private val coroutineScope = CustomCoroutineScope()

    inner class CustomCoroutineScope internal constructor() : CoroutineScope {
        override val coroutineContext: CoroutineContext =
            dispatcher + Job() + CoroutineExceptionHandler() { coroutineContext: CoroutineContext, throwable: Throwable ->
                GlobalScope.launch { println("Caught $throwable") }
            }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (appInstalledOrNot(serverAppUri)) {
            binding.save.setOnClickListener {
                var name = binding.name.text.toString()
                var quantity = binding.quantity.text.toString().toInt()
                var cost = binding.cost.text.toString().toFloat()
                try {
                    service!!.addProduct(name, quantity, cost)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            binding.next.setOnClickListener{
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            }
        }else{
            Toast.makeText(applicationContext, "Server App not installed", Toast.LENGTH_SHORT).show();
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

            // Register CallBack (Using in launch coroutines)
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    service?.registerCallback(productServiceCallback)
                } catch (e: RemoteException) {
                    Log.w(TAG, "Service.registerCallback() failed with: $e")
                    e.printStackTrace()
                }
            }

        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    service?.unregisterCallback(productServiceCallback)
                } catch (e: RemoteException) {
                    Log.w(TAG, "service.unregisterCallback() failed with: $e")
                    e.printStackTrace()
                }
            }
            service=null
            Log.d(TAG, "Service Disconnected")
        }
    }

    private val productServiceCallback: IRemoteCallBack = object: IRemoteCallBack.Stub() {
        override fun resultMessage(message: String?): String {
            binding.result.setText(message)
            return "Coucou from client back"
        }

        override fun getProduct(product: Product?) {
            if (product!=null){
                var result="Name : ${product.name} Quantity : ${product.quantity} Cost : ${product.cost} "
                binding.product.setText(result)
            }
        }

        override fun getAllProducts(products: MutableList<Product>?) {
            if (products!=null){
                var result=""
                for (product in products)
                {
                    result+="Name : ${product.name} Quantity : ${product.quantity} Cost : ${product.cost} \n"
                }
                binding.products.setText(result)
            }
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