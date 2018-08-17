package com.sony.sensorslist

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

import androidx.core.text.buildSpannedString
import androidx.core.text.scale
import androidx.core.text.subscript
import androidx.core.text.superscript
import androidx.core.text.toSpanned

class Sensors(ctx: Context) {
    private var context = ctx.applicationContext
    private var manager = context.getSystemService(SensorManager::class.java)
    private var sensors = manager.getSensorList(Sensor.TYPE_ALL)

    val names: List<String>
        get() = sensors.map { sensor -> sensor.name }

    fun getSensorName(sel: Int): String = if (sel in 0..sensors.size) sensors[sel].name else ""
    fun getSensorInfoAsString(sel: Int) = if (sel in 0..sensors.size) sensors[sel].info(context) else ""

    fun listen(sel: Int, listener: SensorEventListener) =
            if (sel in 0..sensors.size) {
                manager.registerListener(listener, sensors[sel], SensorManager.SENSOR_DELAY_UI)
            } else {
                false
            }
    fun stop(listener: SensorEventListener) = manager.unregisterListener(listener)
}

val Sensor.isDeprecated: Boolean
    get() = type == Sensor.TYPE_ORIENTATION

fun Sensor.info(ctx: Context) = with(this) {
        "\n" + ctx.resources.getString(R.string.sensor_name) + ": $name" +
        "\n" + ctx.resources.getString(R.string.sensor_id) + ": $id" +
        "\n" + ctx.resources.getString(R.string.sensor_type_int) + ": $type" +
        "\n" + ctx.resources.getString(R.string.sensor_type_str) + ": $stringType" +
        "\n" + ctx.resources.getString(R.string.sensor_vendor) + ": $vendor" +
        "\n" + ctx.resources.getString(R.string.sensor_version) + ": $version" +
        "\n" + ctx.resources.getString(R.string.sensor_resolution) + ": $resolution" +
        "\n" + ctx.resources.getString(R.string.sensor_reporting_mode) + ": ${stringReportingMode(ctx)}" +
        "\n" + ctx.resources.getString(R.string.sensor_power) + ": ${power}mAh" +
        "\n" + ctx.resources.getString(R.string.sensor_range) + ": $maximumRange" +
        "\n" + ctx.resources.getString(R.string.sensor_delay_min) + ": $minDelay" +
        "\n" + ctx.resources.getString(R.string.sensor_delay_max) + ": $maxDelay" +
        "\n" + ctx.resources.getString(R.string.sensor_dynamic) + ": " +
                if (isDynamicSensor) ctx.resources.getString(R.string.values_true) else ctx.resources.getString(R.string.values_false) +
        "\n" + ctx.resources.getString(R.string.sensor_wakeup) + ": " +
                if (isWakeUpSensor) ctx.resources.getString(R.string.values_true) else ctx.resources.getString(R.string.values_false) +
        "\n" + ctx.resources.getString(R.string.sensor_additional_info) + ": " +
                if (isDeprecated) {
                    if (isAdditionalInfoSupported) {
                        ctx.resources.getString(R.string.values_true) + ", " + ctx.resources.getString(R.string.sensor_deprecated)
                    } else {
                        ctx.resources.getString(R.string.sensor_deprecated)
                    }
                } else {
                    if (isAdditionalInfoSupported) ctx.resources.getString(R.string.values_true)
                    else ctx.resources.getString(R.string.values_false)
                }
    }

fun stringAccuracy(ctx: Context, accuracy: Int): String {
    val accuracyArray = ctx.resources.getStringArray(R.array.accuracy)
    return if (accuracy in 0..accuracyArray.size) accuracyArray[accuracy]
    else ctx.resources.getString(R.string.not_applicable)
}

fun Sensor.stringReportingMode(ctx: Context): String {
    val reportingModeArray = ctx.resources.getStringArray(R.array.reportingMode)
    return if (reportingMode in 0..reportingModeArray.size) reportingModeArray[reportingMode]
    else ctx.resources.getString(R.string.not_applicable)
}

fun SensorEvent.stringValues(ctx: Context) =
        if (values == null) {
            ctx.resources.getString(R.string.not_applicable).toSpanned()
        } else when (sensor.type) {
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GRAVITY,
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                buildSpannedString {
                    append("x: ${values[0]}m/s")
                    superscript { scale(0.75f) { append("2") } }
                    append("\ny: ${values[1]}m/s")
                    superscript { scale(0.75f) { append("2") } }
                    append("\nz: ${values[2]}m/s")
                    superscript { scale(0.75f) { append("2") } }
                }
            }
            Sensor.TYPE_ACCELEROMETER_UNCALIBRATED -> {
                buildSpannedString {
                    append("x: ${values[0]}m/s")
                    superscript { scale(0.75f) { append("2") } }
                    append("\ny: ${values[1]}m/s")
                    superscript { scale(0.75f) { append("2") } }
                    append("\nz: ${values[2]}m/s")
                    superscript { scale(0.75f) { append("2") } }
                    append("\nx")
                    subscript { scale(0.75f) { append("bias") } }
                    append(": ${values[3]}m/s")
                    superscript { scale(0.75f) { append("2") } }
                    append("\ny")
                    subscript { scale(0.75f) { append("bias") } }
                    append(": ${values[4]}m/s")
                    superscript { scale(0.75f) { append("2") } }
                    append("\nz")
                    subscript { scale(0.75f) { append("bias") } }
                    append(": ${values[5]}m/s")
                    superscript { scale(0.75f) { append("2") } }
                }
            }
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                buildSpannedString {
                    append("${values[0]}")
                    superscript { scale(0.75f) { append("o") } }
                    append("C")
                }
            }
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
            Sensor.TYPE_ROTATION_VECTOR -> {
                ("x*sin(\u03B8/2):${values[0]}\n" +
                        "y*sin(\u03B8/2):${values[1]}\n" +
                        "z*sin(\u03B8/2):${values[2]}\n" +
                        "cos(\u03B8/2):${values[3]}\n" +
                        ctx.resources.getString(R.string.sensor_accuracy) +
                        ": ${values[4]}rad").toSpanned()
            }
            Sensor.TYPE_GAME_ROTATION_VECTOR -> {
                ("x*sin(\u03B8/2):${values[0]}\n" +
                        "y*sin(\u03B8/2):${values[1]}\n" +
                        "z*sin(\u03B8/2):${values[2]}\n" +
                        "cos(\u03B8/2):${values[3]}\n").toSpanned()
            }
            Sensor.TYPE_GYROSCOPE -> {
                ("x: ${values[0]}rad/s" +
                        "\ny: ${values[1]}rad/s" +
                        "\nz: ${values[2]}rad/s").toSpanned()
            }
            Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> {
                buildSpannedString {
                    append("x: ${values[0]}rad/s")
                    append("\ny: ${values[1]}rad/s")
                    append("\nz: ${values[2]}rad/s")
                    append("\nx")
                    subscript { scale(0.75f) { append("ed") } }
                    append(": ${values[3]}rad/s")
                    append("\ny")
                    subscript { scale(0.75f) { append("ed") } }
                    append(": ${values[4]}rad/s")
                    append("\nz")
                    subscript { scale(0.75f) { append("ed") } }
                    append(": ${values[5]}rad/s")
                }
            }
            Sensor.TYPE_HEART_BEAT -> {
                (ctx.resources.getString(R.string.sensor_confidence) +
                        "[0.0 - 1.0]: ${values[0]}").toSpanned()
            }
            Sensor.TYPE_LIGHT -> {
                "${values[0]}lux".toSpanned()
            }
            Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT -> {
                "[0.0 | 1.0]: ${values[0]}".toSpanned()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                buildSpannedString {
                    append("x: ${values[0]}\u00B5T")
                    append("\ny: ${values[1]}\u00B5T")
                    append("\nz: ${values[2]}\u00B5T")
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> {
                buildSpannedString {
                    append("x: ${values[0]}\u00B5T")
                    append("\ny: ${values[1]}\u00B5T")
                    append("\nz: ${values[2]}\u00B5T")
                    append("\nx")
                    subscript { scale(0.75f) { append("b") } }
                    append(": ${values[3]}\u00B5T")
                    append("\ny")
                    subscript { scale(0.75f) { append("b") } }
                    append(": ${values[4]}\u00B5T")
                    append("\nz")
                    subscript { scale(0.75f) { append("b") } }
                    append(": ${values[5]}\u00B5T")
                }
            }
            Sensor.TYPE_MOTION_DETECT -> {
                "${values[0]}".toSpanned()
            }
            Sensor.TYPE_ORIENTATION -> {
                var str = ""
                values?.forEach { str += "$it\n" }
                str.toSpanned()
            }
            Sensor.TYPE_POSE_6DOF -> {
                val str = "(${values[0]}, ${values[1]}, ${values[2]},  ${values[3]}\n" +
                        "${values[4]}, ${values[5]}, ${values[6]}\n" +
                        "${values[7]}, ${values[8]}, ${values[9]},  ${values[10]}\n" +
                        "${values[11]}, ${values[12]}, ${values[13]}\n" +
                        "${values[14]})"
                str.toSpanned()
            }
            Sensor.TYPE_PRESSURE -> {
                "${values[0]}hPa".toSpanned()
            }
            Sensor.TYPE_PROXIMITY -> {
                "${values[0]}cm".toSpanned()
            }
            Sensor.TYPE_RELATIVE_HUMIDITY -> {
                "${values[0]}%".toSpanned()
            }
            Sensor.TYPE_STATIONARY_DETECT -> {
                "[1.0] ${values[0]}".toSpanned()
            }
            Sensor.TYPE_HEART_RATE,
            Sensor.TYPE_STEP_COUNTER,
            Sensor.TYPE_SIGNIFICANT_MOTION -> {
                var str = ""
                values?.forEach { str += "$it\n" }
                str.toSpanned()
            }
            else -> ctx.resources.getString(R.string.not_applicable).toSpanned()
        }
