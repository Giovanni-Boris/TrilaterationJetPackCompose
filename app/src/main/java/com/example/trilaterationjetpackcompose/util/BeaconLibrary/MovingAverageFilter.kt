package com.example.trilaterationjetpackcompose.util.BeaconLibrary

import android.util.Log
import java.util.LinkedList

class MovingAverageFilter(private val size: Int) {
    val distanceQueue = LinkedList<Double>()
    private val TAG = "MovingAverageFilter";
    var lastDistance : Double = 0.0;
    fun calculateDistance(txPower:Int, rssi: Int): Double {
        val measuredPower = -65;
        val factor = (measuredPower - rssi) / (10 * 3.2)
        val distance = Math.pow(10.0, factor)
        var movingAverage = distance

        if (distanceQueue.size == size){

            val sum = distanceQueue.stream().map { it }.reduce{ acc, elem -> acc + elem }.orElse(0.0)
            Log.d(TAG, "Using moving filter" + sum)

            movingAverage = sum / size
        }

        distanceQueue.add(movingAverage)
        lastDistance = movingAverage
        if (distanceQueue.size > size) {
            distanceQueue.remove()
        }
        return movingAverage
    }
}