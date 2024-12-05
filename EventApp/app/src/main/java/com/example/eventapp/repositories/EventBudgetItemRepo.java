package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.EventBudgetItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class EventBudgetItemRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void create(EventBudgetItemFetchCallback callback, EventBudgetItem eventBudget){
        eventBudget.setId("budgetItem_"+generateUniqueString());
        ArrayList<EventBudgetItem> budgets = new ArrayList<>();
        db.collection("budgetItems")
                .document(eventBudget.getId())
                .set(eventBudget)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        budgets.add(eventBudget);
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + eventBudget.getId());
                        callback.onEventBudgetItemFetch(budgets);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                        callback.onEventBudgetItemFetch(null);
                    }
                });
    }

    public interface EventBudgetItemFetchCallback {
        void onEventBudgetItemFetch(ArrayList<EventBudgetItem> budgets);
    }

    public static void delete(String id){
        DocumentReference docRef = db.collection("budgetItems").document(id);
        docRef.update("deleted", true)
                .addOnSuccessListener(aVoid -> Log.d("REZ_DB", "Budget item successfully deleted"))
                .addOnFailureListener(e -> Log.w("REZ_DB", "Error getting documents.", e));
    }

    public void getAll(EventBudgetItemFetchCallback callback) {
        ArrayList<EventBudgetItem> budgets = new ArrayList<>();

        db.collection("budgetItems")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EventBudgetItem budget = document.toObject(EventBudgetItem.class);

                            budgets.add(budget);

                        }
                        // Invoke the callback with the products list
                        callback.onEventBudgetItemFetch(budgets);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onEventBudgetItemFetch(null);
                    }
                });
    }

    public static void update(EventBudgetItem updated) {
        db.collection("budgetItems")
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


    public void getByBudget(EventBudgetItemFetchCallback callback, EventBudget eventBudget) {
        ArrayList<EventBudgetItem> budgets = new ArrayList<>();

        db.collection("budgetItems")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EventBudgetItem budget = document.toObject(EventBudgetItem.class);
                            if(eventBudget.getEventBudgetItemsIds().stream().anyMatch(b -> b.equals(budget.getId())))
                                budgets.add(budget);
                        }
                        // Invoke the callback with the products list
                        callback.onEventBudgetItemFetch(budgets);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onEventBudgetItemFetch(null);
                    }
                });
    }

    public void getByBudgetAndSubcategory(EventBudgetItemFetchCallback callback, EventBudget eventBudget, String subcategory) {
        ArrayList<EventBudgetItem> budgets = new ArrayList<>();

        db.collection("budgetItems")
                .whereEqualTo("subcategoryId", subcategory)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EventBudgetItem budget = document.toObject(EventBudgetItem.class);
                            if(eventBudget.getEventBudgetItemsIds().stream().anyMatch(b -> b.equals(budget.getId())))
                                budgets.add(budget);
                        }
                        // Invoke the callback with the products list
                        callback.onEventBudgetItemFetch(budgets);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onEventBudgetItemFetch(null);
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
