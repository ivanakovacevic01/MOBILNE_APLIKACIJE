package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Category;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Subcategory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;

public class CategoryRepo {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createCategory(Category newCategory){
        newCategory.setId("category_"+generateUniqueString());
        db.collection("categories")
                .document(newCategory.getId())
                .set(newCategory)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newCategory.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });
    }

    public static void updateCategory(Category updatedCategory, CategoryRepo.CategUpdateCallback callback) {
        db.collection("categories")
                .document(updatedCategory.getId())
                .set(updatedCategory)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + updatedCategory.getId());
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
    public interface CategUpdateCallback {
        void onUpdateSuccess();
        void onUpdateFailure(String errorMessage);
    }

    public static void deleteCategory(String categoryId) {
        db.collection("categories")
                .document(categoryId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "Category deleted successfully");

                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error deleting category", e);

                });
    }







    public static void getAllCategories(CategoryRepo.CategoryFetchCallback callback) {
        ArrayList<Category> categories = new ArrayList<>();

        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Category category = document.toObject(Category.class);
                            categories.add((category));
                        }

                        callback.onCategoryFetch(categories);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onCategoryFetch(null);
                    }
                });
    }

    public static void getCategoryById(String categoryId, CategoryFetchCallback callback) {
        db.collection("categories")
                .document(categoryId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Category category = document.toObject(Category.class);
                            callback.onCategoryByIdFetch(category);
                        } else {
                            Log.d("REZ_DB", "No such document");
                            callback.onCategoryByIdFetch(null);
                        }
                    } else {
                        Log.d("REZ_DB", "get failed with ", task.getException());
                        callback.onCategoryByIdFetch(null);
                    }
                });
    }


    public static void getByIds(CategoryRepo.CategoryFetchCallback callback, ArrayList<String> ids) {
        if (ids == null || ids.isEmpty()) {

            callback.onCategoryFetch(new ArrayList<>());
            return;
        }
        ArrayList<Category> categories = new ArrayList<>();

        db.collection("categories").whereIn(FieldPath.documentId(), ids)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Category category = document.toObject(Category.class);
                            categories.add((category));
                        }

                        callback.onCategoryFetch(categories);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onCategoryFetch(null);
                    }
                });
    }


    public interface CategoryFetchCallback {
        default void onCategoryFetch(ArrayList<Category> categories) {

        }
        default void onCategoryByIdFetch(Category category) {

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
