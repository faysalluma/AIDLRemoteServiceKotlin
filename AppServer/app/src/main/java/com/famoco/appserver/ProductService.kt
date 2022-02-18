package com.famoco.appserver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.famoco.appserver.ProductManager.IListener
import com.famoco.appserver.models.Product
import kotlin.collections.ArrayList

class ProductService : Service() {

    private lateinit var mProductManager : ProductManager
    private val mCallbackList: ArrayList<IRemoteCallBack?> = ArrayList()

    override fun onCreate() {
        super.onCreate()

        // Implements ProductManager
        mProductManager = ProductManager(object : IListener{
            override fun resultMessage(message: String) {
                try {
                    notifyProductAdded(message)
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun getProduct(product: Product) {
                try {
                    sendProductAdded(product)
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun getAllProducts(products: ArrayList<Product>) {
                try {
                    sendAllProductAdded(products)
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun notifyProductAdded(message: String) = mCallbackList.forEach {
        val result = it?.resultMessage(message)
        Log.d("guillebr", "notifyProductAdded: $result")
    }
    private fun sendProductAdded(product: Product) = mCallbackList.forEach {it?.getProduct(product)}
    private fun sendAllProductAdded(products: ArrayList<Product>) = mCallbackList.forEach {it?.getAllProducts(products)}

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    private val mBinder = object : IRemoteProductService.Stub() {
        var products = ArrayList<Product>()
        override fun addProduct(name: String?, quantity: Int, cost: Float) {
            val product = Product(name!!,quantity,cost)
            products.add(product)
            // Set Results
            mProductManager.setResultMessage("Save succesfully")
            mProductManager.setProduct(product)
            mProductManager.setAllProducts(products)
        }

        override fun getProduct(name: String?): Product ?{
            for (product in products) {
                if (product.name.equals(name)) {
                    return product
                }
            }
            return null
        }

        override fun getAllProducts(): ArrayList<Product> {
            return products;
        }

        override fun registerCallback(callback: IRemoteCallBack?) {
            mCallbackList.add(callback)
        }

        override fun unregisterCallback(callback: IRemoteCallBack?) {
            mCallbackList.remove(callback)
        }
    }
}