package com.sony.sensorslist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import androidx.core.text.bold
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        title = resources.getText(R.string.action_about)
        aboutText.movementMethod = ScrollingMovementMethod()

        val licenseText = resources.assets.open("LICENSE.md")
                .bufferedReader()
                .use {
                    it.readText()
                }

        aboutText.text = SpannableStringBuilder()
                .bold { append(resources.getString(R.string.app_name)) }
                .append("\n")
                .append(BuildConfig.VERSION_NAME)
                .append("\n\n")
                .bold { append(resources.getString(R.string.license_title)) }
                .append("\n")
                .append(licenseText)
                .append("\n")
    }
}
