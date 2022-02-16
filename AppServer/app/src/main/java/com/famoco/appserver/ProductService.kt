package com.famoco.appserver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.famoco.appserver.models.Product
import java.util.*

class ProductService : Service() {

    override fun onBind(intent: Intent): IBinder {
        Log.d("toto", "Service passe")
        return mBinder
    }

    private val mBinder = object : IRemoteProductService.Stub() {

        var products = Collections.synchronizedList(ArrayList<Product>())
        override fun addProduct(name: String?, quantity: Int, cost: Float) {
            val product = Product(name!!,quantity,cost)
            products.add(product)
        }

        override fun getProduct(name: String?): Product ?{
            for (product in products) {
                if (product.name.equals(name)) {
                    return product
                }
            }
            return null
        }

        override fun getAllProducts(): MutableList<Product> {
            return products;
        }

    }
}