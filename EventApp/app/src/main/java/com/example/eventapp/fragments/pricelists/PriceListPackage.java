package com.example.eventapp.fragments.pricelists;

import android.os.Bundle;

import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.adapters.PackagePriceListAdapter;
import com.example.eventapp.model.Package;
import com.example.eventapp.repositories.PackageRepo;

import java.util.ArrayList;

public class PriceListPackage extends ListFragment {


    public PriceListPackage() {
        // Required empty public constructor
    }
    private PackageRepo packageRepo;
    private PackagePriceListAdapter adapter;


    public static PriceListPackage newInstance() {
        PriceListPackage fragment = new PriceListPackage();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        packageRepo=new PackageRepo();
        packageRepo.getAllPackages(new PackageRepo.PackageFetchCallback() {
            @Override
            public void onPackageFetch(ArrayList<Package> packages) {
                adapter=new PackagePriceListAdapter(getActivity(),packages);
                setListAdapter(adapter);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_price_list_package, container, false);
    }
}