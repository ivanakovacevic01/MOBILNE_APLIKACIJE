package com.example.eventapp.fragments.packages;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.ListFragment;

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
import com.example.eventapp.adapters.packages.PackageListAdapter;
import com.example.eventapp.databinding.FragmentPackageListBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.PackageRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PackageListFragment extends ListFragment {
    private FragmentPackageListBinding binding;
    private PackageListAdapter adapter;
    private PackageRepo packageRepository;
    private RadioButton packageName;
    private RadioButton productName;
    private RadioButton serviceName;
    private SearchView searchName;
    private ArrayList<Category> selectedCategories=new ArrayList<>();
    private ArrayList<Subcategory> selectedSubcategories=new ArrayList<>();
    private ArrayList<EventType> selectedEventTypes=new ArrayList<>();
    private EditText minPrice;
    private EditText maxPrice;
    private String minPriceText="";
    private String maxPriceText="";

    public PackageListFragment() {
        // Required empty public constructor
    }


    public static PackageListFragment newInstance() {
        PackageListFragment fragment = new PackageListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageRepository=new PackageRepo();
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        packageRepository.getAllPackages(new PackageRepo.PackageFetchCallback() {
            @Override
            public void onPackageFetch(ArrayList<Package> packages) {
                UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                    @Override
                    public void onUserObjectFetched(User user, String errorMessage) {
                        UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                        if(user.getType().equals(UserType.OWNER)){
                            OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                                @Override
                                public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                    OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                    ArrayList<Package> packagesByFirm=new ArrayList<>();
                                    if (packages != null) {
                                        for(Package p:packages){
                                            if(p.getFirmId().equals(owner.getFirmId()))
                                                packagesByFirm.add(p);
                                        }
                                        adapter=new PackageListAdapter(getActivity(),packagesByFirm);
                                        setListAdapter(adapter);
                                    }
                                }
                            });

                        }else{
                            EmployeeRepo.getByEmail(user.getEmail(), new EmployeeRepo.EmployeeFetchCallback() {
                                @Override
                                public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                                    EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                                    ArrayList<Package> packagesByFirm=new ArrayList<>();
                                    if (packages != null) {
                                        for(Package p:packages){
                                            if(p.getFirmId().equals(employee.getFirmId()))
                                                packagesByFirm.add(p);
                                        }
                                        adapter=new PackageListAdapter(getActivity(),packagesByFirm);
                                        setListAdapter(adapter);
                                    }
                                }
                            });
                        }
                    }
                });

            }
        }); ;



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPackageListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        packageName=binding.packageName;
        productName=binding.productName;
        serviceName=binding.serviceName;
        searchName=binding.searchText;
        packageName.setChecked(true);
        packageRepository=new PackageRepo();
        FloatingActionButton btnNewPackage = (FloatingActionButton) root.findViewById(R.id.btnNewPackage);
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null) {
            UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                @Override
                public void onUserObjectFetched(User user, String errorResponse) {
                    if (user != null && user.isActive()) {
                        if (user.getType() != UserType.OWNER)
                            btnNewPackage.setVisibility(View.GONE);

                    }
                }
            });
        }else{
            btnNewPackage.setVisibility(View.GONE);
        }
        //*****************************SEARCH******************************************
        searchName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(packageName.isChecked()) {
                    packageRepository.getAllPackages(new PackageRepo.PackageFetchCallback() {
                        @Override
                        public void onPackageFetch(ArrayList<Package> packages) {
                            if (packages != null) {
                                ArrayList<Package> mPackages = new ArrayList<>();
                                for (Package p : packages) {
                                    if (!p.getDeleted() && p.getName().toLowerCase().contains(newText.toLowerCase()) && !mPackages.contains(p))
                                        mPackages.add(p);
                                }
                                adapter = new PackageListAdapter(getActivity(), mPackages);
                                setListAdapter(adapter);
                            }
                        }
                    });
                }else if(productName.isChecked()){
                    packageRepository.getAllPackages(new PackageRepo.PackageFetchCallback() {
                        @Override
                        public void onPackageFetch(ArrayList<Package> packages) {
                            if (packages != null) {
                                ArrayList<Package> mPackages = new ArrayList<>();
                                for (Package p : packages) {
                                    ProductRepo productRepo=new ProductRepo();
                                    productRepo.getAllProducts(new ProductRepo.ProductFetchCallback() {
                                        @Override
                                        public void onProductFetch(ArrayList<Product> products) {
                                            for(Product product:products){
                                                if(product.getName().toLowerCase().contains(newText.toLowerCase())){
                                                    for(String id:p.getProducts()){
                                                        if(id.equals(product.getId()))
                                                            mPackages.add(p);
                                                    }
                                                }
                                            }

                                            adapter = new PackageListAdapter(getActivity(), mPackages);
                                            setListAdapter(adapter);
                                        }
                                    });

                                }

                            }
                        }
                    });
                }else if(serviceName.isChecked()){
                    packageRepository.getAllPackages(new PackageRepo.PackageFetchCallback() {
                        @Override
                        public void onPackageFetch(ArrayList<Package> packages) {
                            if (packages != null) {
                                ArrayList<Package> mPackages = new ArrayList<>();
                                for (Package p : packages) {
                                    ServiceRepo serviceRepo=new ServiceRepo();
                                    serviceRepo.getAllServices( new ServiceRepo.ServiceFetchCallback(){
                                        @Override
                                        public void onServiceFetch(ArrayList<Service> services) {
                                            for(Service s:services){
                                                if(s.getName().toLowerCase().contains(newText.toLowerCase())){
                                                    for(String id:p.getServices()){
                                                        if(id.equals(s.getId()))
                                                            mPackages.add(p);
                                                    }
                                                }
                                            }

                                            adapter = new PackageListAdapter(getActivity(), mPackages);
                                            setListAdapter(adapter);
                                        }
                                    });

                                }

                            }
                        }
                    });
                }

                return true;
            }
        });

        searchName.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                packageRepository=new PackageRepo();
                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                FirebaseUser user=mAuth.getCurrentUser();
                packageRepository.getAllPackages(new PackageRepo.PackageFetchCallback() {
                    @Override
                    public void onPackageFetch(ArrayList<Package> packages) {
                        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                            @Override
                            public void onUserObjectFetched(User user, String errorMessage) {
                                UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                                if(user.getType().equals(UserType.OWNER)){
                                    OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                                        @Override
                                        public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                            OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                            ArrayList<Package> packagesByFirm=new ArrayList<>();
                                            if (packages != null) {
                                                for(Package p:packages){
                                                    if(p.getFirmId().equals(owner.getFirmId()))
                                                        packagesByFirm.add(p);
                                                }
                                                adapter=new PackageListAdapter(getActivity(),packagesByFirm);
                                                setListAdapter(adapter);
                                            }
                                        }
                                    });

                                }else{
                                    EmployeeRepo.getByEmail(user.getEmail(), new EmployeeRepo.EmployeeFetchCallback() {
                                        @Override
                                        public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                                            EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                                            ArrayList<Package> packagesByFirm=new ArrayList<>();
                                            if (packages != null) {
                                                for(Package p:packages){
                                                    if(p.getFirmId().equals(employee.getFirmId()))
                                                        packagesByFirm.add(p);
                                                }
                                                adapter=new PackageListAdapter(getActivity(),packagesByFirm);
                                                setListAdapter(adapter);
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    }
                }); ;


                searchName.setQuery("", false);
                return false;
            }
        });


        //**********************FILTERS**************************************
        Button btnFilters = (Button) root.findViewById(R.id.btnFilters);
        btnFilters.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.FullScreenBottomSheetDialog);
            View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter_package, null);
            bottomSheetDialog.setContentView(dialogView);
            minPrice=dialogView.findViewById(R.id.minPrice);
            maxPrice=dialogView.findViewById(R.id.pricePackageMax);
            minPrice.setText(minPriceText);
            maxPrice.setText(maxPriceText);


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

            Button apply=dialogView.findViewById(R.id.applyBtn);
            apply.setOnClickListener(a->{

                minPriceText=minPrice.getText().toString();
                maxPriceText=maxPrice.getText().toString();
                Predicate<Package> combinedPredicate = p -> true;


                if (!minPriceText.isEmpty()) {
                    combinedPredicate = combinedPredicate.and(p -> p.getPrice() > Double.parseDouble(minPriceText));
                }
                if (!maxPriceText.isEmpty()) {
                    combinedPredicate = combinedPredicate.and(p -> p.getPrice() < Double.parseDouble(maxPriceText));
                }



                Predicate<Package> finalCombinedPredicate = combinedPredicate;
                packageRepository.getAllPackages(new PackageRepo.PackageFetchCallback() {
                    @Override
                    public void onPackageFetch(ArrayList<Package> packages) {
                        FirebaseAuth mAuth= FirebaseAuth.getInstance();
                        FirebaseUser user=mAuth.getCurrentUser();
                        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                            @Override
                            public void onUserObjectFetched(User user, String errorMessage) {
                                UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                                if(user.getType().equals(UserType.OWNER)){
                                    OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                                        @Override
                                        public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                            OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                            ArrayList<Package> packagesByFirm=new ArrayList<>();
                                            if(packages!=null){
                                                for(Package p:packages){
                                                    if(p.getFirmId().equals(owner.getFirmId()))
                                                        packagesByFirm.add(p);
                                                }
                                            }
                                            if(packagesByFirm!=null){
                                                List<Package> filteredList=packagesByFirm.stream()
                                                        .filter(finalCombinedPredicate)
                                                        .collect(Collectors.toList());

                                                if (getActivity() != null) {
                                                    List<Package> finalFilteredList = filterByCategorySubcategoryEvents(filteredList);
                                                    getActivity().runOnUiThread(() -> {
                                                        adapter = new PackageListAdapter(getActivity(), new ArrayList<>(finalFilteredList));
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
                                            ArrayList<Package> packagesByFirm=new ArrayList<>();
                                            if(packages!=null){
                                                for(Package p:packages){
                                                    if(p.getFirmId().equals(employee.getFirmId()))
                                                        packagesByFirm.add(p);
                                                }
                                            }
                                            if(packagesByFirm!=null){
                                                List<Package> filteredList=packagesByFirm.stream()
                                                        .filter(finalCombinedPredicate)
                                                        .collect(Collectors.toList());

                                                if (getActivity() != null) {
                                                    List<Package> finalFilteredList = filterByCategorySubcategoryEvents(filteredList);
                                                    getActivity().runOnUiThread(() -> {
                                                        adapter = new PackageListAdapter(getActivity(), new ArrayList<>(finalFilteredList));
                                                        setListAdapter(adapter);
                                                    });
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    }

                });

                bottomSheetDialog.dismiss();

            });
            Button discard=dialogView.findViewById(R.id.discradBtn);
            discard.setOnClickListener(d->{
                packageRepository=new PackageRepo();

                packageRepository.getAllPackages(new PackageRepo.PackageFetchCallback() {
                    @Override
                    public void onPackageFetch(ArrayList<Package> packages) {
                        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                            @Override
                            public void onUserObjectFetched(User user, String errorMessage) {
                                UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                                if(user.getType().equals(UserType.OWNER)){
                                    OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                                        @Override
                                        public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                            OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                            ArrayList<Package> packagesByFirm=new ArrayList<>();
                                            if (packages != null) {
                                                for(Package p:packages){
                                                    if(p.getFirmId().equals(owner.getFirmId()))
                                                        packagesByFirm.add(p);
                                                }
                                                adapter=new PackageListAdapter(getActivity(),packagesByFirm);
                                                setListAdapter(adapter);
                                            }
                                        }
                                    });

                                }else{
                                    EmployeeRepo.getByEmail(user.getEmail(), new EmployeeRepo.EmployeeFetchCallback() {
                                        @Override
                                        public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                                            EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                                            ArrayList<Package> packagesByFirm=new ArrayList<>();
                                            if (packages != null) {
                                                for(Package p:packages){
                                                    if(p.getFirmId().equals(employee.getFirmId()))
                                                        packagesByFirm.add(p);
                                                }
                                                adapter=new PackageListAdapter(getActivity(),packagesByFirm);
                                                setListAdapter(adapter);
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    }
                }); ;

                selectedEventTypes.clear();
                selectedSubcategories.clear();
                selectedCategories.clear();
                minPriceText="";
                maxPriceText="";

                bottomSheetDialog.dismiss();
            });




            bottomSheetDialog.show();
        });
        btnNewPackage.setOnClickListener(v -> {
            FragmentTransition.to(PackageFormFragment.newInstance(null), getActivity(),
                    true, R.id.scroll_package_list);
        });

        return root;
    }

    private List<Package> filterByCategorySubcategoryEvents(List<Package> filteredList){
        List<Package> filtered=new ArrayList<>();

        if(selectedCategories.size()>0){
            for(Category category:selectedCategories) {
                for (Package s : filteredList) {
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
                for (Package s : filteredList)
                    for(String id:s.getSubcategories())
                        if(id.equals(subcategory.getId()) && !filtered.contains(s))
                            filtered.add(s);
            filteredList.clear();
            filteredList.addAll(filtered);
            filtered.clear();
        }

        if(selectedEventTypes.size()>0){
            for(EventType event:selectedEventTypes)
                for(Package s:filteredList)
                    for(String type:s.getType())
                        if(type.equals(event.getId()) && !filtered.contains(s))
                            filtered.add(s);
            filteredList.clear();
            filteredList.addAll(filtered);
        }
        return filteredList;
    }
}