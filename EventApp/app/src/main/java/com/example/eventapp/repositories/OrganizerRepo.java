package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Employee;
import com.example.eventapp.model.EventBudgetItem;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class OrganizerRepo {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createOrganizer(Organizer newOrganizer, OrganizerRepo.OrganizerFetchCallback callback) {
        db.collection("organizers")
                .document(newOrganizer.getId())
                .set(newOrganizer)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newOrganizer.getId());
                    db.collection("users")
                            .document(newOrganizer.getId())
                            .set((User)newOrganizer)
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newOrganizer.getId());
                                callback.onOrganizerObjectFetched(newOrganizer, null);
                            })
                            .addOnFailureListener(e -> {
                                Log.w("REZ_DB", "Error adding document", e);
                                callback.onOrganizerObjectFetched(null, e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onOrganizerObjectFetched(null, e.getMessage());
                });
    }

    public static void update(Organizer updated) {
        db.collection("organizers")
                .document(updated.getId())
                .set(updated)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + updated.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error updating document", e);
                    }
                });
    }


    public interface OrganizerFetchCallback {
        default void onOrganizerFetch(ArrayList<Organizer> organizers) {

        }
        default void onOrganizerObjectFetched(Organizer organizer, String errorMessage) {

        }
    }

    public interface OrganizerUpdateCallback {
        void onUpdateSuccess();
        void onUpdateFailure(String errorMessage);
    }

    public static void getByEmail(String email, OrganizerRepo.OrganizerFetchCallback callback) {
        db.collection("organizers")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Organizer organizer = documentSnapshot.toObject(Organizer.class);
                        callback.onOrganizerObjectFetched(organizer, null);
                    } else {
                        callback.onOrganizerObjectFetched(null, "Organizer not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onOrganizerObjectFetched(null, e.getMessage());
                });
    }

    public static void getAll(OrganizerRepo.OrganizerFetchCallback callback) {
        ArrayList<Organizer> organizers = new ArrayList<>();

        db.collection("organizers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Organizer organizer = document.toObject(Organizer.class);

                            organizers.add(organizer);

                        }
                        callback.onOrganizerFetch(organizers);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        callback.onOrganizerFetch(null);
                    }
                });
    }



}
