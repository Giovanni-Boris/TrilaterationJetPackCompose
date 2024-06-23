package com.example.trilaterationjetpackcompose.canvas

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trilaterationjetpackcompose.services.BeaconScanner
import com.example.trilaterationjetpackcompose.util.BeaconLibrary.Beacon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Optional.empty
import javax.inject.Inject

@HiltViewModel
class BeaconViewModel @Inject constructor(
    private val beaconScanner: BeaconScanner,
) : ViewModel() {
    private var _result = mutableStateOf<List<BeaconData>>(emptyList())
    val result: State<List<BeaconData>>  = _result

    init {
        beaconScanner.initBluetooth()
        beaconScanner.bluetoothScanStart(beaconScanner.bleScanCallback)
        viewModelScope.launch {
            beaconScanner.resultBeacons.collect { result ->
                Log.d(TAG,"Here")
                _result.value = result
            }
        }
    }
    companion object {
        val TAG = "BeaconViewModel"
    }
}