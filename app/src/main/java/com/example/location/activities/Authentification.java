package com.example.location.activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.location.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import android.content.SharedPreferences;


public class Authentification extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout layoutLogin, layoutRegister;
    private Button btnAuth, btnCreateAccount, btnConfirm, btnCancel;
    private EditText etLogin, etPassword, etNom, etPrenom, etPays, etAdresse, etPhone, etEmail, etPwd;
    private Spinner spinnerRole;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authentification);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialisation des vues
        layoutLogin = findViewById(R.id.Layout1);
        layoutRegister = findViewById(R.id.Layout2);
        btnAuth = findViewById(R.id.authButton);
        btnCreateAccount = findViewById(R.id.acchButton);
        btnConfirm = findViewById(R.id.confirmeButton);
        btnCancel = findViewById(R.id.annulerButton);
        spinnerRole = findViewById(R.id.spinnerRole);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        etNom = findViewById(R.id.nomEditText);
        etPrenom = findViewById(R.id.prenomEditText);
        etPays = findViewById(R.id.nysEditText);
        etAdresse = findViewById(R.id.villeEditText);
        etPhone = findViewById(R.id.phoneEditText);
        etEmail = findViewById(R.id.nemsEditText);
        etPwd = findViewById(R.id.pwdEditText);

        // Afficher le login par défaut
        layoutLogin.setVisibility(View.VISIBLE);
        layoutRegister.setVisibility(View.GONE);

        // Spinner pour sélectionner le rôle
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        btnAuth.setOnClickListener(this);
        btnCreateAccount.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.authButton) {
            loginUser();
        } else if (view.getId() == R.id.acchButton) {
            layoutLogin.setVisibility(View.GONE);
            layoutRegister.setVisibility(View.VISIBLE);
        } else if (view.getId() == R.id.confirmeButton) {
            registerUser();
        } else if (view.getId() == R.id.annulerButton) {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutRegister.setVisibility(View.GONE);
        }
    }

    private void loginUser() {
        String email = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("DEBUG_EMAIL", "Email saisi : '" + email + "'");


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            // Vérifier dans Firestore si l'utilisateur est Agent ou Client
                            db.collection("Agents").document(userId).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            startDashboard("Agents");
                                        } else {
                                            db.collection("Clients").document(userId).get()
                                                    .addOnSuccessListener(doc -> {
                                                        if (doc.exists()) {
                                                            startDashboard("Clients");
                                                        } else {
                                                            Toast.makeText(Authentification.this, "Rôle non trouvé.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(Authentification.this, "Erreur d'authentification.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Fonction pour lancer le bon dashboard avec le rôle
    private void startDashboard(String role) {
        // Stocker le rôle dans SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("role", role);
        editor.apply(); // Appliquer les modifications

        Intent intent = new Intent(Authentification.this, DashboardActivity.class);
        intent.putExtra("USER_ROLE", role);
        startActivity(intent);
        finish();
    }



    private void registerUser() {
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String pays = etPays.getText().toString().trim();
        String adresse = etAdresse.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPwd.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

        if (nom.isEmpty() || prenom.isEmpty() || pays.isEmpty() || adresse.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Création de l'utilisateur dans Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            // Création du document dans Firestore
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("nom", nom);
                            userData.put("prenom", prenom);
                            userData.put("pays", pays);
                            userData.put("adresse", adresse);
                            userData.put("phone", phone);
                            userData.put("email", email);
                            userData.put("role", role);

                            // Ajouter à la bonne collection (Clients ou Agents)
                            db.collection(role).document(userId)
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(Authentification.this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Authentification.this, DashboardActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(Authentification.this, "Erreur lors de l'enregistrement.", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(Authentification.this, "Erreur de création de compte.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}