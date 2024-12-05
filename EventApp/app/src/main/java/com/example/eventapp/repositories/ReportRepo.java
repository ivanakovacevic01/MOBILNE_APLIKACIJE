package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Address;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Report;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.Service;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class ReportRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void create(Report report) {
        report.setId("report_"+generateUniqueString());
        db.collection("reports")
                .document(report.getId())
                .set(report)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + report.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                });
    }




    public void getAllUserReports(ReportRepo.ReportFetchCallback callback) {
        ArrayList<Report> reports = new ArrayList<>();

        db.collection("reports")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Report report = document.toObject(Report.class);
                            if(report.getReportedType().equals("korisnik"))
                                reports.add(report);

                        }
                        // Invoke the callback with the products list
                        callback.onReportFetch(reports);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onReportFetch(null);
                    }
                });
    }
    public static void update(Report p) {
        db.collection("reports")
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
    public interface ReportFetchCallback {
        default void onReportFetch(ArrayList<Report> reports) {

        }
        default void onReportObjectFetched(Report report, String errorMessage) {

        }
    }
    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }
/*
    public static void getByOrganizerEmail(String email, ReservationRepo.ReservationFetchCallback callback) {
        Query query = db.collection("reservations").whereEqualTo("organizerEmail", email);
        ArrayList<Reservation> reservations = new ArrayList<>();
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Reservation s = document.toObject(Reservation.class);
                    reservations.add(s);
                }
                callback.onReservationFetch(reservations);

            } else {
                Log.d("ReservtionRepo", "Error getting  by firmId", task.getException());
                callback.onReservationFetch(null);
            }
        });
    }

 */
}
