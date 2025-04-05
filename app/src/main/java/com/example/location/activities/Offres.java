package com.example.location.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.location.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class Offres extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> offreList = new ArrayList<>();
    private FirebaseFirestore db;
    private FloatingActionButton fabAjouterOffre;
    private List<QueryDocumentSnapshot> documents = new ArrayList<>(); // Stocker les documents Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offres);

        listView = findViewById(R.id.listViewOffres);
        fabAjouterOffre = findViewById(R.id.fabAjouterOffre);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, offreList);
        listView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        chargerOffres();

        fabAjouterOffre.setOnClickListener(view -> {
            Intent intent = new Intent(Offres.this, Offre.class);
            startActivity(intent);
        });

        // Ajouter un écouteur de clics pour chaque élément de la liste
        listView.setOnItemClickListener((parent, view, position, id) -> {
            QueryDocumentSnapshot selectedDocument = documents.get(position);
            Intent intent = new Intent(Offres.this, DetailsOffre.class);
            intent.putExtra("titre", selectedDocument.getString("titre"));
            intent.putExtra("prix", selectedDocument.getString("prix"));
            intent.putExtra("description", selectedDocument.getString("description"));
            startActivity(intent);

        });
    }

    private void chargerOffres() {

            db.collection("Offres")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            offreList.clear();
                            documents.clear();
                            if (task.getResult().isEmpty()) {
                                Log.w("Firestore", "Aucune offre trouvée !");
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firestore", "Offre récupérée : " + document.getData());
                                String titre = document.getString("titre");
                                String prix = document.getString("prix");
                                if (titre != null && prix != null) {
                                    offreList.add(titre + " - " + prix + " MAD");
                                    documents.add(document);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("Firestore", "Erreur lors du chargement des offres", task.getException());
                        }
                    });


        db.collection("Offres")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        offreList.clear();
                        documents.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String titre = document.getString("titre");
                            String prix = document.getString("prix");
                            if (titre != null && prix != null) {
                                offreList.add(titre + " - " + prix + " MAD");
                                documents.add(document); // Stocker les documents Firestore
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Erreur lors du chargement des offres", task.getException());
                    }
                });
    }
}
