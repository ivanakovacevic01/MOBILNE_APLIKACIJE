package com.example.eventapp.fragments.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentServiceFormSecondBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Change;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.ServiceChangeHistory;
import com.example.eventapp.model.SubcategorySuggestion;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.ServiceChangeHistoryRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.SubcategorySuggestionsRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class ServiceFormSecondFragment extends Fragment {
    private List<Uri> selectedUriImages;
    private FragmentServiceFormSecondBinding binding;
    private List<String> imageUris;
    private Spinner locationService;
    private Spinner performersService;
    private Service servis;
    private EditText reservationDeadline;
    private EditText cancelationDeadline;
    private RadioButton availableService;
    private RadioButton notAvailableService;
    private RadioButton visibleService;
    private RadioButton notVisibleService;
    private RadioButton manualAcceptance;
    private RadioButton automaticAcceptance;
    //private ArrayList<String> selectedPerformers;
    private ArrayList<Employee> selectedEmployees;
    private SubcategorySuggestion subcategorySuggestion;
    private Service service;
    private boolean isEmployeeChanged;
    public ServiceFormSecondFragment() {
        // Required empty public constructor
    }


    public static ServiceFormSecondFragment newInstance(Service s,SubcategorySuggestion ss) {
        ServiceFormSecondFragment fragment = new ServiceFormSecondFragment();
        Bundle args = new Bundle();
        args.putParcelable("SERVICE", s);
        args.putParcelable("SUGGESTION", ss);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            servis = getArguments().getParcelable("SERVICE");
            service = getArguments().getParcelable("SERVICE");
            subcategorySuggestion = getArguments().getParcelable("SUGGESTION");
        }
        selectedEmployees=new ArrayList<>();
        isEmployeeChanged=false;
        imageUris=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServiceFormSecondBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button uploadImageButton = root.findViewById(R.id.uploadCompanyPhotoButton);
        LinearLayout photoLinearLayout = root.findViewById(R.id.photoLinearLayout);
        List<Uri> selectedUriImages = new ArrayList<>(); // tu ce biti URI od slika


        reservationDeadline=binding.deadlineService;
        cancelationDeadline=binding.cancelationService;
        availableService=binding.availableService;
        notAvailableService=binding.notAvailableService;
        visibleService=binding.visibleService;
        notVisibleService=binding.notVisibleService;
        manualAcceptance=binding.manualService;
        automaticAcceptance=binding.automaticService;
        locationService=binding.spinnerLocationService;

        if(service.getId()==null){
            availableService.setChecked(true);
            visibleService.setChecked(true);
            manualAcceptance.setChecked(true);
        }
        else
            SetUpdatingState();



        ArrayAdapter<String> arrayAdapterLocation = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.location_array));
        arrayAdapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationService.setAdapter(arrayAdapterLocation);

        if(service.getId()!=null)
            locationService.setSelection(arrayAdapterLocation.getPosition(service.getLocation()));

        EmployeeRepo employeeRepo = new EmployeeRepo(); // Instantiate your EmployeeRepo class

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        OwnerRepo.getByEmail(currentUser.getEmail(), new OwnerRepo.OwnerFetchCallback() {
            @Override
            public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);


                EmployeeRepo.getActiveByFirmId(owner.getFirmId(),new EmployeeRepo.EmployeeFetchCallback() {
                    @Override
                    public void onEmployeeFetch(ArrayList<Employee> employees) {
                        if (employees != null) {

                            // Initialize or update your selectedEmployees list here if needed

                            Spinner spinner = (Spinner) root.findViewById(R.id.spinnerServicePerformers);
                            ArrayAdapter<Employee> arrayAdapter = new ArrayAdapter<Employee>(getActivity(), R.layout.multispiner,
                                    employees) {

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
                                    Employee employee = getItem(position);
                                    if (employee != null) {
                                        if(service.getId()!=null){
                                            for(String s:service.getAttendants())
                                                if(s.equals(employee.getId()) && !selectedEmployees.contains(employee))
                                                    selectedEmployees.add(employee);
                                        }

                                        textView.setText(employee.getFirstName()+" "+employee.getLastName());
                                        checkBox.setChecked(selectedEmployees.contains(employee));
                                        checkBox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                isEmployeeChanged=true;
                                                CheckBox checkBox = (CheckBox) v;
                                                Employee selectedEmployee = getItem(position);
                                                if (checkBox.isChecked() && !selectedEmployees.contains(selectedEmployee)) {
                                                    // Add item to selectedEmployees list if checked
                                                    selectedEmployees.add(selectedEmployee);
                                                } else {
                                                    // Remove item from selectedEmployees list if unchecked
                                                    selectedEmployees.remove(selectedEmployee);
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



            }
        });

        //*********************************SLIKE*******************************************

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
                                                servis.setImageUris(imageUris); // Update servis here
                                                if(service!=null){
                                                    List<String> uris=service.getImageUris();
                                                    uris.add(imageUrl);
                                                    service.setImageUris(uris);
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
            if(service.getId()==null) {
                if (addNewService()) {
                    if (subcategorySuggestion.getName() != null) {
                        SubcategorySuggestionsRepo.create(subcategorySuggestion);
                        Notification newNotification=new Notification();
                        newNotification.setMessage("New subcategory suggestion.");
                        newNotification.setDate(new Date().toString());
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
                        FragmentTransition.to(ServicesPageFragment.newInstance(), getActivity(),
                                true, R.id.scroll_products_list_2);
                    }

                    else {
                        ServiceRepo.createService(servis);
                        FirebaseAuth mAuth= FirebaseAuth.getInstance();
                        FirebaseUser user=mAuth.getCurrentUser();
                        OwnerRepo.getByEmail(user.getEmail(), new OwnerRepo.OwnerFetchCallback() {
                            @Override
                            public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                servis.setFirmId(owner.getFirmId());
                                ServiceRepo.updateService(servis);
                                FragmentTransition.to(ServicesPageFragment.newInstance(), getActivity(),
                                        true, R.id.scroll_products_list_2);
                            }
                        });
                    }

                }
            }else{
                if(updateService()){
                    LocalDateTime now=LocalDateTime.now();
                    ServiceChangeHistory serviceChangeHistory =new ServiceChangeHistory();
                    serviceChangeHistory.setChangeTime(now.toString());
                    serviceChangeHistory.setId("serviceChange_"+generateUniqueString());
                    serviceChangeHistory.setService(servis);
                    ArrayList<Change> serviceChanges=service.getChanges();
                    serviceChanges.add(new Change(serviceChangeHistory.getId(),now));
                    service.setChanges(serviceChanges);
                    service.setLastChange(now.toString());
                    ServiceChangeHistoryRepo.createServiceHistoryChange(serviceChangeHistory);
                    ServiceRepo.updateService(service);

                    FragmentTransition.to(ServicesPageFragment.newInstance(), getActivity(),
                            true, R.id.scroll_products_list_2);
                }

            }
        });



    }

    private boolean isLocationValid(){
        if(locationService.getSelectedItemPosition()<=0){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); // Pass your activity or context here

            builder.setTitle("Error!");
            builder.setMessage("Please select location.");
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

    private boolean AreEmployeesValid(){
        if(selectedEmployees.size()<=0){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); // Pass your activity or context here

            builder.setTitle("Error!");
            builder.setMessage("Please select performers.");

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
        if(isLocationValid() && AreEmployeesValid()){
            servis.setCancellationDeadline(Integer.parseInt(cancelationDeadline.getText().toString()));
            servis.setReservationDeadline(Integer.parseInt(reservationDeadline.getText().toString()));
            servis.setLocation(locationService.getSelectedItem().toString());
            ArrayList<String> employees=new ArrayList<>();
            for(Employee e:selectedEmployees)
                employees.add(e.getId());
            servis.setAttendants(employees);
            if(availableService.isChecked())
                servis.setAvailable(true);
            else
                servis.setAvailable(false);
            if(visibleService.isChecked())
                servis.setVisible(true);
            else
                servis.setVisible(false);
            if(manualAcceptance.isChecked()) {
                servis.setManualConfirmation(true);
            }
            else if(automaticAcceptance.isChecked()){
                servis.setManualConfirmation(false);
            }
            //servis.setImageUris(imageUris);

            return true;
        }
        return false;

    }


    private void SetUpdatingState(){
        if(service.getVisible())
            visibleService.setChecked(true);
        else
            notVisibleService.setChecked(true);
        if(service.getAvailable())
            availableService.setChecked(true);
        else
            notAvailableService.setChecked(true);
        if(service.getManualConfirmation())
            manualAcceptance.setChecked(true);
        else
            automaticAcceptance.setChecked(true);
        cancelationDeadline.setText(Double.toString(service.getCancellationDeadline()));
        reservationDeadline.setText(Double.toString(service.getReservationDeadline()));
    }

    private boolean updateService(){
        if(!isEmployeeChanged)
            selectedEmployees.add(new Employee());
        if(isLocationValid() && AreEmployeesValid()){
            service.setCancellationDeadline((int)Double.parseDouble(cancelationDeadline.getText().toString()));
            service.setReservationDeadline((int)Double.parseDouble(reservationDeadline.getText().toString()));
            service.setLocation(locationService.getSelectedItem().toString());
            if(isEmployeeChanged) {
                ArrayList<String> employees = new ArrayList<>();
                for (Employee e : selectedEmployees)
                    employees.add(e.getId());
                service.setAttendants(employees);
            }
            if(availableService.isChecked())
                service.setAvailable(true);
            else
                service.setAvailable(false);
            if(visibleService.isChecked())
                service.setVisible(true);
            else
                service.setVisible(false);
            if(manualAcceptance.isChecked()) {
                service.setManualConfirmation(true);
            }
            else if(automaticAcceptance.isChecked()){
                service.setManualConfirmation(false);
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
}