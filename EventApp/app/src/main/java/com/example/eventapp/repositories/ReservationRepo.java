package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Address;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.ReportReview;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.ReservationStatus;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class ReservationRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static void createReservation(Reservation newReservation, ReservationRepo.ReservationFetchCallback callback) {
        newReservation.setId("reservation_" + generateUniqueString());
        db.collection("reservations")
                .document(newReservation.getId())
                .set(newReservation)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newReservation.getId());
                    callback.onReservationObjectFetched(newReservation, null);
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onReservationObjectFetched(null, e.getMessage());
                });
    }

    public static void createReservations(ArrayList<Reservation> reservations, ReservationRepo.ReservationFetchCallback callback) {
        CountDownLatch latch = new CountDownLatch(reservations.size());
        ArrayList<String> errors = new ArrayList<>();
        ConcurrentLinkedQueue<Reservation> successfulReservations = new ConcurrentLinkedQueue<>();

        for (Reservation reservation : reservations) {
            reservation.setId("reservation_" + generateUniqueString());
            db.collection("reservations")
                    .document(reservation.getId())
                    .set(reservation)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + reservation.getId());
                        successfulReservations.add(reservation);
                        latch.countDown();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("REZ_DB", "Error adding document", e);
                        errors.add(e.getMessage());
                        latch.countDown();
                    });
        }

        new Thread(() -> {
            try {
                latch.await();
                if (errors.isEmpty()) {
                    callback.onReservationObjectsFetched(new ArrayList<>(successfulReservations), null);
                } else {
                    callback.onReservationObjectsFetched(new ArrayList<>(successfulReservations), String.join(", ", errors));
                }
            } catch (InterruptedException e) {
                callback.onReservationObjectsFetched(new ArrayList<>(successfulReservations), e.getMessage());
            }
        }).start();
    }




    public interface ReservationFetchCallback {
        default void onReservationObjectsFetched(ArrayList<Reservation> reservations, String errorMessage) {
            
        }
        default void onReservationFetch(ArrayList<Reservation> reservations) {

        }
        default void onReservationObjectFetched(Reservation reservation, String errorMessage) {

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

    public void getAll(ReservationRepo.ReservationFetchCallback callback) {
        ArrayList<Reservation> reservations = new ArrayList<>();

        db.collection("reservations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reservation reservation = document.toObject(Reservation.class);

                            boolean hasServiceId = reservation.getServiceId() != null && !reservation.getServiceId().isEmpty();
                            boolean hasPackageId = reservation.getPackageId() != null && !reservation.getPackageId().isEmpty();

                            if ((reservation.getType().toString().equals("Service") || reservation.getType().toString().equals("Package"))
                                    && (hasServiceId ^ hasPackageId)) {
                                reservations.add(reservation);
                            }
                        }
                        callback.onReservationFetch(reservations);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        callback.onReservationFetch(null);
                    }
                });
    }


    public static void getByEmail(String email, ReservationRepo.ReservationFetchCallback callback) {
        Query query = db.collection("reservations").whereEqualTo("organizerEmail", email);
        ArrayList<Reservation> reservations = new ArrayList<>();
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Reservation reservation = document.toObject(Reservation.class);

                    boolean hasServiceId = reservation.getServiceId() != null && !reservation.getServiceId().isEmpty();
                    boolean hasPackageId = reservation.getPackageId() != null && !reservation.getPackageId().isEmpty();

                    if ((reservation.getType().toString().equals("Service") || reservation.getType().toString().equals("Package"))
                            && (hasServiceId ^ hasPackageId)) {
                        reservations.add(reservation);
                    }
                }
                callback.onReservationFetch(reservations);
            } else {
                Log.d("OwnerRepo", "Error getting reservations by organizer email", task.getException());
                callback.onReservationFetch(null);
            }
        });
    }


    public static void getByFirmId(String firmId, ReservationRepo.ReservationFetchCallback callback) {
        Query query = db.collection("reservations").whereEqualTo("firmId", firmId);
        ArrayList<Reservation> reservations = new ArrayList<>();
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Reservation reservation = document.toObject(Reservation.class);
                    if(reservation.getType().toString().equals("Service") || reservation.getType().toString().equals("Package"))
                        reservations.add(reservation);
                }
                callback.onReservationFetch(reservations);

            } else {
                Log.d("OwnerRepo", "Error getting reservations by organizer email", task.getException());
                callback.onReservationFetch(null);
            }
        });
    }

    public static void update(Reservation reservation,ReservationRepo.ReservationFetchCallback callback) {
        db.collection("reservations")
                .document(reservation.getId())
                .set(reservation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + reservation.getId());
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

    public static void update(Reservation p) {
        db.collection("reservations")
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
    public static void getAllAcceptedAndNewByFirmId(String firmId,ReservationRepo.ReservationFetchCallback callback) {
        ArrayList<Reservation> reservations = new ArrayList<>();

        db.collection("reservations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reservation reservation = document.toObject(Reservation.class);
                            if(reservation.getFirmId().equals(firmId) && (reservation.getStatus().equals(ReservationStatus.ACCEPTED) || reservation.getStatus().equals(ReservationStatus.NEW)) )
                                reservations.add(reservation);
                        }
                        callback.onReservationFetch(reservations);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onReservationFetch(null);
                    }
                });
    }

    public static void getAllAccepted(ReservationRepo.ReservationFetchCallback callback) {
        ArrayList<Reservation> reservations = new ArrayList<>();

        db.collection("reservations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reservation reservation = document.toObject(Reservation.class);
                            if(reservation.getStatus().equals(ReservationStatus.ACCEPTED))
                                reservations.add(reservation);
                        }
                        callback.onReservationFetch(reservations);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onReservationFetch(null);
                    }
                });
    }

    public static void getNotFree(Date date, String employeeId, ReservationRepo.ReservationFetchCallback callback) {
        ArrayList<Reservation> reservations = new ArrayList<>();

        db.collection("reservations")// serviceId nije prazan string
                .whereArrayContains("employees", employeeId)  // employees sadrži employeeId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reservation reservation = document.toObject(Reservation.class);
                            if(reservation.getStatus().equals(ReservationStatus.ACCEPTED) && !reservation.getServiceId().equals("")
                            && reservation.getEventDate().toString().equals(date.toString()))
                                reservations.add(reservation);
                        }
                        callback.onReservationFetch(reservations);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onReservationFetch(null);
                    }
                });
    }


    public static void getByEmployeeId(String id, ReservationRepo.ReservationFetchCallback callback) {
        Query query = db.collection("reservations").whereArrayContains("employees", id);
        ArrayList<Reservation> reservations = new ArrayList<>();

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Reservation reservation = document.toObject(Reservation.class);

                    boolean hasServiceId = reservation.getServiceId() != null && !reservation.getServiceId().isEmpty();
                    boolean hasPackageId = reservation.getPackageId() != null && !reservation.getPackageId().isEmpty();

                    if ((reservation.getType().toString().equals("Service") || reservation.getType().toString().equals("Package"))
                            && (hasServiceId ^ hasPackageId)) {
                        reservations.add(reservation);
                    }
                }
                callback.onReservationFetch(reservations);
            } else {
                Log.w("REZ_DB", "Error getting documents.", task.getException());
                callback.onReservationFetch(null);
            }
        });
    }
    /*public static void getByServiceIdAndPackageId(String serviceId, String packageId, ReservationRepo.ReservationFetchCallback callback) {
        Query query = db.collection("reservations").whereEqualTo("serviceId", serviceId);
        ArrayList<Reservation> reservations = new ArrayList<>();

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Reservation reservation = document.toObject(Reservation.class);

                    if (reservation.getPackageId() != null && reservation.getPackageId().equals(packageId)) {
                        reservations.add(reservation);
                    }
                }
                callback.onReservationFetch(reservations);
            } else {
                Log.w("REZ_DB", "Error getting documents.", task.getException());
                callback.onReservationFetch(null);
            }
        });
    }*/
    public static void getServiesByPackageId(String packageId, ReservationRepo.ReservationFetchCallback callback) {
        Query query = db.collection("reservations").whereEqualTo("packageId", packageId);
        ArrayList<Reservation> reservations = new ArrayList<>();

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Reservation reservation = document.toObject(Reservation.class);

                    if (!reservation.getServiceId().isEmpty()) {
                        reservations.add(reservation);
                    }
                }
                callback.onReservationFetch(reservations);
            } else {
                Log.w("REZ_DB", "Error getting documents.", task.getException());
                callback.onReservationFetch(null);
            }
        });
    }

    public static void get(String id, String packageId,ReservationRepo.ReservationFetchCallback callback) {
        db.collection("reservations")
                .whereEqualTo("id", id)
                .whereEqualTo("packageId",packageId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Reservation reservation = documentSnapshot.toObject(Reservation.class);
                        callback.onReservationObjectFetched(reservation, null);
                    } else {
                        callback.onReservationObjectFetched(null, "Organizer not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onReservationObjectFetched(null, e.getMessage());
                });
    }

    public static void getByPackageId( String packageId,ReservationRepo.ReservationFetchCallback callback) {
        db.collection("reservations")
                .whereEqualTo("packageId",packageId)
                .whereEqualTo("serviceId","")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Reservation reservation = documentSnapshot.toObject(Reservation.class);
                        callback.onReservationObjectFetched(reservation, null);
                    } else {
                        callback.onReservationObjectFetched(null, "Organizer not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onReservationObjectFetched(null, e.getMessage());
                });
    }

}
