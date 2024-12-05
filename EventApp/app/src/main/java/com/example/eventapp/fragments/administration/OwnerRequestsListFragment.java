package com.example.eventapp.fragments.administration;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.eventapp.R;
import com.example.eventapp.adapters.OwnerRequestsListAdapter;
import com.example.eventapp.adapters.categories.CategoriesListAdapter;
import com.example.eventapp.adapters.eventTypes.EventTypesListAdapter;
import com.example.eventapp.databinding.FragmentOwnerRequestsListBinding;
import com.example.eventapp.databinding.FragmentSubcategoriesListBinding;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.RegistrationOwnerRequest;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.FirmRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.RegistrationOwnerRequestRepo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OwnerRequestsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnerRequestsListFragment extends ListFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    EditText searchEditText;
    EditText startDateEditText;
    EditText endDateEditText;
    Spinner categSpinner;
    Spinner eventTypeSpinner;
    private OwnerRequestsListAdapter adapter;
    private ArrayList<RegistrationOwnerRequest> mRequests;
    private FragmentOwnerRequestsListBinding binding;
    private RegistrationOwnerRequestRepo requestRepository;
    private ArrayList<RegistrationOwnerRequest> filtered;
    private ArrayList<RegistrationOwnerRequest> all;
    private ArrayList<Owner> storedOwners;
    private ArrayList<Firm> storedFirms;
    private ArrayList<EventType> storedEventTypes;
    private ArrayList<Category> storedCategories;


    public OwnerRequestsListFragment() {
        // Required empty public constructor
    }

    public static OwnerRequestsListFragment newInstance(ArrayList<RegistrationOwnerRequest> requests) {
        OwnerRequestsListFragment fragment = new OwnerRequestsListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("REQ_LIST", requests);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOwnerRequestsListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        searchEditText = binding.searchBarFilters;
        startDateEditText = binding.editTextStartDate;
        endDateEditText = binding.editTextEndDate;
        categSpinner = binding.spinnerCategory;
        eventTypeSpinner = binding.spinnerEventType;

        requestRepository = new RegistrationOwnerRequestRepo();
        requestRepository.getAllRequests(new RegistrationOwnerRequestRepo.RequestFetchCallback() {
            @Override
            public void onRequestFetch(ArrayList<RegistrationOwnerRequest> requests) {
                if (requests != null) {
                    filtered = requests;
                    all = requests;
                    FirmRepo firmRepo = new FirmRepo();
                    firmRepo.getAllNotActive(new FirmRepo.FirmFetchCallback() {
                        @Override
                        public void onFirmFetch(ArrayList<Firm> firms) {
                            if (firms != null) {
                                storedFirms = firms;
                                OwnerRepo ownerRepo = new OwnerRepo();
                                ownerRepo.getAll(new OwnerRepo.OwnerFetchCallback() {
                                    @Override
                                    public void onOwnerFetch(ArrayList<Owner> owners) {
                                        OwnerRepo.OwnerFetchCallback.super.onOwnerFetch(owners);
                                        if(owners!=null){
                                            storedOwners = owners;
                                            adapter = new OwnerRequestsListAdapter(getActivity(), requests, storedOwners, storedFirms);
                                            setListAdapter(adapter);
                                            EventTypeRepo eventTypeRepository = new EventTypeRepo();
                                            eventTypeRepository.getAllEventTypes(new EventTypeRepo.EventTypeFetchCallback() {
                                                @Override
                                                public void onEventTypeFetch(ArrayList<EventType> eventTypes) {
                                                    if (eventTypes != null) {
                                                        storedEventTypes = eventTypes;
                                                        CategoryRepo categoryRepository = new CategoryRepo();
                                                        categoryRepository.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
                                                            @Override
                                                            public void onCategoryFetch(ArrayList<Category> categories) {
                                                                if (categories != null) {
                                                                    storedCategories = categories;
                                                                    //popunjavanje spinnera
                                                                    if (storedCategories != null) {
                                                                        ArrayList<String> categoryNames = new ArrayList<>();
                                                                        for (Category category : storedCategories) {
                                                                            categoryNames.add(category.getName());
                                                                        }
                                                                        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
                                                                        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                                        categoryAdapter.insert("", 0);
                                                                        categSpinner.setAdapter(categoryAdapter);

                                                                    }
                                                                    if (storedEventTypes != null) {
                                                                        ArrayList<String> eventTypeNames = new ArrayList<>();
                                                                        for (EventType eventType : storedEventTypes) {
                                                                            eventTypeNames.add(eventType.getName());
                                                                        }
                                                                        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, eventTypeNames);
                                                                        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                                        eventTypeAdapter.insert("", 0);
                                                                        eventTypeSpinner.setAdapter(eventTypeAdapter);
                                                                    }

                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });


                }
            }
        });



        Button searchBtn = binding.buttonSearch;

        searchBtn.setOnClickListener(v -> {
                String searchText = searchEditText.getText().toString().toLowerCase().trim();
                Log.i("ispis", searchText);

                ArrayList<RegistrationOwnerRequest> filteredList = new ArrayList<>();
                for (RegistrationOwnerRequest request : all) {
                    String ownerId = request.getOwnerId();

                    Owner owner = getOwnerById(ownerId);
                    Firm firm = getFirmById(owner.getFirmId());
                    if (owner.getFirstName().toLowerCase().contains(searchText) ||
                            owner.getLastName().toLowerCase().contains(searchText) ||
                            owner.getEmail().toLowerCase().contains(searchText) || firm.getEmail().toLowerCase().contains(searchText)
                            || firm.getName().toLowerCase().contains(searchText)) {
                        filteredList.add(request);

                    }
                }
                filtered = filteredList;
                adapter = new OwnerRequestsListAdapter(getActivity(), filteredList, storedOwners, storedFirms);
                setListAdapter(adapter);

        });


        Button resetBtn = binding.buttonReset;

        resetBtn.setOnClickListener(v -> {
                searchEditText.setText("");
                filtered = all;
                startDateEditText.setText("");
                endDateEditText.setText("");
                categSpinner.setSelection(0);
                eventTypeSpinner.setSelection(0);
                //dodati selected spinnera na prazno
                adapter = new OwnerRequestsListAdapter(getActivity(), all, storedOwners, storedFirms);
                setListAdapter(adapter);

        });

        Button filterBtn = binding.buttonFilter;

        filterBtn.setOnClickListener(v -> {

            if(startDateEditText.getText().toString()!=null && !startDateEditText.getText().toString().equals("")
                && endDateEditText.getText().toString()!=null  && !endDateEditText.getText().toString().equals(""))
            {
                if (isValidDateFormat(startDateEditText.getText().toString()) && isValidDateFormat(endDateEditText.getText().toString())) {
                    // Format datuma je validan
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String userStartDate = startDateEditText.getText().toString();
                    String userEndDate = endDateEditText.getText().toString();

                    try {
                        // Parsiranje unetih datuma
                        Date startDate = dateFormat.parse(userStartDate);
                        Log.i("start", userStartDate);
                        Date endDate = dateFormat.parse(userEndDate);

                        ArrayList<RegistrationOwnerRequest> toFilter = new ArrayList<>();
                        for(RegistrationOwnerRequest req : filtered) {
                            Date reqDate = dateFormat.parse((req.getCreatedDate()));
                            if(reqDate.after(startDate) && reqDate.before(endDate)) {
                                toFilter.add(req);
                            }
                        }
                        filtered = toFilter;

                        // Provera da li je datum zahteva unutar raspona

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    // Format datuma nije validan
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("Error!");
                    builder.setMessage("Please set date in format: dd-mm-yyyy.");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }


            }
            Log.i("kateg", categSpinner.getSelectedItem().toString() );
            Log.i("event", eventTypeSpinner.getSelectedItem().toString() );
            if(!categSpinner.getSelectedItem().toString().equals("")){
                ArrayList<RegistrationOwnerRequest> toFilter = new ArrayList<>();
                for(RegistrationOwnerRequest req : filtered) {
                    String ownerId = req.getOwnerId();
                    Owner owner = getOwnerById(ownerId);
                    Firm firm = getFirmById(owner.getFirmId());
                    if(firm.getCategoriesIds().contains(getCategoryIdByName(categSpinner.getSelectedItem().toString()))) {
                        toFilter.add(req);
                    }
                }
                filtered = toFilter;
            }
            if(!eventTypeSpinner.getSelectedItem().toString().equals("")){
                ArrayList<RegistrationOwnerRequest> toFilter = new ArrayList<>();
                for(RegistrationOwnerRequest req : filtered) {
                    String ownerId = req.getOwnerId();
                    Owner owner = getOwnerById(ownerId);
                    Firm firm = getFirmById(owner.getFirmId());
                    if(firm.getEventTypesIds().contains(getEventTypeIdByName(eventTypeSpinner.getSelectedItem().toString()))) {
                        toFilter.add(req);
                    }
                }
                filtered = toFilter;
            }


            adapter = new OwnerRequestsListAdapter(getActivity(), filtered, storedOwners, storedFirms);
            setListAdapter(adapter);

        });


        return root;
    }

    private Owner getOwnerById(String id) {
        for (Owner owner : storedOwners) {
            if (owner.getId().equals(id)) {
                return owner;
            }
        }
        return null;
    }
    private Firm getFirmById(String id) {
        for (Firm firm : storedFirms) {
            if (firm.getId().equals(id)) {
                return firm;
            }
        }
        return null;
    }
    private String getCategoryIdByName(String name) {
        for(Category cat : storedCategories) {
            if(cat.getName().equals(name))
                return cat.getId();
        }
        return "";
    }


    private String getEventTypeIdByName(String name) {
        for(EventType ev : storedEventTypes) {
            if(ev.getName().equals(name))
                return ev.getId();
        }
        return "";
    }
    public boolean isValidDateFormat(String input) {
        // Definišite regularni izraz za format datuma "brojevi-brojevi-brojevi"
        String regex = "\\d{1,2}-\\d{1,2}-\\d{4}";

        // Kreirajte Pattern objekat koristeći regularni izraz
        Pattern pattern = Pattern.compile(regex);

        // Kreirajte Matcher objekat koristeći ulazni tekst i pattern
        Matcher matcher = pattern.matcher(input);

        // Vratite true ako se ulazni tekst podudara sa obrascem, inače false
        return matcher.matches();
    }

}