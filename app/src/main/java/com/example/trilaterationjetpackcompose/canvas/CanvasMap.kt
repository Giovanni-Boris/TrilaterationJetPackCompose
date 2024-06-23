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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.trilaterationjetpackcompose.util.BeaconLibrary.Beacon

val TAG = "MAP"

@Composable
fun CanvasMap(result: List<BeaconData>) {
    var point by remember { mutableStateOf(Pair(0f, 0f)) }
    var maxCanvasW = 0f
    var maxCanvasH = 0f

    Column {
        BoxWithConstraints {
            Canvas(
                modifier = Modifier
                    .size(maxWidth)
                    .padding(10.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            Log.d(TAG, "Se toca la pantalla en ${offset}")
                        }
                    }){

                maxCanvasW = size.width
                maxCanvasH = size.height

                var path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    moveTo(size.width, 50f)
                    lineTo(size.width, size.height)
                    lineTo((size.width*79/100), size.height)
                    moveTo((size.width*79/100)-50, size.height)
                    lineTo(size.width*40/100,size.height)
                    moveTo((size.width*40/100)-50, size.height)
                    lineTo(0f,size.height)
                    lineTo(0f, 0f)
                    moveTo(size.width*55/100,0f)
                    lineTo(size.width*55/100,size.height*29/100)
                    lineTo(size.width-50,size.height*29/100)
                    moveTo(size.width*55/100,size.height*29/100)
                    lineTo(size.width*515/1000,size.height*29/100)

                    moveTo(size.width*48/100,0f)
                    lineTo(size.width*48/100,size.height*29/100-50)
                    lineTo(size.width*515/1000,size.height*29/100-50)
                    lineTo(size.width*515/1000,size.height*29/100-25)
                    moveTo(size.width*48/100,size.height*29/100-50)
                    lineTo(size.width*41/100,size.height*29/100-50)
                    lineTo(size.width*41/100,size.height*29/100)
                    lineTo(size.width*41/100+50,size.height*29/100)
                    moveTo(size.width*41/100,size.height*29/100)
                    lineTo(size.width*41/100-25,size.height*29/100)

                    moveTo(size.width*41/100-75,size.height*29/100)
                    lineTo(0f,size.height*29/100)

                    moveTo(size.width-50, size.height*71/100)
                    lineTo(size.width-100, size.height*71/100)
                    lineTo(size.width-100, size.height*805/1000)
                    moveTo(size.width-100, size.height*805/1000 + 50)
                    lineTo(size.width-100, size.height)
                    moveTo(size.width-100, size.height-100)
                    lineTo(size.width*40/100, size.height-100)
                }

                drawPath(
                    path = path,
                    color = Color.LightGray,
                    style = Stroke(width = 5.dp.toPx())
                )

                path = Path().apply {
                    moveTo(0f, size.height*.5f)
                    lineTo(0f, size.height*(.5f-.15f))

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
            picture(1, maxWidth*.1f, maxHeight*.1f, maxWidth, maxHeight)
            picture(2, maxWidth*.3f, maxHeight*.1f, maxWidth, maxHeight)
            picture(3, maxWidth*.6f, maxHeight*.1f, maxWidth, maxHeight)
            picture(4, maxWidth*.8f, maxHeight*.1f, maxWidth, maxHeight)
        }
        Button(onClick = { point = Pair((Math.random()*maxCanvasW).toFloat(), (Math.random()*maxCanvasH).toFloat()) }) {
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
            .size(w*.1f)
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
