package com.example.eventapp.fragments.products;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.Date;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Change;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Pricelist;
import com.example.eventapp.model.ProductChangeHistory;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.SubcategorySuggestion;
import com.example.eventapp.model.SubcategoryType;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.ProductChangeHistoryRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.example.eventapp.repositories.SubcategorySuggestionsRepo;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ImageView;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.Random;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentProductFormBinding;
import com.example.eventapp.model.Product;
import com.example.eventapp.repositories.UserRepo;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.content.ClipData;
import android.content.Intent;
import android.app.Activity;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;


public class ProductForm extends Fragment {
    private ArrayList<String> imageUris=new ArrayList<>();
    private List<Uri> selectedUriImages;
    private FragmentProductFormBinding binding;
    private Boolean isFirstSelection=true;
    private TextView subcateSuggestion;
    private String subcatSuggestionName="";
    private EditText name;
    private EditText description;
    private EditText discount;
    private EditText price;
    private String selectedCategory="";
    private RadioButton available;
    private RadioButton notAvailable;
    private RadioButton visible;
    private RadioButton notVisible;
    private Spinner category;
    private Spinner subcategory;
    private Spinner eventType;
    private  String productCategory="";
    private ArrayList<EventType> selectedEvents;
    private String selectedSubcategory="";
    private String subcategoryName;
    private String subcategoryDescription;
    private Spinner spinnerCategory;
    private Spinner spinnersubcategory;
    private TextView categoryTextView;
    private Button subcategorySuggestion;
    private SubcategoryRepo subcategoryRepo;
    private  Boolean isEventTypeChanged;
    public ProductForm() {
        // Required empty public constructor
    }

    private Product newProduct;
    private Product product;

    public static ProductForm newInstance(Product p) {
        ProductForm fragment = new ProductForm();
        Bundle args = new Bundle();
        args.putParcelable("PRODUCT", p);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            product = getArguments().getParcelable("PRODUCT");
            newProduct = getArguments().getParcelable("PRODUCT");

        }
        selectedEvents=new ArrayList<>();
        subcategoryRepo = new SubcategoryRepo();
        isEventTypeChanged=false;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        subcateSuggestion=binding.twSubcatSuggestion;
        spinnerCategory = (Spinner) root.findViewById(R.id.spinnerProductCategory);
        spinnersubcategory=binding.spinnerProductSubcategory;
        subcateSuggestion.setText("Or add new subcategory suggestion: ");
        name = binding.nameProduct;
        description = binding.descriptionProduct;
        discount = binding.discountProduct;
        price = binding.priceProduct;
        visible = binding.visibleProduct;
        notVisible = binding.notVisibleProduct;
        available = binding.availableProduct;
        notAvailable = binding.notAvailableProduct;
        eventType = binding.spinnerProductEventType;
        subcategorySuggestion = (Button) root.findViewById(R.id.subcategory_suggestion);



        if(product==null)
            setCreatingState();
        else
            setUpdateState();  //IZMENA


        //******************************************EVENT TYPE SPINNER******************************************************
        EventTypeRepo eventTypeRepo = new EventTypeRepo(); // Instantiate your EventTypeRepo class

        eventTypeRepo.getAllActicateEventTypes(new EventTypeRepo.EventTypeFetchCallback() {
            @Override
            public void onEventTypeFetch(ArrayList<EventType> eventTypes) {
                if (eventTypes != null) {
                    Spinner spinner = (Spinner) root.findViewById(R.id.spinnerProductEventType);
                    ArrayAdapter<EventType> arrayAdapter = new ArrayAdapter<EventType>(getActivity(), R.layout.multispiner,
                            eventTypes) {

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

                            // Set your text and checkbox state here
                            EventType eventType = getItem(position);
                            if (eventType != null) {
                                if(product!=null){
                                    for(String s:product.getType())
                                        if(s.equals(eventType.getId()) && !selectedEvents.contains(eventType))
                                            selectedEvents.add(eventType);
                                }
                                textView.setText(eventType.getName()); // Assuming EventType has a getName() method
                                checkBox.setChecked(selectedEvents.contains(eventType)); // Assuming selectedEvents is a list of selected EventType objects

                                checkBox.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        isEventTypeChanged=true;
                                        CheckBox checkBox = (CheckBox) v;
                                        EventType selectedEventType = getItem(position);
                                        if (checkBox.isChecked() && !selectedEvents.contains(selectedEventType)) {
                                            // Add item to selectedEvents list if checked
                                            selectedEvents.add(selectedEventType);
                                        } else {
                                            // Remove item from selectedEvents list if unchecked
                                            selectedEvents.remove(selectedEventType);
                                        }
                                    }
                                });
                            }

                            return view;
                        }
                    };
                    spinner.setAdapter(arrayAdapter);
                } else {
                    // Handle the case where fetching event types failed
                    // Show a toast or handle the error appropriately
                }
            }
        });

        //*************************SUBCATEGORY SUGGESTION***********************************

        subcategorySuggestion.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.FullScreenBottomSheetDialog);
            View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_subcategory, null);
            bottomSheetDialog.setContentView(dialogView);

            EditText name=dialogView.findViewById(R.id.subcatName);
            EditText description=dialogView.findViewById(R.id.subcatDescription);
            Button submit=dialogView.findViewById(R.id.submitSubcat);

            submit.setOnClickListener(s->{
                if(!name.getText().toString().isEmpty() && !description.getText().toString().isEmpty()) {
                    subcatSuggestionName=name.getText().toString();
                    subcategoryName=name.getText().toString();
                    subcategoryDescription=description.getText().toString();
                    bottomSheetDialog.dismiss();
                    subcateSuggestion.setText("Subcategory suggestion: "+name.getText());
                    subcategorySuggestion.setVisibility(View.GONE);
                }
                if(name.getText().toString().isEmpty()){
                    name.setError("Name is required.");
                }
                if(description.getText().toString().isEmpty()){
                    description.setError("Description is required.");
                }

            });

            bottomSheetDialog.show();
        });





        //******************************SLIKE*****************************************
        Button uploadImageButton = root.findViewById(R.id.uploadCompanyPhotoButton);
        LinearLayout photoLinearLayout = root.findViewById(R.id.photoLinearLayout);
        List<Uri> selectedUriImages = new ArrayList<>(); // tu ce biti URI od slika

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            selectedUriImages.clear();  //da mi ne duplira slike

                            if (data.getClipData() != null) {
                                ClipData clipData=data.getClipData();
                                for(int i=0;i<clipData.getItemCount();i++){
                                    ClipData.Item item=clipData.getItemAt(i);
                                    Uri selectedImageUri = item.getUri();
                                    selectedUriImages.add(selectedImageUri);
                                }

                            } else if (data.getData() != null) {
                               Uri selectedImageUri=data.getData();
                               selectedUriImages.add(selectedImageUri);
                            }
                            // za prikaz svih slika u layoutu
                            for (Uri imageUri : selectedUriImages) {
                                ImageView imageView = new ImageView(getContext());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        300,300
                                );
                                layoutParams.setMargins(0, 0, 10, 0);
                                imageView.setLayoutParams(layoutParams);
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                imageView.setImageURI(imageUri);
                                photoLinearLayout.addView(imageView);
                            }
                            for(Uri imageUri:selectedUriImages){
                                StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

                                imageRef.putFile(imageUri)
                                        .addOnSuccessListener(taskSnapshot -> {
                                            // Image uploaded successfully, get the download URL
                                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                // Save the download URL to the Realtime Database
                                                String imageUrl = uri.toString();
                                                imageUris.add(imageUrl);
                                                if(product!=null){
                                                    List<String> uris=product.getImageUris();
                                                    uris.add(imageUrl);
                                                    product.setImageUris((ArrayList<String>) uris);
                                                }
                                            });
                                        })
                                        .addOnFailureListener(exception -> {
                                            // Handle unsuccessful uploads
                                            Log.e("TAG", "Image upload failed: " + exception.getMessage());
                                        });
                            }
                        }
                    }
                });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                mGetContent.launch(intent);
            }
        });

        return root;

    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {

        Button btnSubmit = (Button) binding.btnSubmit;
        btnSubmit.setOnClickListener(v -> {
            if(product==null) {
                if (addNewProduct()) {
                    if (subcatSuggestionName.equals("")) {
                        ProductRepo.createProduct(newProduct);
                        FirebaseAuth mAuth= FirebaseAuth.getInstance();
                        FirebaseUser user=mAuth.getCurrentUser();
                        OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                            @Override
                            public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                newProduct.setFirmId(owner.getFirmId());
                                ProductRepo.updateProduct(newProduct);
                                FragmentTransition.to(ProductsListFragment.newInstance(new ArrayList<>()), getActivity(),
                                        true, R.id.scroll_products_list);
                            }
                        });
                        productCategory = "";
                    } else {
                        addNewSubcategorySuggestion(subcategoryName, subcategoryDescription);
                        Notification newNotification=new Notification();
                        newNotification.setDate(new Date().toString());
                        newNotification.setMessage("New subcategory suggestion.");
                        newNotification.setReceiverRole(UserType.ADMIN);

                        FirebaseAuth mAuth= FirebaseAuth.getInstance();
                        FirebaseUser user=mAuth.getCurrentUser();
                        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                            @Override
                            public void onUserObjectFetched(User user, String errorResponse) {
                                if (user != null && user.isActive()) {
                                    newNotification.setSenderId(user.getId());
                                    NotificationRepo.create(newNotification);
                                }
                            }
                        });
                        FragmentTransition.to(ProductsListFragment.newInstance(new ArrayList<>()), getActivity(),
                                true, R.id.scroll_products_list);
                    }

                }
            }else{
                if(editProduct()){
                    LocalDateTime now=LocalDateTime.now();
                    ProductChangeHistory productChangeHistory =new ProductChangeHistory();
                    productChangeHistory.setChangeTime(now.toString());
                    productChangeHistory.setId("productChange_"+generateUniqueString());
                    productChangeHistory.setProduct(newProduct);
                    ArrayList<Change> productChanges=product.getChanges();
                    productChanges.add(new Change(productChangeHistory.getId(),now));
                    product.setChanges(productChanges);
                    product.setLastChange(now.toString());
                    ProductChangeHistoryRepo.createProductHistoryChangeRepo(productChangeHistory);
                    ProductRepo.updateProduct(product);
                    FragmentTransition.to(ProductsListFragment.newInstance(new ArrayList<>()), getActivity(),
                            true, R.id.scroll_products_list);
                }
            }

        });




    }
    private boolean areValidFields(){
        if(name.getText().toString().isEmpty()) {
            name.setError("Name is required.");
            return false;
        }
        if(description.getText().toString().isEmpty()){
            description.setError("Description is required.");
            return false;
        }
        if(price.getText().toString().isEmpty()){
            price.setError("Price is required.");
            return false;
        }
        if(!price.getText().toString().isEmpty() && Double.parseDouble(price.getText().toString())<0){
            price.setError("Input positive number.");
            return false;
        }
        if(!discount.getText().toString().isEmpty() && (Double.parseDouble(discount.getText().toString())<0) ||(Double.parseDouble(discount.getText().toString())>100)){
            discount.setError("Input number in range 0-100.");
            return false;
        }
        if( selectedEvents.size()==0 ){
            Log.i("SE",selectedEvents.size()+"");
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); // Pass your activity or context here

            builder.setTitle("Error!");
            builder.setMessage("Please select event type.");

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

    private boolean areCategoryAndSubcategoryValid(){
        if( selectedCategory.isEmpty() ){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); // Pass your activity or context here

            builder.setTitle("Error!");
            builder.setMessage("Please select category.");

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
        if( selectedSubcategory.isEmpty() ){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); // Pass your activity or context here

            builder.setTitle("Error!");
            builder.setMessage("Please select subcategory.");

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
    private boolean addNewProduct() {
        newProduct = new Product();
        if(areValidFields() && areCategoryAndSubcategoryValid()) {
            String productName = name.getText().toString();
            String productDescription = description.getText().toString();
            double productPrice = Double.parseDouble(price.getText().toString());
            double productDiscount=0;
            if(!discount.getText().toString().isEmpty())
                 productDiscount = Double.parseDouble(discount.getText().toString());
            if (subcatSuggestionName.equals("")) {
                newProduct.setSubcategory(selectedSubcategory);
            }

            newProduct.setName(productName);
            newProduct.setDescription(productDescription);
            newProduct.setPrice(productPrice);
            newProduct.setDiscount(productDiscount);
            if (visible.isChecked()) {
                newProduct.setVisible(true);
            } else if (notVisible.isChecked()) {
                newProduct.setVisible(false);
            }
            if (available.isChecked()) {
                newProduct.setAvailable(true);
            } else if (notAvailable.isChecked()) {
                newProduct.setAvailable(false);
            }
            ArrayList<String> events=new ArrayList<>();
            for(EventType e:selectedEvents)
                events.add(e.getId());
            newProduct.setTypes(events);
            newProduct.setCategory(selectedCategory);
            newProduct.setImageUris(imageUris);
            ArrayList<Pricelist> pricelists=new ArrayList<>();
            Pricelist pricelist=new Pricelist();
            pricelist.setName(productName);
            pricelist.setFrom(new Date().toString());
            pricelist.setPrice(productPrice);
            pricelist.setDiscount(productDiscount);
            pricelist.setDiscountPrice(productPrice*(1-productDiscount/100));
            pricelists.add(pricelist);
            newProduct.setPricelist(pricelists);
            return true;
        }else{
            return false;
        }
    }

    private void addNewSubcategorySuggestion(String name, String description){

        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
            @Override
            public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                SubcategorySuggestion subcategorySuggestion=new SubcategorySuggestion();
                productCategory=selectedCategory;
                subcategorySuggestion.setCategory(productCategory);
                newProduct.setFirmId(owner.getFirmId());
                subcategorySuggestion.setProduct(newProduct);
                subcategorySuggestion.setName(name);
                subcategorySuggestion.setDescription(description);
                subcategorySuggestion.setSubcategoryType(SubcategoryType.PRODUCT);
                subcatSuggestionName=name;

                subcategorySuggestion.setUserId(user.getUid());
                SubcategorySuggestionsRepo.create(subcategorySuggestion);
            }
        });


    }

    private boolean editProduct(){
        if(!isEventTypeChanged)
            selectedEvents.add(new EventType());

        if(areValidFields()){

            if(product.getPrice()!=Double.parseDouble(price.getText().toString()) || product.getDiscount()!=Double.parseDouble(discount.getText().toString())){
                Pricelist pricelist=new Pricelist();
                pricelist.setName(product.getName());
                pricelist.setPrice(Double.parseDouble(price.getText().toString()));
                pricelist.setDiscount(Double.parseDouble(discount.getText().toString()));
                Double discountPrice=pricelist.getPrice()*(1- pricelist.getDiscount()/100);
                pricelist.setDiscountPrice(Double.parseDouble(discountPrice.toString()));
                pricelist.setFrom(new Date().toString());
                if(product.getPricelist().size()>0) {
                    Pricelist p = product.getPricelist().get(product.getPricelist().size() - 1);
                    p.setTo(new Date().toString());
                }
                ArrayList<Pricelist> pricelists=product.getPricelist();
                pricelists.add(pricelist);
                product.setPricelist(pricelists);
            }

            product.setName(name.getText().toString());
            product.setDescription(description.getText().toString());
            product.setSubcategory(selectedSubcategory);
            product.setPrice(Double.parseDouble(price.getText().toString()));
            if(!discount.getText().toString().isEmpty())
                product.setDiscount(Double.parseDouble(discount.getText().toString()));
            if (visible.isChecked()) {
                product.setVisible(true);
            } else if (notVisible.isChecked()) {
                product.setVisible(false);
            }
            if (available.isChecked()) {
                product.setAvailable(true);
            } else if (notAvailable.isChecked()) {
                product.setAvailable(false);
            }
            if(isEventTypeChanged) {
                ArrayList<String> events = new ArrayList<>();
                for (EventType e : selectedEvents)
                    events.add(e.getId());
                product.setTypes(events);
            }


            return true;
        }
        return false;
    }
    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }


    private void setUpdateState(){
        selectedCategory=product.getCategory();
        selectedSubcategory=product.getSubcategory();
        subcategoryRepo.getSubcategoriesByCategoryId(selectedCategory, new SubcategoryRepo.SubcategoryFetchCallback() {
            @Override
            public void onSubcategoryFetch(ArrayList<Subcategory> subcategories) {
                if (subcategories != null) {
                    ArrayAdapter<Subcategory> subcategoryAdapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, subcategories);
                    subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnersubcategory.setAdapter(subcategoryAdapter);

                    if(product!=null){
                        for(Subcategory s:subcategories)
                            if(s.getId().equals(product.getSubcategory()))
                                spinnersubcategory.setSelection(subcategoryAdapter.getPosition(s));
                    }
                }
            }
        });

        spinnersubcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Subcategory s = (Subcategory) parent.getItemAtPosition(position);
                selectedSubcategory=s.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        name.setText(product.getName());
        description.setText(product.getDescription());
        price.setText(Double.toString(product.getPrice()));
        discount.setText(Double.toString(product.getDiscount()));
        if(product.getAvailable())
            available.setChecked(true);
        else
            notAvailable.setChecked(true);
        if(product.getVisible())
            visible.setChecked(true);
        else
            notVisible.setChecked(true);

        spinnerCategory.setEnabled(false);
        //spinnersubcategory.setEnabled(false);
        LinearLayout cat=binding.llcatProduct;
        cat.setVisibility(View.GONE);
        //LinearLayout subcat=binding.llsubcatProduct;
        //subcat.setVisibility(View.GONE);
        subcategorySuggestion.setVisibility(View.GONE);
        CategoryRepo.getAllCategories( new CategoryRepo.CategoryFetchCallback() {
            @Override
            public void onCategoryFetch(ArrayList<Category> categories) {
                for(Category c:categories)
                    if(c.getId().equals(product.getCategory())){
                        SubcategoryRepo.getAllSubcategories(new SubcategoryRepo.SubcategoryFetchCallback() {
                            @Override
                            public void onSubcategoryFetch(ArrayList<Subcategory> subcategories) {
                                for(Subcategory s:subcategories)
                                    if(s.getId().equals(product.getSubcategory()))
                                        subcateSuggestion.setText("Category:          "+c.getName());
                            }
                        });
                    }
            }
        });
    }

    private void setCreatingState(){
        available.setChecked(true);
        visible.setChecked(true);

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
                } else {
                    // Handle the case where fetching categories failed
                    // Show a toast or handle the error appropriately
                }
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected category
                Category selected= (Category) parent.getItemAtPosition(position);
                selectedCategory=selected.getId();

                // Fetch subcategories based on the selected category
                subcategoryRepo.getSubcategoriesByCategoryId(selectedCategory, new SubcategoryRepo.SubcategoryFetchCallback() {
                    @Override
                    public void onSubcategoryFetch(ArrayList<Subcategory> subcategories) {
                        if (subcategories != null) {
                            ArrayAdapter<Subcategory> subcategoryAdapter = new ArrayAdapter<>(requireContext(),
                                    android.R.layout.simple_spinner_item, subcategories);
                            subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spinnersubcategory.setAdapter(subcategoryAdapter);
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected
            }
        });
        spinnersubcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Subcategory s = (Subcategory) parent.getItemAtPosition(position);
                selectedSubcategory=s.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }
}