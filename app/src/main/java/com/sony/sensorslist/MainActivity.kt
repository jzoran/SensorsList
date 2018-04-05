package com.sony.sensorslist

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.widget.Toast
import androidx.core.view.forEach


class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        SensorEventListener {

    private lateinit var sensors: Sensors
    private var listening: Boolean = false
    private val NOT_CHECKED: Int = -1

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        val indexChecked = getChecked()
        if (indexChecked != NOT_CHECKED) {
            contentView.text = sensors.getSensorInfoAsString(indexChecked)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        var str = ""
        event?.values?.forEach {
            str += "\n$it"
        }
        sensorValues.text = str
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            sensors.stop(this)
            if (listening) {
                listening = false
                sensorValues.text = resources.getText(R.string.values_default)
                fab.setImageDrawable(resources.getDrawable(android.R.drawable.button_onoff_indicator_off, null))
                fab.backgroundTintList = resources.getColorStateList(R.color.colorPrimary, null)

            } else {
                val id = getChecked()
                if (id > NOT_CHECKED) {
                    sensors.listen(sensors.getSensor(id)!!, this)
                    listening = true
                    fab.setImageDrawable(resources.getDrawable(android.R.drawable.button_onoff_indicator_on, null))
                    fab.backgroundTintList = resources.getColorStateList(R.color.colorAccent, null)
                }
            }
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        sensors = Sensors()
        for ((i, sensor) in sensors.getSensors(this).withIndex()) {
            nav_view.menu.add(R.menu.activity_main_drawer, i, Menu.NONE, sensor.name)
        }
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(this, "TODO: implement Settings", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        sensors.stop(this)
        sensorValues.text = resources.getText(R.string.values_default)
        nav_view.menu.forEach {
            it.isChecked = false
        }
        nav_view.menu.getItem(item.itemId).isChecked = true
        contentView.text = sensors.getSensorInfoAsString(item.itemId)
        title = sensors.getSensorName(item.itemId)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getChecked(): Int {
        nav_view.menu.forEach {
            if(it.isChecked) return it.itemId
        }
        return -1
    }
}
