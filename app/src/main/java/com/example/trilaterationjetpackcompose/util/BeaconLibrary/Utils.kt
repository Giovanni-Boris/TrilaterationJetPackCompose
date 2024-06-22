package com.example.projecto_suarez.util.BeaconLibrary

object Utils {
    @OptIn(ExperimentalStdlibApi::class)
    fun toHexString(bytes:ByteArray):String{
        return bytes.toHexString()
    }
}