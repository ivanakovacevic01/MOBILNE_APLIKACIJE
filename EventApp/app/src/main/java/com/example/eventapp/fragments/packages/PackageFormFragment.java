package com.example.eventapp.fragments.packages;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.adapters.products.ProductListForPackageAdapter;
import com.example.eventapp.adapters.services.ServiceListForPackageAdapter;
import com.example.eventapp.databinding.FragmentPackageFormBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Change;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.PackageChangeHistory;
import com.example.eventapp.model.Pricelist;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Service;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.PackageChangeHistoryRepo;
import com.example.eventapp.repositories.PackageRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class PackageFormFragment extends Fragment implements ProductListForPackageAdapter.OnProductSelectionListener {

    private ProductListForPackageAdapter productPackageListAdapter;
    private ServiceListForPackageAdapter servicePackageListAdapter;
    private FragmentPackageFormBinding binding;
    private boolean isFirstSelection = true;
    private TextView priceTextView;
    private double price=0;
    private EditText packageName;
    private EditText description;
    private EditText discount;
    private RadioButton visible;
    private RadioButton available;
    private RadioButton notVisible;
    private RadioButton notAvailable;
    private String name;
    private String category;
    private ProductRepo productRepository;
    private ServiceRepo serviceRepository;
    private ArrayList<Product> addedProducts=new ArrayList<>();
    private ArrayList<Service> addedServices=new ArrayList<>();

    private Package newPackage;
    private ArrayList<Product> checkedProducts=new ArrayList<Product>();
    private ArrayList<Service> checkedService=new ArrayList<>();
    private TextView cancelationPeriod;
    private TextView reservationPeriod;
    private double productsPrice=0;
    private double servicePrice=0;
    private Package paket;
    private Spinner spinnerCategory;
    private TextView categoryTw;


    public PackageFormFragment() {
        // Required empty public constructor
    }


    public static PackageFormFragment newInstance(Package p) {
        PackageFormFragment fragment = new PackageFormFragment();
        Bundle args = new Bundle();
        args.putParcelable("PACKAGE", p);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paket = getArguments().getParcelable("PACKAGE");
            newPackage = getArguments().getParcelable("PACKAGE");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPackageFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ArrayList<Service> services=new ArrayList<>();
        ArrayList<Product> products=new ArrayList<Product>();
        packageName=binding.packageName;
        description=binding.packageDescription;
        discount=binding.packageDiscount;
        available=binding.availablePackage;
        notAvailable=binding.notAvailablePackage;
        visible=binding.visiblePackage;
        notVisible=binding.notVisiblePackage;
        cancelationPeriod=binding.cancelationPeriodPackage;
        reservationPeriod=binding.reservationPeriodPackage;
        spinnerCategory = binding.spinnerPackageCategory;
        categoryTw=binding.categoryTw;
        priceTextView=binding.packagePrice;

        if(paket==null)
            SetCreatingState();
        else
            SetUpdatingState();




        ListView productsList=root.findViewById(R.id.listPackages);
        ListView servicesList=root.findViewById(R.id.listServices);

        Button btnProducts = (Button) root.findViewById(R.id.addProductToPackage);
        Button btnServices = (Button) root.findViewById(R.id.addServiceToPackage);


        priceTextView=(TextView)root.findViewById(R.id.packagePrice);


        btnProducts.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

            dialog.setMessage("Check products you want to add to package");
            View dialogView = inflater.inflate(R.layout.dialog_products, null);
            dialog.setView(dialogView);

            ListView listViewProducts = dialogView.findViewById(R.id.listViewProducts);
            productRepository=new ProductRepo();
            productRepository.getAllProducts(new ProductRepo.ProductFetchCallback() {
                @Override
                public void onProductFetch(ArrayList<Product> products) {
                    if (products != null) {
                        ArrayList<Product> productsForCategory=new ArrayList<>();
                        FirebaseAuth mAuth= FirebaseAuth.getInstance();
                        FirebaseUser user=mAuth.getCurrentUser();
                        OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                            @Override
                            public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                for(Product p:products) {
                                    if(category.equals(p.getCategory()) && p.getFirmId().equals(owner.getFirmId()))
                                        productsForCategory.add(p);
                                }

                                ProductListForPackageAdapter productAdapter = new ProductListForPackageAdapter(getActivity(), productsForCategory,addedProducts,paket,PackageFormFragment.this, false);
                                productAdapter.setValueFromEditText(name);


                                listViewProducts.setAdapter(productAdapter);
                            }
                        });

                    }
                }
            });


            AlertDialog alert = dialog.create();
            alert.show();
        });

        btnServices.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("Check service you want to add to package");
            View dialogView = inflater.inflate(R.layout.dialog_products, null);
            dialog.setView(dialogView);

            ListView listViewProducts = dialogView.findViewById(R.id.listViewProducts);

            serviceRepository=new ServiceRepo();
            serviceRepository.getAllServices(new ServiceRepo.ServiceFetchCallback() {
                @Override
                public void onServiceFetch(ArrayList<Service> services) {
                    if (services != null) {
                        ArrayList<Service> servicesForCatgory=new ArrayList<>();
                        FirebaseAuth mAuth= FirebaseAuth.getInstance();
                        FirebaseUser user=mAuth.getCurrentUser();
                        OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                            @Override
                            public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                for(Service s:services) {
                                    if(category.equals(s.getCategory()) && s.getFirmId().equals(owner.getFirmId()))
                                        servicesForCatgory.add(s);
                                }
                                ServiceListForPackageAdapter serviceAdapter = new ServiceListForPackageAdapter(getActivity(), servicesForCatgory,addedServices,paket,PackageFormFragment.this);
                                serviceAdapter.setValueFromEditText(name);
                                listViewProducts.setAdapter(serviceAdapter);
                            }
                        });


                    }
                }
            }) ;


            AlertDialog alert = dialog.create();
            alert.show();
        });


        Button submit=binding.btnSubmit;
        submit.setOnClickListener(v->{
            if(paket==null) {
              addPackage();
            }else{
                if(updatePackage()){
                    LocalDateTime now=LocalDateTime.now();
                    PackageChangeHistory packageChangeHistory =new PackageChangeHistory();
                    packageChangeHistory.setChangeTime(now.toString());
                    packageChangeHistory.setId("packageChange_"+generateUniqueString());
                    packageChangeHistory.setaPackage(newPackage);
                    ArrayList<Change> packageChanges=paket.getChanges();
                    packageChanges.add(new Change(packageChangeHistory.getId(),now));
                    paket.setChanges(packageChanges);
                    paket.setLastChange(now.toString());
                    PackageChangeHistoryRepo.createPackageHistoryChangeRepo(packageChangeHistory);
                    PackageRepo.updatePackage(paket);
                    FragmentTransition.to(PackageListFragment.newInstance(), getActivity(),
                            true, R.id.scroll_package_list);
                }
            }
        });

        return root;
    }
    @Override
    public void onProductsSelected(ArrayList<Product> selectedProducts) {
        productsPrice=0;
        for(Product p:selectedProducts)
            productsPrice+=p.getPrice();
        price=productsPrice+servicePrice;
        priceTextView.setText(Double.toString(price)+" din");
        addedProducts.clear();
        addedProducts.addAll(selectedProducts);

        for(Product p: addedProducts)
            Log.i("KOLICINAE",addedProducts.size()+" "+p.toString());

    }
    @Override
    public void onServicesSelected(ArrayList<Service> selectedServices) {
        servicePrice=0;
        for(Service s:selectedServices)
            servicePrice+=s.getPrice();
        price=servicePrice+productsPrice;
        priceTextView.setText(Double.toString(price)+" din");
        addedServices.clear();
        addedServices.addAll(selectedServices);
        if(addedServices.size()>0) {
            double minCancelationPeriod = addedServices.get(0).getCancellationDeadline();
            double minReservationPeriod = addedServices.get(0).getReservationDeadline();


            for (Service s : addedServices) {
                if (s.getCancellationDeadline() < minCancelationPeriod)
                    minCancelationPeriod = s.getCancellationDeadline();
                if (s.getReservationDeadline() < minReservationPeriod)
                    minReservationPeriod = s.getReservationDeadline();
            }
            cancelationPeriod.setText(minCancelationPeriod + " d");
            reservationPeriod.setText(minReservationPeriod + " d");
        }
    }

    private boolean updatePackage(){
        if(AreFieldsValid()) {
            if(paket.getDiscount()!=Double.parseDouble(discount.getText().toString())){
                Pricelist pricelist=new Pricelist();
                pricelist.setName(paket.getName());
                pricelist.setPrice(price);
                pricelist.setDiscount(Double.parseDouble(discount.getText().toString()));
                Double discountPrice=pricelist.getPrice()*(1- pricelist.getDiscount()/100);
                pricelist.setDiscountPrice(Double.parseDouble(discountPrice.toString()));
                pricelist.setFrom(new Date().toString());
                if(paket.getPricelist().size()>0) {
                    Pricelist p = paket.getPricelist().get(paket.getPricelist().size() - 1);
                    p.setTo(new Date().toString());
                }
                List<Pricelist> pricelists=paket.getPricelist();
                pricelists.add(pricelist);
                paket.setPricelist(pricelists);
            }
            paket.setName(packageName.getText().toString());
            paket.setDescription(description.getText().toString());
            ArrayList<String> products=new ArrayList<>();
            ArrayList<String> services=new ArrayList<>();
            for(Product p:addedProducts)
                products.add(p.getId());
            for(Service s:addedServices)
                services.add(s.getId());
            paket.setProducts(products);
            paket.setServices(services);
            paket.setPrice(servicePrice+productsPrice);
            paket.setDiscount(Double.parseDouble(discount.getText().toString()));
            if(available.isChecked())
                paket.setAvailable(true);
            if(notAvailable.isChecked())
                paket.setAvailable(false);
            if(visible.isChecked())
                paket.setVisible(true);
            if(notVisible.isChecked())
                paket.setVisible(false);
            ArrayList<String> subcategories=new ArrayList<>();
            ArrayList<String> eventTypes=new ArrayList<>();
            ArrayList<String> images=new ArrayList<>();
            for(Product p:addedProducts) {
                subcategories.add(p.getSubcategory());
                for(String event:p.getType()) {
                    boolean contain=false;
                    for (String type : eventTypes)
                        if (type.equals(event)) {
                            contain = true;
                            break;
                        }

                    if(contain==false)
                        eventTypes.add(event);
                }

                images.addAll(p.getImageUris());
            }
            for(Service s:addedServices) {
                subcategories.add(s.getSubcategory());
                for(String event:s.getType()) {
                    boolean contain=false;
                    for (String type : eventTypes)
                        if (type.equals(event)) {
                            contain = true;
                            break;
                        }

                    if(contain==false)
                        eventTypes.add(event);
                }
                images.addAll(s.getImageUris());
            }
            paket.setSubcategories(subcategories);
            paket.setType(eventTypes);
            paket.setImageUris(images);
            if(addedServices.size()>0){
                double minCancelationPeriod=addedServices.get(0).getCancellationDeadline();
                double minReservationPeriod=addedServices.get(0).getReservationDeadline();

                for(Service s:addedServices) {
                    if (s.getCancellationDeadline() < minCancelationPeriod)
                        minCancelationPeriod = s.getCancellationDeadline();
                    if(s.getReservationDeadline()<minReservationPeriod)
                        minReservationPeriod=s.getReservationDeadline();
                    if(s.getManualConfirmation())
                        paket.setManualConfirmation(true);
                }
                paket.setCancellationDeadline(minCancelationPeriod);
                paket.setReservationDeadline(minReservationPeriod);
            }
            return true;
        }
        return false;
    }
    private boolean addPackage(){
        if(AreFieldsValid()) {
            newPackage = new Package();
            newPackage.setName(packageName.getText().toString());
            newPackage.setDescription(description.getText().toString());
            newPackage.setCategory(category);
            ArrayList<String> products=new ArrayList<>();
            ArrayList<String> services=new ArrayList<>();
            for(Product p:addedProducts)
                products.add(p.getId());
            for(Service s:addedServices)
                services.add(s.getId());
            newPackage.setProducts(products);
            newPackage.setServices(services);
            newPackage.setPrice(price);
            if(!discount.getText().toString().isEmpty())
                newPackage.setDiscount(Double.parseDouble(discount.getText().toString()));
            if(available.isChecked())
                newPackage.setAvailable(true);
            if(notAvailable.isChecked())
                newPackage.setAvailable(false);
            if(visible.isChecked())
                newPackage.setVisible(true);
            if(notVisible.isChecked())
                notVisible.setChecked(false);
            ArrayList<String> subcategories=new ArrayList<>();
            ArrayList<String> eventTypes=new ArrayList<>();
            ArrayList<String> images=new ArrayList<>();
            for(Product p:addedProducts) {
                subcategories.add(p.getSubcategory());
                eventTypes.addAll(p.getType());
                images.addAll(p.getImageUris());
            }
            for(Service s:addedServices) {
                subcategories.add(s.getSubcategory());
                eventTypes.addAll(s.getType());
                images.addAll(s.getImageUris());
            }
            newPackage.setSubcategories(subcategories);
            newPackage.setType(eventTypes);
            newPackage.setImageUris(images);
            if(addedServices.size()>0){
                double minCancelationPeriod=addedServices.get(0).getCancellationDeadline();
                double minReservationPeriod=addedServices.get(0).getReservationDeadline();

                for(Service s:addedServices) {
                    if (s.getCancellationDeadline() < minCancelationPeriod)
                        minCancelationPeriod = s.getCancellationDeadline();
                    if(s.getReservationDeadline()<minReservationPeriod)
                        minReservationPeriod=s.getReservationDeadline();
                    if(s.getManualConfirmation())
                        newPackage.setManualConfirmation(true);
                }
                newPackage.setCancellationDeadline(minCancelationPeriod);
                newPackage.setReservationDeadline(minReservationPeriod);
            }

            ArrayList<Pricelist> pricelists=new ArrayList<>();
            Pricelist pricelist=new Pricelist();
            pricelist.setFrom(new Date().toString());
            pricelist.setName(packageName.getText().toString());
            pricelist.setPrice(price);
            pricelist.setDiscountPrice(Double.parseDouble(discount.getText().toString()));
            pricelists.add(pricelist);
            newPackage.setPricelist(pricelists);

            PackageRepo.createPackage(newPackage);
            FirebaseAuth mAuth= FirebaseAuth.getInstance();
            FirebaseUser user=mAuth.getCurrentUser();
            OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                @Override
                public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                    OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                    newPackage.setFirmId(owner.getFirmId());
                    PackageRepo.updatePackage(newPackage);
                    FragmentTransition.to(PackageListFragment.newInstance(), getActivity(),
                            true, R.id.scroll_package_list);
                }
            });

            return true;
        }
        return false;
    }

    private boolean AreFieldsValid(){
        if(!discount.getText().toString().isEmpty() && (Double.parseDouble(discount.getText().toString())<0) ||(Double.parseDouble(discount.getText().toString())>100)){
            discount.setError("Input number in range 0-100.");
            return false;
        }
        if(description.getText().toString().isEmpty()){
            description.setError("Description is required.");
            return false;
        }
        if(addedServices.size()==0 && addedProducts.size()==0){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext()); // Pass your activity or context here

            builder.setTitle("Error!");
            builder.setMessage("Please select at least one product or service.");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            android.app.AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
        return true;
    }


    private void SetCreatingState(){
        visible.setChecked(true);
        available.setChecked(true);
        categoryTw.setText("Category");
        CategoryRepo.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
            @Override
            public void onCategoryFetch(ArrayList<Category> categories) {
                if (categories != null) {
                    // Create an adapter for the spinner with the fetched categories
                    ArrayAdapter<Category> arrayAdapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, categories);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Set the adapter to the spinner
                    spinnerCategory.setAdapter(arrayAdapter);
                }
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected category
                Category c = (Category) parent.getItemAtPosition(position);
                category=c.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }
    @SuppressLint("SetTextI18n")
    private void SetUpdatingState(){
        productRepository=new ProductRepo();
        productRepository.getAllProducts(new ProductRepo.ProductFetchCallback() {
            @Override
            public void onProductFetch(ArrayList<Product> products) {
                if (products != null) {
                    for(String s:paket.getProducts())
                        for(Product p:products)
                            if(p.getId().equals(s)) {
                                productsPrice += p.getPrice();
                                addedProducts.add(p);
                            }
                }
            }
        });
        serviceRepository=new ServiceRepo();
        serviceRepository.getAllServices(new ServiceRepo.ServiceFetchCallback() {
            @Override
            public void onServiceFetch(ArrayList<Service> services) {
                if (services != null) {
                    for(String id:paket.getServices())
                        for(Service s:services)
                            if(s.getId().equals(id)) {
                                addedServices.add(s);
                                servicePrice += s.getPrice();
                            }
                }
            }
        }) ;
        category=paket.getCategory();
        priceTextView.setText(paket.getPrice()+" din");
        packageName.setText(paket.getName());
        cancelationPeriod.setText(paket.getCancellationDeadline()+" d");
        reservationPeriod.setText(paket.getReservationDeadline()+" d");
        description.setText(paket.getDescription());
        discount.setText(Double.toString(paket.getDiscount()));
        spinnerCategory.setVisibility(View.GONE);
        if(paket.getAvailable())
            available.setChecked(true);
        else
            notAvailable.setChecked(true);
        if(paket.getVisible())
            visible.setChecked(true);
        else
            notVisible.setChecked(true);
        categoryTw.setWidth(200);
        CategoryRepo.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
            @Override
            public void onCategoryFetch(ArrayList<Category> categories) {
                for(Category c:categories)
                    if(c.getId().equals(paket.getCategory()))
                        categoryTw.setText("Category   "+c.getName());
            }
        });


    }
}