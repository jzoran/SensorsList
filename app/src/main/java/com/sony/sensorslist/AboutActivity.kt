package com.sony.sensorslist

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.ScrollingMovementMethod
import android.text.style.StyleSpan
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        title = resources.getText(R.string.action_about)
        aboutText.movementMethod = ScrollingMovementMethod()

        val licenseText = resources.assets.open("LICENSE")
                .bufferedReader()
                .use {
                    it.readText()
                }

        val appNameStr = resources.getString(R.string.app_name)
        val licenseStr = resources.getString(R.string.license_title)

        aboutText.text = SpannableStringBuilder()
                .append(appNameStr, StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                .append("\n")
                .append(BuildConfig.VERSION_NAME)
                .append("\n\n")
                .append(licenseStr, StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                .append("\n")
                .append(licenseText)
                .append("\n")
    }
}
