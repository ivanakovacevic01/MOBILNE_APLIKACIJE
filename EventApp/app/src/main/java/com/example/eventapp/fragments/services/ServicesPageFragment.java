package com.example.eventapp.fragments.services;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.adapters.services.ServiceListAdapter;
import com.example.eventapp.databinding.FragmentServicesPageBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class ServicesPageFragment extends ListFragment {

    private FragmentServicesPageBinding binding;
    private ServiceListAdapter adapter;
    private ServiceRepo serviceRepository;
    private SearchView searchName;
    private ArrayList<Category> selectedCategories=new ArrayList<>();
    private ArrayList<Subcategory> selectedSubcategories=new ArrayList<>();
    private ArrayList<EventType> selectedEventTypes=new ArrayList<>();
    private EditText minPrice;
    private EditText maxPrice;
    private RadioButton availableService;
    private RadioButton notAvailableService;
    private RadioButton allServices;
    private EditText performers;
    private String minPriceText="";
    private String maxPriceText="";
    private String performersText="";
    private int availableServiceFilterState=2; //0 available 1 not available 2 all

    public ServicesPageFragment() {
    }


    public static ServicesPageFragment newInstance() {
        ServicesPageFragment fragment = new ServicesPageFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceRepository=new ServiceRepo();
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        serviceRepository.getAllServices(new ServiceRepo.ServiceFetchCallback() {
            @Override
            public void onServiceFetch(ArrayList<Service> services) {
                UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                    @Override
                    public void onUserObjectFetched(User user, String errorMessage) {
                        UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                        if(user.getType().equals(UserType.OWNER)){
                            OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                                @Override
                                public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                    OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                    ArrayList<Service> servicesByFirm=new ArrayList<>();
                                    if (services != null) {
                                        for(Service s:services){
                                            Log.i("TTT",s.getFirmId()+" "+owner.getFirmId());
                                            if(s.getFirmId().equals(owner.getFirmId()))
                                                servicesByFirm.add(s);
                                        }
                                        adapter=new ServiceListAdapter(getActivity(),servicesByFirm);
                                        setListAdapter(adapter);
                                    }
                                }
                            });
                        }else{
                            EmployeeRepo.getByEmail(user.getEmail(), new EmployeeRepo.EmployeeFetchCallback() {
                                @Override
                                public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                                    EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                                    ArrayList<Service> servicesByFirm=new ArrayList<>();
                                    for(Service s:services){
                                        if(s.getFirmId().equals(employee.getFirmId()))
                                            servicesByFirm.add(s);
                                    }
                                    adapter=new ServiceListAdapter(getActivity(),servicesByFirm);
                                    setListAdapter(adapter);
                                }
                            });
                        }
                    }
                });

            }
        }) ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServicesPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //***************************SEARCH NAME****************************************

        searchName=binding.searchText;
        searchName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {





                serviceRepository=new ServiceRepo();
                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                FirebaseUser user=mAuth.getCurrentUser();
                serviceRepository.getAllServices(new ServiceRepo.ServiceFetchCallback() {
                    @Override
                    public void onServiceFetch(ArrayList<Service> services) {
                        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                            @Override
                            public void onUserObjectFetched(User user, String errorMessage) {
                                UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                                if(user.getType().equals(UserType.OWNER)){
                                    OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                                        @Override
                                        public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                            OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                            ArrayList<Service> servicesByFirm=new ArrayList<>();
                                            if (services != null) {
                                                for(Service s:services){
                                                    if(s.getFirmId().equals(owner.getFirmId()))
                                                        servicesByFirm.add(s);
                                                }
                                                ArrayList mServices=new ArrayList<>();
                                                for(Service service:servicesByFirm){
                                                    if(service!=null)
                                                        if (!service.getDeleted() && service.getName().toLowerCase().contains(newText.toLowerCase()) && !mServices.contains(service))
                                                            mServices.add(service);
                                                }
                                                adapter=new ServiceListAdapter(getActivity(),mServices);
                                                setListAdapter(adapter);
                                            }
                                        }
                                    });
                                }else{
                                    EmployeeRepo.getByEmail(user.getEmail(), new EmployeeRepo.EmployeeFetchCallback() {
                                        @Override
                                        public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                                            EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                                            ArrayList<Service> servicesByFirm=new ArrayList<>();
                                            for(Service s:services){
                                                if(s.getFirmId().equals(employee.getFirmId()))
                                                    servicesByFirm.add(s);
                                            }
                                            ArrayList mServices=new ArrayList<>();
                                            for(Service service:servicesByFirm){
                                                if(service!=null)
                                                    if (!service.getDeleted() && service.getName().toLowerCase().contains(newText.toLowerCase()) && !mServices.contains(service))
                                                        mServices.add(service);
                                            }
                                            adapter=new ServiceListAdapter(getActivity(),mServices);
                                            setListAdapter(adapter);
                                        }
                                    });
                                }
                            }
                        });

                    }
                }) ;



                /*
                serviceRepository.getAllServices(new ServiceRepo.ServiceFetchCallback() {
                    @Override
                    public void onServiceFetch(ArrayList<Service> services) {
                        if (services != null) {
                            ArrayList mServices=new ArrayList<>();
                            for(Service service:services){
                                if(service!=null)
                                    if (!service.getDeleted() && service.getName().toLowerCase().contains(newText.toLowerCase()) && !mServices.contains(service))
                                        mServices.add(service);
                            }
                            adapter=new ServiceListAdapter(getActivity(),mServices);
                            setListAdapter(adapter);
                        }
                    }
                });*/

                return true;
            }
        });

        searchName.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                serviceRepository=new ServiceRepo();
                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                FirebaseUser user=mAuth.getCurrentUser();
                serviceRepository.getAllServices(new ServiceRepo.ServiceFetchCallback() {
                    @Override
                    public void onServiceFetch(ArrayList<Service> services) {
                        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                            @Override
                            public void onUserObjectFetched(User user, String errorMessage) {
                                UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                                if(user.getType().equals(UserType.OWNER)){
                                    OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                                        @Override
                                        public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                            OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                            ArrayList<Service> servicesByFirm=new ArrayList<>();
                                            if (services != null) {
                                                for(Service s:services){
                                                    if(s.getFirmId().equals(owner.getFirmId()))
                                                        servicesByFirm.add(s);
                                                }
                                                adapter=new ServiceListAdapter(getActivity(),servicesByFirm);
                                                setListAdapter(adapter);
                                            }
                                        }
                                    });
                                }else{
                                    EmployeeRepo.getByEmail(user.getEmail(), new EmployeeRepo.EmployeeFetchCallback() {
                                        @Override
                                        public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                                            EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                                            ArrayList<Service> servicesByFirm=new ArrayList<>();
                                            for(Service s:services){
                                                if(s.getFirmId().equals(employee.getFirmId()))
                                                    servicesByFirm.add(s);
                                            }
                                            adapter=new ServiceListAdapter(getActivity(),servicesByFirm);
                                            setListAdapter(adapter);
                                        }
                                    });
                                }
                            }
                        });

                    }
                }) ;
                searchName.setQuery("", false);
                return false;
            }
        });


        //************************************FILTERS*****************************************************

        Button btnFilters = (Button) root.findViewById(R.id.btnFilters);
        btnFilters.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.FullScreenBottomSheetDialog);
            View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter_service, null);
            bottomSheetDialog.setContentView(dialogView);


            minPrice=dialogView.findViewById(R.id.priceServiceMin);
            maxPrice=dialogView.findViewById(R.id.priceServiceMax);
            allServices=dialogView.findViewById(R.id.allService);
            availableService=dialogView.findViewById(R.id.availableService);
            notAvailableService=dialogView.findViewById(R.id.notAvailableService);
            performers=dialogView.findViewById(R.id.editTextFilterPerformers);

            minPrice.setText(minPriceText);
            maxPrice.setText(maxPriceText);
            performers.setText(performersText);
            if(availableServiceFilterState==0)
                availableService.setChecked(true);
            else if(availableServiceFilterState==1)
                notAvailableService.setChecked(true);
            else
                allServices.setChecked(true);

            Spinner spinnerCat = dialogView.findViewById(R.id.spinCat);
            CategoryRepo.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
                @Override
                public void onCategoryFetch(ArrayList<Category> categories) {
                    ArrayAdapter<Category> arrayAdapter = new ArrayAdapter<Category>(getActivity(), R.layout.multispiner, categories) {

                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            return createCustomView(position, convertView, parent);
                        }

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            return createCustomView(position, convertView, parent);
                        }

                        private View createCustomView(int position, View convertView, ViewGroup parent) {
                            LayoutInflater inflater = LayoutInflater.from(getContext());
                            View view = convertView;
                            if (view == null) {
                                view = inflater.inflate(R.layout.multispiner, parent, false);
                            }

                            TextView textView = view.findViewById(R.id.textView);
                            CheckBox checkBox = view.findViewById(R.id.checkBox);

                            Category category = getItem(position);
                            textView.setText(category.getName());
                            for(Category c:selectedCategories)
                                if(c.getId().equals(category.getId()))
                                    checkBox.setChecked(true);
                            checkBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CheckBox checkBox = (CheckBox) v;
                                    Category selectedItem = getItem(position);
                                    if (checkBox.isChecked()) {
                                        selectedCategories.add(selectedItem);
                                    } else {
                                        ArrayList<Category> varCategories=new ArrayList<>();
                                        for(Category c:selectedCategories)
                                            if(!c.getId().equals(selectedItem.getId()))
                                                varCategories.add(c);

                                        selectedCategories.clear();
                                        selectedCategories.addAll(varCategories);                                             }
                                }
                            });

                            return view;
                        }
                    };
                    spinnerCat.setAdapter(arrayAdapter);
                }
            });

            Spinner spinnerSubCat = dialogView.findViewById(R.id.spinSubcat);
            SubcategoryRepo.getAllSubcategories(new SubcategoryRepo.SubcategoryFetchCallback() {
                @Override
                public void onSubcategoryFetch(ArrayList<Subcategory> subcategories) {

                    ArrayAdapter<Subcategory> arrayAdapterSub = new ArrayAdapter<Subcategory>(getActivity(), R.layout.multispiner, subcategories) {

                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            return createCustomView(position, convertView, parent);
                        }

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            return createCustomView(position, convertView, parent);
                        }

                        private View createCustomView(int position, View convertView, ViewGroup parent) {
                            LayoutInflater inflater = LayoutInflater.from(getContext());
                            View view = convertView;
                            if (view == null) {
                                view = inflater.inflate(R.layout.multispiner, parent, false);
                            }

                            TextView textView = view.findViewById(R.id.textView);
                            CheckBox checkBox = view.findViewById(R.id.checkBox);

                            Subcategory subCategory = getItem(position);
                            textView.setText(subCategory.getName());
                            for(Subcategory c:selectedSubcategories)
                                if(c.getId().equals(subCategory.getId()))
                                    checkBox.setChecked(true);
                            checkBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CheckBox checkBox = (CheckBox) v;
                                    Subcategory selectedItem = getItem(position);
                                    if (checkBox.isChecked()) {
                                        selectedSubcategories.add(selectedItem);
                                    } else {
                                        ArrayList<Subcategory> varSubcategories=new ArrayList<>();
                                        for(Subcategory c:selectedSubcategories)
                                            if(!c.getId().equals(selectedItem.getId()))
                                                varSubcategories.add(c);

                                        selectedSubcategories.clear();
                                        selectedSubcategories.addAll(varSubcategories);                                             }
                                }
                            });

                            return view;
                        }
                    };
                    spinnerSubCat.setAdapter(arrayAdapterSub);
                }
            });

            Spinner spinnerEvent = dialogView.findViewById(R.id.spinEvent);
            EventTypeRepo.getAllEventTypes(new EventTypeRepo.EventTypeFetchCallback() {
                @Override
                public void onEventTypeFetch(ArrayList<EventType> eventTypes) {
                    ArrayAdapter<EventType> arrayAdapterEvent = new ArrayAdapter<EventType>(getActivity(), R.layout.multispiner, eventTypes) {

                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            return createCustomView(position, convertView, parent);
                        }

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            return createCustomView(position, convertView, parent);
                        }

                        private View createCustomView(int position, View convertView, ViewGroup parent) {
                            LayoutInflater inflater = LayoutInflater.from(getContext());
                            View view = convertView;
                            if (view == null) {
                                view = inflater.inflate(R.layout.multispiner, parent, false);
                            }

                            TextView textView = view.findViewById(R.id.textView);
                            CheckBox checkBox = view.findViewById(R.id.checkBox);

                            EventType eventType = getItem(position);
                            textView.setText(eventType.getName());
                            for(EventType c:selectedEventTypes)
                                if(c.getId().equals(eventType.getId()))
                                    checkBox.setChecked(true);
                            checkBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CheckBox checkBox = (CheckBox) v;
                                    EventType selectedItem = getItem(position);
                                    if (checkBox.isChecked()) {
                                        selectedEventTypes.add(selectedItem);
                                    } else {
                                        ArrayList<EventType> varEventTypes=new ArrayList<>();
                                        for(EventType c:selectedEventTypes)
                                            if(!c.getId().equals(selectedItem.getId()))
                                                varEventTypes.add(c);

                                        selectedEventTypes.clear();
                                        selectedEventTypes.addAll(varEventTypes);                                             }
                                }
                            });

                            return view;
                        }
                    };
                    spinnerEvent.setAdapter(arrayAdapterEvent);
                }
            });

            Button apply=dialogView.findViewById(R.id.applyFilterService);
            apply.setOnClickListener(a->{

                minPriceText=minPrice.getText().toString();
                maxPriceText=maxPrice.getText().toString();
                performersText=performers.getText().toString();
                Predicate<Service> combinedPredicate = p -> true;

                if(availableService.isChecked()) {
                    availableServiceFilterState = 0;
                    combinedPredicate = combinedPredicate.and(Service::getAvailable);
                }
                else if (notAvailableService.isChecked()) {
                    availableServiceFilterState = 1;
                    combinedPredicate = combinedPredicate.and(p-> !p.getAvailable());
                }
                else
                    availableServiceFilterState=2;

                if (!minPriceText.isEmpty()) {
                    combinedPredicate = combinedPredicate.and(p -> p.getPricePerHour() > Double.parseDouble(minPriceText));
                }
                if (!maxPriceText.isEmpty()) {
                    combinedPredicate = combinedPredicate.and(p -> p.getPricePerHour() < Double.parseDouble(maxPriceText));
                }

                if (!performersText.isEmpty()) {
                    String desc = performersText.toString();
                    combinedPredicate = combinedPredicate.and(p ->{
                        for(String performer:p.getAttendants()){
                            if(performer.toLowerCase().contains(desc.toLowerCase()))
                                return true;
                        }
                        return false;
                    });

                }


                Predicate<Service> finalCombinedPredicate = combinedPredicate;





                serviceRepository=new ServiceRepo();
                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                FirebaseUser user=mAuth.getCurrentUser();
                serviceRepository.getAllServices(new ServiceRepo.ServiceFetchCallback() {
                    @Override
                    public void onServiceFetch(ArrayList<Service> services) {
                        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                            @Override
                            public void onUserObjectFetched(User user, String errorMessage) {
                                UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                                if(user.getType().equals(UserType.OWNER)){
                                    OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                                        @Override
                                        public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                            OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                            ArrayList<Service> servicesByFirm=new ArrayList<>();
                                            if (services != null) {
                                                for(Service s:services){
                                                    if(s.getFirmId().equals(owner.getFirmId()))
                                                        servicesByFirm.add(s);
                                                }
                                                List<Service> filteredList=servicesByFirm.stream()
                                                        .filter(finalCombinedPredicate)
                                                        .collect(Collectors.toList());

                                                if (getActivity() != null) {
                                                    List<Service> finalFilteredList = filterByCategorySubcategoryEvents(filteredList);
                                                    getActivity().runOnUiThread(() -> {
                                                        adapter = new ServiceListAdapter(getActivity(), new ArrayList<>(finalFilteredList));
                                                        setListAdapter(adapter);
                                                    });
                                                }
                                            }
                                        }
                                    });
                                }else{
                                    EmployeeRepo.getByEmail(user.getEmail(), new EmployeeRepo.EmployeeFetchCallback() {
                                        @Override
                                        public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                                            EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                                            ArrayList<Service> servicesByFirm=new ArrayList<>();
                                            for(Service s:services){
                                                if(s.getFirmId().equals(employee.getFirmId()))
                                                    servicesByFirm.add(s);
                                            }
                                            List<Service> filteredList=servicesByFirm.stream()
                                                    .filter(finalCombinedPredicate)
                                                    .collect(Collectors.toList());

                                            if (getActivity() != null) {
                                                List<Service> finalFilteredList = filterByCategorySubcategoryEvents(filteredList);
                                                getActivity().runOnUiThread(() -> {
                                                    adapter = new ServiceListAdapter(getActivity(), new ArrayList<>(finalFilteredList));
                                                    setListAdapter(adapter);
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    }
                }) ;



               /*

                serviceRepository.getAllServices(new ServiceRepo.ServiceFetchCallback() {
                    @Override
                    public void onServiceFetch(ArrayList<Service> services) {
                        if(services!=null){
                            List<Service> filteredList=services.stream()
                                    .filter(finalCombinedPredicate)
                                    .collect(Collectors.toList());

                            if (getActivity() != null) {
                                List<Service> finalFilteredList = filterByCategorySubcategoryEvents(filteredList);
                                getActivity().runOnUiThread(() -> {
                                    adapter = new ServiceListAdapter(getActivity(), new ArrayList<>(finalFilteredList));
                                    setListAdapter(adapter);
                                });
                            }
                        }
                    }

                });

                */

                bottomSheetDialog.dismiss();

            });
            Button discard=dialogView.findViewById(R.id.discardFilterService);
            discard.setOnClickListener(d->{
                serviceRepository=new ServiceRepo();
                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                FirebaseUser user=mAuth.getCurrentUser();
                serviceRepository.getAllServices(new ServiceRepo.ServiceFetchCallback() {
                    @Override
                    public void onServiceFetch(ArrayList<Service> services) {
                        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                            @Override
                            public void onUserObjectFetched(User user, String errorMessage) {
                                UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                                if(user.getType().equals(UserType.OWNER)){
                                    OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                                        @Override
                                        public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                            OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                            ArrayList<Service> servicesByFirm=new ArrayList<>();
                                            if (services != null) {
                                                for(Service s:services){
                                                    if(s.getFirmId().equals(owner.getFirmId()))
                                                        servicesByFirm.add(s);
                                                }
                                                adapter=new ServiceListAdapter(getActivity(),servicesByFirm);
                                                setListAdapter(adapter);
                                            }
                                        }
                                    });
                                }else{
                                    EmployeeRepo.getByEmail(user.getEmail(), new EmployeeRepo.EmployeeFetchCallback() {
                                        @Override
                                        public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                                            EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                                            ArrayList<Service> servicesByFirm=new ArrayList<>();
                                            for(Service s:services){
                                                if(s.getFirmId().equals(employee.getFirmId()))
                                                    servicesByFirm.add(s);
                                            }
                                            adapter=new ServiceListAdapter(getActivity(),servicesByFirm);
                                            setListAdapter(adapter);
                                        }
                                    });
                                }
                            }
                        });

                    }
                }) ;
                selectedEventTypes.clear();
                selectedSubcategories.clear();
                selectedCategories.clear();
                minPriceText="";
                maxPriceText="";
                performersText="";
                availableServiceFilterState=2;
                bottomSheetDialog.dismiss();
            });



            bottomSheetDialog.show();
        });

        //*************************FLOATING BUTTON***********************************
        FloatingActionButton btnNewService=(FloatingActionButton)root.findViewById(R.id.btnNewService);

        btnNewService.setOnClickListener(v -> {
            FragmentTransition.to(ServiceFormFragment.newInstance(null), getActivity(),
                    true, R.id.scroll_products_list_2);
        });

        //********************************SHARED PREF ROLES*******************************
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user != null) {

            UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                @Override
                public void onUserObjectFetched(User user, String errorResponse) {
                    if (user != null && user.isActive()) {
                        if (user.getType() != UserType.OWNER)
                            btnNewService.setVisibility(View.GONE);

                    }
                }
            });
        }else{
            btnNewService.setVisibility(View.GONE);
        }
        return root;
    }

    private List<Service> filterByCategorySubcategoryEvents(List<Service> filteredList){
        List<Service> filtered=new ArrayList<>();

        if(selectedCategories.size()>0){
            for(Category category:selectedCategories) {
                for (Service s : filteredList) {
                    if (s.getCategory().equals(category.getId()) && !filtered.contains(s))
                        filtered.add(s);
                }
            }

            filteredList.clear();
            filteredList.addAll(filtered);
            filtered.clear();
        }


        if(selectedSubcategories.size()>0) {
            for (Subcategory subcategory : selectedSubcategories)
                for (Service s : filteredList)
                    if (s.getSubcategory().equals(subcategory.getId()) && !filtered.contains(s))
                        filtered.add(s);
            filteredList.clear();
            filteredList.addAll(filtered);
            filtered.clear();
        }

        if(selectedEventTypes.size()>0){
            for(EventType event:selectedEventTypes)
                for(Service s:filteredList)
                    for(String type:s.getType())
                        if(type.equals(event.getId()) && !filtered.contains(s))
                            filtered.add(s);
            filteredList.clear();
            filteredList.addAll(filtered);
        }
        return filteredList;
    }
}