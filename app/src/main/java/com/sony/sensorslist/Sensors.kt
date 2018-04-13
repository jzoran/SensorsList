package com.sony.sensorslist

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import androidx.core.text.inSpans
import androidx.core.text.scale
import androidx.core.text.toSpannable

class Sensors {
    private var sensors: List<Sensor> = emptyList()
    private var manager: SensorManager

    constructor(context: Context) {
        manager = context.getSystemService(SensorManager::class.java)
        sensors = manager.getSensorList(Sensor.TYPE_ALL)
    }

    val names: List<String>
        get() = sensors.map { sensor -> sensor.name  }

    companion object {
        fun accuracyToString(accuracy: Int) = when(accuracy) {
            -1 -> "<not connected>"
            0 -> "unreliable"
            1 -> "low"
            2 -> "medium"
            3 -> "high"
            else -> "n/a"
        }
    }

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
    get() = Sensors.accuracyToString(accuracy)

val SensorEvent.valuesToString: Spannable
    get() = when(sensor.type) {
        Sensor.TYPE_ACCELEROMETER,
        Sensor.TYPE_GRAVITY,
        Sensor.TYPE_LINEAR_ACCELERATION -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                SpannableStringBuilder()
                        .append("x: ${values[0]}m/s")
                        .superscript { scale(0.75f) { append("2") } }
                        .append("\ny: ${values[1]}m/s")
                        .superscript { scale(0.75f) { append("2") } }
                        .append("\nz: ${values[2]}m/s")
                        .superscript { scale(0.75f) { append("2") } }
            }
        }
        Sensor.TYPE_ACCELEROMETER_UNCALIBRATED -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                SpannableStringBuilder()
                        .append("x: ${values[0]}m/s")
                        .superscript { scale(0.75f) { append("2") } }
                        .append("\ny: ${values[1]}m/s")
                        .superscript { scale(0.75f) { append("2") } }
                        .append("\nz: ${values[2]}m/s")
                        .superscript { scale(0.75f) { append("2") } }
                        .append("\nx")
                        .subscript { scale(0.75f) { append("bias")} }
                        .append(": ${values[3]}m/s")
                        .superscript { scale(0.75f) { append("2") } }
                        .append("\ny")
                        .subscript { scale(0.75f) { append("bias")} }
                        .append(": ${values[4]}m/s")
                        .superscript { scale(0.75f) { append("2") } }
                        .append("\nz")
                        .subscript { scale(0.75f) { append("bias")} }
                        .append(": ${values[5]}m/s")
                        .superscript { scale(0.75f) { append("2") } }
            }
        }
        Sensor.TYPE_AMBIENT_TEMPERATURE -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                SpannableStringBuilder()
                        .append("${values[0]}")
                        .superscript { scale(0.75f) { append("o") } }
                        .append("C")
            }
        }
        Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
        Sensor.TYPE_ROTATION_VECTOR -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                var str = "x*sin(\u03B8/2):${values[0]}\n"
                str += "y*sin(\u03B8/2):${values[1]}\n"
                str += "z*sin(\u03B8/2):${values[2]}\n"
                str += "cos(\u03B8/2):${values[3]}\n"
                str += "accuracy: ${values[4]}rad"
                str.toSpannable()
            }
        }
        Sensor.TYPE_GAME_ROTATION_VECTOR -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                var str = "x*sin(\u03B8/2):${values[0]}\n"
                str += "y*sin(\u03B8/2):${values[1]}\n"
                str += "z*sin(\u03B8/2):${values[2]}\n"
                str += "cos(\u03B8/2):${values[3]}\n"
                str.toSpannable()
            }
        }
        Sensor.TYPE_GYROSCOPE -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                SpannableStringBuilder()
                        .append("x: ${values[0]}rad/s")
                        .append("\ny: ${values[1]}rad/s")
                        .append("\nz: ${values[2]}rad/s")
            }
        }
        Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                SpannableStringBuilder()
                        .append("x: ${values[0]}rad/s")
                        .append("\ny: ${values[1]}rad/s")
                        .append("\nz: ${values[2]}rad/s")
                        .append("\nx")
                        .subscript { scale(0.75f) { append("ed") } }
                        .append(": ${values[3]}rad/s")
                        .append("\ny")
                        .subscript { scale(0.75f) { append("ed") } }
                        .append(": ${values[4]}rad/s")
                        .append("\nz")
                        .subscript { scale(0.75f) { append("ed") } }
                        .append(": ${values[5]}rad/s")
            }
        }
        Sensor.TYPE_HEART_BEAT -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                "confidence [0.0 - 1.0]: ${values[0]}".toSpannable()
            }
        }
        Sensor.TYPE_LIGHT -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                "${values[0]}lux".toSpannable()
            }
        }
        Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                "[0.0 | 1.0]: ${values[0]}".toSpannable()
            }
        }
        Sensor.TYPE_MAGNETIC_FIELD -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                SpannableStringBuilder()
                        .append("x: ${values[0]}\u00B5T")
                        .append("\ny: ${values[1]}\u00B5T")
                        .append("\nz: ${values[2]}\u00B5T")
            }
        }
        Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                SpannableStringBuilder()
                        .append("x: ${values[0]}\u00B5T")
                        .append("\ny: ${values[1]}\u00B5T")
                        .append("\nz: ${values[2]}\u00B5T")
                        .append("\nx")
                        .subscript{ scale(0.75f) { append("b") } }
                        .append(": ${values[3]}\u00B5T")
                        .append("\ny")
                        .subscript{ scale(0.75f) { append("b") } }
                        .append(": ${values[4]}\u00B5T")
                        .append("\nz")
                        .subscript{ scale(0.75f) { append("b") } }
                        .append(": ${values[5]}\u00B5T")
            }
        }
        Sensor.TYPE_MOTION_DETECT -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                "${values[0]}".toSpannable()
            }
        }
        Sensor.TYPE_ORIENTATION -> {
            var str = "deprecated:\n"
            values?.forEach { str += "$it\n" }
            str.toSpannable()
        }
        Sensor.TYPE_POSE_6DOF -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                var str = "(${values[0]}, ${values[1]}, ${values[2]},  ${values[3]}\n" +
                        "${values[4]}, ${values[5]}, ${values[6]}\n" +
                        "${values[7]}, ${values[8]}, ${values[9]},  ${values[10]}\n" +
                        "${values[11]}, ${values[12]}, ${values[13]}\n" +
                        "${values[14]})"
                str.toSpannable()
            }
        }
        Sensor.TYPE_PRESSURE -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                "${values[0]}hPa".toSpannable()
            }
        }
        Sensor.TYPE_PROXIMITY -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                "${values[0]}cm".toSpannable()
            }
        }
        Sensor.TYPE_RELATIVE_HUMIDITY -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                "${values[0]}%".toSpannable()
            }
        }
        Sensor.TYPE_STATIONARY_DETECT -> {
            if (values == null) {
                "n/a".toSpannable()
            } else {
                "[1.0] ${values[0]}".toSpannable()
            }
        }
        Sensor.TYPE_HEART_RATE,
        Sensor.TYPE_STEP_COUNTER,
        Sensor.TYPE_SIGNIFICANT_MOTION -> {
                var str = ""
                values?.forEach { str += "$it\n" }
                str.toSpannable()
            }
        else -> SpannableStringBuilder().append("n/a")
    }

inline fun SpannableStringBuilder.superscript(builderAction: SpannableStringBuilder.() -> Unit) =
        inSpans(SuperscriptSpan(), builderAction = builderAction)

inline fun SpannableStringBuilder.subscript(builderAction: SpannableStringBuilder.() -> Unit) =
        inSpans(SubscriptSpan(), builderAction = builderAction)
