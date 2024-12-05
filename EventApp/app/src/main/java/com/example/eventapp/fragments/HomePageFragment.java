package com.example.eventapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentEventBinding;
import com.example.eventapp.databinding.HomePageFragmentBinding;

public class HomePageFragment extends Fragment {
    private HomePageFragmentBinding binding;


    public HomePageFragment() {
        // Required empty public constructor
    }


    public static HomePageFragment newInstance() {
        HomePageFragment fragment = new HomePageFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = HomePageFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        FragmentTransition.to(OverviewFragment.newInstance(), getActivity(),
                false, R.id.home_page_fragment);


        return root;

    }

}
