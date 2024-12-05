package com.example.eventapp.fragments.administration;

import androidx.fragment.app.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentSubcategorySuggestionPageBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.SubcategorySuggestion;

import java.util.ArrayList;

public class SubcategorySuggestionPageFragment extends Fragment {

    public static ArrayList<SubcategorySuggestion> suggestions = new ArrayList<SubcategorySuggestion>();
    private FragmentSubcategorySuggestionPageBinding binding;


    public static SubcategorySuggestionPageFragment newInstance() {
        return new SubcategorySuggestionPageFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentSubcategorySuggestionPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FragmentTransition.to(SubcategorySuggestionListFragment.newInstance(), getActivity(),
                false, R.id.scroll_suggestions_list);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}