package com.rodrigmatrix.sippa

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId

//  Created by Rodrigo G. Resende on 2019-11-16.

class SippaApp : Application(){


    override fun onCreate() {
        super.onCreate()

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