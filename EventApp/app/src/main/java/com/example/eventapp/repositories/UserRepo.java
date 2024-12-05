package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.Product;

import com.example.eventapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class UserRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static void getUserById(String userId, UserRepo.UserFetchCallback callback) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onUserObjectFetched(user, null);
                    } else {
                        // Ako dokument ne postoji, korisnik nije pronađen
                        callback.onUserObjectFetched(null, "User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting document", e);
                    callback.onUserObjectFetched(null, e.getMessage());
                });
    }


    public static void getUserByEmail(String email, UserRepo.UserFetchCallback callback) {
        db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Ako je pronađen korisnik sa datim email-om, uzmi prvi dokument iz rezultata upita
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        User user = documentSnapshot.toObject(User.class);
                        callback.onUserObjectFetched(user, null);
                    } else {
                        // Ako nije pronađen korisnik sa datim email-om
                        callback.onUserObjectFetched(null, "User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onUserObjectFetched(null, e.getMessage());
                });
    }

    public static void deleteUserByEmail(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String userId = documentSnapshot.getId();
                        String role = documentSnapshot.toObject(User.class).getType().toString();
                        db.collection("users")
                                .document(userId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("REZ_DB", "Organizer deleted successfully");
                                    if(role.equals("ORGANIZER")){
                                        db.collection("organizers")
                                                .document(userId)
                                                .delete()
                                                .addOnSuccessListener(aVoid1 -> {
                                                    Log.d("REZ_DB", "User deleted successfully");
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.w("REZ_DB", "Error deleting user", e);
                                                });
                                    }
                                    else if(role.equals("EMPLOYEE")){
                                        db.collection("employees")
                                                .document(userId)
                                                .delete()
                                                .addOnSuccessListener(aVoid1 -> {
                                                    Log.d("REZ_DB", "User deleted successfully");
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.w("REZ_DB", "Error deleting user", e);
                                                });
                                    }
                                    else if(role.equals("OWNER")){
                                        db.collection("owners")
                                                .document(userId)
                                                .delete()
                                                .addOnSuccessListener(aVoid1 -> {
                                                    Log.d("REZ_DB", "User deleted successfully");
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.w("REZ_DB", "Error deleting user", e);
                                                });
                                    }

                                })
                                .addOnFailureListener(e -> {
                                    Log.w("REZ_DB", "Error deleting organizer", e);
                                });
                    } else {
                        Log.d("REZ_DB", "Organizer not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                });
    }


    public interface UserFetchCallback {
        default void onUserObjectFetched(User user, String errorMessage){}
        default void onUserFetch(ArrayList<User> users) {

        }

    }
    public interface UserUpdateCallback {
        void onUpdateSuccess();
        void onUpdateFailure(String errorMessage);
    }

    public static void activateUserByEmail(String email, UserRepo.UserUpdateCallback callback) {
        db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String userId = documentSnapshot.getId();
                        String role = documentSnapshot.toObject(User.class).getType().toString();

                        db.collection("users")
                                .document(userId)
                                .update("active", true) // active na true
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("REZ_DB", "User updated successfully");

                                    if(role.equals("ORGANIZER"))
                                    {
                                        db.collection("organizers")
                                                .document(userId)
                                                .update("active", true)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    Log.d("REZ_DB", "User updated successfully");
                                                    callback.onUpdateSuccess();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.w("REZ_DB", "Error updating user", e);
                                                    callback.onUpdateFailure("Error updating user: " + e.getMessage());
                                                });
                                    }
                                    else if(role.equals("EMPLOYEE"))
                                    {
                                        db.collection("employees")
                                                .document(userId)
                                                .update("active", true)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    Log.d("REZ_DB", "User updated successfully");
                                                    callback.onUpdateSuccess();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.w("REZ_DB", "Error updating user", e);
                                                    callback.onUpdateFailure("Error updating user: " + e.getMessage());
                                                });
                                    }
                                    else if(role.equals("OWNER"))
                                    {
                                        db.collection("owners")
                                                .document(userId)
                                                .update("active", true)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    Log.d("REZ_DB", "User updated successfully");
                                                    callback.onUpdateSuccess();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.w("REZ_DB", "Error updating user", e);
                                                    callback.onUpdateFailure("Error updating user: " + e.getMessage());
                                                });
                                    }

                                })
                                .addOnFailureListener(e -> {
                                    Log.w("REZ_DB", "Error updating organizer", e);
                                    callback.onUpdateFailure("Error updating organizer: " + e.getMessage());
                                });
                    } else {
                        String errorMessage = "Organizer not found";
                        Log.d("REZ_DB", errorMessage);
                        callback.onUpdateFailure(errorMessage);
                    }
                })
                .addOnFailureListener(e -> {
                    String errorMessage = "Error getting documents: " + e.getMessage();
                    Log.w("REZ_DB", errorMessage, e);
                    callback.onUpdateFailure(errorMessage);
                });
    }
    public static void update(User p) {
        db.collection("users")
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

    public static void updatePassword(String p, String userId) {
        db.collection("users")
                .document(userId)
                .update("password", p)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot updated with ID: " + userId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error updating document", e);
                    }
                });
    }
    public static void create(User user, UserRepo.UserFetchCallback callback){
        user.setDeactivated(false);
        db.collection("users")
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + user.getId());
                        callback.onUserObjectFetched(user, null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                        callback.onUserObjectFetched(null, e.getMessage());
                    }
                });
    }
    public interface DeleteUserCallback {
        void onUserDeleted(boolean success, String errorMessage);
    }

    public static void deleteUser(String email, UserRepo.DeleteUserCallback callback) {
        db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String userId = documentSnapshot.getId();
                        String role = documentSnapshot.toObject(User.class).getType().toString();
                        db.collection("users")
                                .document(userId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("REZ_DB", "Organizer deleted successfully");
                                    callback.onUserDeleted(true, null);
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("REZ_DB", "Error deleting organizer", e);
                                    callback.onUserDeleted(false, e.getMessage());
                                });
                    } else {
                        // Ako nije pronađen organizator sa datom email adresom
                        Log.d("REZ_DB", "Organizer not found");
                        callback.onUserDeleted(false, "Organizer not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("REZ_DB", "Error getting documents", e);
                    callback.onUserDeleted(false, e.getMessage());
                });
    }

    public void getAll(UserRepo.UserFetchCallback callback) {
        ArrayList<User> users = new ArrayList<>();

        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);

                            users.add(user);

                        }
                        // Invoke the callback with the products list
                        callback.onUserFetch(users);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onUserFetch(null);
                    }
                });
    }

}
