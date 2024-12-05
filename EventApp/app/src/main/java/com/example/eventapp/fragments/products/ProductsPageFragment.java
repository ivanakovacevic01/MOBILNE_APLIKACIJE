package com.example.eventapp.fragments.products;

import androidx.fragment.app.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentProductsPageBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProductsPageFragment extends Fragment {

    public static ArrayList<Product> products = new ArrayList<Product>();
    private FragmentProductsPageBinding binding;
    private boolean isFirstSelection = true;

    public static ProductsPageFragment newInstance() {
        return new ProductsPageFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentProductsPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        prepareProductList(products);


        FragmentTransition.to(ProductsListFragment.newInstance(products), getActivity(),
                false, R.id.scroll_products_list);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void prepareProductList(ArrayList<Product> products) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            products.add(product);
                        }

                        for (Product product : products) {
                            Log.d("REZ_DB", "Product: " + product.toString());
                        }

                    } else {
                        Log.w("REZ_DB", "Error getting documents.", task.getException());
                    }
                });

    }

}
