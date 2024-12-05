package com.example.eventapp.repositories;

import android.util.Log;

import com.example.eventapp.model.Address;
import com.example.eventapp.model.Deadline;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class DeadlineRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void create(Deadline deadline, DeadlineRepo.DeadlineFetchCallback callback) {
        deadline.setId("deadline_"+generateUniqueString());
        db.collection("deadlines")
                .document(deadline.getId())
                .set(deadline)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + deadline.getId());
                    callback.onDeadlineObjectFetched(deadline, null);
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onDeadlineObjectFetched(null, e.getMessage());
                });
    }

    public interface DeadlineFetchCallback {
        default void onDeadlineFetch(ArrayList<Deadline> deadlines) {

        }
        default void onDeadlineObjectFetched(Deadline deadline, String errorMessage) {

        }
    }
    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }

    public void getByOrganizer(String email, DeadlineRepo.DeadlineFetchCallback callback) {
        ArrayList<Deadline> deadlines = new ArrayList<>();

        db.collection("deadlines")
                .whereEqualTo("organizerEmail", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                        Date today = new Date();
                        Deadline closestDeadline = null;
                        long minDifference = Long.MAX_VALUE;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Deadline deadline = document.toObject(Deadline.class);
                            try {
                                Date deadlineDate = dateFormat.parse(deadline.getDate());
                                long difference = Math.abs(deadlineDate.getTime() - today.getTime());

                                if (difference < minDifference) {
                                    minDifference = difference;
                                    closestDeadline = deadline;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        ArrayList<Deadline> result = new ArrayList<>();
                        if (closestDeadline != null) {
                            result.add(closestDeadline);
                        }
                        callback.onDeadlineFetch(result);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        callback.onDeadlineFetch(null);
                    }
                });
    }
}
