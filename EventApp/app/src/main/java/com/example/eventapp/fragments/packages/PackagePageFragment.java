package com.example.eventapp.fragments.packages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentPackagePageBinding;
import com.example.eventapp.fragments.FragmentTransition;


public class PackagePageFragment extends Fragment {
    private FragmentPackagePageBinding binding;


    public PackagePageFragment() {
        // Required empty public constructor
    }


    public static PackagePageFragment newInstance() {
        PackagePageFragment fragment = new PackagePageFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPackagePageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        FragmentTransition.to(PackageListFragment.newInstance(), getActivity(),
                false, R.id.scroll_package_list);


        return root;

    }
}