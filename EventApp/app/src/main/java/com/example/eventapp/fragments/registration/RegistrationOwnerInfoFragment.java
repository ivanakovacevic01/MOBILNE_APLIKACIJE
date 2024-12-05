package com.example.eventapp.fragments.registration;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentRegistrationOwnerCompanyInfoBinding;
import com.example.eventapp.databinding.FragmentRegistrationOwnerInfoBinding;
import com.example.eventapp.databinding.FragmentRegistrationOwnerOtherInfoBinding;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.RegistrationOwnerHelper;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationOwnerInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationOwnerInfoFragment extends Fragment {

    private ArrayList<User> storedUsers;

    private RegistrationOwnerCompanyInfoFragment nextFragment;
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
    private Owner newOwner;
    private Address address;
    private RegistrationOwnerHelper helper;
    ImageView mImageView;
    private String imagePath;
    private FragmentRegistrationOwnerInfoBinding binding;

    public RegistrationOwnerInfoFragment() {
        // Required empty public constructor
    }


    public static RegistrationOwnerInfoFragment newInstance() {
        RegistrationOwnerInfoFragment fragment = new RegistrationOwnerInfoFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public void onPause() {
        super.onPause();
        Bundle outState = new Bundle();
        onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRegistrationOwnerInfoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();



        //upload slika
        ActivityResultLauncher<String> mGetContent;
        ImageView mImageView = view.findViewById(R.id.ownerImage);
        ImageView uploadImageButton = view.findViewById(R.id.uploadOwnerPhotoButton);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();



        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            mImageView.setImageURI(result);
                            StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

                            imageRef.putFile(result)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        // Image uploaded successfully, get the download URL
                                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            // Save the download URL to the Realtime Database
                                            imagePath = uri.toString();


                                        });
                                    })
                                    .addOnFailureListener(exception -> {
                                        // Handle unsuccessful uploads
                                        Log.e("TAG", "Image upload failed: " + exception.getMessage());
                                    });

                        }
                    }
                });
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pokrenite odabir slike
                mGetContent.launch("image/*");
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


        return view;
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

        RelativeLayout nextButton = view.findViewById(R.id.next1RegistrationOwnerButton);
        /*nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zamjena trenutnog fragmenta s drugim fragmentom

                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.registrationFragmentContainer, nextFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });*/


        nextButton.setOnClickListener(v -> {
            if(areFieldsValid() )
            {
                createNewOwnerObject();
                createNewHelperObject();
                nextFragment = RegistrationOwnerCompanyInfoFragment.newInstance(helper);
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.registrationFragmentContainer, nextFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
    }



    private boolean areFieldsValid() {

        if(email.getText().toString().isEmpty()) {
            email.setError("Email is required.");
            return false;
        }
        String emailInput = email.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.setError("Please enter a valid email address.");
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
        if(name.getText().toString().isEmpty()) {
            name.setError("Name is required.");
            return false;
        }
        if(surname.getText().toString().isEmpty()) {
            surname.setError("Surname is required.");
            return false;
        }
        if(street.getText().toString().isEmpty()) {
            street.setError("Street is required.");
            return false;
        }
        if(streetN.getText().toString().isEmpty()) {
            streetN.setError("Street number is required.");
            return false;
        }
        if(city.getText().toString().isEmpty()) {
            city.setError("City is required.");
            return false;
        }
        if(country.getText().toString().isEmpty()) {
            country.setError("Country is required.");
            return false;
        }
        if(phoneNumber.getText().toString().isEmpty()) {
            phoneNumber.setError("Phone number is required.");
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


    private void createNewOwnerObject() {
        newOwner = new Owner();
        newOwner.setPassword(password.getText().toString());
        newOwner.setFirstName(name.getText().toString());
        newOwner.setLastName(surname.getText().toString());
        newOwner.setImage(imagePath);
        newOwner.setEmail(email.getText().toString());
        newOwner.setPhoneNumber(phoneNumber.getText().toString());
        newOwner.setType(UserType.OWNER);
        newOwner.setActive(false);  //sve dok ne potvrdi mejl, nije aktivan
        address=new Address();
        address.setStreet(street.getText().toString());
        address.setStreetNumber(streetN.getText().toString());
        address.setCity(city.getText().toString());
        address.setCountry(country.getText().toString());
        address.setFirmId("");


    }

    private void createNewHelperObject() {
        helper = new RegistrationOwnerHelper();
        helper.setOwner(newOwner);
        helper.setOwnerAddress(address);
        helper.setPassword(password.getText().toString());
    }



}