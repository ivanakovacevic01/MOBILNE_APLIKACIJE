package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.R;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.User;
import com.example.eventapp.model.WorkingTime;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class WorkingTimeRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void create(WorkingTime workingTime, WorkingTimeRepo.WorkingTimeFetchCallback callback) {
        workingTime.setId("working_time_"+generateUniqueString());
        db.collection("working_times")
                .document(workingTime.getId())
                .set(workingTime)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + workingTime.getId());
                    callback.onWorkingTimeObjectFetched(workingTime, null);
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onWorkingTimeObjectFetched(null, e.getMessage());
                });
    }


    public void getAll(WorkingTimeRepo.WorkingTimeFetchCallback callback) {
        ArrayList<WorkingTime> workingTimes = new ArrayList<>();

        db.collection("working_times")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            WorkingTime wt = document.toObject(WorkingTime.class);

                            workingTimes.add(wt);

                        }
                        // Invoke the callback with the products list
                        callback.onWorkingTimeFetch(workingTimes);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onWorkingTimeFetch(null);
                    }
                });
    }

    public void getByEmployee(String employeeId, WorkingTimeRepo.WorkingTimeFetchCallback callback) {
        ArrayList<WorkingTime> workingTimes = new ArrayList<>();

        db.collection("working_times").whereEqualTo("userId", employeeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            WorkingTime wt = document.toObject(WorkingTime.class);

                            workingTimes.add(wt);

                        }
                        // Invoke the callback with the products list
                        callback.onWorkingTimeFetch(workingTimes);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onWorkingTimeFetch(null);
                    }
                });
    }

    public void getOneByEmployee(String employeeId, WorkingTimeRepo.SingleWorkingTimeFetchCallback callback) {
        db.collection("working_times").whereEqualTo("userId", employeeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        WorkingTime wt = document.toObject(WorkingTime.class);
                        callback.onSingleWorkingTimeFetch(wt);
                    } else {
                        Log.w("REZ_DB", "Error getting documents or no documents found.", task.getException());
                        callback.onSingleWorkingTimeFetch(null);
                    }
                });
    }

    public interface SingleWorkingTimeFetchCallback {
        void onSingleWorkingTimeFetch(WorkingTime workingTime);
    }


    public interface WorkingTimeFetchCallback {
        default void onWorkingTimeFetch(ArrayList<WorkingTime> workingTimes) {

        }
        default void onWorkingTimeObjectFetched(WorkingTime workingTime, String errorMessage) {

        }
    }
    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }
    public static void update(WorkingTime wt) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference wtRef = db.collection("working_times").document(wt.getId());

        wtRef.update(
                        "startDate", wt.getStartDate(),
                        "endDate", wt.getEndDate(),
                        "mondayStartTime", wt.getMondayStartTime(),
                        "mondayEndTime", wt.getMondayEndTime(),
                        "tuesdayStartTime", wt.getTuesdayStartTime(),
                        "tuesdayEndTime", wt.getTuesdayEndTime(),
                        "wednesdayStartTime", wt.getWednesdayStartTime(),
                        "wednesdayEndTime", wt.getWednesdayEndTime(),
                        "thursdayStartTime", wt.getThursdayStartTime(),
                        "thursdayEndTime", wt.getThursdayEndTime(),
                        "fridayStartTime", wt.getFridayStartTime(),
                        "fridayEndTime", wt.getFridayEndTime(),
                        "saturdayStartTime", wt.getSaturdayStartTime(),
                        "saturdayEndTime", wt.getSaturdayEndTime(),
                        "sundayStartTime", wt.getSundayStartTime(),
                        "sundayEndTime", wt.getSundayEndTime()
                )
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });

    }
}
