package com.example.eventapp.fragments.pricelists;

import android.os.Bundle;

import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.adapters.ProductPriceListAdapter;
import com.example.eventapp.model.Product;
import com.example.eventapp.repositories.ProductRepo;

import java.util.ArrayList;

public class PriceListProduct extends ListFragment {
    private ProductRepo productRepo;
    private ProductPriceListAdapter adapter;

    public PriceListProduct() {
    }

    public static PriceListProduct newInstance() {
        PriceListProduct fragment = new PriceListProduct();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productRepo=new ProductRepo();
        productRepo.getAllProducts(new ProductRepo.ProductFetchCallback() {
            @Override
            public void onProductFetch(ArrayList<Product> products) {
                ProductRepo.ProductFetchCallback.super.onProductFetch(products);
                if (products != null) {
                    adapter=new ProductPriceListAdapter(getActivity(),products);
                    setListAdapter(adapter);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_price_list_product, container, false);
    }
}