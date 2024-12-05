package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.ProductChangeHistory;
import com.example.eventapp.model.ServiceChangeHistory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class ServiceChangeHistoryRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createServiceHistoryChange(ServiceChangeHistory newChangeService){
        db.collection("services_changes")
                .document(newChangeService.getId())
                .set(newChangeService)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newChangeService.getId());
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
