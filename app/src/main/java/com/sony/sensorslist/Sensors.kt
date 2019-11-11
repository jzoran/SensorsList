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
import java.util.Collections

class Sensors(ctx: Context) {

    companion object {
        const val ITEM_NOT_CHECKED = -1
    }

    private val context = ctx.applicationContext
    private val manager = ctx.getSystemService(SensorManager::class.java)
    private val sensors = manager?.getSensorList(Sensor.TYPE_ALL)

    var indexChecked = ITEM_NOT_CHECKED

    val names: List<String>
        get() = when (sensors) {
            null -> Collections.emptyList()
            else -> sensors.map { sensor -> sensor.name }
        }

    val sensorInfoToString: String
        get() = when {
            sensors == null || indexChecked !in 0..sensors.size -> ""
            else -> sensors[indexChecked].info(context)
        }

    fun listen(listener: SensorEventListener) = when {
        manager == null || sensors == null || indexChecked !in 0..sensors.size -> false
        else -> manager.registerListener(listener, sensors[indexChecked], SensorManager.SENSOR_DELAY_UI)
    }

    fun stop(listener: SensorEventListener) = manager?.unregisterListener(listener)
}

fun accuracyToString(ctx: Context, accuracy: Int): String {
    val accuracyArray = ctx.resources.getStringArray(R.array.accuracy)
    return if (accuracy in 0..accuracyArray.size) accuracyArray[accuracy]
    else ctx.resources.getString(R.string.not_applicable)
}

private val Sensor.isDeprecated: Boolean
    get() = type == Sensor.TYPE_ORIENTATION

private fun Sensor.info(ctx: Context) = with(this) {
        "\n" + ctx.resources.getString(R.string.sensor_name) + ": $name" +
        "\n" + ctx.resources.getString(R.string.sensor_id) + ": $id" +
        "\n" + ctx.resources.getString(R.string.sensor_type_int) + ": $type" +
        "\n" + ctx.resources.getString(R.string.sensor_type_str) + ": $stringType" +
        "\n" + ctx.resources.getString(R.string.sensor_vendor) + ": $vendor" +
        "\n" + ctx.resources.getString(R.string.sensor_version) + ": $version" +
        "\n" + ctx.resources.getString(R.string.sensor_resolution) + ": $resolution" +
        "\n" + ctx.resources.getString(R.string.sensor_reporting_mode) + ": ${reportingModeToString(ctx)}" +
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

private fun Sensor.reportingModeToString(ctx: Context): String {
    val reportingModeArray = ctx.resources.getStringArray(R.array.reportingMode)
    return if (reportingMode in 0..reportingModeArray.size) reportingModeArray[reportingMode]
    else ctx.resources.getString(R.string.not_applicable)
}

private val THETA: String
    get() = "\u03B8"

private val MU: String
    get() = "\u00B5"

fun SensorEvent.valuesToString(ctx: Context) =
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
                ("x*sin($THETA/2):${values[0]}\n" +
                        "y*sin($THETA/2):${values[1]}\n" +
                        "z*sin($THETA/2):${values[2]}\n" +
                        "cos($THETA/2):${values[3]}\n" +
                        ctx.resources.getString(R.string.sensor_accuracy) +
                        ": ${values[4]}rad").toSpanned()
            }
            Sensor.TYPE_GAME_ROTATION_VECTOR -> {
                ("x*sin($THETA/2):${values[0]}\n" +
                        "y*sin($THETA/2):${values[1]}\n" +
                        "z*sin($THETA/2):${values[2]}\n" +
                        "cos($THETA/2):${values[3]}\n").toSpanned()
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
                    append("x: ${values[0]}${MU}T")
                    append("\ny: ${values[1]}${MU}T")
                    append("\nz: ${values[2]}${MU}T")
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> {
                buildSpannedString {
                    append("x: ${values[0]}${MU}T")
                    append("\ny: ${values[1]}${MU}T")
                    append("\nz: ${values[2]}${MU}T")
                    append("\nx")
                    subscript { scale(0.75f) { append("b") } }
                    append(": ${values[3]}${MU}T")
                    append("\ny")
                    subscript { scale(0.75f) { append("b") } }
                    append(": ${values[4]}${MU}T")
                    append("\nz")
                    subscript { scale(0.75f) { append("b") } }
                    append(": ${values[5]}${MU}T")
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
