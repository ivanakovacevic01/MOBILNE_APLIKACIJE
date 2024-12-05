package com.example.eventapp.fragments.pricelists;

import android.os.Bundle;

import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.adapters.ServicePriceListAdapter;
import com.example.eventapp.model.Service;
import com.example.eventapp.repositories.ServiceRepo;

import java.util.ArrayList;

public class PriceListService extends ListFragment {

    private ServiceRepo serviceRepo;
    private ServicePriceListAdapter adapter;


    public PriceListService() {
        // Required empty public constructor
    }

    public static PriceListService newInstance() {
        PriceListService fragment = new PriceListService();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceRepo=new ServiceRepo();

        serviceRepo.getAllServices(new ServiceRepo.ServiceFetchCallback() {
            @Override
            public void onServiceFetch(ArrayList<Service> services) {
                ServiceRepo.ServiceFetchCallback.super.onServiceFetch(services);
                adapter=new ServicePriceListAdapter(getActivity(),services);
                setListAdapter(adapter);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_price_list_service, container, false);
    }
}