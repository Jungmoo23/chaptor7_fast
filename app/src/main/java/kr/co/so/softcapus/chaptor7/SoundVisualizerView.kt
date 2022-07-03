package kr.co.so.softcapus.chaptor7

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class SoundVisualizerView(
        context: Context,
        attrs:AttributeSet?=null
    ):View(context, attrs){

    var onRequestCurrentAmplutude:(()->Int)?=null


        private val amplitudePaint  = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.purple_500)
            strokeWidth  = LINE_WIDTH
            strokeCap = Paint.Cap.ROUND
        }
    private var drawingWidth: Int=0
    private var drawingHeight: Int = 0
    private var drawAmplitudes: List<Int> = emptyList()
    private var isReplaying: Boolean = false
    private var replayingPosition: Int = 0


    private val visuallizeRepeatAction: Runnable = object :Runnable{
        override fun run() {
            if(!isReplaying) {
                val currentAmplitude = onRequestCurrentAmplutude?.invoke() ?:0

                drawAmplitudes = listOf(currentAmplitude) + drawAmplitudes
            }
            else{
                replayingPosition++
            }
            invalidate()
            handler?.postDelayed(this,ACTION_INTERVAL)
        }
    }


        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            drawingWidth = w
            drawingHeight = h
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val centerY = drawingHeight / 2f
        var offsetX = drawingWidth.toFloat()

        drawAmplitudes
            .let { amplitudes ->
                if (isReplaying) {
                    amplitudes.takeLast(replayingPosition)
                } else {
                    amplitudes
                }
            }
            .forEach { amplitude ->
                val lineLength = amplitude / MAX_AMP * drawingHeight * 0.8F

                offsetX -= LINE_SPACE
                if (offsetX < 0) return@forEach

                canvas.drawLine(
                    offsetX,
                    centerY - lineLength / 2F,
                    offsetX,
                    centerY + lineLength / 2F,
                    amplitudePaint
                )
            }
    }

        fun startVisualizing(isReplaying: Boolean){
            this.isReplaying = isReplaying
            handler?.post(visuallizeRepeatAction)
        }

        fun stopVisualizing(){
            handler?.removeCallbacks(visuallizeRepeatAction)
        }

        companion object{
            private const val LINE_WIDTH = 10F
            private const val LINE_SPACE = 15F
            private const val MAX_AMP = Short.MAX_VALUE.toFloat()
            private const val ACTION_INTERVAL = 20L
        }
}