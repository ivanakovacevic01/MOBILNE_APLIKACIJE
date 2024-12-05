package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.R;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.FirmWorkingTime;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.WorkingTime;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class FirmWorkingTimeRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createFirmWorkingTime(FirmWorkingTime newTime){
        newTime.setId("firmWorkingTime"+generateUniqueString());
        db.collection("firm_working_times")
                .document(newTime.getId())
                .set(newTime)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newTime.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });
    }
    public void getAll(FirmWorkingTimeRepo.FirmWorkingTimeFetchCallback callback) {
        ArrayList<FirmWorkingTime> workingTimes = new ArrayList<>();

        db.collection("firm_working_times")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FirmWorkingTime firmWorkingTime = document.toObject(FirmWorkingTime.class);
                            workingTimes.add(firmWorkingTime);
                        }
                        // Invoke the callback with the products list
                        callback.onFirmWorkingTimeFetch(workingTimes);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onFirmWorkingTimeFetch(null);
                    }
                });
    }
    public interface FirmWorkingTimeFetchCallback {
        default void onFirmWorkingTimeFetch(ArrayList<FirmWorkingTime> firmWorkingTimes) {

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
