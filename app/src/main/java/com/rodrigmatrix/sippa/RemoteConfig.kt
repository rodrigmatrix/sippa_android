package com.rodrigmatrix.sippa

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.rodrigmatrix.sippa.entity.Version
import com.rodrigmatrix.sippa.entity.Versions
import java.lang.Exception

//  Created by Rodrigo G. Resende on 2019-12-25.

class RemoteConfig(private val remoteConfig: FirebaseRemoteConfig){

    fun initRemoteConfig(){
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetch()
        .addOnSuccessListener {
            remoteConfig.activate()
        }
    }

    fun getVersions(): List<Version> {
        val versions = remoteConfig.getString("sippa_updates")
        return try {
            Gson().fromJson(versions, Versions::class.java).versions
        }
        catch(e: Exception){
            listOf()
        }
    }


}