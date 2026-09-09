package com.fakestore.app.ui;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.fakestore.app.R;
import com.fakestore.app.model.User;
import com.fakestore.app.network.ApiClient;
import com.fakestore.app.session.LogoutHelper;
import com.fakestore.app.session.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Pantalla única "Mi Perfil", compartida por los 3 roles (Administrador,
 * Auditor y Cliente). El layout es idéntico para todos; lo único que
 * cambia según el rol es la insignia (ícono, etiqueta y color de acento).
 *
 * REGLA DE NEGOCIO: el campo password nunca se solicita ni se muestra aquí
 * (User.java no lo define, ver modelo).
 */
public class ProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private TextView tvAvatarInitial;
    private TextView tvRoleBadgeIcon;
    private TextView tvFullName;
    private View pillRole;
    private TextView tvRolePillIcon;
    private TextView tvRolePillLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);

        tvAvatarInitial = findViewById(R.id.tvAvatarInitial);
        tvRoleBadgeIcon = findViewById(R.id.tvRoleBadgeIcon);
        tvFullName = findViewById(R.id.tvFullName);
        pillRole = findViewById(R.id.pillRole);
        tvRolePillIcon = findViewById(R.id.tvRolePillIcon);
        tvRolePillLabel = findViewById(R.id.tvRolePillLabel);

        ImageButton btnLogoutIcon = findViewById(R.id.btnLogoutIcon);
        View btnLogout = findViewById(R.id.btnLogout);
        btnLogoutIcon.setOnClickListener(v -> LogoutHelper.performLogout(this));
        btnLogout.setOnClickListener(v -> LogoutHelper.performLogout(this));

        applyRoleStyling(sessionManager.getRole());
        loadOwnProfile();
    }

    /** Ajusta ícono, etiqueta y color de acento según el rol (secc. 3 del spec). */
    private void applyRoleStyling(SessionManager.Role role) {
        int accentColor;
        int pillBgColor;
        String emoji;
        String label;

        if (role == SessionManager.Role.ADMIN) {
            accentColor = ContextCompat.getColor(this, R.color.role_admin_accent);
            pillBgColor = ContextCompat.getColor(this, R.color.role_admin_pill_bg);
            emoji = "👑";
            label = "Administrador";
        } else if (role == SessionManager.Role.AUDITOR) {
            accentColor = ContextCompat.getColor(this, R.color.role_auditor_accent);
            pillBgColor = ContextCompat.getColor(this, R.color.role_auditor_pill_bg);
            emoji = "⭐";
            label = "Auditor";
        } else {
            accentColor = ContextCompat.getColor(this, R.color.role_client_accent);
            pillBgColor = ContextCompat.getColor(this, R.color.role_client_pill_bg);
            emoji = "🛒";
            label = "Cliente";
        }

        // Fondo del círculo del avatar tintado con el color de acento (tenue).
        tintDrawableBackground(tvAvatarInitial, pillBgColor);
        tintDrawableBackground(pillRole, pillBgColor);

        tvAvatarInitial.setTextColor(accentColor);
        tvRolePillLabel.setTextColor(accentColor);

        tvRoleBadgeIcon.setText(emoji);
        tvRolePillIcon.setText(emoji);
        tvRolePillLabel.setText(label);
    }

    private void tintDrawableBackground(View view, int color) {
        GradientDrawable bg = (GradientDrawable) view.getBackground().mutate();
        bg.setColor(color);
        view.setBackground(bg);
    }

    /** El usuario ve únicamente SU propio perfil, sin importar el rol. */
    private void loadOwnProfile() {
        int currentUserId = sessionManager.getUserId();

        ApiClient.getApiService().getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }
                for (User user : response.body()) {
                    if (user.getId() == currentUserId) {
                        bindProfile(user);
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Alerta de error visual delegada a la capa de vista (color #CF6679).
            }
        });
    }

    private void bindProfile(User user) {
        String fullName = user.getName().getFullName();
        tvFullName.setText(fullName);
        tvAvatarInitial.setText(String.valueOf(Character.toUpperCase(fullName.charAt(0))));

        bindRow(R.id.rowUserId, "🪪", "ID de Usuario", "#" + user.getId());
        bindRow(R.id.rowUsername, "👤", "Usuario", user.getUsername());
        bindRow(R.id.rowEmail, "✉️", "Correo Electrónico", user.getEmail());
        bindRow(R.id.rowPhone, "📞", "Teléfono", user.getPhone());
        bindRow(R.id.rowAddress, "📍", "Dirección", user.getAddress().getFullAddress());
    }

    private void bindRow(int includeId, String icon, String label, String value) {
        View row = findViewById(includeId);
        ((TextView) row.findViewById(R.id.rowIcon)).setText(icon);
        ((TextView) row.findViewById(R.id.rowLabel)).setText(label);
        ((TextView) row.findViewById(R.id.rowValue)).setText(value);
    }
}
