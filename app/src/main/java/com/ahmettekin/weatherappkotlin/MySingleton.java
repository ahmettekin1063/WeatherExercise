package com.ahmettekin.weatherappkotlin;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {

    private static RequestQueue requestQueue = null;

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    private MySingleton(Context context) {
        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(context);
        }
    }


}
