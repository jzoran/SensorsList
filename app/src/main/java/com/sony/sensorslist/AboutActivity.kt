package com.sony.sensorslist

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_about.view.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        title = resources.getText(R.string.action_about)

        versionCard.textViewVersion.text = buildSpannedString {
            bold { append(resources.getString(R.string.app_name)) }
            append("\n")
            append(BuildConfig.VERSION_NAME)
        }

        val licenseText = resources.assets.open("LICENSE.md")
                .bufferedReader()
                .use {
                    it.readText()
                }
        licenseCard.textViewLicense.run {
            movementMethod = ScrollingMovementMethod()
            text = buildSpannedString {
                bold { append(resources.getString(R.string.license_title)) }
                append("\n")
                append(licenseText)
                append("\n")
            }
        }
    }
}
