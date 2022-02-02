package com.example.orangepistate

import android.app.AlertDialog
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.biansemao.widget.ThermometerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var button : Button
    private lateinit var fab : FloatingActionButton
    private lateinit var temperatureView: ThermometerView
    private lateinit var sharedPreferences: SharedPreferences
    private var SHARED_NAME = "shared_prefs_orange"
    private var SHARED_URL_ADDRESS = "url_address"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.btn_anim)
        temperatureView = findViewById(R.id.tv_thermometer)
        fab = findViewById(R.id.fab)
        sharedPreferences = getSharedPreferences(SHARED_NAME, MODE_PRIVATE)
        refreshTemperature()
        fab.setOnClickListener {
            showServerSettingDialog()
        }
        button.setOnClickListener {
            refreshTemperature()
        }
    }

    private fun refreshTemperature() {
        val queue = Volley.newRequestQueue(this)
        val url = getUrl()
        if(!url.isNullOrEmpty()) {
            val stringRequestq = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val value = Gson().fromJson(response, HttpRequestTemp::class.java)
                    temperatureView.setValueAndStartAnim(value.temp / 1000F)
                    Log.d("TAG", "Response is: ${value.temp / 1000F}")
                },
                {
                    Snackbar.make(temperatureView, "ERROR!! $it",
                        Snackbar.LENGTH_LONG).show()
                })

            queue.add(stringRequestq)
        }else{
        showServerSettingDialog()
        }
    }

    private fun showServerSettingDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Warning!")

        val input = EditText(this)
        input.hint = "Enter server adress"
        val url = getUrl()
        if(!url.isNullOrEmpty()){
            input.setText(url)
        }
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            sharedPreferences.edit().putString(
                SHARED_URL_ADDRESS,
                input.text.toString()
            ).apply()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun getUrl(): String? {
        return sharedPreferences.getString(SHARED_URL_ADDRESS,"")
    }
}