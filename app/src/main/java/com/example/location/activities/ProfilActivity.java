package com.example.location.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.location.R;

public class ProfilActivity extends AppCompatActivity {

    EditText editNom;
    TextView textEmail, textRole;
    Button btnEnregistrer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        editNom = findViewById(R.id.editNom);
        textEmail = findViewById(R.id.textEmail);
        textRole = findViewById(R.id.textRole);
        btnEnregistrer = findViewById(R.id.btnEnregistrer);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String nom = prefs.getString("nom", "Nom inconnu");
        String email = prefs.getString("email", "email@exemple.com");
        String role = prefs.getString("role", "Clients");

        editNom.setText(nom);
        textEmail.setText(email);
        textRole.setText(role);

        btnEnregistrer.setOnClickListener(v -> {
            String nouveauNom = editNom.getText().toString().trim();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nom", nouveauNom);
            editor.apply();

            Toast.makeText(this, "Profil mis à jour", Toast.LENGTH_SHORT).show();
        });
    }
}
