package com.worka.eroyal.data

import com.google.gson.Gson

fun <T> T.json(): String = Gson().toJson(this).replace("\\", "")


fun <T> String.toGson(clazz: Class<T>): T = Gson().fromJson(this, clazz)