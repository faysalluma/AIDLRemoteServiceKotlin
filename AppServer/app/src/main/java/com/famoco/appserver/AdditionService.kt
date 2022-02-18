package com.famoco.appserver

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AdditionService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return mBinder;
    }

    private val mBinder = object : IAdd.Stub() {
        override fun addNumbers(num1: Int, num2: Int): Int {
            return num1.plus(num2)
        }

        override fun getStringList(): MutableList<String> {
            val country = mutableListOf<String>()
            country.add("India")
            country.add("Bhutan")
            country.add("Nepal")
            country.add("USA")
            country.add("Canada")
            country.add("China")
            return country
        }
    }

}