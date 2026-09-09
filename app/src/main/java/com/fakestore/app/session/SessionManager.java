package com.fakestore.app.session;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Maneja la persistencia segura de la sesión (token, id, rol) usando
 * SharedPreferences, tal como indica la sección 4 - "Logout: Limpieza Profunda".
 *
 * IMPORTANTE: en ningún método de esta clase se acepta ni se guarda un
 * parámetro "password". Solo token + id + rol.
 */
public class SessionManager {

    private static final String PREFS_NAME = "fakestore_session_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "user_role";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, int userId, Role role) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_ROLE, role.name())
                .apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public Role getRole() {
        String stored = prefs.getString(KEY_ROLE, null);
        return stored != null ? Role.valueOf(stored) : null;
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    /**
     * Logout: Limpieza Profunda.
     * Elimina token de SharedPreferences, limpia carrito local e ID/Rol.
     * La destrucción del historial de pantallas y el bloqueo de "Atrás"
     * se resuelven a nivel de navegación (ver LogoutHelper).
     */
    public void clearSessionDeep(Context context) {
        prefs.edit().clear().apply();
        CartLocalStorage.clear(context);
    }

    public enum Role {
        ADMIN,
        AUDITOR,
        CLIENT;

        /**
         * Sección 3: el rol se decide en código a partir del ID del usuario
         * devuelto tras un login exitoso (200 OK).
         *   IDs 1 y 2  -> Administrador
         *   ID 3       -> Auditor
         *   Resto      -> Cliente
         */
        public static Role fromUserId(int userId) {
            if (userId == 1 || userId == 2) {
                return ADMIN;
            } else if (userId == 3) {
                return AUDITOR;
            } else {
                return CLIENT;
            }
        }
    }
}
