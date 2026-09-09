package com.fakestore.app.session;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.fakestore.app.ui.LoginActivity;

/**
 * Escenario "Logout: Limpieza Profunda" (sección 4).
 * 1. Muestra un micro-loader de 0.5s.
 * 2. Elimina token de SharedPreferences, destruye historial de pantallas,
 *    limpia carrito local e ID/Rol.
 * 3. Redirige a Login reiniciando la pila de navegación por completo.
 */
public class LogoutHelper {

    private static final long MICRO_LOADER_DURATION_MS = 500L;

    private LogoutHelper() {
    }

    public static void performLogout(Activity activity) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Cerrando sesión...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(activity);
            sessionManager.clearSessionDeep(activity);

            progressDialog.dismiss();

            Intent intent = new Intent(activity, LoginActivity.class);
            // Destruye el historial de pantallas: reinicia la pila por completo.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        }, MICRO_LOADER_DURATION_MS);
    }
}
