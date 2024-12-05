package com.example.eventapp.repositories;

import android.util.Log;

import com.example.eventapp.model.LinkExpiration;
import com.example.eventapp.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LinkExpirationRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static void createLinkExpiration(LinkExpiration link, LinkExpirationRepo.LinkFetchCallback callback) {

        db.collection("links")
                .document(link.getEmail())
                .set(link)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + link.getEmail());
                    callback.onLinkObjectFetched(link, null);
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onLinkObjectFetched(null, e.getMessage());
                });
    }

    public static void getLinkExpirationByEmail(String email, LinkExpirationRepo.LinkFetchCallback callback) {
        db.collection("links")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Ako je pronađen link sa datim email-om, uzmi prvi dokument iz rezultata upita
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        LinkExpiration link = documentSnapshot.toObject(LinkExpiration.class);
                        callback.onLinkObjectFetched(link, null);
                    } else {
                        // Ako nije pronađen link sa datim email-om
                        callback.onLinkObjectFetched(null, "Link not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onLinkObjectFetched(null, e.getMessage());
                });
    }

    public static void deleteByEmail(String email) {
        db.collection("links")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String linkId = documentSnapshot.getId();

                        db.collection("links")
                                .document(linkId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("REZ_DB", "LINK deleted successfully");
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("REZ_DB", "Error deleting link", e);
                                });
                    } else {
                        // Ako nije pronađen link sa datom email adresom
                        Log.d("REZ_DB", "Link not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                });
    }

    public interface LinkFetchCallback {
        default void onLinkFetch(ArrayList<LinkExpiration> links) {

        }
        default void onLinkObjectFetched(LinkExpiration link, String errorMessage) {

        }
    }
}
