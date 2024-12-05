package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class OwnerRepo {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createOwner(Owner newOwner, OwnerRepo.OwnerFetchCallback callback){
        db.collection("owners")
                .document(newOwner.getId())
                .set(newOwner)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newOwner.getId());
                    db.collection("users")
                            .document(newOwner.getId())
                            .set((User)newOwner)
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newOwner.getId());
                                callback.onOwnerObjectFetched(newOwner, null);
                            })
                            .addOnFailureListener(e -> {
                                Log.w("REZ_DB", "Error adding document", e);
                                callback.onOwnerObjectFetched(null, e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onOwnerObjectFetched(null, e.getMessage());
                });
    }
    public void getAll(OwnerRepo.OwnerFetchCallback callback) {
        ArrayList<Owner> owners = new ArrayList<>();

        db.collection("owners")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Owner owner = document.toObject(Owner.class);
                            owners.add(owner);
                        }
                        // Invoke the callback with the products list
                        callback.onOwnerFetch(owners);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onOwnerFetch(null);
                    }
                });
    }

    public interface OwnerFetchCallback {
        default void onOwnerFetch(ArrayList<Owner> owners) {

        }
        default void onOwnerObjectFetched(Owner owner, String errorMessage) {

        }
    }
    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }
    public static void get(String ownerId, OwnerFetchCallback callback) {
        DocumentReference docRef = db.collection("owners").document(ownerId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Owner owner = document.toObject(Owner.class);
                    callback.onOwnerObjectFetched(owner,"");
                } else {
                    callback.onOwnerObjectFetched(null,""); // Vlasnik nije pronađen
                }
            } else {
                Log.d("OwnerRepo", "Error getting owner document", task.getException());
                callback.onOwnerObjectFetched(null,""); // Greška pri dohvaćanju podataka
            }
        });
    }
    public static void getByFirmId(String id, OwnerFetchCallback callback) {
        db.collection("owners")
                .whereEqualTo("firmId", id)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Ako je pronađen korisnik sa datim email-om, uzmi prvi dokument iz rezultata upita
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Owner owner = documentSnapshot.toObject(Owner.class);
                        callback.onOwnerObjectFetched(owner, null);
                    } else {
                        // Ako nije pronađen korisnik sa datim email-om
                        callback.onOwnerObjectFetched(null, "User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onOwnerObjectFetched(null, e.getMessage());
                });
    }

    public static void getByEmail(String email, OwnerFetchCallback callback) {
        db.collection("owners")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Ako je pronađen korisnik sa datim email-om, uzmi prvi dokument iz rezultata upita
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Owner owner = documentSnapshot.toObject(Owner.class);
                        callback.onOwnerObjectFetched(owner, null);
                    } else {
                        // Ako nije pronađen korisnik sa datim email-om
                        callback.onOwnerObjectFetched(null, "User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onOwnerObjectFetched(null, e.getMessage());
                });
    }

    public static void update(Owner updated) {
        db.collection("owners")
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

}
