package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Category;
import com.example.eventapp.model.Subcategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class SubcategoryRepo {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createSubcategory(Subcategory newSubcategory, SubcategoryFetchCallback callback) {
        newSubcategory.setId("subcategory_" + generateUniqueString());
        db.collection("subcategories")
                .document(newSubcategory.getId())
                .set(newSubcategory)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newSubcategory.getId());
                    callback.onSubcategoryFetched(newSubcategory, null);
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onSubcategoryFetched(null, e.getMessage());
                });
    }




    public static void updateSubcategory(Subcategory updatedSubcategory, SubcategoryRepo.SubcategoryFetchCallback callback) {
        db.collection("subcategories")
                .document(updatedSubcategory.getId())
                .set(updatedSubcategory)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + updatedSubcategory.getId());
                        callback.onUpdateSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error updating document", e);
                        callback.onUpdateFailure("Failed");
                    }
                });
    }

    public static void deleteSubcategory(String subcategoryId) {
        db.collection("subcategories")
                .document(subcategoryId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot successfully deleted: " + subcategoryId);
                    updateEventTypes(subcategoryId);
                })
                .addOnFailureListener(e -> Log.w("REZ_DB", "Error deleting document " + subcategoryId, e));
    }

    public static void deleteSubcategoriesByCategoryId(String categoryId) {
        Query query = db.collection("subcategories").whereEqualTo("categoryId", categoryId);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    document.getReference().delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d("REZ_DB", "Subcategory successfully deleted: " + document.getId());
                            })
                            .addOnFailureListener(e -> {
                                Log.w("REZ_DB", "Error deleting subcategory: " + document.getId(), e);
                            });
                }
            } else {
                Log.d("REZ_DB", "Error getting subcategories: ", task.getException());
            }
        });
    }




    private static void updateEventTypes(String subcategoryId) {
        db.collection("eventTypes")
                .whereArrayContains("suggestedSubcategoriesIds", subcategoryId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DocumentReference docRef = document.getReference();
                            docRef.update("suggestedSubcategoriesIds", FieldValue.arrayRemove(subcategoryId))
                                    .addOnSuccessListener(aVoid -> Log.d("REZ_DB", "Event updated successfully"))
                                    .addOnFailureListener(e -> Log.w("REZ_DB", "Error updating event", e));
                        }
                    } else {
                        Log.d("REZ_DB", "Error getting events: ", task.getException());
                    }
                });
    }





    public static void getSubcategoriesByCategoryId(String categoryId, SubcategoryRepo.SubcategoryFetchCallback callback) {
        ArrayList<Subcategory> subcategories = new ArrayList<>();

        db.collection("subcategories")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Subcategory subcategory = document.toObject(Subcategory.class);
                            subcategories.add(subcategory);
                        }

                        callback.onSubcategoryFetch(subcategories);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onSubcategoryFetch(null);
                    }
                });
    }

    public static void getAllSubcategories(SubcategoryRepo.SubcategoryFetchCallback callback) {
        ArrayList<Subcategory> subcategories = new ArrayList<>();

        db.collection("subcategories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Subcategory subcategory = document.toObject(Subcategory.class);
                            subcategories.add((subcategory));
                        }

                        callback.onSubcategoryFetch(subcategories);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onSubcategoryFetch(null);
                    }
                });
    }

    public static void getSubcategoriesByIds(ArrayList<String> subcategoryIds, SubcategoryRepo.SubcategoryFetchCallback callback) {
        if (subcategoryIds == null || subcategoryIds.isEmpty()) {

            callback.onSubcategoryFetch(new ArrayList<>());
            return;
        }

        ArrayList<Subcategory> subcategories = new ArrayList<>();


        db.collection("subcategories")
                .whereIn(FieldPath.documentId(), subcategoryIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Subcategory subcategory = document.toObject(Subcategory.class);
                            subcategories.add(subcategory);
                        }
                        callback.onSubcategoryFetch(subcategories);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        callback.onSubcategoryFetch(null);
                    }
                });
    }


    public static void existsForCategory(String categoryId, SubcategoryRepo.CategoryFetchCallback callback) {
        db.collection("subcategories")
                .whereEqualTo("categoryId", categoryId)
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


    public interface SubcategoryFetchCallback {
        default void onSubcategoryFetch(ArrayList<Subcategory> subcategories) {

        }
        default void onSubcategoryFetched(Subcategory subcategory, String errorMessage) {

        }
        default void onUpdateSuccess() {}
        default void onUpdateFailure(String errorMessage) {}
    }

    public interface CategoryFetchCallback {
        default void onCheckComplete(boolean exists) {

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
