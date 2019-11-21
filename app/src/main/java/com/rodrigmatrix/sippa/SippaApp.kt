package com.rodrigmatrix.sippa

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.rodrigmatrix.sippa.persistence.StudentsDatabase

//  Created by Rodrigo G. Resende on 2019-11-16.

class SippaApp : Application(){


    override fun onCreate() {
        super.onCreate()
        setTheme()
        fcmId()
    }

    private fun setTheme(){
        val db = StudentsDatabase.invoke(applicationContext)
        var student = db.studentDao().getStudent()
        if(student != null){
            when (student.theme) {
                "light" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                "dark" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                "automatic" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
    }

    private fun fcmId(){
        val TAG = "fcm"
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                Log.d(TAG, token)
            })
    }
}