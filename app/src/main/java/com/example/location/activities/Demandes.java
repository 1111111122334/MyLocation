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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Demandes extends AppCompatActivity {

    private LinearLayout layoutDemandes;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demandes);

        layoutDemandes = findViewById(R.id.layoutDemandes); // dans activity_demande.xml
        db = FirebaseFirestore.getInstance();

        chargerDemandes();
    }

    private void chargerDemandes() {
        db.collection("Demandes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    layoutDemandes.removeAllViews(); // vider le layout avant d'ajouter
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId();
                        String titre = doc.getString("titreOffre");
                        String statut = doc.getString("statut");

                        View itemView = getLayoutInflater().inflate(R.layout.item_demande, null);

                        TextView textTitre = itemView.findViewById(R.id.textTitreOffre);
                        TextView textStatut = itemView.findViewById(R.id.textStatut);
                        Button btnAccepter = itemView.findViewById(R.id.btnAccepter);
                        Button btnRefuser = itemView.findViewById(R.id.btnRefuser);

                        textTitre.setText("Offre : " + titre);
                        textStatut.setText("Statut : " + statut);

                        // Afficher les boutons seulement si la demande est en attente
                        if (!"en attente".equalsIgnoreCase(statut)) {
                            btnAccepter.setVisibility(View.GONE);
                            btnRefuser.setVisibility(View.GONE);
                        } else {
                            btnAccepter.setOnClickListener(v -> mettreAJourStatut(id, "acceptée"));
                            btnRefuser.setOnClickListener(v -> mettreAJourStatut(id, "refusée"));
                        }

                        layoutDemandes.addView(itemView);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Demandes", "Erreur lors du chargement des demandes", e);
                    Toast.makeText(this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
                });
    }

    private void mettreAJourStatut(String demandeId, String nouveauStatut) {
        DocumentReference docRef = db.collection("Demandes").document(demandeId);
        docRef.update("statut", nouveauStatut)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Demande " + nouveauStatut, Toast.LENGTH_SHORT).show();
                    chargerDemandes(); // Recharger pour mettre à jour l'affichage
                })
                .addOnFailureListener(e -> {
                    Log.e("Demandes", "Erreur de mise à jour", e);
                    Toast.makeText(this, "Erreur de mise à jour", Toast.LENGTH_SHORT).show();
                });
    }
}
