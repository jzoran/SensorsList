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

fun Sensor.info(): String = "\nName: ${this.name}" +
        "\nInt Type: ${this.type}" +
        "\nString Type: ${this.stringType}" +
        "\nVendor: ${this.vendor}" +
        "\nVersion: ${this.version}" +
        "\nResolution: ${this.resolution}" +
        "\nPower: ${this.power}mAh" +
        "\nMaximum Range: ${this.maximumRange}" +
        "\nMinimum Delay: ${this.minDelay}" +
        "\nMaximum Delay: ${this.maxDelay}" +
        "\nDynamic: ${this.isDynamicSensor}" +
        "\nWakeUp: ${this.isWakeUpSensor}" +
        "\nAdditional Info: ${this.isAdditionalInfoSupported}"

fun SensorEvent.getAccuracy() =
        when(accuracy) {
            0 -> "UNRELIABLE"
            1 -> "LOW"
            2 -> "MEDIUM"
            3 -> "HIGH"
            else -> "N/A"
        }
