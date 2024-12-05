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
import androidx.fragment.app.FragmentTransaction;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.databinding.EditPersonalInfoBinding;
import com.example.eventapp.databinding.FragmentEventBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.eventOrganizer.EventFragment;
import com.example.eventapp.fragments.eventOrganizer.EventListFragment;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.OrganizerRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditProfileFragment extends Fragment {
    private EditPersonalInfoBinding binding;
    private Address address;
    private User employee;
    private Uri selectedUriImage;
    public EditProfileFragment() {
        // Required empty public constructor
    }


    public static EditProfileFragment newInstance(User e) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        fragment.employee = e;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EditPersonalInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView firstName = root.findViewById(R.id.editEmployeeTextName);
        firstName.setText(employee.getFirstName());

        TextView lastName = root.findViewById(R.id.editEmployeeTextLastName);
        lastName.setText(employee.getLastName());

        TextView email = root.findViewById(R.id.editEmployeeTextEmail);
        email.setText(employee.getEmail());
        email.setEnabled(false);

        TextView phone = root.findViewById(R.id.editEmployeeTextPhone);
        phone.setText(employee.getPhoneNumber());

        ImageView mimageView=binding.getRoot().findViewById(R.id.editEmployeeTextPicture);
        Picasso.get().load(employee.getImage()).into(mimageView);


        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            selectedUriImage = null;  //da mi ne duplira slike

                            if (data.getData() != null) {
                                // ovo je ako odaberem samo 1 sliku
                                Uri selected = data.getData();
                                selectedUriImage = (selected);
                            } else if (data.getClipData() != null) {
                                // ako odaberem odjednom vise slika
                                ClipData clipData = data.getClipData();
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    ClipData.Item item = clipData.getItemAt(i);
                                    Uri selected = item.getUri();
                                    selectedUriImage = (selected);
                                }
                            }


                            StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

                            imageRef.putFile(selectedUriImage)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        // Image uploaded successfully, get the download URL
                                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            // Save the download URL to the Realtime Database
                                            String imageUrl = uri.toString();
                                            employee.setImage(imageUrl);
                                            Picasso.get().load(employee.getImage()).into(mimageView);
                                        });
                                    })
                                    .addOnFailureListener(exception -> {
                                        // Handle unsuccessful uploads
                                        Log.e("TAG", "Image upload failed: " + exception.getMessage());
                                    });
                        }
                    }
                });
        mimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                mGetContent.launch(intent);
            }
        });

        getAddress();

        Button changePass = binding.getRoot().findViewById(R.id.changePassButton);
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransition.to(ChangePasswordFragment.newInstance(employee), getActivity(),
                        false, R.id.scroll_profile);
            }
        });

        Button save = binding.getRoot().findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty()
                        && !phone.getText().toString().isEmpty() && (phone.getText().toString().length() == 8 || phone.getText().toString().length() == 9)
                        && !((EditText)binding.getRoot().findViewById(R.id.editFirmTextStreet)).getText().toString().isEmpty()
                        && !((EditText)binding.getRoot().findViewById(R.id.editFirmTextStreetN)).getText().toString().isEmpty()
                        && !((EditText)binding.getRoot().findViewById(R.id.editFirmTextCity)).getText().toString().isEmpty()
                        && !((EditText)binding.getRoot().findViewById(R.id.editFirmTextCountry)).getText().toString().isEmpty() ) {

                    employee.setFirstName(firstName.getText().toString());
                    employee.setLastName(lastName.getText().toString());
                    employee.setPhoneNumber(phone.getText().toString());

                    address.setStreet(((EditText)binding.getRoot().findViewById(R.id.editFirmTextStreet)).getText().toString());
                    address.setStreetNumber(((EditText)binding.getRoot().findViewById(R.id.editFirmTextStreetN)).getText().toString());
                    address.setCity(((EditText)binding.getRoot().findViewById(R.id.editFirmTextCity)).getText().toString());
                    address.setCountry(((EditText)binding.getRoot().findViewById(R.id.editFirmTextCountry)).getText().toString());

                    if (SharedPreferencesManager.getUserRole(getContext()).equals("EMPLOYEE")) {
                        Employee e = new Employee();
                        EmployeeRepo.getByEmail(SharedPreferencesManager.getEmail(getContext()), new EmployeeRepo.EmployeeFetchCallback() {
                            @Override
                            public void onEmployeeObjectFetched(Employee employee1, String errorMessage) {
                                EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee1, errorMessage);

                                employee1.setFirstName(employee.getFirstName());
                                employee1.setLastName(employee.getLastName());
                                employee1.setPhoneNumber(employee.getPhoneNumber());
                                employee1.setImage(employee.getImage());

                                EmployeeRepo.updateEmployee(employee1, employee1.getId(), new EmployeeRepo.EmployeeFetchCallback() {
                                    @Override
                                    public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                                        EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                                        FragmentTransition.to(EmployeeDetailsFragment.newInstance(employee1), getActivity(),
                                                false, R.id.scroll_profile);
                                        Toast.makeText(getContext(), "Personal info updated!", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        });
                    } else if (SharedPreferencesManager.getUserRole(getContext()).equals("ORGANIZER")) {
                        Organizer e = new Organizer();
                        OrganizerRepo repo = new OrganizerRepo();
                        repo.getByEmail(SharedPreferencesManager.getEmail(getContext()), new OrganizerRepo.OrganizerFetchCallback() {
                            @Override
                            public void onOrganizerObjectFetched(Organizer employee1, String errorMessage) {
                                OrganizerRepo.OrganizerFetchCallback.super.onOrganizerObjectFetched(employee1, errorMessage);

                                employee1.setFirstName(employee.getFirstName());
                                employee1.setLastName(employee.getLastName());
                                employee1.setPhoneNumber(employee.getPhoneNumber());
                                employee1.setImage(employee.getImage());

                                OrganizerRepo.update(employee1);
                                FragmentTransition.to(OrganizerProfileFragment.newInstance(employee1), getActivity(),
                                        false, R.id.scroll_profile);
                                Toast.makeText(getContext(), "Personal info updated!", Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else if (SharedPreferencesManager.getUserRole(getContext()).equals("OWNER")) {
                        Owner e = new Owner();
                        OwnerRepo repo = new OwnerRepo();
                        repo.getByEmail(SharedPreferencesManager.getEmail(getContext()), new OwnerRepo.OwnerFetchCallback() {
                            @Override
                            public void onOwnerObjectFetched(Owner employee1, String errorMessage) {
                                OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(employee1, errorMessage);

                                employee1.setFirstName(employee.getFirstName());
                                employee1.setLastName(employee.getLastName());
                                employee1.setPhoneNumber(employee.getPhoneNumber());
                                employee1.setImage(employee.getImage());

                                OwnerRepo.update(employee1);
                                FragmentTransition.to(OwnerProfileFragment.newInstance(employee1), getActivity(),
                                        false, R.id.scroll_profile);
                                Toast.makeText(getContext(), "Personal info updated!", Toast.LENGTH_SHORT).show();

                            }

                        });
                    }

                AddressRepo addressRepo = new AddressRepo();
                addressRepo.update(address);

                }
                else if ((phone.getText().toString().length() != 8 || phone.getText().toString().length() != 9)) {
                        Toast.makeText(getContext(), "Phone is not in correct format!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "All fields must be valid!", Toast.LENGTH_SHORT).show();
                }


            }});

        return root;

    }

    private void getAddress()
    {
        AddressRepo repo = new AddressRepo();
        repo.getByUser(employee.getId(), new AddressRepo.AddressFetchCallback() {
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
}
