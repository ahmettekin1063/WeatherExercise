package com.ahmettekin.weatherappkotlin

import android.app.PendingIntent.getActivity
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class MyVolleyRequestQueue private constructor(private var context: Context) {
    private var requestQueue: RequestQueue? = null

    fun <T> addToRequestQueue(req: Request<T>?) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.applicationContext)
        }
        requestQueue!!.add(req)
    }

    companion object {

        private var instance: MyVolleyRequestQueue? = null

        @Synchronized
        fun getInstance(context: Context): MyVolleyRequestQueue? {
            if (instance == null) {
                instance = MyVolleyRequestQueue(context)
            }
            return instance
        }
    }
}