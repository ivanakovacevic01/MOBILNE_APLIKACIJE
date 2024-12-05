package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class MessageRepo{
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void create(Message message, MessageRepo.MessageFetchCallback callback) {
        message.setId("message_"+generateUniqueString());
        db.collection("messages")
                .document(message.getId())
                .set(message)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + message.getId());
                    callback.onMessageObjectFetched(message, null);
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onMessageObjectFetched(null, e.getMessage());
                });
    }

    public static void update(Message updated) {
        db.collection("messages")
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
    public void getBy2Users(String senderId, String recipientId, MessageRepo.MessageFetchCallback callback) {
        ArrayList<Message> messages = new ArrayList<>();

        db.collection("messages")
                .whereEqualTo("senderId", senderId)
                .whereEqualTo("recipientId", recipientId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Message message = document.toObject(Message.class);
                            if(messages.stream().noneMatch(m -> m.getId().equals(message.getId())))
                                messages.add(message);
                        }
                        db.collection("messages")
                                .whereEqualTo("recipientId", senderId)
                                .whereEqualTo("senderId", recipientId)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task1.getResult()) {
                                            Message message = document.toObject(Message.class);
                                            if(messages.stream().noneMatch(m -> m.getId().equals(message.getId())))
                                                messages.add(message);
                                        }
                                        // Invoke the callback with the combined list of messages
                                        callback.onMessageFetch(messages);
                                    } else {
                                        Log.w("REZ_DB", "Error getting documents.", task1.getException());
                                        // Invoke the callback with null if an error occurs
                                        callback.onMessageFetch(null);
                                    }
                                });
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onMessageFetch(null);
                    }
                });
    }

    public void getByUser(String senderId, MessageRepo.MessageFetchCallback callback) {
        ArrayList<Message> messages = new ArrayList<>();

        db.collection("messages")
                .whereEqualTo("senderId", senderId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Message message = document.toObject(Message.class);
                            if(messages.stream().noneMatch(m -> m.getId().equals(message.getId())))
                                messages.add(message);
                        }
                        db.collection("messages")
                                .whereEqualTo("recipientId", senderId)
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task2.getResult()) {
                                            Message message = document.toObject(Message.class);
                                            if(messages.stream().noneMatch(m -> m.getId().equals(message.getId())))
                                                messages.add(message);
                                        }
                                        // Invoke the callback with the products list
                                        callback.onMessageFetch(messages);
                                    } else {
                                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                                        // Invoke the callback with null if an error occurs
                                        callback.onMessageFetch(null);
                                    }
                                });
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onMessageFetch(null);
                    }
                });
    }
    public interface MessageFetchCallback {
        default void onMessageFetch(ArrayList<Message> messages) {

        }
        default void onMessageObjectFetched(Message m, String errorMessage) {

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
