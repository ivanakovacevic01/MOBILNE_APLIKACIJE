package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Category;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EventTypeRepo {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createEventType(EventType newEventType){
        newEventType.setId("eventType_"+generateUniqueString());
        db.collection("eventTypes")
                .document(newEventType.getId())
                .set(newEventType)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newEventType.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });
    }

    public static void updateEventType(EventType updatedEventType) {
        db.collection("eventTypes")
                .document(updatedEventType.getId())
                .set(updatedEventType)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + updatedEventType.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error updating document", e);
                    }
                });
    }


    public static void getAllEventTypes(EventTypeRepo.EventTypeFetchCallback callback) {
        ArrayList<EventType> eventTypes = new ArrayList<>();

        db.collection("eventTypes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EventType eventType = document.toObject(EventType.class);
                            eventTypes.add((eventType));
                           //ne dirati za (de)aktivirane, sve ih prikazujem sebi
                        }

                        callback.onEventTypeFetch(eventTypes);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onEventTypeFetch(null);
                    }
                });
    }

    public static void getByIds(EventTypeRepo.EventTypeFetchCallback callback, ArrayList<String> ids) {
        if (ids == null || ids.isEmpty()) {

            callback.onEventTypeFetch(new ArrayList<>());
            return;
        }
        ArrayList<EventType> categories = new ArrayList<>();

        db.collection("eventTypes").whereIn(FieldPath.documentId(), ids)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EventType category = document.toObject(EventType.class);
                            categories.add((category));
                        }

                        callback.onEventTypeFetch(categories);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onEventTypeFetch(null);
                    }
                });
    }
    public static void getAllActicateEventTypes(EventTypeRepo.EventTypeFetchCallback callback) {
        ArrayList<EventType> eventTypes = new ArrayList<>();

        db.collection("eventTypes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EventType eventType = document.toObject(EventType.class);
                            if(!eventType.getDeactivated())
                                eventTypes.add((eventType));
                            //ne dirati za (de)aktivirane, sve ih prikazujem sebi
                        }
                        // Invoke the callback with the products list
                        callback.onEventTypeFetch(eventTypes);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onEventTypeFetch(null);
                    }
                });
    }


    public static void activate(String id, EventTypeActivationCallback callback) {
        DocumentReference docRef = db.collection("eventTypes").document(id);
        docRef.update("deactivated", false)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "Event type successfully activated.");
                    callback.onActivationSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error activating event type.", e);
                    callback.onActivationFailure("Failed to activate event type.");
                });
    }

    public static void deactivate(String id, EventTypeDeactivationCallback callback) {
        DocumentReference docRef = db.collection("eventTypes").document(id);
        docRef.update("deactivated", true)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "Event type successfully deactivated.");
                    callback.onDeactivationSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error activating event type.", e);
                    callback.onDeactivationFailure("Failed to deactivate event type.");
                });
    }

    public interface EventTypeFetchCallback {
        void onEventTypeFetch(ArrayList<EventType> eventTypes);
    }

    public interface EventTypeActivationCallback {
        void onActivationSuccess();
        void onActivationFailure(String errorMessage);
    }
    public interface EventTypeDeactivationCallback {
        void onDeactivationSuccess();
        void onDeactivationFailure(String errorMessage);
    }

    public interface OnSubcategoryCheckFetch {
        void onCheckComplete(boolean exists);
    }

    public static void containsSubcategory(String subcategoryId, OnSubcategoryCheckFetch callback) {
        db.collection("eventTypes")
                .whereArrayContains("suggestedSubcategoriesIds", subcategoryId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = !task.getResult().isEmpty();
                        callback.onCheckComplete(exists);
                    } else {
                        Log.d("REZ_DB", "Error getting events: ", task.getException());
                        callback.onCheckComplete(false);
                    }
                });
    }


    public static void getEventType(EventTypeRepo.EventTypeFetchCallback callback, String id) {
        ArrayList<EventType> eventTypes = new ArrayList<>();

        db.collection("eventTypes").whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EventType eventType = document.toObject(EventType.class);
                            eventTypes.add((eventType));
                        }

                        callback.onEventTypeFetch(eventTypes);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onEventTypeFetch(null);
                    }
                });
    }




    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }
}
