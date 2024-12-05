package com.example.eventapp.fragments.administration;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentAddingSubcategoryBinding;
import com.example.eventapp.fragments.products.ProductForm;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.SubcategoryType;
import com.example.eventapp.repositories.SubcategoryRepo;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.widget.Spinner;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductForm#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AddingSubcategoryFragment extends DialogFragment {

    private Spinner subcatType;
    private EditText name;
    private EditText description;
    private String categId;
    private Subcategory newSubcat;
    private FragmentAddingSubcategoryBinding binding;
    public AddingSubcategoryFragment() {
        // Required empty public constructor
    }


    public static AddingSubcategoryFragment newInstance(String categoryId) {
        AddingSubcategoryFragment fragment = new AddingSubcategoryFragment();
        Bundle args = new Bundle();
        args.putString("CATEG_ID_ADD", categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            categId = getArguments().getString("CATEG_ID_ADD");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddingSubcategoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;

    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        name = binding.editTextCategoryName;
        description = binding.editTextCategoryDescription;
        subcatType = binding.spinnerType;

        Button btnSubmit = (Button) binding.submitNewSubcategoryButton;
        btnSubmit.setOnClickListener(v -> {
            if(areFieldsValid())
            {
                createNewSubcategoryObject();
                SubcategoryRepo.createSubcategory(newSubcat, new SubcategoryRepo.SubcategoryFetchCallback() {
                    @Override
                    public void onSubcategoryFetched(Subcategory subcategory, String errorMessage) {
                        if (subcategory != null) {
                            Log.d("REZ_DB", "Subcategory created: " + subcategory.getId());
                            //idi na "refresovan" fragment za listu subkategorija (bez dupliranja na steku!)
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.scroll_categories_list, SubcategoriesListFragment.newInstance(categId));
                            transaction.commit();

                            dismiss();
                        } else {
                            Log.w("REZ_DB", "Error creating subcategory: " + errorMessage);
                        }
                    }
                });



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
        if(((String) subcatType.getSelectedItem()).equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle("Error!");
            builder.setMessage("Please select subcategory type.");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }

        return true;
    }


    private void createNewSubcategoryObject() {
        newSubcat = new Subcategory();
        newSubcat.setName(name.getText().toString());
        newSubcat.setDescription(description.getText().toString());
        newSubcat.setCategoryId(categId);
        String selectedSubcategoryTypeString = (String) subcatType.getSelectedItem();
        SubcategoryType selectedSubcategoryType = SubcategoryType.valueOf(selectedSubcategoryTypeString);
        newSubcat.setSubcategoryType(selectedSubcategoryType);


    }
}