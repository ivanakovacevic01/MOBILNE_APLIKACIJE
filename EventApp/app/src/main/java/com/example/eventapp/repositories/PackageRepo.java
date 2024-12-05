package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Service;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;


public class PackageRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createPackage(Package newPackage){
        newPackage.setId("package_"+generateUniqueString());
        db.collection("packages")
                .document(newPackage.getId())
                .set(newPackage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newPackage.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });
    }
    public static void updatePackage(Package p) {
        db.collection("packages")
                .document(p.getId())
                .set(p)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + p.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error updating document", e);
                    }
                });
    }
    public static void deletePacakge(String id){
        DocumentReference docRef = db.collection("packages").document(id);
        docRef.update("deleted", true)
                .addOnSuccessListener(aVoid -> Log.d("REZ_DB", "Package successfully changed"))
                .addOnFailureListener(e -> Log.w("REZ_DB", "Error getting documents.", e));
    }

    public void getAllPackages(PackageRepo.PackageFetchCallback callback) {
        ArrayList<Package> packages = new ArrayList<>();

        db.collection("packages")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Package p = document.toObject(Package.class);
                            if (!p.getDeleted()) {
                                packages.add(p);
                            }
                        }
                        // Invoke the callback with the products list
                        callback.onPackageFetch(packages);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onPackageFetch(null);
                    }
                });
    }

    public interface PackageFetchCallback {
        default void onPackageFetch(ArrayList<Package> packages){}
        default void onPackageObjectFetched(Package p, String errorMessage) {

        }
    }
    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }

    public void getById(String id,PackageRepo.PackageFetchCallback callback) {
        db.collection("packages")
                .whereEqualTo("id", id)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Package p = documentSnapshot.toObject(Package.class);
                        callback.onPackageObjectFetched(p, null);
                    } else {
                        callback.onPackageObjectFetched(null, "Package not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onPackageObjectFetched(null, e.getMessage());
                });
    }

    public static void getByFirmId(String firmId, PackageRepo.PackageFetchCallback callback) {
        Query query = db.collection("packages").whereEqualTo("firmId", firmId);
        ArrayList<Package> packages = new ArrayList<>();
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Package p = document.toObject(Package.class);
                    packages.add(p);
                }
                callback.onPackageFetch(packages);

            } else {
                Log.d("PackageRepo", "Error getting products by firmId", task.getException());
                callback.onPackageFetch(null);
            }
        });
    }
}
