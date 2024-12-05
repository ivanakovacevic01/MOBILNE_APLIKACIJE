package com.example.eventapp.fragments.administration;

import androidx.fragment.app.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentCategoriesPageBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Category;

import java.util.ArrayList;

public class CategoriesPageFragment extends Fragment {

    public static ArrayList<Category> categories = new ArrayList<Category>();
    private FragmentCategoriesPageBinding binding;


    public static CategoriesPageFragment newInstance() {
        return new CategoriesPageFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentCategoriesPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        FragmentTransition.to(CategoriesListFragment.newInstance(categories), getActivity(),
                false, R.id.scroll_categories_list);



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}