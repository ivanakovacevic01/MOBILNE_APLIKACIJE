package com.example.eventapp.fragments.administration;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.databinding.FragmentAddingCategoryBinding;
import com.example.eventapp.fragments.products.ProductForm;
import com.example.eventapp.model.Category;
import com.example.eventapp.repositories.CategoryRepo;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductForm#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AddingCategoryFragment extends DialogFragment {

    private EditText name;
    private EditText description;
    private FragmentAddingCategoryBinding binding;
    private Category category;
    private Category newCategory;
    public AddingCategoryFragment() {
        // Required empty public constructor
    }


    public static AddingCategoryFragment newInstance(Category c) {
        AddingCategoryFragment fragment = new AddingCategoryFragment();
        Bundle args = new Bundle();
        args.putParcelable("CATEGORY", c);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            category = getArguments().getParcelable("CATEGORY");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddingCategoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Log.i("uloga", SharedPreferencesManager.getUserRole(getContext()));
        return root;

    }


    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        name = binding.editTextCategoryName;
        description = binding.editTextCategoryDescription;

        Button btnSubmit = (Button) binding.submitNewCategoryButton;
        btnSubmit.setOnClickListener(v -> {
            if(areFieldsValid())
            {
                createNewCategoryObject();
                CategoryRepo.createCategory(newCategory);

                //idi na "refresovan" fragment za listu categorija (bez dupliranja na steku!)
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.scroll_categories_list, CategoriesListFragment.newInstance(new ArrayList<>()));
                transaction.commit();

                dismiss();

            }
        });
    }

    private boolean areFieldsValid() {
        if(name.getText().toString().isEmpty()) {
            name.setError("Name is required.");
            return false;
        }
        if(description.getText().toString().isEmpty()) {
            description.setError("Description is required.");
            return false;
        }

        return true;
    }


    private void createNewCategoryObject() {
        newCategory = new Category();
        newCategory.setName(name.getText().toString());
        newCategory.setDescription(description.getText().toString());


    }

}