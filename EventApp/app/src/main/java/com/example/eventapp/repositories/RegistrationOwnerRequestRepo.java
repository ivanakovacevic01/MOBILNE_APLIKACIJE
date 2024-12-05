package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Address;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.RegistrationOwnerRequest;
import com.example.eventapp.model.Subcategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class RegistrationOwnerRequestRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void create(RegistrationOwnerRequest newRequest){
        newRequest.setId("ownerRequest_"+generateUniqueString());
        db.collection("owner_requests")
                .document(newRequest.getId())
                .set(newRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newRequest.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });
    }

    public static void getAllRequests(RegistrationOwnerRequestRepo.RequestFetchCallback callback) {
        ArrayList<RegistrationOwnerRequest> requests = new ArrayList<>();

        db.collection("owner_requests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            RegistrationOwnerRequest request = document.toObject(RegistrationOwnerRequest.class);
                            if(request.getStatus().toString().equals("PENDING"))
                                requests.add((request));
                        }

                        callback.onRequestFetch(requests);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onRequestFetch(null);
                    }
                });
    }

    public static void updateRequest(RegistrationOwnerRequest updatedRequest, RegistrationOwnerRequestRepo.RequestFetchCallback callback) {
        db.collection("owner_requests")
                .document(updatedRequest.getId())
                .set(updatedRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + updatedRequest.getId());
                        callback.onUpdateSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error updating document", e);
                        callback.onUpdateFailure("Failed.");
                    }
                });
    }
    public interface RequestFetchCallback {
        default void onRequestFetch(ArrayList<RegistrationOwnerRequest> categories) {

        }
        default void onUpdateSuccess() {}
        default void onUpdateFailure(String errorMessage) {}
    }

    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }
}
