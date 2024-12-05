package com.example.eventapp.fragments.administration;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentEditingCategoryBinding;
import com.example.eventapp.fragments.products.ProductForm;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.google.firebase.auth.FirebaseAuth;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductForm#newInstance} factory method to
 * create an instance of this fragment.
 */

public class EditingCategoryFragment extends DialogFragment {

    Category categToEdit;
    private EditText name;
    private EditText description;
    private FragmentEditingCategoryBinding binding;
    public EditingCategoryFragment() {
        // Required empty public constructor
    }


    public static EditingCategoryFragment newInstance(Category category) {
        EditingCategoryFragment fragment = new EditingCategoryFragment();
        Bundle args = new Bundle();
        args.putParcelable("CATEGORY_TO_EDIT", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            categToEdit = getArguments().getParcelable("CATEGORY_TO_EDIT");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEditingCategoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;

    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        name = binding.editTextCategoryName;
        description = binding.editTextCategoryDescription;
        name.setText(categToEdit.getName()); //inicijalizovan
        description.setText(categToEdit.getDescription());//inicijalizovan

        Button btnSubmit = (Button) binding.submitEditCategoryButton;
        btnSubmit.setOnClickListener(v -> {
            if(areFieldsValid())
            {
                updateCategoryObject();
                CategoryRepo.updateCategory(categToEdit, new CategoryRepo.CategUpdateCallback() {
                    @Override
                    public void onUpdateSuccess() {
                        Notification newNotification = new Notification();
                        newNotification.setMessage("1 new category edit - " + categToEdit.getName());
                        newNotification.setReceiverRole(UserType.OWNER);
                        newNotification.setDate(new Date().toString());
                        newNotification.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        NotificationRepo.create(newNotification);

                        //idi na "refresovan" fragment za listu event typova (bez dupliranja na steku!)
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.scroll_categories_list, CategoriesListFragment.newInstance(new ArrayList<>()));
                        transaction.commit();

                        dismiss();
                    }

                    @Override
                    public void onUpdateFailure(String errorMessage) {

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

        return true;
    }

    private void updateCategoryObject() {
        categToEdit.setName(name.getText().toString());
        categToEdit.setDescription(description.getText().toString());

    }
}