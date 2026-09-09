package com.fakestore.app.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fakestore.app.R;
import com.fakestore.app.model.LoginRequest;
import com.fakestore.app.model.LoginResponse;
import com.fakestore.app.model.User;
import com.fakestore.app.network.ApiClient;
import com.fakestore.app.session.SessionManager;
import com.fakestore.app.util.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Pantalla de Login.
 * Implementa los escenarios US01/US02 descritos en la sección 4:
 *  - Credenciales inválidas (401)
 *  - Sin conexión (bloqueo previo a la llamada API)
 *  - Bloqueo de retroceso tras un logout (ver onBackPressed)
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvError;
    private Button btnLogin;

    private SessionManager sessionManager;

    /** Color de alerta definido en la sección 1: #CF6679 */
    private static final int COLOR_ERROR = Color.parseColor("#CF6679");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        btnLogin = findViewById(R.id.btnLogin);

        tvError.setTextColor(COLOR_ERROR);
        tvError.setVisibility(TextView.INVISIBLE);

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        // Escenario "Login: Sin Conexión" -> detener ANTES de llamar a la API.
        if (!NetworkUtils.isConnected(this)) {
            showNoConnectionModal();
            return;
        }

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        hideError();
        btnLogin.setEnabled(false);

        LoginRequest request = new LoginRequest(username, password);
        ApiClient.getApiService().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    onLoginSuccess(response.body().getToken(), username);
                } else if (response.code() == 401) {
                    // No se borra el formulario, solo se muestra la alerta.
                    showInvalidCredentialsError();
                } else {
                    showGenericError();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                showGenericError();
            }
        });
    }

    /**
     * Tras un 200 OK, se recupera la lista de usuarios para localizar el id
     * correspondiente al username autenticado y así decodificar el rol
     * (sección 3). El password NUNCA se solicita ni se guarda en este flujo.
     */
    private void onLoginSuccess(String token, String username) {
        ApiClient.getApiService().getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showGenericError();
                    return;
                }

                User matchedUser = null;
                for (User u : response.body()) {
                    if (u.getUsername().equalsIgnoreCase(username)) {
                        matchedUser = u;
                        break;
                    }
                }

                if (matchedUser == null) {
                    showGenericError();
                    return;
                }

                SessionManager.Role role = SessionManager.Role.fromUserId(matchedUser.getId());
                sessionManager.saveSession(token, matchedUser.getId(), role);
                navigateByRole(role);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                showGenericError();
            }
        });
    }

    private void navigateByRole(SessionManager.Role role) {
        // Las 3 pantallas por rol se unificaron en una sola "Mi Perfil";
        // ProfileActivity lee el rol guardado en SessionManager y ajusta
        // únicamente la insignia (ícono/etiqueta/color de acento).
        Intent intent = new Intent(this, ProfileActivity.class);
        // Reinicia la pila de navegación por completo (sección 4).
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showInvalidCredentialsError() {
        tvError.setText(R.string.error_invalid_credentials);
        tvError.setVisibility(TextView.VISIBLE);
        // El formulario NO se limpia, permitiendo al usuario corregir.
    }

    private void showGenericError() {
        tvError.setText(R.string.error_generic);
        tvError.setVisibility(TextView.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(TextView.INVISIBLE);
    }

    private void showNoConnectionModal() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_no_connection_title))
                .setMessage(getString(R.string.dialog_no_connection_message))
                .setPositiveButton(R.string.dialog_accept, (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
        // No se invoca ninguna llamada de red ni loader: se detiene aquí mismo.
    }

    /**
     * Bloqueo de Retroceso (sección 4): si el usuario llega a Login tras un
     * logout, no debe poder retroceder al catálogo cacheado. Como la pila ya
     * fue reiniciada por completo (FLAG_ACTIVITY_CLEAR_TASK), el back nativo
     * en esta pantalla solo puede llevar a segundo plano/salir de la app.
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
