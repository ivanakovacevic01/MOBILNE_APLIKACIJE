package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.AgendaActivity;
import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.Guest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class GuestRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void create(Guest guest){
        guest.setId("guest_"+generateUniqueString());
        db.collection("guests")
                .document(guest.getId())
                .set(guest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + guest.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });
    }

    public static void delete(String id){
        db.collection("guests")
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot successfully deleted: " + id);
                })
                .addOnFailureListener(e -> Log.w("REZ_DB", "Error deleting document " + id, e));
    }

    public static void update(Guest guest) {
        db.collection("guests")
                .document(guest.getId())
                .set(guest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + guest.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error updating document", e);
                    }
                });
    }
    public static void getByEventId(String id, GuestRepo.GuestFetchCallback callback) {
        ArrayList<Guest> guests = new ArrayList<>();
        db.collection("guests")
                .whereEqualTo("eventId", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Guest activity = document.toObject(Guest.class);
                            guests.add(activity);
                        }
                        // Invoke the callback with the products list
                        callback.onGuestFetch(guests);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onGuestFetch(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onGuestObjectFetched(null, e.getMessage());
                });
    }

    public interface GuestFetchCallback {
        default void onGuestFetch(ArrayList<Guest> guests){}
        default void onGuestObjectFetched(Guest guest, String errorMessage) {

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
