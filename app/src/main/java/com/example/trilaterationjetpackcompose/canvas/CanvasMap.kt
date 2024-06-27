package com.example.trilaterationjetpackcompose.canvas

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

val TAG = "MAP"

@Composable
fun CanvasMap(result: List<BeaconData>, ubi: Pair<Float, Float>) {
    var _point by remember { mutableStateOf(Pair(0f, 0f)) }

    var maxCanvasW = 0f.dp
    var maxCanvasH = 0f.dp
    var mToDp = 0f

    //metros
    var maxRoomWidth = 100f
    var maxRoomHeight = 100f

    var maxCanvasWPx = 0f
    var maxCanvasHPx = 0f

    //metros
    var distancesArr = arrayOf(71.49, 79.48, 44.55)

    Column {
        BoxWithConstraints(modifier = Modifier
            .padding(10.dp)) {
            maxCanvasW = maxWidth
            mToDp = maxCanvasW.value / maxRoomWidth
            maxCanvasH = (maxRoomHeight*mToDp).dp

            Canvas(
                modifier = Modifier
                    .size(maxCanvasW, maxCanvasH)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            Log.d(TAG, "Se toca la pantalla en ${offset}")
                        }
                    }){
                maxCanvasWPx = maxCanvasW.toPx()
                maxCanvasHPx = maxCanvasH.toPx()

                var path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(maxCanvasW.toPx(), 0f)
                    lineTo(maxCanvasW.toPx(), maxCanvasH.toPx())
                    lineTo(0f, maxCanvasH.toPx())
                    lineTo(0f, 0f)
                }

                drawPath(
                    path = path,
                    color = Color.LightGray,
                    style = Stroke(width = 5.dp.toPx())
                )

                path = Path().apply {
                    moveTo(0f, maxCanvasH.toPx()*.5f)
                    lineTo(0f, maxCanvasH.toPx()*(.5f-.15f))
                    close()
                }

                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(width = 5.dp.toPx())
                )

                _point?.let { (x, y) ->
                    _point = convertPoint(ubi, mToDp)
                    Log.d(TAG, "UBI (dp): $_point")
                    drawPoint(x.dp.toPx(), (maxCanvasH - y.dp).toPx(), Color.Blue)
                }
            }
            picture(1, maxCanvasW*.1f, maxCanvasH*.1f, maxCanvasW, maxCanvasH)
            picture(2, maxCanvasW*.3f, maxCanvasH*.1f, maxCanvasW, maxCanvasH)
            picture(3, maxCanvasW*.6f, maxCanvasH*.1f, maxCanvasW, maxCanvasH)
            picture(4, maxCanvasW*.8f, maxCanvasH*.1f, maxCanvasW, maxCanvasH)
        }
        Button(onClick = { _point = convertPoint(ubi, mToDp) }) {
            Text("Mi ubicación")
        }
        //distances = arrayOf(result[0].distance.toDouble(), result[1].distance.toDouble(), result[2].distance.toDouble())

        val beaconInfo = result.joinToString(separator = "\n") { beacon ->
            "UUID: ${beacon.uuid}\nDistance: ${beacon.distance}\nMinor: ${beacon.minor}"
        }

        // Muestra la información de los Beacons en un Text composable
        Text(text = beaconInfo)
    }

}

fun DrawScope.drawPoint(x: Float, y: Float, color: Color) {
    drawCircle(
        color = color,
        center = Offset(x, y),
        radius = 5.dp.toPx()
    )
}

@Composable
fun picture(ID: Int, x: Dp, y: Dp, w: Dp, h: Dp) {
    Canvas(
        modifier = Modifier
            .size(w * .1f)
            .offset(x, y)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    Log.d(TAG, "Se toca la pintura ${ID} en ${offset}")
                }
            }
    ){
        drawRect(
            color = Color.Red
        )
    }
}

fun randomPoint(maxW: Float, maxH: Float): Pair<Float, Float> {
    return Pair((Math.random()*maxW).toFloat(), (Math.random()*maxH).toFloat())
}

// Función para calcular la distancia euclidiana entre dos puntos en coordenadas cartesianas
fun euclideanDistance(p1: Pair<Double, Double>, p2: Pair<Double, Double>): Double {
    val dx = p1.first - p2.first
    val dy = p1.second - p2.second
    return sqrt(dx * dx + dy * dy)
}

fun convertPoint(p: Pair<Float, Float>, c: Float): Pair<Float, Float>{
    var res = Pair(p.first*c, p.second*c)
    Log.d(TAG, "$res")
    return res
}
