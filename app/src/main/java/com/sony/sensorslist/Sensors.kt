package com.sony.sensorslist

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class Sensors {
    private var sensors: List<Sensor>? = null
    private lateinit var manager: SensorManager

    fun getSensors(context: Context) : List<Sensor> {
        if (sensors == null) {
            manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensors = manager.getSensorList(Sensor.TYPE_ALL)
        }
        return sensors!!
    }

    fun getSensorInfoAsString(sel: Int) = sensors?.get(sel)?.info()
    fun getSensorName(sel:Int) = sensors?.get(sel)?.name
    fun getSensor(sel: Int) = sensors?.get(sel)
    fun listen(sensor: Sensor, listener: SensorEventListener) =
                manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
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
