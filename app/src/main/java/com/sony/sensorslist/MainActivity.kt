package com.sony.sensorslist

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.core.view.GravityCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val MENU_ITEM_NOT_CHECKED: Int = -1
private const val MENU_ITEM_CHECKED_ID = "itemId"
private const val LISTENING_SENSOR = "listening"

class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        SensorEventListener {

    private lateinit var sensors: Sensors
    private var listening: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this,
                drawer_layout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        sensors = Sensors(this)
        for ((i, sensorName) in sensors.names.withIndex()) {
            nav_view.menu.add(R.menu.activity_main_drawer, i, Menu.NONE, sensorName)
        }
        nav_view.setNavigationItemSelectedListener(this)

        fab.setOnClickListener {
            if (listening) {
                stopListenerUpdate()
            } else {
                listenUpdate(nav_view.menu.checkedId)
            }
        }

        if (savedInstanceState != null) {
            listening = savedInstanceState.getBoolean(LISTENING_SENSOR)
            val itemId = savedInstanceState.getInt(MENU_ITEM_CHECKED_ID)
            if (itemId != MENU_ITEM_NOT_CHECKED) {
                selectMenuItem(itemId)
                if (listening) {
                    listenUpdate(itemId)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.run {
            putInt(MENU_ITEM_CHECKED_ID, nav_view.menu.checkedId)
            putBoolean(LISTENING_SENSOR, listening)
        }
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
            R.id.action_oss -> {
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
                OssLicensesMenuActivity.setActivityTitle(getString(R.string.action_oss))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (listening) {
            stopListenerUpdate()
        }
        sensorValues.text = resources.getText(R.string.values_default)
        nav_view.menu.forEach {
            it.isChecked = false
        }
        selectMenuItem(item.itemId)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        val indexChecked = nav_view.menu.checkedId
        if (indexChecked != MENU_ITEM_NOT_CHECKED &&
                sensors.names[indexChecked] == sensor?.name) {
            val str = sensors.getSensorInfoAsString(indexChecked) +
                    "\n" + resources.getString(R.string.sensor_accuracy).capitalize() +
                    ": ${stringAccuracy(this, accuracy)}"
            contentView.text = str
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            sensorValues.text = event.stringValues(this)
        } else {
            sensorValues.text = resources.getString(R.string.values_unavailable)
        }
    }

    private fun selectMenuItem(itemId: Int) {
        nav_view.menu[itemId].isChecked = true
        contentView.text = sensors.getSensorInfoAsString(itemId)
        title = sensors.names[itemId]
    }

    private fun listenUpdate(id: Int) {
        if (id == MENU_ITEM_NOT_CHECKED) return

        sensors.stop(this)
        sensors.listen(id, this)
        listening = true
        fab.run {
            setImageDrawable(resources.getDrawable(android.R.drawable.button_onoff_indicator_on, null))
            backgroundTintList = resources.getColorStateList(R.color.colorAccent, null)
        }
    }

    private fun stopListenerUpdate() {
        sensors.stop(this)
        listening = false
        sensorValues.text = resources.getText(R.string.values_default)
        fab.run {
            setImageDrawable(resources.getDrawable(android.R.drawable.button_onoff_indicator_off, null))
            backgroundTintList = resources.getColorStateList(R.color.colorPrimary, null)
        }
    }
}

private inline val Menu.checkedId: Int
        get() {
            forEach {
                if (it.isChecked) return it.itemId
            }
            return MENU_ITEM_NOT_CHECKED
        }
