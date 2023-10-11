package fi.metropolia.homeweather.util.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * CallService is used to trigger emergency call
 */
object CallService {
    private var number:String = ""
    fun triggerEmergencyCall(context: Context) {
        val intent = Intent(Intent.ACTION_CALL,  Uri.parse("tel:$number")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (number.isNotEmpty()) context.startActivity(intent)
    }

    fun updateNumber(number: String) {
        this.number = number
        Log.d("Alert", "number is $number")
    }

    fun getNumber(): String {
        return number
    }
}