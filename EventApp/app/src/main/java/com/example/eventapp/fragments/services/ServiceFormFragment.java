package com.example.eventapp.fragments.services;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentServiceFormBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Pricelist;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.SubcategorySuggestion;
import com.example.eventapp.model.SubcategoryType;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;

public class ServiceFormFragment extends Fragment {

   private FragmentServiceFormBinding binding;
   private EditText duration;
   private Boolean isFirstSelection =true;
   private EditText name;
   private EditText description;
   private EditText pricePerHour;
   private EditText discount;
   private TextView orrAdd;
   private EditText specifics;
   private EditText minDuration;
   private EditText maxDuration;
   private Service newService;
   private Spinner category;
   private Spinner subcategory;
   private ArrayList<EventType> selectedEvents;
   private SubcategorySuggestion subcategorySuggestion;
   private String selectedCategory="";
   private String selectedSubcategory="";
   private String subcatgoryName="";
   private String subcategoryDescription;
   private Service service;
    private Button btnSubCategorySuggestion ;
    private Spinner spinnerCategory;
    private Spinner spinnersubcategory;

    private boolean isEventTypeChanged;


    public ServiceFormFragment() {
    }


    public static ServiceFormFragment newInstance(Service s) {
        ServiceFormFragment fragment = new ServiceFormFragment();
        Bundle args = new Bundle();
        args.putParcelable("SERVICE", s);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedEvents=new ArrayList<>();
        subcategorySuggestion=new SubcategorySuggestion();
        if (getArguments() != null) {
            service = getArguments().getParcelable("SERVICE");
            newService=getArguments().getParcelable("SERVICE");
        }
        isEventTypeChanged=false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServiceFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        orrAdd=binding.tworAdd;
        name=binding.nameService;
        description=binding.descriptionService;
        pricePerHour=binding.pricePerHourService;
        discount=binding.discountService;
        specifics=binding.specificsService;
        minDuration=binding.minHService;
        maxDuration=binding.maxHService;
        category=binding.spinnerServiceCategory;
        subcategory=binding.spinnerServiceSubcategory;
        duration=binding.dHService;
        btnSubCategorySuggestion = (Button) root.findViewById(R.id.subcategory_suggestion);
        spinnerCategory=binding.spinnerServiceCategory;
        spinnersubcategory=binding.spinnerServiceSubcategory;

        minDuration.addTextChangedListener(textWatcher);
        maxDuration.addTextChangedListener(textWatcher);
        duration.addTextChangedListener(maxDurationWatcher);


        if(service==null)
            SetCreatingState();
        else
            SetUpdatingState();

        EventTypeRepo eventTypeRepo = new EventTypeRepo(); // Instantiate your EventTypeRepo class

        eventTypeRepo.getAllActicateEventTypes(new EventTypeRepo.EventTypeFetchCallback() {
            @Override
            public void onEventTypeFetch(ArrayList<EventType> eventTypes) {
                if (eventTypes != null) {
                    Spinner spinner = (Spinner) root.findViewById(R.id.spinnerServiceEventType);
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

                                if(service!=null){
                                    for(String s:service.getType())
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
                                            selectedEvents.add(selectedEventType);
                                        } else {
                                            selectedEvents.remove(selectedEventType);
                                        }
                                    }
                                });
                            }

                            return view;
                        }
                    };
                    spinner.setAdapter(arrayAdapter);
                }
            }
        });


        return  root;
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {


        Button btnNext = (Button) binding.btnNext;
        btnNext.setOnClickListener(v -> {
            if(service==null){
                if (addNewService()) {
                    if (!subcatgoryName.isEmpty())
                        addNewSubcategorySuggestion(subcatgoryName, subcategoryDescription);
                    FragmentTransition.to(ServiceFormSecondFragment.newInstance(newService, subcategorySuggestion), getActivity(),
                            true, R.id.scroll_products_list_2);
                }
            }else{
                if(updateService()){
                    FragmentTransition.to(ServiceFormSecondFragment.newInstance(service, subcategorySuggestion), getActivity(),
                            true, R.id.scroll_products_list_2);
                }
            }
        });



    }
    private boolean areFieldsValid(){
        if(name.getText().toString().isEmpty()){
            name.setError("Name is required.");
            return false;
        }
        if(description.getText().toString().isEmpty()){
            description.setError("Description is required.");
            return false;
        }
        if(pricePerHour.getText().toString().isEmpty()){
            pricePerHour.setError("Price per hour is required.");
            return false;
        }
        if(!pricePerHour.getText().toString().isEmpty() && Double.parseDouble(pricePerHour.getText().toString())<0){
            pricePerHour.setError("Input positive number.");
            return false;
        }
        if(!discount.getText().toString().isEmpty() && (Double.parseDouble(discount.getText().toString())<0 || Double.parseDouble(discount.getText().toString())>100)){
            discount.setError("Input number in range 0-100.");
            return false;
        }
        if((minDuration.getText().toString().isEmpty() && maxDuration.getText().toString().isEmpty()) && duration.getText().toString().isEmpty()){
            minDuration.setError("Duration is required");
            maxDuration.setError("Duration is required");
            duration.setError("Duration is required");
            return false;
        }
        if(!minDuration.getText().toString().isEmpty() && Double.parseDouble(minDuration.getText().toString())<0){
            minDuration.setError("Input positive number.");
            return false;
        }
        if(!maxDuration.getText().toString().isEmpty() && Double.parseDouble(maxDuration.getText().toString())<0){
            pricePerHour.setError("Input positive number.");
            return false;
        }
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
        if( selectedSubcategory==null ){
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

        if(!minDuration.getText().toString().isEmpty() && !maxDuration.toString().isEmpty() && Double.parseDouble(minDuration.getText().toString())>=Double.parseDouble(maxDuration.getText().toString()))
        {
            maxDuration.setError("Max duration must be greater than min duration .");
            return false;
        }
        if( selectedEvents.size()==0 ){
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


    private boolean addNewService(){
        if(areFieldsValid() ){

            newService=new Service();
            newService.setName(name.getText().toString());
            newService.setDescription(description.getText().toString());
            newService.setPricePerHour(Double.parseDouble(pricePerHour.getText().toString()));
            if(!minDuration.getText().toString().isEmpty()){
                newService.setMinDuration(Double.parseDouble(minDuration.getText().toString()));
                newService.setMaxDuration(Double.parseDouble(maxDuration.getText().toString()));
                newService.setDuration(-1);
            }else{
                newService.setDuration(Double.parseDouble(duration.getText().toString()));
                newService.setMaxDuration(-1);
                newService.setMinDuration(-1);
            }
            if(!specifics.getText().toString().isEmpty())
                newService.setSpecifics(specifics.getText().toString());
            if(!discount.getText().toString().isEmpty())
                newService.setDiscount(Double.parseDouble(discount.getText().toString()));
            newService.setCategory(selectedCategory);
            ArrayList<String> events=new ArrayList<>();
            for(EventType e:selectedEvents)
                events.add(e.getId());
            newService.setType(events);
            if(subcatgoryName.isEmpty())
                newService.setSubcategory(selectedSubcategory);
            newService.setPrice(newService.getPricePerHour()*newService.getMaxDuration());
            ArrayList<Pricelist> pricelists=new ArrayList<>();
            Pricelist pricelist=new Pricelist();
            pricelist.setFrom(new Date().toString());
            pricelist.setName(name.getText().toString());
            pricelist.setPrice(Double.parseDouble(pricePerHour.getText().toString()));
            pricelist.setDiscount(Double.parseDouble(discount.getText().toString()));
            pricelist.setDiscountPrice(pricelist.getPrice()*(1-pricelist.getDiscount()/100));
            pricelists.add(pricelist);
            newService.setPriceList(pricelists);

            return true;
        }
        return false;
    }

    private void addNewSubcategorySuggestion(String name, String description){
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
            @Override
            public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                newService.setFirmId(owner.getFirmId());
                subcategorySuggestion=new SubcategorySuggestion();
                subcategorySuggestion.setCategory(selectedCategory);
                subcategorySuggestion.setService(newService);
                subcategorySuggestion.setName(name);
                subcategorySuggestion.setDescription(description);
                subcategorySuggestion.setSubcategoryType(SubcategoryType.SERVICE);

                subcategorySuggestion.setUserId(user.getUid());
            }
        });


    }

    private void SetCreatingState(){
        orrAdd.setText("Or add new subcategory suggestion: ");
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
                Category c = (Category) parent.getItemAtPosition(position);
                selectedCategory=c.getId();

                // Fetch subcategories based on the selected category
                SubcategoryRepo subcategoryRepo = new SubcategoryRepo();
                subcategoryRepo.getSubcategoriesByCategoryId(selectedCategory, new SubcategoryRepo.SubcategoryFetchCallback() {
                    @Override
                    public void onSubcategoryFetch(ArrayList<Subcategory> subcategories) {
                        if (subcategories != null) {
                            // Create an adapter for the subcategories spinner
                            ArrayAdapter<Subcategory> subcategoryAdapter = new ArrayAdapter<>(requireContext(),
                                    android.R.layout.simple_spinner_item, subcategories);
                            subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Set the adapter to the subcategories spinner
                            spinnersubcategory.setAdapter(subcategoryAdapter);
                        } else {
                            // Handle the case where fetching subcategories failed
                            // Show a toast or handle the error appropriately
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





        btnSubCategorySuggestion.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.FullScreenBottomSheetDialog);
            View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_subcategory, null);
            bottomSheetDialog.setContentView(dialogView);
            EditText name=dialogView.findViewById(R.id.subcatName);
            EditText description=dialogView.findViewById(R.id.subcatDescription);
            Button submit=dialogView.findViewById(R.id.submitSubcat);

            submit.setOnClickListener(s->{
                if(!name.getText().toString().isEmpty() && !description.getText().toString().isEmpty()) {
                    subcatgoryName=name.getText().toString();
                    subcategoryDescription=description.getText().toString();
                    bottomSheetDialog.dismiss();
                    orrAdd.setText("Subcategory suggestion: "+name.getText());
                    btnSubCategorySuggestion.setVisibility(View.GONE);
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
    }

    private void SetUpdatingState(){
        selectedCategory=service.getCategory();
        selectedSubcategory=service.getSubcategory();
        SubcategoryRepo subcategoryRepo = new SubcategoryRepo();
        subcategoryRepo.getSubcategoriesByCategoryId(selectedCategory, new SubcategoryRepo.SubcategoryFetchCallback() {
            @Override
            public void onSubcategoryFetch(ArrayList<Subcategory> subcategories) {
                if (subcategories != null) {
                    // Create an adapter for the subcategories spinner
                    ArrayAdapter<Subcategory> subcategoryAdapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, subcategories);
                    subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Set the adapter to the subcategories spinner
                    spinnersubcategory.setAdapter(subcategoryAdapter);
                    for(Subcategory s:subcategories)
                        if(s.getId().equals(service.getSubcategory()))
                            spinnersubcategory.setSelection(subcategoryAdapter.getPosition(s));
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
        name.setText(service.getName());
        description.setText(service.getDescription());
        pricePerHour.setText(Double.toString(service.getPricePerHour()));
        discount.setText(Double.toString(service.getDiscount()));
        if(service.getDuration()>0)
            duration.setText(Double.toString(service.getDuration()));
        else{
            minDuration.setText(Double.toString(service.getMinDuration()));
            maxDuration.setText(Double.toString(service.getMaxDuration()));
        }


        specifics.setText(service.getSpecifics());
        LinearLayout llCategory=binding.llcatService;
        llCategory.setVisibility(View.GONE);
        btnSubCategorySuggestion.setVisibility(View.GONE);
        CategoryRepo.getAllCategories( new CategoryRepo.CategoryFetchCallback() {
            @Override
            public void onCategoryFetch(ArrayList<Category> categories) {
                for(Category c:categories)
                    if(c.getId().equals(service.getCategory())){
                        SubcategoryRepo.getAllSubcategories(new SubcategoryRepo.SubcategoryFetchCallback() {
                            @Override
                            public void onSubcategoryFetch(ArrayList<Subcategory> subcategories) {
                                for(Subcategory s:subcategories)
                                    if(s.getId().equals(service.getSubcategory()))
                                        orrAdd.setText("Category:          "+c.getName());
                            }
                        });
                    }
            }
        });

    }

    private boolean updateService(){
        if(!isEventTypeChanged)
            selectedEvents.add(new EventType());
        if(areFieldsValid()) {
            service.setSubcategory(selectedSubcategory);
            service.setName(name.getText().toString());
            service.setDescription(description.getText().toString());
            service.setSpecifics(specifics.getText().toString());
            if(service.getPricePerHour()!=Double.parseDouble(pricePerHour.getText().toString()) || service.getDiscount()!=Double.parseDouble(discount.getText().toString())){
                Pricelist pricelist=new Pricelist();
                pricelist.setName(service.getName());
                pricelist.setPrice(Double.parseDouble(pricePerHour.getText().toString()));
                pricelist.setDiscount(Double.parseDouble(discount.getText().toString()));
                Double discountPrice=pricelist.getPrice()*(1- pricelist.getDiscount()/100);
                pricelist.setDiscountPrice(Double.parseDouble(discountPrice.toString()));
                pricelist.setFrom(new Date().toString());
                if(service.getPriceList().size()>0) {
                    Pricelist p = service.getPriceList().get(service.getPriceList().size() - 1);
                    p.setTo(new Date().toString());
                }
                ArrayList<Pricelist> pricelists=service.getPriceList();
                pricelists.add(pricelist);
                service.setPriceList(pricelists);
            }
            service.setPricePerHour(Double.parseDouble(pricePerHour.getText().toString()));
            if(!minDuration.getText().toString().isEmpty()){
                service.setMinDuration(Double.parseDouble(minDuration.getText().toString()));
                service.setMaxDuration(Double.parseDouble(maxDuration.getText().toString()));
                service.setDuration(-1);
            }else{
                service.setDuration(Double.parseDouble(duration.getText().toString()));
                service.setMinDuration(-1);
                service.setMaxDuration(-1);
            }
            if(isEventTypeChanged) {
                ArrayList<String> events = new ArrayList<>();
                for (EventType e : selectedEvents)
                    events.add(e.getId());
                service.setType(events);
            }
            return true;
        }
        return false;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s != null && !s.toString().isEmpty()) {
                duration.setText("");
            }

        }
    };
    private final TextWatcher maxDurationWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Ne morate ništa raditi ovdje
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Ne morate ništa raditi ovdje
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s != null && !s.toString().isEmpty()) {
                Log.i("KK",s.toString());
                minDuration=binding.minHService;
                minDuration.setText("");
                maxDuration=binding.maxHService;
                maxDuration.setText("");
            }
        }
    };
}