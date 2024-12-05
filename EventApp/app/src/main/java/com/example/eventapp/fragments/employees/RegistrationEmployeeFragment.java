package com.example.eventapp.fragments.employees;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentRegistrationEmployeeBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.FirmWorkingTime;
import com.example.eventapp.model.LinkExpiration;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.model.WorkingTime;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.FirmRepo;
import com.example.eventapp.repositories.FirmWorkingTimeRepo;
import com.example.eventapp.repositories.LinkExpirationRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.UserRepo;
import com.example.eventapp.repositories.WorkingTimeRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.eventapp.repositories.EmployeeRepo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class RegistrationEmployeeFragment extends Fragment {

    public static ArrayList<Employee> employees = new ArrayList<Employee>();
    private FragmentRegistrationEmployeeBinding binding;
    private ArrayList<User> storedUsers;
    private List<Uri> selectedUriImage;
    private WorkingTimeRepo workingTimeRepo;
    private String employeeId;
    private String image;
    private ImageView mImageView;
    private ArrayList<String> imageUris;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText name;
    private EditText surname;
    private EditText street;
    private EditText streetN;
    private EditText city;
    private EditText country;
    private EditText phoneNumber;
    private Employee employee=new Employee();
    private Address address;
    private String ownerId;
    private FirmRepo firmRepo;
    private String firmId;
    private OwnerRepo ownerRepo;
    private FirmWorkingTimeRepo firmWorkingTimeRepo;
    //ZA REGISTRACIJU
    private FirebaseAuth mAuth;
    //ZA EMAIL SLANJE LINKA
    ActionCodeSettings actionCodeSettings;
    private boolean visitedWorkingTimePage;
    private FirebaseUser storedUser;

    public static RegistrationEmployeeFragment newInstance(Employee e) {
        RegistrationEmployeeFragment fragment = new RegistrationEmployeeFragment();
        Bundle args = new Bundle();
        args.putParcelable("Employee", e);
        fragment.setArguments(args);
        return fragment;
    }
    private Employee newEmployee;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentRegistrationEmployeeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        selectedUriImage=new ArrayList<>();
        LinearLayout photoLinearLayout = root.findViewById(R.id.photoLinearLayout);
        ImageView uploadImageButton = root.findViewById(R.id.uploadOwnerPhotoButton);
        employeeId="";
        mImageView = root.findViewById(R.id.ownerImage);
        mAuth = FirebaseAuth.getInstance();
        actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        //url na koji preusmjeravam korisnika kad klikne na link
                        .setUrl("https://www.event-app.com/finishSignUp")
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.example.ios")
                        // id android paketa
                        .setAndroidPackageName(
                                "com.example.eventapp",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();


        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            selectedUriImage.clear();

                            if (data.getClipData() != null) {
                                ClipData clipData=data.getClipData();
                                for(int i=0;i<clipData.getItemCount();i++){
                                    ClipData.Item item=clipData.getItemAt(i);
                                    Uri selectedImageUri = item.getUri();
                                    selectedUriImage.add(selectedImageUri);
                                }

                            } else if (data.getData() != null) {
                                Uri selectedImageUri=data.getData();
                                selectedUriImage.add(selectedImageUri);
                            }
                            for (Uri imageUri : selectedUriImage) {
                                ImageView imageView = new ImageView(getContext());
                                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                                        300,300
                                );
                                layoutParams.setMargins(0, 0, 10, 0);
                                imageView.setLayoutParams(layoutParams);
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                imageView.setImageURI(imageUri);
                                mImageView.setImageURI(imageUri);
                            }
                            for(Uri imageUri:selectedUriImage){
                                StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

                                imageRef.putFile(imageUri)
                                        .addOnSuccessListener(taskSnapshot -> {
                                            // Image uploaded successfully, get the download URL
                                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                // Save the download URL to the Realtime Database
                                                String imageUrl = uri.toString();
                                                imageUris.add(imageUrl);
                                                newEmployee.setImage(imageUrl);
                                                image=imageUrl;
                                                Log.i("EventApp","ucitana slika: "+image);
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



        Bundle bundle = new Bundle();
        bundle.putBoolean("visitedWorkingTimePage", false);
        Button workingHoursBtn = root.findViewById(R.id.addWorkingTime);

        workingHoursBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = binding.editTextName.getText().toString();
                String lastName = binding.editTextSurname.getText().toString();
                String email = binding.editTextEmail.getText().toString();
                String street = binding.editTextStreet.getText().toString();
                String streetN = binding.editTextStreetN.getText().toString();
                String city = binding.editTextCity.getText().toString();
                String country = binding.editTextCountry.getText().toString();
                String phone = binding.editTextPhoneNumber.getText().toString();
                String password = binding.editTextPassword.getText().toString();
                String confirmPassword = binding.editTextConfirmPassword.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("firstName", firstName);
                bundle.putString("lastName", lastName);
                bundle.putString("email", email);
                bundle.putString("street", street);
                bundle.putString("streetn", streetN);
                bundle.putString("city", city);
                bundle.putString("country", country);
                bundle.putString("phone", phone);
                bundle.putString("pass", password);
                bundle.putString("confPass", confirmPassword);
                bundle.putString("image",image);

                Fragment registerWorkingTimeFragment = new RegisterEmployeWorkingTimeFragment();
                registerWorkingTimeFragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.scroll_employees_list, registerWorkingTimeFragment).addToBackStack("registration");
                fragmentTransaction.commit();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        name = binding.editTextName;
        surname = binding.editTextSurname;
        street = binding.editTextStreet;
        streetN = binding.editTextStreetN;
        city = binding.editTextCity;
        country = binding.editTextCountry;
        phoneNumber = binding.editTextPhoneNumber;
        email = binding.editTextEmail;
        password = binding.editTextPassword;
        confirmPassword = binding.editTextConfirmPassword;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        storedUser=currentUser;
        if (currentUser != null) {
            ownerId=currentUser.getUid();
            Log.i("user","id"+ownerId);
        }
        /*else {
            // Korisnik nije prijavljen
            ownerId="0QV5giapkUgaqkfzPzmHMQsK6dD3";
        }*/


        firmRepo=new FirmRepo();
        ownerRepo=new OwnerRepo();
        firmWorkingTimeRepo=new FirmWorkingTimeRepo();
        workingTimeRepo=new WorkingTimeRepo();
        ownerRepo.getAll(new OwnerRepo.OwnerFetchCallback() {
            @Override
            public void onOwnerFetch(ArrayList<Owner> owners) {
                OwnerRepo.OwnerFetchCallback.super.onOwnerFetch(owners);
                if(owners!=null){
                    for(Owner o:owners){
                        if(o.getId().equals(ownerId)){
                            firmId=o.getFirmId();
                        }
                    }
                }
            }
        });

        Button btnSubmit = (Button) binding.submitRegistrationOrganizerButton;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Bundle bundle = getArguments();
        if (bundle != null) {
            String firstName = bundle.getString("firstName");
            String lastName = bundle.getString("lastName");
            String emailEm = bundle.getString("email");
            String streetEm = bundle.getString("street");
            String streetNEm = bundle.getString("streetn");
            String cityEm = bundle.getString("city");
            String countryEm = bundle.getString("country");
            String phoneEm = bundle.getString("phone");
            String passwordEm = bundle.getString("pass");
            String confirmPasswordEm = bundle.getString("confPass");
            image=bundle.getString("image");

            name.setText(firstName);
            surname.setText(lastName);
            email.setText(emailEm);
            street.setText(streetEm);
            streetN.setText(streetNEm);
            city.setText(cityEm);
            country.setText(countryEm);
            phoneNumber.setText(phoneEm);
            password.setText(passwordEm);
            confirmPassword.setText(confirmPasswordEm);

            if(image!=null) {
                Picasso.get().load(image).into(mImageView);
            }

        }
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areValidFields()) {
                    employeeId=createUserInFirebase();
                    checkWorkingTime();

                }
            }
        });
        UserRepo userRepo = new UserRepo();
        userRepo.getAll(new UserRepo.UserFetchCallback() {
            @Override
            public void onUserFetch(ArrayList<User> users) {
                UserRepo.UserFetchCallback.super.onUserFetch(users);
                if (users != null) {

                    storedUsers = users;

                }
            }
        });

    }

    private void checkWorkingTime() {
        Bundle bundle = getArguments();
        visitedWorkingTimePage = bundle.getBoolean("visitedWorkingTimePage");
        Log.i("EventApp", "provjera: " + visitedWorkingTimePage);
        if (!visitedWorkingTimePage) {
            firmWorkingTimeRepo.getAll(new FirmWorkingTimeRepo.FirmWorkingTimeFetchCallback() {
                @Override
                public void onFirmWorkingTimeFetch(ArrayList<FirmWorkingTime> firmWorkingTimes) {
                    FirmWorkingTimeRepo.FirmWorkingTimeFetchCallback.super.onFirmWorkingTimeFetch(firmWorkingTimes);
                    if (firmWorkingTimes != null) {
                        for (FirmWorkingTime firmWorkingTime1 : firmWorkingTimes) {
                            if (firmWorkingTime1.getFirmId().equals(firmId)) {
                                WorkingTime workingTime = new WorkingTime();
                                workingTime.setEndDate("");
                                workingTime.setStartDate("");
                                workingTime.setMondayStartTime(firmWorkingTime1.getMondayStartTime());
                                workingTime.setMondayEndTime(firmWorkingTime1.getMondayEndTime());
                                workingTime.setTuesdayStartTime(firmWorkingTime1.getTuesdayStartTime());
                                workingTime.setTuesdayEndTime(firmWorkingTime1.getTuesdayEndTime());
                                workingTime.setWednesdayStartTime(firmWorkingTime1.getWednesdayStartTime());
                                workingTime.setWednesdayEndTime(firmWorkingTime1.getWednesdayEndTime());
                                workingTime.setThursdayStartTime(firmWorkingTime1.getThursdayStartTime());
                                workingTime.setThursdayEndTime(firmWorkingTime1.getThursdayEndTime());
                                workingTime.setFridayStartTime(firmWorkingTime1.getFridayStartTime());
                                workingTime.setFridayEndTime(firmWorkingTime1.getFridayEndTime());
                                workingTime.setSaturdayStartTime(firmWorkingTime1.getSaturdayStartTime());
                                workingTime.setSaturdayEndTime(firmWorkingTime1.getSaturdayEndTime());
                                workingTime.setSundayStartTime(firmWorkingTime1.getSundayStartTime());
                                workingTime.setSundayEndTime(firmWorkingTime1.getSundayEndTime());
                                workingTime.setUserId("");
                                WorkingTimeRepo.create(workingTime, new WorkingTimeRepo.WorkingTimeFetchCallback() {
                                    @Override
                                    public void onWorkingTimeObjectFetched(WorkingTime workingTime, String errorMessage) {
                                        WorkingTimeRepo.WorkingTimeFetchCallback.super.onWorkingTimeObjectFetched(workingTime, errorMessage);
                                        if (workingTime != null) {
                                            workingTimeRepo.getAll(new WorkingTimeRepo.WorkingTimeFetchCallback() {
                                                @Override
                                                public void onWorkingTimeFetch(ArrayList<WorkingTime> workingTimes) {
                                                    if (workingTimes != null) {
                                                        for (WorkingTime wt : workingTimes) {
                                                            if (wt.getUserId().equals("")) {
                                                                wt.setUserId(employeeId);
                                                                updateWorkingTime(wt);
                                                            }
                                                        }
                                                    }

                                                }
                                            });
                                        } else {
                                            Toast.makeText(getContext(), "Failed to create employee. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }

    }
    public static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000); // Adjust range as needed

        String uniqueString = "employee_"+timestampString + "_" + randomInt;

        return uniqueString;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            employee = getArguments().getParcelable("Employee");
        }
        imageUris=new ArrayList<>();
        newEmployee=new Employee();

    }
    private boolean areValidFields(){
        if(name.getText().toString().isEmpty()) {
            name.setError("Name is required.");
            return false;
        }
        if(surname.getText().toString().isEmpty()){
            surname.setError("Surname is required.");
            return false;
        }
        if(email.getText().toString().isEmpty()){
            email.setError("Email is required.");
            return false;
        }
        if(password.getText().toString().isEmpty()){
            password.setError("Password is required.");
            return false;
        }
        if(confirmPassword.getText().toString().isEmpty()){
            confirmPassword.setError("Confirm password is required.");
            return false;
        }
        if(!confirmPassword.getText().toString().equals(password.getText().toString())){
            confirmPassword.setError("Must be equal to the password.");
            return false;
        }
        if(phoneNumber.getText().toString().isEmpty()){
            phoneNumber.setError("Phone number is required.");
            return false;
        }
        if(street.getText().toString().isEmpty()){
            street.setError("Street is required.");
            return false;
        }
        if(streetN.getText().toString().isEmpty()){
            streetN.setError("StreetNumber is required.");
            return false;
        }
        if(city.getText().toString().isEmpty()){
            city.setError("City is required.");
            return false;
        }
        if(country.getText().toString().isEmpty()){
            country.setError("Country is required.");
            return false;
        }
        for(User u : storedUsers) {
            if(u.getEmail().equals(email.getText().toString())) {
                Toast.makeText(getContext(), "Email already in use.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }
    private void updateWorkingTime(WorkingTime wt) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference wtRef = db.collection("working_times").document(wt.getId());

        wtRef.update("userId", wt.getUserId())
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }
    public interface OnEmployeeAddedListener {
        void onEmployeeAdded(Employee employee);
    }

    private String createUserInFirebase() {
        final String[] id = {""};
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            id[0] =addEmployeeToDatabase(user).getId();

                        } else {
                            handleFirebaseRegistrationError(task.getException());
                        }
                    }
                });
        return id[0];
    }

    private Employee addEmployeeToDatabase(FirebaseUser user) {
        newEmployee = new Employee();
        newEmployee.setId(user.getUid());
        newEmployee.setFirstName(name.getText().toString());
        newEmployee.setLastName(surname.getText().toString());
        newEmployee.setEmail(email.getText().toString());
        newEmployee.setPhoneNumber(phoneNumber.getText().toString());
        newEmployee.setImage(image);
        newEmployee.setActive(false);
        newEmployee.setType(UserType.EMPLOYEE);
        newEmployee.setFirmId(firmId);
        newEmployee.setDeactivated(false);
        newEmployee.setPassword(password.getText().toString());
        address = new Address();
        address.setStreet(street.getText().toString());
        address.setStreetNumber(streetN.getText().toString());
        address.setCity(city.getText().toString());
        address.setCountry(country.getText().toString());
        address.setFirmId("");

        EmployeeRepo.createEmployee(newEmployee, new EmployeeRepo.EmployeeFetchCallback() {
            @Override
            public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                if (employee != null) {
                    address.setUserId(employee.getId());
                    AddressRepo.create(address, new AddressRepo.AddressFetchCallback() {
                        @Override
                        public void onAddressObjectFetched(Address address, String errorMessage) {
                            AddressRepo.AddressFetchCallback.super.onAddressObjectFetched(address, errorMessage);
                            workingTimeRepo.getAll(new WorkingTimeRepo.WorkingTimeFetchCallback() {
                                @Override
                                public void onWorkingTimeFetch(ArrayList<WorkingTime> workingTimes) {
                                    if(workingTimes!=null){
                                        for(WorkingTime wt: workingTimes){
                                            if(wt.getUserId()!=null){
                                                Log.i("EventApp","working"+wt.getUserId());
                                                if(wt.getUserId().equals("")){
                                                    wt.setUserId(address.getUserId());
                                                    updateWorkingTime(wt);
                                                    mAuth.updateCurrentUser(storedUser);
                                                    Log.i("EventApp","storedUser "+storedUser.getUid());
                                                    sendEmailVerification(user,employee);
                                                    navigateToEmployeesList();
                                                }
                                            }

                                        }
                                    }

                                }
                            });
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Failed to create employee. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return newEmployee;
    }

    private void navigateToEmployeesList() {
        FragmentTransition.to(EmployeesListFragment.newInstance(new ArrayList<>()), getActivity(), true, R.id.scroll_employees_list);
    }

    private void handleFirebaseRegistrationError(Exception exception) {
        final Context fragmentContext = getContext();
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            Toast.makeText(fragmentContext, "Password not strong enough.", Toast.LENGTH_SHORT).show();
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(fragmentContext, "Invalid email format.", Toast.LENGTH_SHORT).show();
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            Toast.makeText(fragmentContext, "Email already exists.", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("Registracija", "Neuspješna registracija", exception);
            Toast.makeText(fragmentContext, "Not succeed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
    private LinkExpiration createLinkExpiration(Employee e){
        LinkExpiration link = new LinkExpiration();
        link.setEmail(e.getEmail());
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        link.setSentTime(formattedDateTime);
        return link;
    }

    private void sendEmailVerification(FirebaseUser user,Employee e) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Email sent.");
                            LinkExpiration link = createLinkExpiration(e);
                            LinkExpirationRepo.createLinkExpiration(link, new LinkExpirationRepo.LinkFetchCallback() {
                                @Override
                                public void onLinkObjectFetched(LinkExpiration link, String errorMessage) {
                                    LinkExpirationRepo.LinkFetchCallback.super.onLinkObjectFetched(link, errorMessage);
                                }
                            });
                        } else {
                            Log.e("TAG", "Failed to send verification email", task.getException());
                        }
                    });
        }
    }




}