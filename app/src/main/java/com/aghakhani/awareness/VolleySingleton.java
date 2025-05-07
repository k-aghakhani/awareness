package com.aghakhani.awareness;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton instance;
    private RequestQueue requestQueue;

    // Private constructor
    private VolleySingleton(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    // Get instance
    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    // Get request queue
    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}