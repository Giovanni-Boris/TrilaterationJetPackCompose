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
import kotlin.math.abs
import kotlin.math.pow

@HiltViewModel
class BeaconViewModel @Inject constructor(
    private val beaconScanner: BeaconScanner,
) : ViewModel() {
    private var _result = mutableStateOf<List<BeaconData>>(emptyList())
    val result: State<List<BeaconData>>  = _result

    private var _ubi = mutableStateOf(Pair(0f, 0f))
    val ubi: State<Pair<Float, Float>> = _ubi

    var beaconPositions = arrayOf(Pair(0.0, 0.0), Pair(6.0, 6.0), Pair(6.0, 0.0))

    init {
        beaconScanner.initBluetooth()
        beaconScanner.bluetoothScanStart(beaconScanner.bleScanCallback)
        viewModelScope.launch {
            beaconScanner.resultBeacons.collect { result ->
                Log.d(TAG,"Here")
                _result.value = result
                var distances = arrayOf(0.0)
                if (result.size == 3){
                    distances = arrayOf(result[0].distance.toDouble(), result[1].distance.toDouble(), result[2].distance.toDouble())
                    _ubi.value = findDevicePositionRelative(beaconPositions, distances)
                }
            }
        }
    }
    companion object {
        val TAG = "BeaconViewModel"
    }


    // Función para encontrar la posición del dispositivo basándose en las distancias relativas a los iBeacons
    fun findDevicePositionRelative(beaconPositions: Array<Pair<Double, Double>>, distances: Array<Double>): Pair<Float, Float> {
        if (beaconPositions.size!= distances.size) {
            throw IllegalArgumentException("Las posiciones de los beacons y las distancias deben tener la misma longitud")
        }

        val n = beaconPositions.size

        // Matriz de coeficientes para el sistema de ecuaciones
        val A = Array(n) { DoubleArray(2) }
        // Vector de resultados para el sistema de ecuaciones
        val b = DoubleArray(n)

        for (i in 0 until n) {
            val (x_i, y_i) = beaconPositions[i]
            A[i][0] = x_i - beaconPositions[0].first
            A[i][1] = y_i - beaconPositions[0].second
            b[i] = distances[0].pow(2) - distances[i].pow(2) + x_i.pow(2) - beaconPositions[0].first.pow(2) + y_i.pow(2) - beaconPositions[0].second.pow(2)
        }

        // Resolución del sistema de ecuaciones Ax = b mediante mínimos cuadrados
        val x = solveSystem(A, b)

        if (x != null) {
            val result = Pair((x[0] / 2).toFloat(), (x[1] / 2).toFloat())
            Log.d(TAG, "$result")
            return result
        }
        else
            return Pair(0f, 0f)
    }

    // Función auxiliar para resolver el sistema de ecuaciones Ax = b usando mínimos cuadrados
    fun solveSystem(A: Array<DoubleArray>, b: DoubleArray): DoubleArray? {
        val m = A.size
        val n = A[0].size

        val At = Array(n) { DoubleArray(m) }
        val AtA = Array(n) { DoubleArray(n) }
        val Atb = DoubleArray(n)

        // Transpuesta de A
        for (i in 0 until m) {
            for (j in 0 until n) {
                At[j][i] = A[i][j]
            }
        }

        // Multiplicación de At * A
        for (i in 0 until n) {
            for (j in 0 until n) {
                AtA[i][j] = 0.0
                for (k in 0 until m) {
                    AtA[i][j] += At[i][k] * A[k][j]
                }
            }
        }

        // Multiplicación de At * b
        for (i in 0 until n) {
            Atb[i] = 0.0
            for (k in 0 until m) {
                Atb[i] += At[i][k] * b[k]
            }
        }

        // Resolución de AtA * x = Atb
        return gaussianElimination(AtA, Atb)
    }

    // Función auxiliar para resolver un sistema de ecuaciones lineales mediante eliminación gaussiana
    fun gaussianElimination(A: Array<DoubleArray>, b: DoubleArray): DoubleArray {
        val n = A.size
        val x = DoubleArray(n)
        val augmentedMatrix = Array(n) { DoubleArray(n + 1) }

        // Construcción de la matriz aumentada
        for (i in 0 until n) {
            for (j in 0 until n) {
                augmentedMatrix[i][j] = A[i][j]
            }
            augmentedMatrix[i][n] = b[i]
        }

        // Eliminación Gaussiana
        for (i in 0 until n) {
            // Pivoteo
            var maxRow = i
            for (k in i + 1 until n) {
                if (abs(augmentedMatrix[k][i]) > abs(augmentedMatrix[maxRow][i])) {
                    maxRow = k
                }
            }
            val temp = augmentedMatrix[i]
            augmentedMatrix[i] = augmentedMatrix[maxRow]
            augmentedMatrix[maxRow] = temp

            // Escalado
            for (k in i + 1 until n) {
                val factor = augmentedMatrix[k][i] / augmentedMatrix[i][i]
                for (j in i until n + 1) {
                    augmentedMatrix[k][j] -= factor * augmentedMatrix[i][j]
                }
            }
        }

        // Sustitución hacia atrás
        for (i in n - 1 downTo 0) {
            x[i] = augmentedMatrix[i][n] / augmentedMatrix[i][i]
            for (k in i - 1 downTo 0) {
                augmentedMatrix[k][n] -= augmentedMatrix[k][i] * x[i]
            }
        }

        return x
    }
}