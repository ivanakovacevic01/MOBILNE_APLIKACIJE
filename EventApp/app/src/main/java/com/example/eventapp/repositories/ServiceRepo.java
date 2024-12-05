package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Service;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class ServiceRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createService(Service newService){
        newService.setId("service_"+generateUniqueString());
        db.collection("services")
                .document(newService.getId())
                .set(newService)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newService.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });
    }

    public static void deleteService(String id){
        DocumentReference docRef = db.collection("services").document(id);
        docRef.update("deleted", true)
                .addOnSuccessListener(aVoid -> Log.d("REZ_DB", "Service successfully changed"))
                .addOnFailureListener(e -> Log.w("REZ_DB", "Error getting documents.", e));
    }

    public void getAllServices(ServiceRepo.ServiceFetchCallback callback) {
        ArrayList<Service> services = new ArrayList<>();

        db.collection("services")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Service service = document.toObject(Service.class);
                            if (!service.getDeleted()) {
                                services.add(service);
                            }
                        }
                        // Invoke the callback with the products list
                        callback.onServiceFetch(services);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onServiceFetch(null);
                    }
                });
    }
    public static void updateService(Service s) {
        db.collection("services")
                .document(s.getId())
                .set(s)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + s.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error updating document", e);
                    }
                });
    }
    public interface ServiceFetchCallback {
        default void onServiceFetch(ArrayList<Service> services) {

        }
        default void onServiceObjectFetched(Service service, String errorMessage) {

        }
        default void onResult(boolean contains) {

        }
    }
    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }

    public static void containsCategory(String categoryId, ServiceRepo.ServiceFetchCallback callback) {
        db.collection("services")
                .whereEqualTo("category", categoryId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onResult(!task.getResult().isEmpty());
                    } else {
                        Log.d("ProductRepo", "Error getting products: ", task.getException());
                        callback.onResult(false);
                    }
                });
    }

    public static void containsSubcategory(String subcategoryId, ServiceRepo.ServiceFetchCallback callback) {
        db.collection("services")
                .whereEqualTo("subcategory", subcategoryId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onResult(!task.getResult().isEmpty());
                    } else {
                        Log.d("ProductRepo", "Error getting products: ", task.getException());
                        callback.onResult(false);
                    }
                });
    }

    public void getById(String id,ServiceRepo.ServiceFetchCallback callback) {
        db.collection("services")
                .whereEqualTo("id", id)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Service service = documentSnapshot.toObject(Service.class);
                        callback.onServiceObjectFetched(service, null);
                    } else {
                        callback.onServiceObjectFetched(null, "Service not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onServiceObjectFetched(null, e.getMessage());
                });
    }
    public static void getByFirmId(String firmId, ServiceRepo.ServiceFetchCallback callback) {
        Query query = db.collection("services").whereEqualTo("firmId", firmId);
        ArrayList<Service> services = new ArrayList<>();
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Service s = document.toObject(Service.class);
                    services.add(s);
                }
                callback.onServiceFetch(services);

            } else {
                Log.d("ServiceRepo", "Error getting  by firmId", task.getException());
                callback.onServiceFetch(null);
            }
        });
    }
}
