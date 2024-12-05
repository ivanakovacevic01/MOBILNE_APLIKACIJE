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

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentEditingSuggestionBinding;
import com.example.eventapp.fragments.products.ProductForm;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.SubcategorySuggestion;
import com.example.eventapp.model.SubcategoryType;
import com.example.eventapp.model.SuggestionStatus;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.example.eventapp.repositories.SubcategorySuggestionsRepo;
import com.google.firebase.auth.FirebaseAuth;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.widget.Toast;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductForm#newInstance} factory method to
 * create an instance of this fragment.
 */

public class EditingSuggestionFragment extends DialogFragment {

    SubcategorySuggestion suggestionToEdit;
    private EditText name;
    private EditText description;
    private FragmentEditingSuggestionBinding binding;
    private Subcategory newSubcat;
    public EditingSuggestionFragment() {
        // Required empty public constructor
    }


    public static EditingSuggestionFragment newInstance(SubcategorySuggestion suggestion) {
        EditingSuggestionFragment fragment = new EditingSuggestionFragment();
        Bundle args = new Bundle();
        args.putParcelable("SUG_TO_EDIT", suggestion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            suggestionToEdit = getArguments().getParcelable("SUG_TO_EDIT");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEditingSuggestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;

    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        name = binding.editTextCategoryName;
        description = binding.editTextCategoryDescription;
        name.setText(suggestionToEdit.getName()); //inicijalizovan
        description.setText(suggestionToEdit.getDescription());//inicijalizovan



        Button btnSubmit = (Button) binding.submitEditSubcategoryButton;
        btnSubmit.setOnClickListener(v -> {
            if(areFieldsValid())
            {
                updateSuggestionObject();

                SubcategorySuggestionsRepo.updateSubcategorySuggestion(suggestionToEdit, new SubcategorySuggestionsRepo.SubcategorySuggestionUpdateCallback() {
                    @Override
                    public void onUpdateSuccess() {

                        createNewSubcategoryObject(suggestionToEdit);
                        SubcategoryRepo.createSubcategory(newSubcat, new SubcategoryRepo.SubcategoryFetchCallback() {
                            @Override
                            public void onSubcategoryFetched(Subcategory subcategory, String errorMessage) {
                                if (subcategory != null) {

                                    //kreiram uslugu/proizvod
                                    if(suggestionToEdit.getSubcategoryType().toString().equals(SubcategoryType.PRODUCT.toString())) {
                                        Product toCreate = suggestionToEdit.getProduct();
                                        toCreate.setSubcategory(subcategory.getId());
                                        Notification newNotification = new Notification();
                                        newNotification.setDate(new Date().toString());
                                        newNotification.setMessage("1 new subcategory suggestion updated, product created - " + subcategory.getName());
                                        newNotification.setReceiverRole(UserType.OWNER);
                                        newNotification.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        newNotification.setReceiverId(suggestionToEdit.getUserId());
                                        NotificationRepo.create(newNotification);

                                        ProductRepo.createProduct(toCreate);
                                    }

                                    else {
                                        Service toCreate = suggestionToEdit.getService();
                                        toCreate.setSubcategory(subcategory.getId());
                                        Notification newNotification = new Notification();
                                        newNotification.setMessage("1 new subcategory suggestion updated, service created - " + subcategory.getName());
                                        newNotification.setDate(new Date().toString());
                                        newNotification.setReceiverRole(UserType.OWNER);
                                        newNotification.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        newNotification.setReceiverId(suggestionToEdit.getUserId());
                                        NotificationRepo.create(newNotification);
                                        ServiceRepo.createService(suggestionToEdit.getService());
                                    }


                                    Toast.makeText(getContext(), "Successfully updated.", Toast.LENGTH_SHORT).show();
                                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                                    transaction.replace(R.id.scroll_suggestions_list, SubcategorySuggestionListFragment.newInstance());
                                    transaction.commit();

                                    dismiss();

                                }
                                else {
                                    Log.w("REZ_DB", "Error creating subcategory: " + errorMessage);
                                }
                            }
                        });
                    }

                    @Override
                    public void onUpdateFailure(String error) {
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
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

    private void updateSuggestionObject() {
        suggestionToEdit.setName(name.getText().toString());
        suggestionToEdit.setDescription(description.getText().toString());
        suggestionToEdit.setStatus(SuggestionStatus.ACCEPTED);  //cim se updatuje, status je prihvacen

    }

    private void createNewSubcategoryObject(SubcategorySuggestion suggestion) {
        newSubcat = new Subcategory();
        newSubcat.setName(suggestion.getName().toString());
        newSubcat.setDescription(suggestion.getDescription().toString());
        newSubcat.setCategoryId(suggestion.getCategory());
        newSubcat.setSubcategoryType(suggestion.getSubcategoryType());


    }
}