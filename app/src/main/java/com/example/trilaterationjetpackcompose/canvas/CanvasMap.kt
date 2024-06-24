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

val TAG = "MAP"
var point = Pair(0f, 0f)

@Composable
fun CanvasMap(result: List<BeaconData>) {
    val _point by remember { mutableStateOf(point) }

    var maxCanvasW = 0f.dp
    var maxCanvasH = 0f.dp
    var mToDp: Float

    //metros
    var maxRoomWidth = 100f
    var maxRoomHeight = 100f

    var maxCanvasWPx = 0f
    var maxCanvasHPx = 0f

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

                point?.let { (x, y) ->
                    drawPoint(x, y, Color.Red)
                }
            }
            picture(1, maxCanvasW*.1f, maxCanvasH*.1f, maxCanvasW, maxCanvasH)
            picture(2, maxCanvasW*.3f, maxCanvasH*.1f, maxCanvasW, maxCanvasH)
            picture(3, maxCanvasW*.6f, maxCanvasH*.1f, maxCanvasW, maxCanvasH)
            picture(4, maxCanvasW*.8f, maxCanvasH*.1f, maxCanvasW, maxCanvasH)
        }
        Button(onClick = { randomPoint(maxCanvasWPx, maxCanvasHPx) }) {
            Text("Mi ubicación")
        }
        val beaconInfo = result.joinToString(separator = "\n") { beacon ->
            "UUID: ${beacon.uuid}, Distance: ${beacon.distance}"
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

fun randomPoint(maxW: Float, maxH: Float){
    point = Pair((Math.random()*maxW).toFloat(), (Math.random()*maxH).toFloat())
}
