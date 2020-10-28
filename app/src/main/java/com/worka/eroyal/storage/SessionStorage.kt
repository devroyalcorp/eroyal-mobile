package com.worka.eroyal.storage

import android.content.Context
import com.orhanobut.hawk.Hawk
import com.worka.eroyal.base.Constants.ACCESS_TOKEN
import com.worka.eroyal.base.Constants.CLIENT
import com.worka.eroyal.base.Constants.UID
import com.worka.eroyal.data.json
import com.worka.eroyal.data.toGson

class SessionStorage(var context: Context) {
    fun <T> put(key: String, value: T) {
        Hawk.put(key, value.json())
    }

    fun <T> get(key: String, clazz: Class<T>): T {
        return Hawk.get<String>(key).toGson(clazz)
    }

    fun saveAccessToken(accessToken: String?){
        accessToken?.let {
            Hawk.put(ACCESS_TOKEN, accessToken)
        }
    }

    fun getAccessToken() : String {
        return Hawk.get<String>(ACCESS_TOKEN).orEmpty()
    }

    fun saveUid(uid: String?){
        uid?.let {
            Hawk.put(UID, uid)
        }
    }

    fun getUid(): String {
        return Hawk.get<String>(UID).orEmpty()
    }

    fun saveClient(client: String?){
        client?.let {
            Hawk.put(CLIENT, client)
        }
    }

    fun getClient(): String {
        return Hawk.get<String>(CLIENT).orEmpty()
    }

    fun onSignedOut(){
        Hawk.deleteAll()
    }

}