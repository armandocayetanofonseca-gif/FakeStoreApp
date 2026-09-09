package com.fakestore.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * Cuerpo de la petición POST /auth/login.
 * El password SÍ se envía en la petición (es indispensable para autenticar),
 * pero jamás se persiste ni se guarda en un campo de estado/modelo de sesión;
 * vive únicamente como parámetro local dentro del formulario de login.
 */
public class LoginRequest {

    @SerializedName("username")
    private final String username;

    @SerializedName("password")
    private final String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
