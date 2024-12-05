package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.R;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.User;
import com.example.eventapp.model.WeeklyEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Random;

public class WeeklyEventRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void create(WeeklyEvent event, WeeklyEventFetchCallback callback){
        event.setId("weekly_event_"+generateUniqueString());
        db.collection("weekly_events")
                .document(event.getId())
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + event.getId());
                    callback.onWeeklyEventObjectFetched(event, null);
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onWeeklyEventObjectFetched(null, e.getMessage());
                });
    }



    public static void getAll(WeeklyEventFetchCallback callback) {
        ArrayList<WeeklyEvent> events = new ArrayList<>();

        db.collection("weekly_events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            WeeklyEvent event = document.toObject(WeeklyEvent.class);

                            events.add(event);

                        }
                        // Invoke the callback with the products list
                        callback.onWeeklyEventFetch(events);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onWeeklyEventFetch(null);
                    }
                });
    }

    public interface WeeklyEventFetchCallback {
        default void onWeeklyEventFetch(ArrayList<WeeklyEvent> events) {

        }
        default void onWeeklyEventObjectFetched(WeeklyEvent event, String errorMessage) {

        }
    }
    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }
    public static void getByEmployeeId(String id,WeeklyEventFetchCallback callback) {
        ArrayList<WeeklyEvent> events = new ArrayList<>();

        db.collection("weekly_events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            WeeklyEvent event = document.toObject(WeeklyEvent.class);
                            if(event.getEmployeeId().equals(id))
                                events.add(event);

                        }
                        // Invoke the callback with the products list
                        callback.onWeeklyEventFetch(events);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onWeeklyEventFetch(null);
                    }
                });
    }
    public static void getByFirmId(String id,WeeklyEventFetchCallback callback) {
        ArrayList<WeeklyEvent> events = new ArrayList<>();

        db.collection("weekly_events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            WeeklyEvent event = document.toObject(WeeklyEvent.class);
                            if(event.getFirmId().equals(id))
                                events.add(event);

                        }
                        // Invoke the callback with the products list
                        callback.onWeeklyEventFetch(events);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onWeeklyEventFetch(null);
                    }
                });
    }



    public static void getByEmployeeIdAndDate(String id, String date, WeeklyEventFetchCallback callback) {
        ArrayList<WeeklyEvent> events = new ArrayList<>();

        db.collection("weekly_events").whereEqualTo("date", date)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            WeeklyEvent event = document.toObject(WeeklyEvent.class);
                            if(event.getEmployeeId().equals(id))
                                events.add(event);

                        }
                        // Invoke the callback with the products list
                        callback.onWeeklyEventFetch(events);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onWeeklyEventFetch(null);
                    }
                });
    }
    public static void delete(String id) {
        db.collection("weekly_events")
                .whereEqualTo("reservationId", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("weekly_events")
                                    .document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("REZ_DB", "DocumentSnapshot successfully deleted!");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("REZ_DB", "Error deleting document", e);
                                    });
                        }
                    } else {
                        Log.w("REZ_DB", "Error getting documents: ", task.getException());
                    }
                });
    }


    public static void getByReservation(String id, WeeklyEventRepo.WeeklyEventFetchCallback callback) {
        db.collection("weekly_events")
                .whereEqualTo("reservationId", id)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        WeeklyEvent event = documentSnapshot.toObject(WeeklyEvent.class);
                        callback.onWeeklyEventObjectFetched(event, null);
                    } else {
                        callback.onWeeklyEventObjectFetched(null, "User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onWeeklyEventObjectFetched(null, e.getMessage());
                });
    }

}
