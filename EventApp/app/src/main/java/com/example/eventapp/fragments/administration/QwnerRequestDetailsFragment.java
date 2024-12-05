package com.example.eventapp.fragments.administration;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.adapters.employee.EmployeeListAdapter;
import com.example.eventapp.databinding.FragmentQwnerRequestDetailsBinding;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.RegistrationOwnerRequest;


import com.example.eventapp.databinding.FragmentQwnerRequestDetailsBinding;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class QwnerRequestDetailsFragment extends DialogFragment {

    private Address ownerAddress;
    private TextView email;
    private TextView name;
    private TextView address;

    private TextView phoneNumber;
    RegistrationOwnerRequest request;
    private FragmentQwnerRequestDetailsBinding binding;

    public QwnerRequestDetailsFragment() {
        // Required empty public constructor
    }


    public static QwnerRequestDetailsFragment newInstance(RegistrationOwnerRequest sentRequest) {
        QwnerRequestDetailsFragment fragment = new QwnerRequestDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("SENT_REQ", sentRequest);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            request = getArguments().getParcelable("SENT_REQ");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQwnerRequestDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        OwnerRepo ownerRepo = new OwnerRepo();
        ownerRepo.get(request.getOwnerId(), new OwnerRepo.OwnerFetchCallback() {
            @Override
            public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                if (owner != null) {


                    AddressRepo addressRepo=new AddressRepo();
                    addressRepo.getAll(new AddressRepo.AddressFetchCallback() {
                        @Override
                        public void onAddressFetch(ArrayList<Address> addresses) {
                            for(Address address:addresses){
                                if(address.getUserId().equals(owner.getId())){

                                    ownerAddress = address;
                                    break;
                                }
                            }
                            name = binding.name;
                            address = binding.address;
                            phoneNumber = binding.phone;
                            email = binding.email;

                            name.setText(owner.getFirstName() + " " + owner.getLastName());
                            address.setText(ownerAddress.getCity() + ", " + ownerAddress.getCountry() + ", " +ownerAddress.getStreet() + ", " + ownerAddress.getStreetNumber());
                            phoneNumber.setText(owner.getPhoneNumber());
                            email.setText(owner.getEmail());




                            //postavljanje slike
                            ImageView mimageView=binding.ownerImage;
                            ImageView imageView = new ImageView(getContext());

                            if(owner.getImage()!=null && !owner.getImage().equals("")) {
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                Picasso.get().load(owner.getImage()).rotate(90).into(mimageView);
                            }

                        }
                    });


                }
            }
        });



        return view;
    }
}