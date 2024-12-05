package com.example.eventapp.fragments.employees;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.databinding.EditCompanyInfoBinding;
import com.example.eventapp.databinding.EditPersonalInfoBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.FirmRepo;
import com.example.eventapp.repositories.OrganizerRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditCompanyFragment extends Fragment {

    private EditCompanyInfoBinding binding;
    private Address address;
    private Firm firm = new Firm();
    private List<Uri> selectedUriImages;
    public EditCompanyFragment() {
        // Required empty public constructor
    }


    public static EditCompanyFragment newInstance(String id) {
        EditCompanyFragment fragment = new EditCompanyFragment();
        fragment.firm.setId(id);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EditCompanyInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getFirm();
        getAddress();


        Button save = binding.getRoot().findViewById(R.id.save);

        selectedUriImages = new ArrayList<>(); // tu ce biti URI od slika

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!((EditText)binding.getRoot().findViewById(R.id.editFirmTextPhone)).getText().toString().isEmpty()
                        && !((EditText)binding.getRoot().findViewById(R.id.editFirmTextDescription)).getText().toString().isEmpty()
                        && !((EditText)binding.getRoot().findViewById(R.id.editFirmTextStreet)).getText().toString().isEmpty()
                        && !((EditText)binding.getRoot().findViewById(R.id.editFirmTextStreetN)).getText().toString().isEmpty()
                        && !((EditText)binding.getRoot().findViewById(R.id.editFirmTextCity)).getText().toString().isEmpty()
                        && !((EditText)binding.getRoot().findViewById(R.id.editFirmTextCountry)).getText().toString().isEmpty()
                        && !selectedUriImages.isEmpty()) {

                    firm.setPhoneNumber(((EditText)binding.getRoot().findViewById(R.id.editFirmTextPhone)).getText().toString());
                    firm.setDescription(((EditText)binding.getRoot().findViewById(R.id.editFirmTextDescription)).getText().toString());
                    ArrayList<String> images = new ArrayList<>();
                    for(Uri u: selectedUriImages)
                        images.add(u.toString());
                    firm.setImages(images);

                    address.setStreet(((EditText)binding.getRoot().findViewById(R.id.editFirmTextStreet)).getText().toString());
                    address.setStreetNumber(((EditText)binding.getRoot().findViewById(R.id.editFirmTextStreetN)).getText().toString());
                    address.setCity(((EditText)binding.getRoot().findViewById(R.id.editFirmTextCity)).getText().toString());
                    address.setCountry(((EditText)binding.getRoot().findViewById(R.id.editFirmTextCountry)).getText().toString());


                    FirmRepo firmRepo = new FirmRepo();
                    firmRepo.update(firm);

                    AddressRepo addressRepo = new AddressRepo();
                    addressRepo.update(address);

                    FragmentTransition.to(FirmProfileFragment.newInstance(firm.getId(),""), getActivity(), true, R.id.scroll_profile);

                }
                else {
                    Toast.makeText(getContext(), "All fields must be valid!", Toast.LENGTH_SHORT).show();
                }

            }});

        Button uploadImageButton = root.findViewById(R.id.uploadPhotos);


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

                        }
                        firm.getImages().clear();
                        for(Uri imageUri:selectedUriImages){
                            StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

                            imageRef.putFile(imageUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        // Image uploaded successfully, get the download URL
                                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            // Save the download URL to the Realtime Database
                                            String imageUrl = uri.toString();
                                            firm.getImages().add(imageUrl);

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

        return root;

    }

    private void getAddress()
    {
        AddressRepo repo = new AddressRepo();
        repo.getByFirm(firm.getId(), new AddressRepo.AddressFetchCallback() {
            @Override
            public void onAddressFetch(ArrayList<Address> addresses) {
                AddressRepo.AddressFetchCallback.super.onAddressFetch(addresses);
                address = addresses.get(0);

                EditText street = binding.getRoot().findViewById(R.id.editFirmTextStreet);
                street.setText(address.getStreet());

                EditText streetN = binding.getRoot().findViewById(R.id.editFirmTextStreetN);
                streetN.setText(address.getStreetNumber());

                EditText city = binding.getRoot().findViewById(R.id.editFirmTextCity);
                city.setText(address.getCity());

                EditText country = binding.getRoot().findViewById(R.id.editFirmTextCountry);
                country.setText(address.getCountry());
            }
        });
    }

    private void getFirm()
    {
        FirmRepo repo = new FirmRepo();
        repo.getById(this.firm.getId(), new FirmRepo.FirmFetchCallback() {
            @Override
            public void onFirmFetch(ArrayList<Firm> firms) {
                FirmRepo.FirmFetchCallback.super.onFirmFetch(firms);
                firm = firms.get(0);

                EditText name = binding.getRoot().findViewById(R.id.editFirmTextName);
                name.setText(firm.getName());
                name.setEnabled(false);

                EditText phone = binding.getRoot().findViewById(R.id.editFirmTextPhone);
                phone.setText(firm.getPhoneNumber());

                EditText desc = binding.getRoot().findViewById(R.id.editFirmTextDescription);
                desc.setText(firm.getDescription());
            }
        });
    }
}
