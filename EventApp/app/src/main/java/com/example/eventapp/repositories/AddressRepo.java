package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.R;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.WorkingTime;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Random;

public class AddressRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void create(Address address, AddressRepo.AddressFetchCallback callback) {
        address.setId("address_"+generateUniqueString());
        db.collection("addresses")
                .document(address.getId())
                .set(address)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REZ_DB", "DocumentSnapshot added with ID: " + address.getId());
                    callback.onAddressObjectFetched(address, null);
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error adding document", e);
                    callback.onAddressObjectFetched(null, e.getMessage());
                });
    }



    public static void updateUserIdByOldUserId(String oldUserId, String newUserId, AddressRepo.AddressFetchCallback callback) {
        db.collection("addresses")
                .whereEqualTo("userId", oldUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Ako postoje dokumenti koji odgovaraju upitu
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String addressId = document.getId();
                            db.collection("addresses")
                                    .document(addressId)
                                    .update("userId", newUserId)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("REZ_DB", "Document updated with new userId: " + newUserId);
                                        // Ako je ažuriranje uspešno, prosleđujemo novi userId i adresu u callback
                                        Address updatedAddress = new Address();
                                        updatedAddress.setId(addressId);
                                        updatedAddress.setUserId(newUserId);
                                        callback.onAddressObjectFetched(updatedAddress, null);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Ako dođe do greške prilikom ažuriranja
                                        Log.w("REZ_DB", "Error updating document", e);
                                        callback.onAddressObjectFetched(null, e.getMessage());
                                    });
                        }
                    } else {
                        // Ako nema dokumenata koji odgovaraju upitu
                        callback.onAddressObjectFetched(null, "No address found for the given oldUserId");
                    }
                })
                .addOnFailureListener(e -> {
                    // Ako dođe do greške prilikom dobavljanja adresa
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onAddressObjectFetched(null, e.getMessage());
                });
    }






    public void getAll(AddressFetchCallback callback) {
        ArrayList<Address> addresses = new ArrayList<>();

        db.collection("addresses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Address address = document.toObject(Address.class);

                            addresses.add(address);

                        }
                        // Invoke the callback with the products list
                        callback.onAddressFetch(addresses);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onAddressFetch(null);
                    }
                });
    }

    public void getByUser(String userId, AddressFetchCallback callback) {
        ArrayList<Address> addresses = new ArrayList<>();

        db.collection("addresses")
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Address address = document.toObject(Address.class);
                            addresses.add(address);
                        }
                        // Invoke the callback with the products list
                        callback.onAddressFetch(addresses);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onAddressFetch(null);
                    }
                });
    }

    public void getByFirm(String firmId, AddressFetchCallback callback) {
        ArrayList<Address> addresses = new ArrayList<>();

        db.collection("addresses")
                .whereEqualTo("firmId", firmId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Address address = document.toObject(Address.class);
                            addresses.add(address);
                        }
                        // Invoke the callback with the products list
                        callback.onAddressFetch(addresses);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onAddressFetch(null);
                    }
                });
    }

    public static void update(Address updated) {
        db.collection("addresses")
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
    public interface AddressFetchCallback {
        default void onAddressFetch(ArrayList<Address> addresses) {

        }
        default void onAddressObjectFetched(Address address, String errorMessage) {

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
