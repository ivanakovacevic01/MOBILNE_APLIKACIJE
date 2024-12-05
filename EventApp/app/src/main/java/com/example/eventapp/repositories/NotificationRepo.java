package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Address;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.NotificationStatus;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.ShowStatus;
import com.example.eventapp.model.UserType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class NotificationRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void create(Notification notification){
        notification.setId("notification_"+generateUniqueString());
        db.collection("notifications")
                .document(notification.getId())
                .set(notification)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + notification.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });
    }

    public static void createNew(Notification notification, NotificationRepo.NotifFetchCallback callback) {
        notification.setId("notification_"+generateUniqueString());
        db.collection("notifications")
                .document(notification.getId())
                .set(notification)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + notification.getId());
                    callback.onNotifObjectFetched(notification, null);
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onNotifObjectFetched(null, e.getMessage());
                });
    }

    public static void getAllForAdmin(NotificationRepo.NotificationFetchCallback callback) {
        ArrayList<Notification> notificiations = new ArrayList<>();

        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification notification = document.toObject(Notification.class);
                            if(notification.getReceiverRole()==UserType.ADMIN)
                                notificiations.add(notification);

                        }
                        // Invoke the callback with the products list
                        callback.onNotificationFetch(notificiations);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onNotificationFetch(null);
                    }
                });
    }

    public static void getAllByReceiverId(String receiverId,NotificationRepo.NotificationFetchCallback callback) {
        ArrayList<Notification> notificiations = new ArrayList<>();

        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification notification = document.toObject(Notification.class);
                            if(notification.getReceiverId().equals(receiverId))
                                notificiations.add(notification);

                        }
                        // Invoke the callback with the products list
                        callback.onNotificationFetch(notificiations);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onNotificationFetch(null);
                    }
                });
    }

    public static void getNewsForAdmin(NotificationRepo.NotificationFetchCallback callback) {
        ArrayList<Notification> notificiations = new ArrayList<>();

        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification notification = document.toObject(Notification.class);
                            if(notification.getStatus()== NotificationStatus.NEW && notification.getReceiverRole()== UserType.ADMIN)
                                notificiations.add(notification);

                        }
                        // Invoke the callback with the products list
                        callback.onNotificationFetch(notificiations);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onNotificationFetch(null);
                    }
                });
    }

    public void getNewsForOwner(NotificationRepo.NotificationFetchCallback callback) {
        ArrayList<Notification> notificiations = new ArrayList<>();

        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification notification = document.toObject(Notification.class);
                            if(notification.getStatus()== NotificationStatus.NEW && notification.getReceiverRole()== UserType.OWNER && notification.getReceiverId().equals(""))
                                notificiations.add(notification);

                        }
                        // Invoke the callback with the products list
                        callback.onNotificationFetch(notificiations);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onNotificationFetch(null);
                    }
                });
    }
    public void getNewsForOwnerById(String id,NotificationRepo.NotificationFetchCallback callback) {
        ArrayList<Notification> notificiations = new ArrayList<>();

        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification notification = document.toObject(Notification.class);
                            if(notification.getStatus()== NotificationStatus.NEW && notification.getReceiverRole().toString().equals("OWNER") && notification.getReceiverId().equals(id))
                                notificiations.add(notification);

                        }
                        // Invoke the callback with the products list
                        callback.onNotificationFetch(notificiations);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onNotificationFetch(null);
                    }
                });
    }
    public void getNewsForEmployee(String id,NotificationRepo.NotificationFetchCallback callback) {
        ArrayList<Notification> notificiations = new ArrayList<>();

        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification notification = document.toObject(Notification.class);
                            if(notification.getStatus()== NotificationStatus.NEW && notification.getReceiverId().equals(id))
                                notificiations.add(notification);

                        }
                        // Invoke the callback with the products list
                        callback.onNotificationFetch(notificiations);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onNotificationFetch(null);
                    }
                });
    }

    public static void createNotifications(ArrayList<Notification> notifications, NotificationFetchCallback callback) {
        CountDownLatch latch = new CountDownLatch(notifications.size());
        ArrayList<String> errors = new ArrayList<>();
        ConcurrentLinkedQueue<Notification> successfulNotifications = new ConcurrentLinkedQueue<>();

        for (Notification notification : notifications) {
            notification.setId("notification_" + generateUniqueString());
            db.collection("notifications")
                    .document(notification.getId())
                    .set(notification)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("NOTIF_DB", "DocumentSnapshot added with ID: " + notification.getId());
                        successfulNotifications.add(notification);
                        latch.countDown();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("NOTIF_DB", "Error adding document", e);
                        errors.add(e.getMessage());
                        latch.countDown();
                    });
        }

        new Thread(() -> {
            try {
                latch.await();
                if (errors.isEmpty()) {
                    callback.onNotificationObjectsFetched(new ArrayList<>(successfulNotifications), null);
                } else {
                    callback.onNotificationObjectsFetched(new ArrayList<>(successfulNotifications), String.join(", ", errors));
                }
            } catch (InterruptedException e) {
                callback.onNotificationObjectsFetched(new ArrayList<>(successfulNotifications), e.getMessage());
            }
        }).start();
    }


    public interface NotifFetchCallback {

        default void onNotifObjectFetched(Notification notification, String errorMessage) {

        }
    }

    public interface NotificationFetchCallback {
        default void onNotificationFetch(ArrayList<Notification> notifications) {

        }
        default void onNotificationObjectsFetched(ArrayList<Notification> notifications, String errorMessage) {

        }
    }
    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }

    public static void updateNotificationStatus(String id) {
        DocumentReference docRef = db.collection("notifications").document(id);
        docRef.update("status", NotificationStatus.SEEN)
                .addOnSuccessListener(aVoid -> Log.d("REZ_DB", "Notification successfully changed"))
                .addOnFailureListener(e -> Log.w("REZ_DB", "Error getting documents.", e));
    }

    public static void updateShowingStatus(String id) {
        DocumentReference docRef = db.collection("notifications").document(id);
        docRef.update("showStatus", ShowStatus.SHOWED)
                .addOnSuccessListener(aVoid -> Log.d("REZ_DB", "Notification successfully changed"))
                .addOnFailureListener(e -> Log.w("REZ_DB", "Error getting documents.", e));
    }
    public void getNewsForOrganizer(String id,NotificationRepo.NotificationFetchCallback callback) {
        ArrayList<Notification> notificiations = new ArrayList<>();

        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification notification = document.toObject(Notification.class);
                            if(notification.getStatus()== NotificationStatus.NEW && notification.getReceiverId().equals(id))
                                notificiations.add(notification);

                        }
                        // Invoke the callback with the products list
                        callback.onNotificationFetch(notificiations);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onNotificationFetch(null);
                    }
                });
    }
}

