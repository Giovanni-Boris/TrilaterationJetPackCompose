package com.example.trilaterationjetpackcompose.util.BeaconLibrary

import android.os.Parcel
import android.os.Parcelable

class Beacon(mac: String?) {
    enum class beaconType {
        iBeacon, eddystoneUID, any
    }

    val macAddress = mac
    var manufacturer: String? = null
    var type: beaconType = beaconType.any
    var uuid: String? = null
    var major: Int? = null
    var minor: Int? = null
    var namespace: String? = null
    var instance: String? = null
    var rssi: Int? = null
    var lastDistance: Int? = null
    var movingAverageFilter = MovingAverageFilter(5)

    fun calculateDistance(txPower: Int, rssi: Int): Double? {
        return movingAverageFilter.calculateDistance(txPower, rssi);
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Beacon) return false

        if (macAddress != other.macAddress) return false

        return true
    }

    override fun hashCode(): Int {
        return macAddress?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Beacon(macAddress=$macAddress, manufacturer=$manufacturer, type=$type, uuid=$uuid, major=$major, minor=$minor, rssi=$rssi)"
    }




}
