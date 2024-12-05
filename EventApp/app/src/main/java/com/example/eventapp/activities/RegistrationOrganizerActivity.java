package com.example.eventapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventapp.R;
import com.example.eventapp.activities.MainActivity;
import com.example.eventapp.databinding.ActivityRegistrationOrganizerBinding;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.LinkExpiration;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.LinkExpirationRepo;
import com.example.eventapp.repositories.OrganizerRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

public class RegistrationOrganizerActivity extends AppCompatActivity {

    private ArrayList<User> storedUsers;
    private ActivityRegistrationOrganizerBinding binding;

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
    private Organizer newOrganizer;
    private Address address;

    ImageView mImageView;
    private String imagePath;

    //ZA REGISTRACIJU
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_organizer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mAuth = FirebaseAuth.getInstance();


        binding = ActivityRegistrationOrganizerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //upload slika
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        ActivityResultLauncher<String> mGetContent;
        mImageView = findViewById(R.id.organizerImage);
        ImageView uploadImageButton = findViewById(R.id.uploadOrganizerPhotoButton);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            mImageView.setImageURI(result);
                            StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

                            imageRef.putFile(result)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            imagePath = uri.toString();

                                        });
                                    })
                                    .addOnFailureListener(exception -> {
                                        Log.e("TAG", "Image upload failed: " + exception.getMessage());
                                    });
                        }


                    }
                });
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

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

        Button btnSubmit = (Button) binding.submitRegistrationOrganizerButton;
        btnSubmit.setOnClickListener(v -> {
            if(areFieldsValid())
            {
                createNewOrganizerObject();
                Log.i("mejl", newOrganizer.getEmail());
                mAuth.createUserWithEmailAndPassword(newOrganizer.getEmail(), password.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("uspjesno", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(RegistrationOrganizerActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    // Email sent
                                                    newOrganizer.setId(user.getUid());
                                                    LinkExpiration link = createLinkExpiration(newOrganizer);
                                                    OrganizerRepo.createOrganizer(newOrganizer, new OrganizerRepo.OrganizerFetchCallback() {
                                                        @Override
                                                        public void onOrganizerObjectFetched(Organizer organizer, String errorMessage) {
                                                            if(organizer != null) {
                                                                LinkExpirationRepo.createLinkExpiration(link, new LinkExpirationRepo.LinkFetchCallback() {
                                                                    @Override
                                                                    public void onLinkObjectFetched(LinkExpiration returned, String errorMessage) {
                                                                        address.setUserId(organizer.getId());
                                                                        AddressRepo.create(address, new AddressRepo.AddressFetchCallback() {
                                                                            @Override
                                                                            public void onAddressFetch(ArrayList<Address> addresses) {
                                                                                AddressRepo.AddressFetchCallback.super.onAddressFetch(addresses);
                                                                            }
                                                                        });

                                                                        mAuth.signOut();
                                                                        //Log.i("odjava", mAuth.getCurrentUser().getEmail());
                                                                        Toast.makeText(RegistrationOrganizerActivity.this, "Please check your email for activation link.", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(RegistrationOrganizerActivity.this, MainActivity.class);
                                                                        startActivity(intent);
                                                                    }
                                                                });

                                                            }
                                                        }
                                                    });

                                                }
                                            });

                                } else {
                                    Exception exception = task.getException();
                                    if (exception instanceof FirebaseAuthWeakPasswordException) {
                                        Toast.makeText(RegistrationOrganizerActivity.this, "Password not strong enough.", Toast.LENGTH_SHORT).show();
                                    } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(RegistrationOrganizerActivity.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                                    } else if (exception instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(RegistrationOrganizerActivity.this, "Email already exists.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e("Registracija", "Neuspješna registracija", task.getException());

                                        Toast.makeText(RegistrationOrganizerActivity.this, "Not succeed. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
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


    private boolean areFieldsValid() {
        if(email.getText().toString().isEmpty()) {
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
                Toast.makeText(this, "Email already in use.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }




        return true;
    }

    private void createNewOrganizerObject() {
        newOrganizer = new Organizer();
        newOrganizer.setFirstName(name.getText().toString());
        newOrganizer.setLastName(surname.getText().toString());
        newOrganizer.setImage(imagePath);
        newOrganizer.setEmail(email.getText().toString());
        newOrganizer.setPhoneNumber(phoneNumber.getText().toString());
        newOrganizer.setType(UserType.ORGANIZER);
        newOrganizer.setActive(false);  //sve dok ne potvrdi mejl, nije aktivan
        newOrganizer.setPassword(password.getText().toString());
        address=new Address();
        address.setStreet(street.getText().toString());
        address.setStreetNumber(streetN.getText().toString());
        address.setCity(city.getText().toString());
        address.setCountry(country.getText().toString());
        address.setFirmId("");


    }


    private LinkExpiration createLinkExpiration(Organizer organizer){
        LinkExpiration link = new LinkExpiration();
        link.setEmail(organizer.getEmail());
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Formatiranje datuma i vremena
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        link.setSentTime(formattedDateTime);
        return link;
    }



}