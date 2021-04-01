package com.ahmettekin.weatherexercise

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var locationManager: LocationManager
    lateinit var spinnerdekiSehirIsmi: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val spinnerAdapter =
            ArrayAdapter.createFromResource(this, R.array.sehirler, R.layout.spinner_tek_satir)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnSehirler.setTitle("Şehir Seç")
        spnSehirler.setPositiveButton("SEÇ")
        spnSehirler.adapter = spinnerAdapter

        spnSehirler.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (position == 0) {
                    if (ActivityCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            33
                        )
                    } else {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 50L, 10f
                        ) {
                            verileriGetir(it = it)
                        }
                    }
                } else {
                    verileriGetir(parent?.getItemAtPosition(position).toString())
                }
                spinnerdekiSehirIsmi = view as TextView
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 33) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 50L, 10f
            ) {
                verileriGetir(it = it)
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun verileriGetir(sehir: String? = null, it: Location? = null) {

        val url: String = if (it != null) {
            "https://api.openweathermap.org/data/2.5/weather?lat=${it.latitude}&lon=${it.longitude}&appid=42cdc93c083ccc6797e8c9643f5470d2&lang=tr&units=metric"
        } else {
            "https://api.openweathermap.org/data/2.5/weather?q=$sehir&appid=42cdc93c083ccc6797e8c9643f5470d2&lang=tr&units=metric"
        }
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
            spinnerdekiSehirIsmi.text = sehirAdi

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



