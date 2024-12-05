package com.example.eventapp.fragments.administration;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentEditingEventTypeBinding;
import com.example.eventapp.fragments.employees.FirmProfileFragment;
import com.example.eventapp.fragments.employees.OwnerProfileFragment;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.SubcategoryRepo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddingEventTypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditingEventTypeFragment extends DialogFragment {

    EventType eventTypeToEdit;
    private EditText description;
    private ArrayList<String> selectedSubcategories;
    private ArrayList<Subcategory> subcategories; // sve iz tabele
    private TableLayout subcategoryTable;
    private FragmentEditingEventTypeBinding binding;
    public String firmId = "";


    private void addSubcategory(String subcategoryId) {
        selectedSubcategories.add(subcategoryId);
        refreshSubcategoriesTable();
    }

    private void removeSubcategory(String subcategoryId) {
        ArrayList<String> updatedList = new ArrayList<>();
        for (String item : selectedSubcategories) {
            if (!item.equals(subcategoryId)) {
                updatedList.add(item);
            }
        }
        selectedSubcategories = updatedList;
        refreshSubcategoriesTable();
    }
    public EditingEventTypeFragment() {
        // Required empty public constructor
    }

    public static EditingEventTypeFragment newInstance(EventType eventType) {
        EditingEventTypeFragment fragment = new EditingEventTypeFragment();
        Bundle args = new Bundle();
        args.putParcelable("EVENT_TYPE_TO_EDIT", eventType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventTypeToEdit = getArguments().getParcelable("EVENT_TYPE_TO_EDIT");
            selectedSubcategories = (ArrayList<String>) eventTypeToEdit.getSuggestedSubcategoriesIds();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditingEventTypeBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        TextView typeName = root.findViewById(R.id.textViewNameEventType);
        typeName.setText(eventTypeToEdit.getName());


        subcategories = new ArrayList<>();
        SubcategoryRepo.getAllSubcategories(new SubcategoryRepo.SubcategoryFetchCallback() {
            @Override
            public void onSubcategoryFetch(ArrayList<Subcategory> storedSubcategories) {
                if (storedSubcategories != null) {
                    subcategories = storedSubcategories;
                    // Dodavanje dinamicko u tabelu
                    subcategoryTable = root.findViewById(R.id.editSuggestedSubcategoriesTable);
                    for (Subcategory subcateg : subcategories) {
                        TableRow row = new TableRow(getContext());
                        TextView textView = new TextView(getContext());
                        textView.setText(subcateg.getName());

                        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                        textView.setLayoutParams(params);
                        row.addView(textView);

                        Button actionButton;

                        if (isSelected(subcateg.getId())) {
                            // ako je subkategorija dodata vec, mogu je ukloniti
                            actionButton = new Button(getContext());
                            actionButton.setText("Remove");
                            actionButton.setBackgroundColor(getResources().getColor(R.color.remove_red));
                            actionButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    removeSubcategory(subcateg.getId());

                                    System.out.println(selectedSubcategories.size());

                                }
                            });
                        } else {
                            actionButton = new Button(getContext());
                            actionButton.setText("Add");
                            actionButton.setBackgroundColor(getResources().getColor(R.color.add_green));
                            actionButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addSubcategory(subcateg.getId());
                                    //actionButton.setText("Remove");
                                    System.out.println(selectedSubcategories.size());

                                }
                            });
                        }
                        TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        buttonLayoutParams.setMargins(0, 10, 0, 10);
                        actionButton.setLayoutParams(buttonLayoutParams);
                        row.addView(actionButton);
                        subcategoryTable.addView(row);
                    }
                }
            }
        });


        return root;
    }


    private boolean isSelected(String subcategoryId) {
        for (String id : selectedSubcategories) {
            if (id.equals(subcategoryId)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        description = binding.editTextEventTypeDescription;
        description.setText(eventTypeToEdit.getDescription());//inicijalizizovan

        Button btnSubmit = (Button) binding.submitEditEventTypeButton;
        btnSubmit.setOnClickListener(v -> {
            if(areFieldsValid())
            {
                updateEventTypeObject();
                EventTypeRepo.updateEventType(eventTypeToEdit);


                if(!firmId.isEmpty())
                {
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.scroll_profile, FirmProfileFragment.newInstance(firmId,""));
                    transaction.commit();

                }else {

                    //idi na "refresovan" fragment za listu event typova (bez dupliranja na steku!)
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.scroll_event_types_list, EventTypesListFragment.newInstance(new ArrayList<>()));
                    transaction.commit();
                }

                dismiss();

            }
        });
    }

    private void refreshSubcategoriesTable() {
        subcategoryTable.removeAllViews();
        for (Subcategory subcateg : subcategories) {
            TableRow row = new TableRow(getContext());
            TextView textView = new TextView(getContext());
            textView.setText(subcateg.getName());

            TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            textView.setLayoutParams(params);
            row.addView(textView);

            Button actionButton = new Button(getContext());
            actionButton.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelected(subcateg.getId())) {
                        removeSubcategory(subcateg.getId());
                    } else {
                        addSubcategory(subcateg.getId());
                    }
                }
            });

            if (isSelected(subcateg.getId())) {
                actionButton.setText("Remove");
                actionButton.setBackgroundColor(getResources().getColor(R.color.remove_red));
            } else {
                actionButton.setText("Add");
                actionButton.setBackgroundColor(getResources().getColor(R.color.add_green));
            }

            TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonLayoutParams.setMargins(0, 10, 0, 10);
            actionButton.setLayoutParams(buttonLayoutParams);
            row.addView(actionButton);
            subcategoryTable.addView(row);
        }
    }

    private boolean areFieldsValid() {
        if(description.getText().toString().isEmpty()) {
            description.setError("Description is required.");
            return false;
        }

        return true;
    }

    private void updateEventTypeObject() {
        eventTypeToEdit.setDescription(description.getText().toString());
        if(selectedSubcategories == null || selectedSubcategories.size()==0)
            selectedSubcategories = new ArrayList<>();
        eventTypeToEdit.setSuggestedSubcategoriesIds(selectedSubcategories);

    }


}