package kr.co.so.softcapus.chaptor7

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.util.*

class CountUpView(
    context: Context,
    attrs:AttributeSet?=null
): AppCompatTextView(context,attrs) {

    private var startTimeStamp: Long = 0L

    private val countUpAction: Runnable = object :Runnable{
        override fun run() {
            TODO("Not yet implemented")
            val currentTimeStamp = SystemClock.elapsedRealtime()

            val countTimeSeconds=
                ((currentTimeStamp - startTimeStamp)/1000L).toInt()
            updateCountTime(countTimeSeconds)

            handler?.postDelayed(this,1000L)
        }
    }

    fun startCountUp(){
        startTimeStamp = SystemClock.elapsedRealtime()
        handler?.post(countUpAction)
    }

    fun stopCountup(){
        handler?.removeCallbacks(countUpAction)
    }

    private fun updateCountTime(countTimeSeconds : Int){
        val min = countTimeSeconds /60
        val seconds = countTimeSeconds %60

        text ="%2d:%02d".format(min,seconds)


    }

}