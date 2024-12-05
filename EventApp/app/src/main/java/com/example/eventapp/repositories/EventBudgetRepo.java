package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.Subcategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class EventBudgetRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void create(EventBudget eventBudget){
        eventBudget.setId("budget_"+generateUniqueString());
        db.collection("budgets")
                .document(eventBudget.getId())
                .set(eventBudget)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + eventBudget.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });
    }

    public interface EventBudgetFetchCallback {
        void onEventBudgetFetch(ArrayList<EventBudget> budgets);
        void onEventBudgetFetchByEvent(EventBudget budget);
    }

    public void getByEventId(EventBudgetFetchCallback callback, String eventId) {
       AtomicReference<EventBudget> budget = new AtomicReference<>(new EventBudget());

        db.collection("budgets").whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            budget.set(document.toObject(EventBudget.class));
                        }
                        // Invoke the callback with the products list
                        callback.onEventBudgetFetchByEvent(budget.get());
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onEventBudgetFetchByEvent(null);
                    }
                });
    }
    public static void update(EventBudget updatedSubcategory) {
        db.collection("budgets")
                .document(updatedSubcategory.getId())
                .set(updatedSubcategory)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + updatedSubcategory.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error updating document", e);
                    }
                });
    }

    public void getAll(EventBudgetFetchCallback callback) {
        ArrayList<EventBudget> budgets = new ArrayList<>();

        db.collection("budgets")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EventBudget budget = document.toObject(EventBudget.class);

                            budgets.add(budget);

                        }
                        // Invoke the callback with the products list
                        callback.onEventBudgetFetch(budgets);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onEventBudgetFetch(null);
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
