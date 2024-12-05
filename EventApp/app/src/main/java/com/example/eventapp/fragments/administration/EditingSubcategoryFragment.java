package com.example.eventapp.fragments.administration;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentEditingSubcategoryBinding;
import com.example.eventapp.fragments.products.ProductForm;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.SubcategoryType;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.google.firebase.auth.FirebaseAuth;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.widget.Spinner;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductForm#newInstance} factory method to
 * create an instance of this fragment.
 */

public class EditingSubcategoryFragment extends DialogFragment {

    Subcategory subcatToEdit;
    private EditText name;
    private EditText description;
    private Spinner subcatType;
    private FragmentEditingSubcategoryBinding binding;
    public EditingSubcategoryFragment() {
        // Required empty public constructor
    }


    public static EditingSubcategoryFragment newInstance(Subcategory subcategory) {
        EditingSubcategoryFragment fragment = new EditingSubcategoryFragment();
        Bundle args = new Bundle();
        args.putParcelable("SUBCAT_TO_EDIT", subcategory);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            subcatToEdit = getArguments().getParcelable("SUBCAT_TO_EDIT");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEditingSubcategoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;

    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        name = binding.editTextCategoryName;
        description = binding.editTextCategoryDescription;
        subcatType = binding.spinnerType;
        name.setText(subcatToEdit.getName()); //inicijalizovan
        description.setText(subcatToEdit.getDescription());//inicijalizovan
        //inicijalizacija spinnera
        SubcategoryType currentSubcatType = subcatToEdit.getSubcategoryType();
        int index = -1;
        for (int i = 0; i < SubcategoryType.values().length; i++) {
            if (SubcategoryType.values()[i] == currentSubcatType) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            subcatType.setSelection(index);
        }


        Button btnSubmit = (Button) binding.submitEditSubcategoryButton;
        btnSubmit.setOnClickListener(v -> {
            if(areFieldsValid())
            {
                updateSubcategoryObject();
                SubcategoryRepo.updateSubcategory(subcatToEdit, new SubcategoryRepo.SubcategoryFetchCallback() {
                    @Override
                    public void onUpdateSuccess() {
                        Notification newNotification = new Notification();
                        newNotification.setDate(new Date().toString());
                        newNotification.setMessage("1 new subcategory edit - " + subcatToEdit.getName());
                        newNotification.setReceiverRole(UserType.OWNER);
                        newNotification.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        NotificationRepo.create(newNotification);

                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.scroll_categories_list, SubcategoriesListFragment.newInstance(subcatToEdit.getCategoryId()));
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

    private void updateSubcategoryObject() {
        subcatToEdit.setName(name.getText().toString());
        subcatToEdit.setDescription(description.getText().toString());
        String selectedSubcategoryTypeString = (String) subcatType.getSelectedItem();
        SubcategoryType selectedSubcategoryType = SubcategoryType.valueOf(selectedSubcategoryTypeString);
        subcatToEdit.setSubcategoryType(selectedSubcategoryType);

    }
}