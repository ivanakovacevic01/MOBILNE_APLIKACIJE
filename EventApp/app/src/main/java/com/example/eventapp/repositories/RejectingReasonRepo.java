package com.example.eventapp.repositories;

import android.util.Log;

import com.example.eventapp.model.RejectingReason;
import com.example.eventapp.model.ReportReview;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Random;

public class RejectingReasonRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static void create(RejectingReason rejectingReason, RejectingReasonRepo.RejectingReasonFetchCallback callback) {
        rejectingReason.setId("rejectingReason_"+generateUniqueString());
        db.collection("rejectingReasons")
                .document(rejectingReason.getId())
                .set(rejectingReason)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + rejectingReason.getId());
                    callback.onRejectingReasonObjectFetched(rejectingReason, null);
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onRejectingReasonObjectFetched(null, e.getMessage());
                });
    }
    public interface RejectingReasonFetchCallback {
        default void onRejectingReasonFetch(ArrayList<RejectingReason> rejectingReasons) {

        }
        default void onRejectingReasonObjectFetched(RejectingReason rejectingReason, String errorMessage) {

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
