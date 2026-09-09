package com.fakestore.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Escenario "Login: Sin Conexión":
 * Si el dispositivo está en modo avión o sin señal, se debe detener la
 * ejecución ANTES del consumo de la API (evitar loaders infinitos).
 */
public class NetworkUtils {

    private NetworkUtils() {
    }

    @SuppressWarnings("deprecation")
    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.net.Network network = cm.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                     capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                     capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
    }
}
