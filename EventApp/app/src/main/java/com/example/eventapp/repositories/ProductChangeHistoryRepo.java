package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.ProductChangeHistory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductChangeHistoryRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createProductHistoryChangeRepo(ProductChangeHistory newChangeProduct){
        db.collection("products_changes")
                .document(newChangeProduct.getId())
                .set(newChangeProduct)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newChangeProduct.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });
    }


}
