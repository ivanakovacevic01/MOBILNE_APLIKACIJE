package com.example.eventapp.fragments.registration;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentRegistrationOwnerOtherInfoBinding;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.RegistrationOwnerHelper;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EventTypeRepo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationOwnerOtherInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationOwnerOtherInfoFragment extends Fragment {

    private ArrayList<String> selectedCategoriesIds;
    private ArrayList<String> selectedEventTypesIds;
    ArrayList<Category> categories = new ArrayList<>();   //sve iz tabele
    ArrayList<EventType> eventTypes = new ArrayList<>();   //sve iz tabele

    private RegisterOwnerWorkingTimeFragment nextFragment;
    private FragmentRegistrationOwnerOtherInfoBinding binding;
    private RegistrationOwnerHelper helper;


    public RegistrationOwnerOtherInfoFragment() {
        // Required empty public constructor
    }


    public static RegistrationOwnerOtherInfoFragment newInstance(RegistrationOwnerHelper helper) {
        RegistrationOwnerOtherInfoFragment fragment = new RegistrationOwnerOtherInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("FULL_HELPER", helper);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            helper = getArguments().getParcelable("FULL_HELPER");

        }
        Log.i("helpercic", helper.getOwner().getFirstName());
        Log.i("helpercic", helper.getFirm().getName());

        selectedEventTypesIds = new ArrayList<>();
        selectedCategoriesIds = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRegistrationOwnerOtherInfoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        //tabele
        categories = new ArrayList<>();
        eventTypes = new ArrayList<>();

        //dodavanje dinamicko u tabelu

        CategoryRepo.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
            @Override
            public void onCategoryFetch(ArrayList<Category> storedCategories) {
                if (storedCategories != null) {
                    categories = storedCategories;
                    // Dodavanje dinamicko u tabelu
                    TableLayout categoryTable = view.findViewById(R.id.categoryTable);
                    for (Category categ : categories) {
                        TableRow row = new TableRow(getContext());
                        TextView textView = new TextView(getContext());
                        textView.setText(categ.getName());

                        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                        textView.setLayoutParams(params);
                        row.addView(textView);

                        Button addButton = new Button(getContext());
                        addButton.setText("Add");
                        addButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectedCategoriesIds.add(categ.getId());
                                addButton.setEnabled(false);
                            }
                        });
                        TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        buttonLayoutParams.setMargins(0, 10, 0, 10);
                        addButton.setLayoutParams(buttonLayoutParams);
                        addButton.setBackgroundColor(getResources().getColor(R.color.add_green));
                        row.addView(addButton);
                        categoryTable.addView(row);

                    }

                    //sad dodajem tabelu event typova
                    EventTypeRepo.getAllActicateEventTypes(new EventTypeRepo.EventTypeFetchCallback() {
                        @Override
                        public void onEventTypeFetch(ArrayList<EventType> storedEventTypes) {
                            if (storedEventTypes != null) {
                                eventTypes = storedEventTypes;
                                // Dodavanje dinamicko u tabelu
                                TableLayout eventTypesTable = view.findViewById(R.id.eventTypeTable);
                                for (EventType type : eventTypes) {
                                    TableRow row = new TableRow(getContext());
                                    TextView textView = new TextView(getContext());
                                    textView.setText(type.getName());

                                    TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                                    textView.setLayoutParams(params);
                                    row.addView(textView);

                                    Button addButton = new Button(getContext());
                                    addButton.setText("Add");
                                    addButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            selectedEventTypesIds.add(type.getId());
                                            addButton.setEnabled(false);
                                        }
                                    });
                                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    buttonLayoutParams.setMargins(0, 10, 0, 10);
                                    addButton.setLayoutParams(buttonLayoutParams);
                                    addButton.setBackgroundColor(getResources().getColor(R.color.add_green));
                                    row.addView(addButton);
                                    eventTypesTable.addView(row);

                                }

                            }
                        }
                    });

                }
            }
        });





        return view;
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        RelativeLayout nextButton = view.findViewById(R.id.next3RegistrationOwnerButton);
        nextButton.setOnClickListener(v -> {
            if(areFieldsValid())
            {
                updateHelperObject();
                nextFragment = RegisterOwnerWorkingTimeFragment.newInstance(helper);
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.registrationFragmentContainer, nextFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
    }


    private boolean areFieldsValid() {
        if(selectedCategoriesIds.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle("Error!");
            builder.setMessage("Please select at least one category.");

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
    private void updateHelperObject() {
        Firm firm = helper.getFirm();
        firm.setCategoriesIds(selectedCategoriesIds);
        firm.setEventTypesIds(selectedEventTypesIds);
        helper.setFirm(firm);
    }
}