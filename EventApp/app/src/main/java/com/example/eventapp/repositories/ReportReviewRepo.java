package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Rating;
import com.example.eventapp.model.ReportReview;
import com.example.eventapp.model.Reservation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class ReportReviewRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static void create(ReportReview reportReview, ReportReviewRepo.ReportReviewFetchCallback callback) {
        reportReview.setId("reportReview_"+generateUniqueString());
        db.collection("reportReviews")
                .document(reportReview.getId())
                .set(reportReview)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + reportReview.getId());
                    callback.onReportReviewObjectFetched(reportReview, null);
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onReportReviewObjectFetched(null, e.getMessage());
                });
    }
    public interface ReportReviewFetchCallback {
        default void onReportReviewFetch(ArrayList<ReportReview> reportReviews) {

        }
        default void onReportReviewObjectFetched(ReportReview reportReview, String errorMessage) {

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

    public void getAll(ReportReviewRepo.ReportReviewFetchCallback callback) {
        ArrayList<ReportReview> reports = new ArrayList<>();

        db.collection("reportReviews")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ReportReview reportReview = document.toObject(ReportReview.class);
                            reports.add(reportReview);

                        }
                        // Invoke the callback with the products list
                        callback.onReportReviewFetch(reports);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onReportReviewFetch(null);
                    }
                });
    }

    public static void update(ReportReview reportReview,ReportReviewRepo.ReportReviewFetchCallback callback) {
        db.collection("reportReviews")
                .document(reportReview.getId())
                .set(reportReview)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + reportReview.getId());
                        callback.onUpdateSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error updating document", e);
                        callback.onUpdateFailure("Failed");
                    }
                });
    }
    public static void getById(String id, ReportReviewRepo.ReportReviewFetchCallback callback) {
        db.collection("reportReviews")
                .whereEqualTo("id", id)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        ReportReview report = documentSnapshot.toObject(ReportReview.class);
                        callback.onReportReviewObjectFetched(report, null);
                    } else {
                        callback.onReportReviewObjectFetched(null, "Report not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onReportReviewObjectFetched(null, e.getMessage());
                });
    }

}
