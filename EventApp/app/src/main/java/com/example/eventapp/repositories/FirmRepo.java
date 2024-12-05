package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Firm;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.Subcategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class FirmRepo {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createFirm(Firm newFirm, FirmRepo.FirmFetchCallback callback) {
        newFirm.setId("firm_" + generateUniqueString());
        db.collection("firms")
                .document(newFirm.getId())
                .set(newFirm)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newFirm.getId());
                    callback.onFirmObjectFetched(newFirm, null);
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onFirmObjectFetched(null, e.getMessage());
                });
    }

    public static void activateFirm(String firmId, FirmRepo.FirmFetchCallback callback) {
        db.collection("firms")
                .document(firmId)
                .update("active", true)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "Firm activated with ID: " + firmId);
                    // Ako je aktiviranje uspešno, prosleđujemo aktiviranu firmu u callback
                    callback.onFirmObjectFetched(null, null);
                })
                .addOnFailureListener(e -> {
                    // Ako dođe do greške prilikom aktiviranja firme
                    Log.w("REZ_DB", "Error activating firm", e);
                    callback.onFirmObjectFetched(null, e.getMessage());
                });
    }

    public void getAll(FirmRepo.FirmFetchCallback callback) {
        ArrayList<Firm> firms = new ArrayList<>();

        db.collection("firms")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Firm firm = document.toObject(Firm.class);
                            firms.add(firm);
                        }
                        // Invoke the callback with the products list
                        callback.onFirmFetch(firms);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onFirmFetch(null);
                    }
                });
    }
    public void getAllNotActive(FirmRepo.FirmFetchCallback callback) {
        ArrayList<Firm> firms = new ArrayList<>();

        db.collection("firms")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Firm firm = document.toObject(Firm.class);
                            firms.add(firm);
                        }
                        // Invoke the callback with the products list
                        callback.onFirmFetch(firms);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onFirmFetch(null);
                    }
                });
    }

    public void getById(String id, FirmRepo.FirmFetchCallback callback) {
        ArrayList<Firm> firms = new ArrayList<>();

        db.collection("firms")
                .whereEqualTo("id", id)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Firm firm = document.toObject(Firm.class);
                            firms.add(firm);
                        }
                        // Invoke the callback with the products list
                        callback.onFirmFetch(firms);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onFirmFetch(null);
                    }
                });
    }
    public static void checkIfCategoryExistsInFirms(String categoryId, CategoryExistenceCallback callback) {
        db.collection("firms")
                .whereArrayContains("categoriesIds", categoryId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            callback.onExistence(true);
                        } else {
                            callback.onExistence(false);
                        }
                    } else {
                        Log.d("REZ_DB", "Error checking category existence in firms: ", task.getException());
                        callback.onExistence(false);
                    }
                });
    }

    public static void update(Firm updated) {
        db.collection("firms")
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

    public static void checkIfEventTypeExistsInFirms(String eventTypeId, CategoryExistenceCallback callback) {
        db.collection("firms")
                .whereArrayContains("eventTypesIds", eventTypeId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            callback.onExistence(true);
                        } else {
                            callback.onExistence(false);
                        }
                    } else {
                        Log.d("REZ_DB", "Error checking category existence in firms: ", task.getException());
                        callback.onExistence(false);
                    }
                });
    }

    public interface DeleteCallback {
        void onDeleteSuccess();
        void onDeleteFailure(String errorMessage);
    }

    public static void deleteFirm(String firmId, DeleteCallback callback) {
        Log.d("REZ_DB", "Attempting to delete firm with ID: " + firmId);
        db.collection("firms")
                .document(firmId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "Firm deleted successfully");
                    callback.onDeleteSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error deleting firm", e);
                    callback.onDeleteFailure(e.getMessage());
                });
    }

    public interface FirmFetchCallback {
        default void onFirmFetch(ArrayList<Firm> firms) {

        }
        default void onFirmObjectFetched(Firm firm, String errorMessage) {

        }
    }
    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }

    public interface CategoryExistenceCallback {
        void onExistence(boolean exists);
    }


}
