package com.example.eventapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventapp.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class ProductRepo {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createProduct(Product newProduct){
        newProduct.setId("product_"+generateUniqueString());
        db.collection("products")
                .document(newProduct.getId())
                .set(newProduct)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + newProduct.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });
    }

    public static void deleteProduct(String id){
        DocumentReference docRef = db.collection("products").document(id);
        docRef.update("deleted", true)
                .addOnSuccessListener(aVoid -> Log.d("REZ_DB", "Product successfully changed"))
                .addOnFailureListener(e -> Log.w("REZ_DB", "Error getting documents.", e));
    }

    public void getAllProducts(ProductFetchCallback callback) {
        ArrayList<Product> products = new ArrayList<>();

        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            if (!product.getDeleted()) {
                                products.add(product);
                            }
                        }
                        // Invoke the callback with the products list
                        callback.onProductFetch(products);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onProductFetch(null);
                    }
                });
    }
    public static void updateProduct(Product p) {
        db.collection("products")
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


    public interface ProductFetchCallback {
        default void onProductFetch(ArrayList<Product> products) {
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


    public static void containsCategory(String categoryId, ProductFetchCallback callback) {
        db.collection("products")
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

    public static void containsSubcategory(String subcategoryId, ProductFetchCallback callback) {
        db.collection("products")
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

    public void getById(ProductFetchCallback callback, String id) {
        ArrayList<Product> products = new ArrayList<>();

        db.collection("products").whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            if (!product.getDeleted()) {
                                products.add(product);
                            }
                        }
                        // Invoke the callback with the products list
                        callback.onProductFetch(products);
                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                        // Invoke the callback with null if an error occurs
                        callback.onProductFetch(null);
                    }
                });
    }

    public static void getByFirmId(String firmId, ProductRepo.ProductFetchCallback callback) {
        Query query = db.collection("products").whereEqualTo("firmId", firmId);
        ArrayList<Product> products = new ArrayList<>();
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Product p = document.toObject(Product.class);
                    products.add(p);
                }
                callback.onProductFetch(products);

            } else {
                Log.d("ProductRepo", "Error getting products by firmId", task.getException());
                callback.onProductFetch(null);
            }
        });
    }
}
