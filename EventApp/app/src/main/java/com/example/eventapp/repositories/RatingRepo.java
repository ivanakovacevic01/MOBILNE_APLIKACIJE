package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Address;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Rating;
import com.example.eventapp.model.Reservation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class RatingRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static void create(Rating rating, RatingRepo.RatingFetchCallback callback) {
        rating.setId("rating_"+generateUniqueString());
        db.collection("ratings")
                .document(rating.getId())
                .set(rating)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + rating.getId());
                    callback.onRatingObjectFetched(rating, null);
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onRatingObjectFetched(null, e.getMessage());
                });
    }
    public interface RatingFetchCallback {
        default void onRatingFetch(ArrayList<Rating> ratings) {

        }
        default void onRatingObjectFetched(Rating rating, String errorMessage) {

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

    public void getByCompanyId(String id,RatingRepo.RatingFetchCallback callback) {
        Query query = db.collection("ratings").whereEqualTo("companyId", id);
        ArrayList<Rating> ratings = new ArrayList<>();
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Rating rating = document.toObject(Rating.class);
                    ratings.add(rating);
                }
                callback.onRatingFetch(ratings);

            } else {
                Log.d("RatingRepo", "Error getting ratings by company id", task.getException());
                callback.onRatingFetch(null);
            }
        });
    }

    public static void update(Rating rating,RatingRepo.RatingFetchCallback callback) {
        db.collection("ratings")
                .document(rating.getId())
                .set(rating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + rating.getId());
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
    public static void delete(String id) {
        db.collection("ratings")
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot successfully deleted: " + id);
                })
                .addOnFailureListener(e -> Log.w("REZ_DB", "Error deleting document " + id, e));
    }
}
