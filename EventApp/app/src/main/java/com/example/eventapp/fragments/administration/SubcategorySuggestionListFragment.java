package com.example.eventapp.fragments.administration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.eventapp.adapters.subcategories.SubcategorySuggestionListAdapter;
import com.example.eventapp.databinding.FragmentSubcategorySuggestionListBinding;
import com.example.eventapp.model.SubcategorySuggestion;
import com.example.eventapp.repositories.SubcategorySuggestionsRepo;

import java.util.ArrayList;

public class SubcategorySuggestionListFragment extends ListFragment {
    private SubcategorySuggestionListAdapter adapter;
    private ArrayList<SubcategorySuggestion> mSuggestions;
    private FragmentSubcategorySuggestionListBinding binding;
    private SubcategorySuggestionsRepo subsugRepository;

    public static SubcategorySuggestionListFragment newInstance(){
        SubcategorySuggestionListFragment fragment = new SubcategorySuggestionListFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subsugRepository = new SubcategorySuggestionsRepo();

        subsugRepository.getPendingSubcategorySuggestions(new SubcategorySuggestionsRepo.SubcategorySuggestionFetchCallback() {
            @Override
            public void onSubcategorySuggestionFetch(ArrayList<SubcategorySuggestion> suggestions) {
                if (suggestions != null) {
                    adapter = new SubcategorySuggestionListAdapter(getActivity(), suggestions);
                    setListAdapter(adapter);

                }
            }
        });
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("ShopApp", "onCreateView Categories List Fragment");
        binding = FragmentSubcategorySuggestionListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
