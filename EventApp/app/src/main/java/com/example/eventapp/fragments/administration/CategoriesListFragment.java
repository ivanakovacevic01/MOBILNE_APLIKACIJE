package com.example.eventapp.fragments.administration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.eventapp.R;
import com.example.eventapp.adapters.categories.CategoriesListAdapter;
import com.example.eventapp.databinding.FragmentCategoriesListBinding;
import com.example.eventapp.model.Category;
import com.example.eventapp.repositories.CategoryRepo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class CategoriesListFragment extends ListFragment {
    private CategoriesListAdapter adapter;
    private ArrayList<Category> mCategories;
    private FragmentCategoriesListBinding binding;
    private CategoryRepo categoryRepository;

    public static CategoriesListFragment newInstance(ArrayList<Category> categories){
        CategoriesListFragment fragment = new CategoriesListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("CATEG_LIST", categories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryRepository = new CategoryRepo();
        categoryRepository.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
            @Override
            public void onCategoryFetch(ArrayList<Category> categories) {
                if (categories != null) {
                    adapter = new CategoriesListAdapter(getActivity(), categories);
                    setListAdapter(adapter);

                }
            }
        });

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("ShopApp", "onCreateView Categories List Fragment");
        binding = FragmentCategoriesListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FloatingActionButton button = root.findViewById(R.id.floating_action_add_categ_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddingCategoryFragment dialog = new AddingCategoryFragment();
                dialog.show(requireActivity().getSupportFragmentManager(), "AddingCategoryFragment");            }
        });
        return root;


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
