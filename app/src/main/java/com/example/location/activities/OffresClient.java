package com.example.location.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.location.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OffresClient extends AppCompatActivity {

    private LinearLayout layoutOffres;
    private FirebaseFirestore db;
    private List<QueryDocumentSnapshot> documents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offres_client);

        layoutOffres = findViewById(R.id.layoutOffresClient);
        db = FirebaseFirestore.getInstance();

        chargerOffresPourClient();
    }

    private void chargerOffresPourClient() {
        db.collection("Offres")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        layoutOffres.removeAllViews();
                        documents.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String titre = doc.getString("titre");
                            String prix = doc.getString("prix");
                            String description = doc.getString("description");

                            View offreView = getLayoutInflater().inflate(R.layout.item_offre_client, null);
                            ((TextView) offreView.findViewById(R.id.textTitre)).setText(titre);
                            ((TextView) offreView.findViewById(R.id.textPrix)).setText(prix + " MAD");
                            ((TextView) offreView.findViewById(R.id.textDescription)).setText(description);

                            Button btnDemander = offreView.findViewById(R.id.btnDemander);
                            btnDemander.setOnClickListener(v -> envoyerDemande(doc));

                            layoutOffres.addView(offreView);
                        }
                    } else {
                        Log.e("Firestore", "Erreur chargement offres client", task.getException());
                    }
                });
    }

    private void envoyerDemande(QueryDocumentSnapshot offre) {
        String idOffre = offre.getId();
        String agentId = offre.getString("agentId");
        String titre = offre.getString("titre");

        String idClient = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String emailClient = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Map<String, Object> demande = new HashMap<>();
        demande.put("titreOffre", titre);
        demande.put("idOffre", idOffre);
        demande.put("agentId", agentId);
        demande.put("idClient", idClient);
        demande.put("emailClient", emailClient);
        demande.put("statut", "en attente");
        demande.put("timestamp", FieldValue.serverTimestamp());

        db.collection("Demandes").add(demande)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(OffresClient.this, "Demande envoyée avec succès", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(OffresClient.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
