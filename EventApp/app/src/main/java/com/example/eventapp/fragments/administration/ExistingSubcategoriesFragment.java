package com.example.eventapp.fragments.administration;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentExistingSubcategoriesBinding;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.SubcategorySuggestion;
import com.example.eventapp.model.SubcategoryType;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.example.eventapp.repositories.SubcategorySuggestionsRepo;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExistingSubcategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExistingSubcategoriesFragment extends DialogFragment {

    ArrayList<Subcategory> subcategories;   //sve subkategorije
    private FragmentExistingSubcategoriesBinding binding;
    private SubcategorySuggestion toReject;
    public ExistingSubcategoriesFragment() {
        // Required empty public constructor
    }

    public static ExistingSubcategoriesFragment newInstance(SubcategorySuggestion suggestionToApprove) {
        ExistingSubcategoriesFragment fragment = new ExistingSubcategoriesFragment();
        Bundle args = new Bundle();
        args.putParcelable("SUG_TO_APPROVE", suggestionToApprove);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            toReject = getArguments().getParcelable("SUG_TO_APPROVE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentExistingSubcategoriesBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        //tabela sa subkategorijama
        subcategories = new ArrayList<>();

        SubcategoryRepo.getAllSubcategories(new SubcategoryRepo.SubcategoryFetchCallback() {
            @Override
            public void onSubcategoryFetch(ArrayList<Subcategory> storedSubcategories) {
                if (storedSubcategories != null) {
                    storedSubcategories.removeIf(s -> !s.getSubcategoryType().toString().equals(toReject.getSubcategoryType().toString())); //nudim samo subkategorije za servis ili samo subkategorije za proizvod
                    subcategories = storedSubcategories;

                    // Dodavanje dinamicko u tabelu
                    TableLayout subcategoryTable = view.findViewById(R.id.existingSubcategoriesTable);
                    for (Subcategory subcateg : subcategories) {
                        TableRow row = new TableRow(getContext());
                        TextView textView = new TextView(getContext());
                        textView.setText(subcateg.getName());

                        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                        textView.setLayoutParams(params);
                        row.addView(textView);

                        Button addButton = new Button(getContext());
                        addButton.setText("Add");
                        addButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Existing subcategory");
                                builder.setMessage("Are you sure you want to add this subcategory?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                SubcategorySuggestionsRepo.reject(toReject.getId(), new SubcategorySuggestionsRepo.SubcategorySuggestionUpdateCallback() {
                                                    @Override
                                                    public void onUpdateSuccess() {

                                                                    //kreiram uslugu/proizvod
                                                        if(toReject.getSubcategoryType().toString().equals(SubcategoryType.PRODUCT.toString())) {
                                                            Product toCreate = toReject.getProduct();
                                                            toCreate.setSubcategory(subcateg.getId());
                                                            Notification newNotification = new Notification();
                                                            newNotification.setDate(new Date().toString());
                                                            newNotification.setMessage("1 product with existing subcategory created - " + subcateg.getName());
                                                            newNotification.setReceiverRole(UserType.OWNER);
                                                            newNotification.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                            newNotification.setReceiverId(toReject.getUserId());
                                                            NotificationRepo.create(newNotification);
                                                            ProductRepo.createProduct(toCreate);
                                                        }

                                                        else {
                                                            Service toCreate = toReject.getService();
                                                            toCreate.setSubcategory(subcateg.getId());
                                                            Notification newNotification = new Notification();
                                                            newNotification.setMessage("1 service with existing subcategory created - " + subcateg.getName());
                                                            newNotification.setReceiverRole(UserType.OWNER);
                                                            newNotification.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                            newNotification.setReceiverId(toReject.getUserId());
                                                            newNotification.setDate(new Date().toString());
                                                            NotificationRepo.create(newNotification);
                                                            ServiceRepo.createService(toCreate);
                                                        }

                                                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                                                        transaction.replace(R.id.scroll_suggestions_list, SubcategorySuggestionListFragment.newInstance());
                                                        transaction.commit();

                                                        dismiss();
                                                        Toast.makeText(getContext(), "Successfully done.", Toast.LENGTH_SHORT).show();


                                                    }

                                                    @Override
                                                    public void onUpdateFailure(String error) {
                                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton("No", null);


                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });
                        TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        buttonLayoutParams.setMargins(0, 10, 0, 10);
                        addButton.setLayoutParams(buttonLayoutParams);
                        addButton.setBackgroundColor(getResources().getColor(R.color.add_green));
                        row.addView(addButton);
                        subcategoryTable.addView(row);

                    }

                }
            }
        });


        return view;
    }
}