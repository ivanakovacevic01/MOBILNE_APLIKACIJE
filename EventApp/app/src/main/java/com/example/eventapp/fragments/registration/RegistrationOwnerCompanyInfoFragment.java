package com.example.eventapp.fragments.registration;

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
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentRegistrationOwnerCompanyInfoBinding;
import com.example.eventapp.databinding.FragmentRegistrationOwnerInfoBinding;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.FirmType;
import com.example.eventapp.model.RegistrationOwnerHelper;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegistrationOwnerCompanyInfoFragment extends Fragment {

    private ArrayList<String> imageUris=new ArrayList<>();  //stringovi, idu u objekat
    private List<Uri> selectedUriImages;
    private Spinner firmType;
    private EditText email;
    private EditText name;
    private EditText street;
    private EditText streetN;
    private EditText city;
    private EditText country;
    private EditText phoneNumber;
    private EditText description;

    private Firm newFirm;
    private Address address;
    private RegistrationOwnerHelper helper;

    private RegistrationOwnerOtherInfoFragment nextFragment;
    private FragmentRegistrationOwnerCompanyInfoBinding binding;

    public static RegistrationOwnerCompanyInfoFragment newInstance(RegistrationOwnerHelper helper) {
        RegistrationOwnerCompanyInfoFragment fragment = new RegistrationOwnerCompanyInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("HELPER_OWNER", helper);
        fragment.setArguments(args);
        return fragment;
    }

    public RegistrationOwnerCompanyInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            helper = getArguments().getParcelable("HELPER_OWNER");
        }
        selectedUriImages = new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentRegistrationOwnerCompanyInfoBinding.inflate(inflater, container, false);

        //za prelazak fragmenata
        View view = binding.getRoot();




        //upload slika
        ImageView uploadImageButton = view.findViewById(R.id.uploadCompanyPhotoButton);
        LinearLayout photoLinearLayout = view.findViewById(R.id.photoLinearLayout);
        selectedUriImages = new ArrayList<>(); // tu ce biti URI od slika

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            selectedUriImages.clear();  //da mi ne duplira slike

                            if (data.getData() != null) {
                                // ovo je ako odaberem samo 1 sliku
                                Uri selectedImageUri = data.getData();
                                selectedUriImages.add(selectedImageUri);
                            } else if (data.getClipData() != null) {
                                // ako odaberem odjednom vise slika
                                ClipData clipData = data.getClipData();
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    ClipData.Item item = clipData.getItemAt(i);
                                    Uri selectedImageUri = item.getUri();
                                    selectedUriImages.add(selectedImageUri);
                                }
                            }
                            // za prikaz svih slika u layoutu
                            for (Uri imageUri : selectedUriImages) {
                                ImageView imageView = new ImageView(getContext());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        510, 580
                                );
                                layoutParams.setMargins(0, 0, 10, 0);
                                imageView.setLayoutParams(layoutParams);
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                imageView.setImageURI(imageUri);
                                photoLinearLayout.addView(imageView);
                            }
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

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                mGetContent.launch(intent);
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        name = binding.editTextName;
        street = binding.editTextStreet;
        streetN = binding.editTextStreetN;
        city = binding.editTextCity;
        country = binding.editTextCountry;
        phoneNumber = binding.editTextPhoneNumber;
        email = binding.editTextEmail;
        description = binding.editTextAboutCompany;
        firmType = binding.spinnerCompType;

        RelativeLayout nextButton = view.findViewById(R.id.next2RegistrationOwnerButton);
        nextButton.setOnClickListener(v -> {
            if(areFieldsValid())
            {
                createNewFirmObject();
                updateHelperObject();
                nextFragment = RegistrationOwnerOtherInfoFragment.newInstance(helper);
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.registrationFragmentContainer, nextFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
    }

    private boolean areFieldsValid() {
        if(((String) firmType.getSelectedItem()).equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle("Error!");
            builder.setMessage("Please select firm type.");

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
        if(email.getText().toString().isEmpty()) {
            email.setError("Email is required.");
            return false;
        }

        if(name.getText().toString().isEmpty()) {
            name.setError("Name is required.");
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
        if(description.getText().toString().isEmpty()) {
            description.setError("Description is required.");
            return false;
        }


        return true;
    }

    private void createNewFirmObject() {
        newFirm = new Firm();
        newFirm.setEmail(email.getText().toString());
        newFirm.setName(name.getText().toString());
        newFirm.setPhoneNumber(phoneNumber.getText().toString());
        newFirm.setDescription(description.getText().toString());
        String selectedFirmTypeString = (String) firmType.getSelectedItem();
        FirmType selectedFirmType = FirmType.valueOf(selectedFirmTypeString);
        newFirm.setType(selectedFirmType);
        newFirm.setImages(imageUris);
        newFirm.setActive(false);   //dodato za rad i prikaz samo onih sa true


        address=new Address();
        address.setStreet(street.getText().toString());
        address.setStreetNumber(streetN.getText().toString());
        address.setCity(city.getText().toString());
        address.setCountry(country.getText().toString());
        address.setUserId("");
    }
    private void updateHelperObject() {
        helper.setFirm(newFirm);
        helper.setFirmAddress(address);
        Log.i("helpercic", helper.getOwner().getFirstName());
    }
}