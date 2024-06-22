package com.example.trilaterationjetpackcompose.canvas

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trilaterationjetpackcompose.services.BeaconScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BeaconViewModel @Inject constructor(
    private val beaconScanner: BeaconScanner,
) : ViewModel() {
    val _result = mutableStateOf("");
    val result : State<String> = _result;


    init {
        beaconScanner.initBluetooth()
        beaconScanner.bluetoothScanStart(beaconScanner.bleScanCallback)
        viewModelScope.launch {
            beaconScanner.resultBeacons.collect { result ->
                _result.value = result
            }
        }
    }
    companion object {
        val TAG = "BeaconViewModel"
    }
}