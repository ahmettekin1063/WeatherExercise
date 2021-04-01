package com.ahmettekin.weatherappkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinnerAdapter= ArrayAdapter.createFromResource(this,R.array.sehirler,R.layout.spinner_tek_satir)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnSehirler.setTitle("Şehir Seç")
        spnSehirler.setPositiveButton("SEÇ")
        spnSehirler.adapter= spinnerAdapter

        spnSehirler.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                verileriGetir(parent?.getItemAtPosition(position).toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        verileriGetir("Hatay")
    }

    private fun verileriGetir(sehir: String) {
        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=$sehir&appid=42cdc93c083ccc6797e8c9643f5470d2&lang=tr&units=metric"

        val havaDurumuObjeRequest = JsonObjectRequest(Request.Method.GET, url, null, {
            val main = it.getJSONObject("main")
            val sicaklik = main.getInt("temp")
            val sehirAdi = it.getString("name")
            val weather = it.getJSONArray("weather")
            val desc = weather.getJSONObject(0).getString("description")
            val icon = weather.getJSONObject(0).getString("icon")


            tvAciklama.text = desc
            tvSicaklik.text = sicaklik.toString()
            tvTarih.text = tarihYazdir()

            Picasso.get().load(HEAD_OF_ICON_PATH + icon + END_OF_ICON_PATH).into(imgHavaDurumu)
            rootLayout.setBackgroundByTime(icon.time)

        }, {
            
        })
        MyVolleyRequestQueue.getInstance(this)?.addToRequestQueue(havaDurumuObjeRequest)
    }

    companion object {
        const val HEAD_OF_ICON_PATH = "https://openweathermap.org/img/wn/"
        const val END_OF_ICON_PATH = "@2x.png"
    }

    private fun ConstraintLayout.setBackgroundByTime(time: Char) {
        if (time == 'n') this.setBackgroundResource(R.drawable.gece)
        else this.setBackgroundResource(R.drawable.bg)
    }

    private fun tarihYazdir(): String {

        val takvim = Calendar.getInstance().time
        val formatlayici = SimpleDateFormat("dd-MM-yyyy", Locale("tr"))
        val tarih = formatlayici.format(takvim)

        return tarih
    }

    private val String.time: Char
        get() = this.last()
}



