package com.sony.sensorslist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import androidx.core.text.bold
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_about.view.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        title = resources.getText(R.string.action_about)

        versionCard.textViewVersion.text = SpannableStringBuilder()
                .bold { append(resources.getString(R.string.app_name)) }
                .append("\n")
                .append(BuildConfig.VERSION_NAME)

        val licenseText = resources.assets.open("LICENSE.md")
                .bufferedReader()
                .use {
                    it.readText()
                }
        licenseCard.textViewLicense.movementMethod = ScrollingMovementMethod()
        licenseCard.textViewLicense.text = SpannableStringBuilder()
                .bold { append(resources.getString(R.string.license_title)) }
                .append("\n")
                .append(licenseText)
                .append("\n")
    }
}
