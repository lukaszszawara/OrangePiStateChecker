package com.example.orangepistate

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.biansemao.widget.ThermometerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class InformationTab(tabName: String) : Fragment() {
    val tabName = tabName
    private lateinit var button : Button
private lateinit var fab : FloatingActionButton
private lateinit var mainInfoText: TextView
private lateinit var temperatureView: ThermometerView
private lateinit var sharedPreferences: SharedPreferences
private lateinit var image : ImageFilterView
private var SHARED_NAME = "shared_prefs_orange"
private var SHARED_URL_ADDRESS = "url_address"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.informations_tab, container, false)
        button = view.findViewById(R.id.btn_anim)
        image = view.findViewById(R.id.image)
        temperatureView = view.findViewById(R.id.tv_thermometer)
        fab = view.findViewById(R.id.fab)
        mainInfoText = view.findViewById(R.id.main_info);
        sharedPreferences = requireContext().getSharedPreferences(SHARED_NAME, MODE_PRIVATE)
        if(tabName.equals(Const.RASPPERRY_PI)){
            image.setImageResource(R.drawable.rpilogo)
        }
        refreshTemperature()
        fab.setOnClickListener {
            showServerSettingDialog()
        }
        button.setOnClickListener {
            refreshTemperature()
        }
        return view
    }


      private fun refreshTemperature() {
          val queue = Volley.newRequestQueue(context)
          val url = getUrl()
          if(!url.isNullOrEmpty()) {
              val stringRequestq = StringRequest(
                  Request.Method.GET, url,
                  { response ->
                      val value = Gson().fromJson(response, HttpRequestTemp::class.java)
                      if(tabName.equals(Const.ORANGE_PI)) {
                          temperatureView.setValueAndStartAnim(value.temp / 1000F)
                          mainInfoText.setText(
                              value.sysInfo.replace("/dev/mmcblk2p1","\n/dev/mmcblk2p1")
                                  .replace("{ lukasz@orangepiplus2e\n","").replace("/media/82fe8fc9-188b-4352-a343-96463f90347c}",""))
                      }else if(tabName.equals(Const.RASPPERRY_PI)){
                          temperatureView.setValueAndStartAnim(value.temp / 1000f)
                          mainInfoText.setText(
                              value.sysInfo.replace("{ root@lukaszraspi\n",""))
                      }
                          Log.d("TAG", "Response is: ${response}")
                  },
                  {
                      Snackbar.make(temperatureView, "ERROR!! $it",
                          Snackbar.LENGTH_LONG).show()
                  })

              queue.add(stringRequestq)
          }else{
              view?.let {
                  Snackbar.make(
                      it, "Set Server adress!!",
                      Snackbar.LENGTH_LONG).show()
              }
          }
      }

      private fun showServerSettingDialog() {
          val builder: AlertDialog.Builder = AlertDialog.Builder(context)
          builder.setTitle("Warning!")

          val input = EditText(context)
          input.hint = "Enter server adress"
          val url = getUrl()
          if(!url.isNullOrEmpty()){
              input.setText(url)
          }
          input.inputType = InputType.TYPE_CLASS_TEXT
          builder.setView(input)

          builder.setPositiveButton("OK") { _, _ ->
              sharedPreferences.edit().putString(
                  SHARED_URL_ADDRESS+tabName,
                  input.text.toString()
              ).apply()
          }
          builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
          builder.show()
      }

      private fun getUrl(): String? {
          return sharedPreferences.getString(SHARED_URL_ADDRESS+tabName,"")
      }
}
