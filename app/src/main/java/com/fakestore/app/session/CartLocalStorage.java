package com.fakestore.app.session;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Carrito local del rol Cliente (sección 3). Se limpia por completo
 * durante el logout ("Limpieza Profunda", sección 4).
 */
public class CartLocalStorage {

    private static final String CART_PREFS_NAME = "fakestore_cart_prefs";

    private CartLocalStorage() {
    }

    public static void clear(Context context) {
        SharedPreferences cartPrefs =
                context.getSharedPreferences(CART_PREFS_NAME, Context.MODE_PRIVATE);
        cartPrefs.edit().clear().apply();
    }
}
