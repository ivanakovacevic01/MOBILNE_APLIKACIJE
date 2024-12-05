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
import com.example.eventapp.adapters.subcategories.SubcategoriesListAdapter;
import com.example.eventapp.databinding.FragmentSubcategoriesListBinding;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SubcategoriesListFragment extends ListFragment {
    private SubcategoriesListAdapter adapter;
    private String categId;
    private ArrayList<Subcategory> mSubcategories;
    private FragmentSubcategoriesListBinding binding;
    private SubcategoryRepo subcategoryRepository;

    public static SubcategoriesListFragment newInstance(String categoryId){
        SubcategoriesListFragment fragment = new SubcategoriesListFragment();
        Bundle args = new Bundle();
        args.putString("CATEG_ID", categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categId = getArguments().getString("CATEG_ID");
        subcategoryRepository = new SubcategoryRepo();
        //preuzimam potkategorije po categId
        subcategoryRepository.getSubcategoriesByCategoryId(getArguments().getString("CATEG_ID"), new SubcategoryRepo.SubcategoryFetchCallback() {
            @Override
            public void onSubcategoryFetch(ArrayList<Subcategory> subcategories) {
                if (subcategories != null) {
                    adapter = new SubcategoriesListAdapter(getActivity(), subcategories);
                    setListAdapter(adapter);
                    //mozda zatreba ovo ispod posle
                    /*adapter.setEventTypeUpdateListener(new EventTypesListAdapter.EventTypeUpdateListener() {
                        @Override
                        public void onEventTypesUpdated(ArrayList<EventType> eventTypes) {
                            // Osvežite adapter sa novim podacima
                            adapter.clear();
                            adapter.addAll(eventTypes);
                            adapter.notifyDataSetChanged();
                        }
                    });*/
                }
            }
        });
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("ShopApp", "onCreateView Categories List Fragment");
        binding = FragmentSubcategoriesListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FloatingActionButton button = root.findViewById(R.id.floating_action_add_subcateg_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddingSubcategoryFragment dialog = AddingSubcategoryFragment.newInstance(categId);
                dialog.show(requireActivity().getSupportFragmentManager(), "AddingSubcategoryFragment");            }
        });
        return root;


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
