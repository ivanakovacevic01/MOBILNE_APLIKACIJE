package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.Favourites;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class FavouritesRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static void create(Favourites favourites){
        favourites.setId("favourite_"+generateUniqueString());
        db.collection("favourites")
                .document(favourites.getId())
                .set(favourites)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + favourites.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });
    }

    public static void update(Favourites updated) {
        db.collection("favourites")
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
                        create(updated);
                    }
                });
    }
    public void getByOrganizer(FavouritesRepo.FavouriteFetchCallback callback, String email) {
        ArrayList<Favourites> events = new ArrayList<>();

        db.collection("favourites").whereEqualTo("userEmail", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Favourites event = document.toObject(Favourites.class);
                            events.add(event);
                        }
                        // Invoke the callback with the products list
                        callback.onFavouriteFetch(events);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onFavouriteFetch(null);
                    }
                });
    }

    public interface FavouriteFetchCallback {
        void onFavouriteFetch(ArrayList<Favourites> favourites);
        default void onFavouriteObjectFetched(Favourites f, String errorMessage) {

        }

    }
    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }
}
