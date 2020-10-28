package com.worka.eroyal.utils

object JNIUtil {

    init {
        System.loadLibrary("native-lib")
    }

    external fun apiEndpoint(): String
}