package com.example.trilaterationjetpackcompose.services

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.projecto_suarez.util.BeaconLibrary.Utils
import com.example.trilaterationjetpackcompose.canvas.BeaconData
import com.example.trilaterationjetpackcompose.util.BeaconLibrary.Beacon
import com.example.trilaterationjetpackcompose.util.BeaconLibrary.BleScanCallback
import com.example.trilaterationjetpackcompose.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class BeaconScanner(private val context: Context) {
    private val TAG: String = "BeaconScanner"

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var btScanner: BluetoothLeScanner? = null;
    var beaconSet: HashMap<String,Beacon> = HashMap()
    private val _beaconsFlow = MutableStateFlow<List<BeaconData>>(emptyList())
    val resultBeacons =  _beaconsFlow.asStateFlow()

    val UUID_default = "46ca9f8463e64f2aaea77c72f4826baf"

    fun initBluetooth() {
        bluetoothManager = getSystemService(context,BluetoothManager::class.java)!!
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter != null) {
            btScanner = bluetoothAdapter.bluetoothLeScanner
        } else {
            Log.d(TAG, "BluetoothAdapter is null")
        }
    }

    fun bluetoothScanStart(bleScanCallback: BleScanCallback) {
        Log.d(TAG, "btScan ...1")
        if (btScanner != null) {

            Log.d(TAG, "btScan ...2")

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            if(!checkPermissions(context, Constants.PERMISSIONS)) return;
            btScanner?.startScan(bleScanCallback)

        } else {
            Log.d(TAG, "btScanner is null")
        }

    }
    private fun checkPermissions(context: Context, permissions: Array<String>): Boolean{
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission)!= PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
    @SuppressLint("MissingPermission")
    private fun bluetoothScanStop(bleScanCallback: BleScanCallback) {
        Log.d(TAG, "btScan ...1")
        if (btScanner != null) {
            Log.d(TAG, "btScan ...2")
            btScanner!!.stopScan(bleScanCallback)

        } else {
            Log.d(TAG, "btScanner is null")
        }

    }

    @SuppressLint("MissingPermission")
    val onScanResultAction: (ScanResult?) -> Unit = { result ->
        Log.d(TAG, "onScanResultAction ")
        val scanRecord = result?.scanRecord
        val beacon = Beacon(result?.device?.address)
        beacon.manufacturer = result?.device?.name

        beacon.rssi = result?.rssi

        if (scanRecord != null) {
            scanRecord?.bytes?.let {
                val iBeaconManufactureData = scanRecord.getManufacturerSpecificData(0X004c)
                if (iBeaconManufactureData != null && iBeaconManufactureData.size >= 23) {
                    val iBeaconUUID = Utils.toHexString(iBeaconManufactureData.copyOfRange(2, 18))
                    Log.d(TAG, iBeaconUUID)

                    if (iBeaconUUID != UUID_default)
                        return@let

                    val major = Integer.parseInt(Utils.toHexString(iBeaconManufactureData.copyOfRange(18, 20)), 16)
                    val minor = Integer.parseInt(Utils.toHexString(iBeaconManufactureData.copyOfRange(20, 22)), 16)
                    val txPower = Integer.parseInt(
                        Utils.toHexString(iBeaconManufactureData.copyOfRange(22, 23)),
                        16
                    )
                    beacon.type = Beacon.beaconType.iBeacon
                    beacon.uuid = iBeaconUUID
                    beacon.major = major
                    beacon.minor = minor

                    val key = "${beacon.uuid}${beacon.minor}${beacon.major}"

                    if(!beaconSet.containsKey(key)){
                        beaconSet.put(key, beacon)
                    }

                    val beacounInSet = beaconSet.get(key)

                    beacon.rssi?.let { it1 -> beacounInSet?.calculateDistance(txPower = txPower, rssi = it1) }

                    Log.e(TAG, "iBeaconUUID:$iBeaconUUID major:$major minor:$minor rssi:${beacon?.rssi} distance ${beacounInSet?.movingAverageFilter?.lastDistance}" )

                    _beaconsFlow.value = beaconSet.values.toList().map { BeaconData(it.uuid!!,
                        it.movingAverageFilter.lastDistance.toFloat(), it.minor!!
                    ) }
                }

            }
        }

    }

    val onBatchScanResultAction: (MutableList<ScanResult>?) -> Unit = {
        if (it != null) {
            Log.d(TAG, "BatchScanResult " + it.toString())
        }
        Log.d(TAG, "Size of beacons " + it?.size)

    }

    val onScanFailedAction: (Int) -> Unit = {
        Log.d(TAG, "ScanFailed " + it.toString())
    }
    val bleScanCallback = BleScanCallback(
        onScanResultAction,
        onBatchScanResultAction,
        onScanFailedAction
    )


}