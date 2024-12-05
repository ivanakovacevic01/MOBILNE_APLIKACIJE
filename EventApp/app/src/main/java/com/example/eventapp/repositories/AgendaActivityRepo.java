package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.AgendaActivity;
import com.example.eventapp.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class AgendaActivityRepo {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void create(AgendaActivity activity){
        activity.setId("agenda_activity_"+generateUniqueString());
        db.collection("agendaActivities")
                .document(activity.getId())
                .set(activity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + activity.getId());
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
        db.collection("agendaActivities")
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot successfully deleted: " + id);
                })
                .addOnFailureListener(e -> Log.w("REZ_DB", "Error deleting document " + id, e));
    }

    public static void getByEventId(String id, AgendaActivityFetchCallback callback) {
        ArrayList<AgendaActivity>activities = new ArrayList<>();
        db.collection("agendaActivities")
                .whereEqualTo("eventId", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AgendaActivity activity = document.toObject(AgendaActivity.class);
                            activities.add(activity);
                        }
                        // Invoke the callback with the products list
                        callback.onAgendaActivityFetch(activities);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onAgendaActivityFetch(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onAgendaActivityObjectFetched(null, e.getMessage());
                });
    }

    public interface AgendaActivityFetchCallback {
        default void onAgendaActivityFetch(ArrayList<AgendaActivity> activities){}
        default void onAgendaActivityObjectFetched(AgendaActivity event, String errorMessage) {

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
