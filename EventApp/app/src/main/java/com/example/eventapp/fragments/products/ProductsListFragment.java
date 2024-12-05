package com.example.eventapp.fragments.products;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;
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
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.ListFragment;

import com.example.eventapp.R;
import com.example.eventapp.adapters.products.ProductListAdapter;
import com.example.eventapp.databinding.FragmentProductsListBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.ProductRepo;
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

public class ProductsListFragment extends ListFragment {
    private ProductListAdapter adapter;
    private static final String ARG_PARAM = "param";
    private ArrayList<Product> mProducts;
    private ArrayList<Product> allProducts;

    private FragmentProductsListBinding binding;
    private Boolean isFirstSelection=true;
    private SearchView searchName;
    private EditText minPrice;
    private EditText maxPrice;
    private EditText description;
    private RadioButton availableProductsFilter;
    private RadioButton notAvailableProductsFilter;
    private RadioButton allProductsFilter;
    private String minPriceText="";
    private String maxPriceText="";
    private String descriptionText="";
    private List<EventType> selectedEvents = new ArrayList<>();
    private List<Category> selectedCategories = new ArrayList<>();
    private List<Subcategory> selectedSubcategories = new ArrayList<>();
    private int availableProductFilterState=2; //0 available 1 not available 2 all
    private ProductRepo productRepository;

    public static ProductsListFragment newInstance(ArrayList<Product> products){
        ProductsListFragment fragment = new ProductsListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM, products);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productRepository=new ProductRepo();
        productRepository.getAllProducts(new ProductRepo.ProductFetchCallback() {
            @Override
            public void onProductFetch(ArrayList<Product> products) {
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
                                    ArrayList<Product> ProductsByFirm=new ArrayList<>();

                                    if (products != null) {
                                        for(Product p:products){
                                            if(p.getFirmId().equals(owner.getFirmId()))
                                                ProductsByFirm.add(p);
                                        }
                                        adapter=new ProductListAdapter(getActivity(),ProductsByFirm, false);
                                        setListAdapter(adapter);
                                    }
                                }
                            });
                        }else{
                            EmployeeRepo.getByEmail(user.getEmail(), new EmployeeRepo.EmployeeFetchCallback() {
                                @Override
                                public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                                    EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                                    ArrayList<Product> ProductsByFirm=new ArrayList<>();

                                    if (products != null) {
                                        for(Product p:products){
                                            if(p.getFirmId().equals(employee.getFirmId()))
                                                ProductsByFirm.add(p);
                                        }
                                        adapter=new ProductListAdapter(getActivity(),ProductsByFirm, false);
                                        setListAdapter(adapter);
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductsListBinding.inflate(inflater, container, false);
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


                productRepository=new ProductRepo();
                productRepository.getAllProducts(new ProductRepo.ProductFetchCallback() {
                    @Override
                    public void onProductFetch(ArrayList<Product> products) {
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
                                            ArrayList<Product> ProductsByFirm=new ArrayList<>();

                                            if (products != null) {
                                                for(Product p:products){
                                                    if(p.getFirmId().equals(owner.getFirmId()))
                                                        ProductsByFirm.add(p);
                                                }
                                                mProducts = new ArrayList<>();
                                                for(Product product:ProductsByFirm){
                                                    if (!product.getDeleted() && product.getName().toLowerCase().contains(newText.toLowerCase()) && !mProducts.contains(product))
                                                        mProducts.add(product);
                                                }
                                                adapter=new ProductListAdapter(getActivity(),mProducts, false);
                                                setListAdapter(adapter);
                                            }
                                        }
                                    });
                                }else{
                                    EmployeeRepo.getByEmail(user.getEmail(), new EmployeeRepo.EmployeeFetchCallback() {
                                        @Override
                                        public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                                            EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                                            ArrayList<Product> ProductsByFirm=new ArrayList<>();

                                            if (products != null) {
                                                for(Product p:products){
                                                    if(p.getFirmId().equals(employee.getFirmId()))
                                                        ProductsByFirm.add(p);
                                                }
                                                mProducts = new ArrayList<>();
                                                for(Product product:ProductsByFirm){
                                                    if (!product.getDeleted() && product.getName().toLowerCase().contains(newText.toLowerCase()) && !mProducts.contains(product))
                                                        mProducts.add(product);
                                                }
                                                adapter=new ProductListAdapter(getActivity(),mProducts, false);
                                                setListAdapter(adapter);
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    }
                });






                /*productRepository.getAllProducts(new ProductRepo.ProductFetchCallback() {
                    @Override
                    public void onProductFetch(ArrayList<Product> products) {
                        if (products != null) {
                            mProducts=new ArrayList<>();
                            for(Product product:products){
                                if (!product.getDeleted() && product.getName().toLowerCase().contains(newText.toLowerCase()) && !mProducts.contains(product))
                                    mProducts.add(product);
                            }
                            adapter=new ProductListAdapter(getActivity(),mProducts);
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
                productRepository=new ProductRepo();
                productRepository.getAllProducts(new ProductRepo.ProductFetchCallback() {
                    @Override
                    public void onProductFetch(ArrayList<Product> products) {
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
                                            ArrayList<Product> ProductsByFirm=new ArrayList<>();

                                            if (products != null) {
                                                for(Product p:products){
                                                    if(p.getFirmId().equals(owner.getFirmId()))
                                                        ProductsByFirm.add(p);
                                                }
                                                adapter=new ProductListAdapter(getActivity(),ProductsByFirm, false);
                                                setListAdapter(adapter);
                                            }
                                        }
                                    });
                                }else{
                                    EmployeeRepo.getByEmail(user.getEmail(), new EmployeeRepo.EmployeeFetchCallback() {
                                        @Override
                                        public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                                            EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                                            ArrayList<Product> ProductsByFirm=new ArrayList<>();

                                            if (products != null) {
                                                for(Product p:products){
                                                    if(p.getFirmId().equals(employee.getFirmId()))
                                                        ProductsByFirm.add(p);
                                                }
                                                adapter=new ProductListAdapter(getActivity(),ProductsByFirm, false);
                                                setListAdapter(adapter);
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    }
                });
                searchName.setQuery("", false);
                return false;
            }
        });

        //******************************FILTERS**********************************************************

        Button btnFilters = (Button) root.findViewById(R.id.btnFilters);
        btnFilters.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.FullScreenBottomSheetDialog);
            View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);
            bottomSheetDialog.setContentView(dialogView);

            minPrice=dialogView.findViewById(R.id.priceProductMin);
            maxPrice=dialogView.findViewById(R.id.priceProductMax);
            description=dialogView.findViewById(R.id.editTextFilterDescription);
            availableProductsFilter=dialogView.findViewById(R.id.availableProductFilter);
            notAvailableProductsFilter=dialogView.findViewById(R.id.notAvailableProductFilter);
            allProductsFilter=dialogView.findViewById(R.id.allProductFilter);

            //initial state set
            minPrice.setText(minPriceText);
            maxPrice.setText(maxPriceText);
            description.setText(descriptionText);
            if(availableProductFilterState==0)
                availableProductsFilter.setChecked(true);
            else if(availableProductFilterState==1)
                notAvailableProductsFilter.setChecked(true);
            else
                allProductsFilter.setChecked(true);
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
                                        selectedCategories.addAll(varCategories);

                                    }
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
                                        selectedSubcategories.addAll(varSubcategories);                                    }
                                }
                            });

                            return view;
                        }
                    };
                    spinnerSubCat.setAdapter(arrayAdapterSub);
                }
            });

            Spinner spinnerEvent = dialogView.findViewById(R.id.spinEvent);
            EventTypeRepo.getAllActicateEventTypes(new EventTypeRepo.EventTypeFetchCallback() {
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

                            EventType event = getItem(position);
                            textView.setText(event.getName());
                            for(EventType c:selectedEvents)
                                if(c.getId().equals(event.getId()))
                                    checkBox.setChecked(true);
                            checkBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CheckBox checkBox = (CheckBox) v;
                                    EventType selectedItem = getItem(position);
                                    if (checkBox.isChecked()) {
                                        selectedEvents.add(selectedItem);
                                    } else {
                                        ArrayList<EventType> varEventTypes=new ArrayList<>();
                                        for(EventType c:selectedEvents)
                                            if(!c.getId().equals(selectedItem.getId()))
                                                varEventTypes.add(c);

                                        selectedEvents.clear();
                                        selectedEvents.addAll(varEventTypes);                                       }
                                }
                            });

                            return view;
                        }
                    };
                    spinnerEvent.setAdapter(arrayAdapterEvent);
                }
            });
/*
            Spinner spinnerEvent = dialogView.findViewById(R.id.spinEvent);
            ArrayAdapter<String> arrayAdapterEvent = new ArrayAdapter<String>(getActivity(), R.layout.multispiner,
                    getResources().getStringArray(R.array.event_type_array)) {

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

                    textView.setText(getItem(position));
                    checkBox.setChecked(selectedEvents.contains(getItem(position)));
                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckBox checkBox = (CheckBox) v;
                            String selectedItem = getItem(position);
                            if (checkBox.isChecked()) {
                                // Add item to selectedItems list if checked
                                selectedEvents.add(selectedItem);
                            } else {
                                // Remove item from selectedItems list if unchecked
                                selectedEvents.remove(selectedItem);
                            }
                        }
                    });

                    return view;
                }
            };
            spinnerEvent.setAdapter(arrayAdapterEvent);
*/

            //***********************FILTERS*********************************
            Button apply=dialogView.findViewById(R.id.applyProductFilters);
            apply.setOnClickListener(a->{

                minPriceText=minPrice.getText().toString();
                maxPriceText=maxPrice.getText().toString();
                descriptionText=description.getText().toString();
                Predicate<Product> combinedPredicate = p -> true;

                if(availableProductsFilter.isChecked()) {
                    availableProductFilterState = 0;
                    combinedPredicate = combinedPredicate.and(Product::getAvailable);
                }
                else if (notAvailableProductsFilter.isChecked()) {
                    availableProductFilterState = 1;
                    combinedPredicate = combinedPredicate.and(p-> !p.getAvailable());
                }
                else
                    availableProductFilterState=2;

                if (!minPriceText.isEmpty()) {
                    combinedPredicate = combinedPredicate.and(p -> p.getPrice() > Double.parseDouble(minPriceText));
                }
                if (!maxPriceText.isEmpty()) {
                    combinedPredicate = combinedPredicate.and(p -> p.getPrice() < Double.parseDouble(maxPriceText));
                }

                if (!descriptionText.isEmpty()) {
                    String desc = descriptionText.toLowerCase();
                    combinedPredicate = combinedPredicate.and(p -> p.getDescription().toLowerCase().contains(desc));
                }

                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                FirebaseUser user=mAuth.getCurrentUser();

                Predicate<Product> finalCombinedPredicate = combinedPredicate;
                UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                    @Override
                    public void onUserObjectFetched(User user, String errorMessage) {
                        UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                        if(user.getType().equals(UserType.OWNER)){
                            OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                                @Override
                                public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                    OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                    productRepository.getAllProducts(new ProductRepo.ProductFetchCallback() {
                                        @Override
                                        public void onProductFetch(ArrayList<Product> products) {
                                            ArrayList<Product> productsByFirm=new ArrayList<>();
                                            for(Product p:products){
                                                if(p.getFirmId().equals(owner.getFirmId()))
                                                    productsByFirm.add(p);
                                            }
                                            List<Product> filteredList = productsByFirm.stream()
                                                    .filter(finalCombinedPredicate)
                                                    .collect(Collectors.toList());

                                            if (getActivity() != null ) {
                                                getActivity().runOnUiThread(() -> {
                                                    adapter = new ProductListAdapter(getActivity(), new ArrayList<>(filterByCategorySubcategoryEvents(filteredList)), false);
                                                    setListAdapter(adapter);
                                                });
                                            }
                                        }
                                    });

                                    bottomSheetDialog.dismiss();
                                }
                            });
                        }else{
                            EmployeeRepo.getByEmail(user.getEmail(), new EmployeeRepo.EmployeeFetchCallback() {
                                @Override
                                public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                                    EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);



                                    productRepository.getAllProducts(new ProductRepo.ProductFetchCallback() {
                                        @Override
                                        public void onProductFetch(ArrayList<Product> products) {
                                            ArrayList<Product> productsByFirm=new ArrayList<>();
                                            for(Product p:products){
                                                if(p.getFirmId().equals(employee.getFirmId()))
                                                    productsByFirm.add(p);
                                            }
                                            List<Product> filteredList = productsByFirm.stream()
                                                    .filter(finalCombinedPredicate)
                                                    .collect(Collectors.toList());

                                            if (getActivity() != null ) {
                                                getActivity().runOnUiThread(() -> {
                                                    adapter = new ProductListAdapter(getActivity(), new ArrayList<>(filterByCategorySubcategoryEvents(filteredList)), false);
                                                    setListAdapter(adapter);
                                                });
                                            }
                                        }
                                    });

                                    bottomSheetDialog.dismiss();




                                }
                            });
                        }
                    }
                });


            });

            //****************DISCARD FILTERS********************************

            Button discard=dialogView.findViewById(R.id.discardProductFilters);
            discard.setOnClickListener(d->{
                productRepository=new ProductRepo();
                productRepository.getAllProducts(new ProductRepo.ProductFetchCallback() {
                    @Override
                    public void onProductFetch(ArrayList<Product> products) {
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
                                            ArrayList<Product> ProductsByFirm=new ArrayList<>();

                                            if (products != null) {
                                                for(Product p:products){
                                                    if(p.getFirmId().equals(owner.getFirmId()))
                                                        ProductsByFirm.add(p);
                                                }
                                                adapter=new ProductListAdapter(getActivity(),ProductsByFirm, false);
                                                setListAdapter(adapter);
                                            }
                                        }
                                    });
                                }else{
                                    EmployeeRepo.getByEmail(user.getEmail(), new EmployeeRepo.EmployeeFetchCallback() {
                                        @Override
                                        public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                                            EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                                            ArrayList<Product> ProductsByFirm=new ArrayList<>();

                                            if (products != null) {
                                                for(Product p:products){
                                                    if(p.getFirmId().equals(employee.getFirmId()))
                                                        ProductsByFirm.add(p);
                                                }
                                                adapter=new ProductListAdapter(getActivity(),ProductsByFirm, false);
                                                setListAdapter(adapter);
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    }
                });
                selectedEvents.clear();
                selectedSubcategories.clear();
                selectedCategories.clear();
                minPriceText="";
                maxPriceText="";
                descriptionText="";
                availableProductFilterState=2;
                bottomSheetDialog.dismiss();
            });


            bottomSheetDialog.show();
        });



        //**************************FLOATING ACTION BUTTON*******************************************

        FloatingActionButton floating_action_add_categ_button = (FloatingActionButton) root.findViewById(R.id.floating_action_add_categ_button);
        floating_action_add_categ_button.setOnClickListener(v -> {
            FragmentTransition.to(ProductForm.newInstance(null), getActivity(),
                    true, R.id.scroll_products_list);
        });


        //*************************ROLES****************************************

        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null) {
            UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                @Override
                public void onUserObjectFetched(User user, String errorResponse) {
                    if (user != null && user.isActive()) {
                        if (user.getType() != UserType.OWNER)
                            floating_action_add_categ_button.setVisibility(View.GONE);

                    }
                }
            });

        }else{
            floating_action_add_categ_button.setVisibility(View.GONE);
        }
        return root;


    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<Product> filterByCategorySubcategoryEvents(List<Product> filteredList){
        List<Product> filtered=new ArrayList<>();

        if(selectedCategories.size()>0){
            for(Category category:selectedCategories) {
                for (Product s : filteredList) {
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
                for (Product s : filteredList)
                    if (s.getSubcategory().equals(subcategory.getId()) && !filtered.contains(s))
                        filtered.add(s);
            filteredList.clear();
            filteredList.addAll(filtered);
            filtered.clear();
        }

        if(selectedEvents.size()>0){
            for(EventType event:selectedEvents)
                for(Product s:filteredList)
                    for(String type:s.getType())
                        if(type.equals(event.getId()) && !filtered.contains(s))
                            filtered.add(s);
            filteredList.clear();
            filteredList.addAll(filtered);
        }
        return filteredList;
    }

}
