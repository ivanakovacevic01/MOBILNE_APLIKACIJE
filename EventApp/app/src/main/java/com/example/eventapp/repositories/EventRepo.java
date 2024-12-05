package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Service;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class EventRepo {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void create(Event event){
        event.setId("event_"+generateUniqueString());
        db.collection("events")
                .document(event.getId())
                .set(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + event.getId());
                        EventBudgetRepo eventBudgetRepo = new EventBudgetRepo();
                        eventBudgetRepo.create(new EventBudget(event.getId(), new ArrayList<>()));
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
        DocumentReference docRef = db.collection("events").document(id);
        docRef.update("deleted", true)
                .addOnSuccessListener(aVoid -> Log.d("REZ_DB", "Service successfully changed"))
                .addOnFailureListener(e -> Log.w("REZ_DB", "Error getting documents.", e));
    }

    public void getAll(EventRepo.EventFetchCallback callback) {
        ArrayList<Event> events = new ArrayList<>();

        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            events.add(event);
                        }
                        // Invoke the callback with the products list
                        callback.onEventFetch(events);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onEventFetch(null);
                    }
                });
    }

    public void getByOrganizer(EventRepo.EventFetchCallback callback, String email) {
        ArrayList<Event> events = new ArrayList<>();

        db.collection("events").whereEqualTo("organizerEmail", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            events.add(event);
                        }
                        // Invoke the callback with the products list
                        callback.onEventFetch(events);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onEventFetch(null);
                    }
                });
    }

    public static void getById(String id, EventRepo.EventFetchCallback callback) {
        db.collection("events")
                .whereEqualTo("id", id)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Event e = documentSnapshot.toObject(Event.class);
                        callback.onEventObjectFetched(e, null);
                    } else {
                        callback.onEventObjectFetched(null, "Event not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onEventObjectFetched(null, e.getMessage());
                });
    }

    public interface EventFetchCallback {
        default void onEventFetch(ArrayList<Event> events){}
        default void onEventObjectFetched(Event event, String errorMessage) {

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
