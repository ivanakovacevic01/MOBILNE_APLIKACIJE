package com.example.eventapp.repositories;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.EventType;
import com.example.eventapp.model.SubcategorySuggestion;
import com.example.eventapp.model.SuggestionStatus;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class SubcategorySuggestionsRepo {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void create(SubcategorySuggestion subcategorySuggestion){
        subcategorySuggestion.setId("subcategory_suggestion_"+generateUniqueString());
        db.collection("subcategory_suggestions")
                .document(subcategorySuggestion.getId())
                .set(subcategorySuggestion)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + subcategorySuggestion.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });

    }

    public static void getPendingSubcategorySuggestions(SubcategorySuggestionsRepo.SubcategorySuggestionFetchCallback callback) {
        ArrayList<SubcategorySuggestion> suggestions = new ArrayList<>();

        db.collection("subcategory_suggestions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            SubcategorySuggestion suggestion = document.toObject(SubcategorySuggestion.class);
                            if(suggestion.getStatus().toString().equals(SuggestionStatus.PENDING.toString()))
                                suggestions.add((suggestion));
                        }
                        // Invoke the callback with the products list
                        callback.onSubcategorySuggestionFetch(suggestions);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onSubcategorySuggestionFetch(null);
                    }
                });
    }

    public static void approve(String id, SubcategorySuggestionsRepo.SubcategorySuggestionUpdateCallback callback) {
        DocumentReference docRef = db.collection("subcategory_suggestions").document(id);
        docRef.update("status", SuggestionStatus.ACCEPTED)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "Suggestion successfully approved.");
                    callback.onUpdateSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error approving suggestion.", e);
                    callback.onUpdateFailure("Failed to approve suggestion.");
                });
    }
    public static void reject(String id, SubcategorySuggestionsRepo.SubcategorySuggestionUpdateCallback callback) {
        DocumentReference docRef = db.collection("subcategory_suggestions").document(id);
        docRef.update("status", SuggestionStatus.REJECTED)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "Suggestion successfully rejected.");
                    callback.onUpdateSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error rejecting suggestion.", e);
                    callback.onUpdateFailure("Failed to reject suggestion.");
                });
    }

    public static void updateSubcategorySuggestion(SubcategorySuggestion updatedSuggestion, SubcategorySuggestionsRepo.SubcategorySuggestionUpdateCallback callback) {
        db.collection("subcategory_suggestions")
                .document(updatedSuggestion.getId())
                .set(updatedSuggestion)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + updatedSuggestion.getId());
                        callback.onUpdateSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error updating document", e);
                        callback.onUpdateFailure("Failed to update suggestion.");
                    }
                });
    }

    public static void existsForCategory(String categoryId, SubcategorySuggestionsRepo.CategoryFetchCallback callback) {
        db.collection("subcategory_suggestions")
                .whereEqualTo("category", categoryId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = !task.getResult().isEmpty();
                        callback.onCheckComplete(exists);
                    } else {
                        Log.d("REZ_DB", "Error getting subcategories: ", task.getException());
                        callback.onCheckComplete(false);
                    }
                });
    }
    public interface CategoryFetchCallback {
        default void onCheckComplete(boolean exists) {

        }

    }


    public interface SubcategorySuggestionUpdateCallback {
        void onUpdateSuccess();
        void onUpdateFailure(String errorMessage);
    }

    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }

    public interface SubcategorySuggestionFetchCallback {
        void onSubcategorySuggestionFetch(ArrayList<SubcategorySuggestion> suggestions);
    }
}
