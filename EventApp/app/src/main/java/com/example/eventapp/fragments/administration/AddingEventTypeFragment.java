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
import com.example.eventapp.databinding.FragmentAddingEventTypeBinding;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.SubcategoryRepo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddingEventTypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddingEventTypeFragment extends DialogFragment {

    private EditText name;
    private EditText description;
    private ArrayList<String> selectedSubcategories;
    ArrayList<Subcategory> subcategories = new ArrayList<>();   //sve iz tabele


    private FragmentAddingEventTypeBinding binding;
    private EventType eventType;
    private EventType newEventType;


    public AddingEventTypeFragment() {
        // Required empty public constructor
    }



    public static AddingEventTypeFragment newInstance() {
        AddingEventTypeFragment fragment = new AddingEventTypeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventType = getArguments().getParcelable("EVENTTYPE");
        }
        selectedSubcategories = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddingEventTypeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //tabela sa subkategorijama
        subcategories = new ArrayList<>();

        SubcategoryRepo.getAllSubcategories(new SubcategoryRepo.SubcategoryFetchCallback() {
            @Override
            public void onSubcategoryFetch(ArrayList<Subcategory> storedSubcategories) {
                if (storedSubcategories != null) {
                    subcategories = storedSubcategories;
                    // Dodavanje dinamicko u tabelu
                    TableLayout subcategoryTable = root.findViewById(R.id.suggestedSubcategoriesTable);
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
                                selectedSubcategories.add(subcateg.getId());
                                addButton.setEnabled(false);
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

        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        name = binding.editTextEventTypeName;
        description = binding.editTextEventTypeDescription;

        Button btnSubmit = (Button) binding.submitNewEventTypeButton;
        btnSubmit.setOnClickListener(v -> {
            if(areFieldsValid())
            {
                createNewEventTypeObject();
                EventTypeRepo.createEventType(newEventType);

                //idi na "refresovan" fragment za listu event typova (bez dupliranja na steku!)
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.scroll_event_types_list, EventTypesListFragment.newInstance(new ArrayList<>()));
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

    private void createNewEventTypeObject() {
        newEventType = new EventType();
        newEventType.setName(name.getText().toString());
        newEventType.setDescription(description.getText().toString());
        newEventType.setDeactivated(false); //u pocetku kreiram uvijek aktivan tip dogadjaja
        if(selectedSubcategories == null || selectedSubcategories.size()==0)
            selectedSubcategories = new ArrayList<>();
        newEventType.setSuggestedSubcategoriesIds(selectedSubcategories);

    }


}