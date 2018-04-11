package com.sony.sensorslist

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class Sensors {
    private var sensors: List<Sensor> = emptyList()
    private var manager: SensorManager

    constructor(context: Context) {
        manager = context.getSystemService(SensorManager::class.java)
        sensors = manager.getSensorList(Sensor.TYPE_ALL)
    }

    val names: List<String>
        get() = sensors.map { sensor -> sensor.name  }

    fun getSensorName(sel:Int): String = if (sel in 0..sensors.size) sensors[sel].name else ""
    fun getSensorInfoAsString(sel: Int) = if (sel in 0..sensors.size) sensors[sel].info() else ""
    fun listen(sel: Int, listener: SensorEventListener) =
            if (sel in 0..sensors.size) {
                manager.registerListener(listener, sensors[sel], SensorManager.SENSOR_DELAY_UI)
            } else {
                false
            }
    fun stop(listener: SensorEventListener) = manager.unregisterListener(listener)
}

fun Sensor.info(): String = with(this) {
    "\nName: $name" +
    "\nId: $id" +
    "\nInt Type: $type" +
    "\nString Type: $stringType" +
    "\nVendor: $vendor" +
    "\nVersion: $version" +
    "\nResolution: $resolution" +
    "\nReporting mode: $stringReportingMode" +
    "\nPower: ${power}mAh" +
    "\nMaximum Range: $maximumRange" +
    "\nMinimum Delay: $minDelay" +
    "\nMaximum Delay: $maxDelay" +
    "\nDynamic: $isDynamicSensor" +
    "\nWakeUp: $isWakeUpSensor" +
    "\nAdditional Info: $isAdditionalInfoSupported"
}

val Sensor.stringReportingMode: String
    get() = when(reportingMode) {
        0 -> "continuous"
        1 -> "on change"
        2 -> "one shot"
        3 -> "special trigger"
        else -> "n/a"
    }

val SensorEvent.stringAccuracy: String
    get() = accuracyToString(accuracy)

fun accuracyToString(accuracy: Int) = when(accuracy) {
    -1 -> "<not connected>"
    0 -> "unreliable"
    1 -> "low"
    2 -> "medium"
    3 -> "high"
    else -> "n/a"
}
