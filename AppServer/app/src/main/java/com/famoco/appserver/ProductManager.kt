package com.famoco.appserver

import com.famoco.appserver.models.Product

class ProductManager (private val mListener: IListener) {

    interface IListener {
        fun resultMessage(message : String)
        fun getProduct(product : Product)
        fun getAllProducts(products : ArrayList<Product>)
    }

    fun setResultMessage(message : String){
        mListener.resultMessage(message)
    }

    fun setProduct(product: Product){
        mListener.getProduct(product)
    }

    fun setAllProducts(products: ArrayList<Product>){
        mListener.getAllProducts(products)
    }
}