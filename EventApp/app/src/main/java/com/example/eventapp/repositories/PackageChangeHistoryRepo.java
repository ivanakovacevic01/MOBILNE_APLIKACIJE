package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.PackageChangeHistory;
import com.example.eventapp.model.ServiceChangeHistory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class PackageChangeHistoryRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createPackageHistoryChangeRepo(PackageChangeHistory newChangePackage){
        db.collection("packages_changes")
                .document(newChangePackage.getId())
                .set(newChangePackage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newChangePackage.getId());
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
