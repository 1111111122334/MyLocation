package com.example.location.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.location.R;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Récupérer le rôle depuis SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = prefs.getString("role", "Clients"); // Valeur par défaut : client

        // Références aux layouts
        LinearLayout layoutClient = findViewById(R.id.layoutClient);
        LinearLayout layoutAgent = findViewById(R.id.layoutAgent);

        // Afficher le bon layout selon le rôle
        if ("Agents".equals(role)) {
            layoutAgent.setVisibility(View.VISIBLE);
        } else {
            layoutClient.setVisibility(View.VISIBLE);
        }

// Gestion des boutons pour les agents
        Button btnGestionOffresAgent = findViewById(R.id.btnGestionOffresAgent);
        Button btnConsulterDemandesAgent = findViewById(R.id.btnConsulterDemandesAgent);
        Button btnGererProfilAgent = findViewById(R.id.btnGererProfilAgent);
        Button btnAjouterOffresAgent = findViewById(R.id.btnAjouterOffresAgent);

        btnGestionOffresAgent.setOnClickListener(v -> startActivity(new Intent(this, Offres.class)));
        btnConsulterDemandesAgent.setOnClickListener(v -> startActivity(new Intent(this, Demandes.class)));
        btnGererProfilAgent.setOnClickListener(v ->
                startActivity(new Intent(this, ProfilActivity.class))
        );
        btnAjouterOffresAgent.setOnClickListener(v -> startActivity(new Intent(this, Offre.class)));
        btnConsulterDemandesAgent.setOnClickListener(v -> startActivity(new Intent(this, Demandes.class)));
        Button btnGestionOffresClient = findViewById(R.id.btnGestionOffresClient);
        btnGestionOffresClient.setOnClickListener(v -> startActivity(new Intent(this, OffresClient.class)));

        // Déconnexion
        Button btnDeconnexion = findViewById(R.id.btnDeconnexion);
        btnDeconnexion.setOnClickListener(v -> {
            startActivity(new Intent(this, Authentification.class));
            finish(); // Fermer le Dashboard
        });
    }
}