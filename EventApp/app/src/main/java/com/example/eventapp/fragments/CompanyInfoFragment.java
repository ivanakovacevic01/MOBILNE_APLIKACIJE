package com.example.eventapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.adapters.RatingAdapter;
import com.example.eventapp.databinding.CompanyInfoBinding;
import com.example.eventapp.fragments.employees.OwnerProfileFragment;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Rating;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.FirmRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.NotificationStatus;
import com.example.eventapp.model.Rating;
import com.example.eventapp.model.Report;
import com.example.eventapp.model.ReportingStatus;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.FirmRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.RatingRepo;
import com.example.eventapp.repositories.ReportRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CompanyInfoFragment extends Fragment {
    private CompanyInfoBinding binding;
    private Address address;
    private Firm firm = new Firm();
    private Button report;
    private ArrayList<Rating> ratings = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    public CompanyInfoFragment() {
        // Required empty public constructor
    }


    public static CompanyInfoFragment newInstance(String id) {
        CompanyInfoFragment fragment = new CompanyInfoFragment();
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
        binding = CompanyInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getFirm();
        getAddress();
        getOwner();
        getRatings();

        report=binding.buttonReport;
        report.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflaterPopUp = LayoutInflater.from(getContext());
            View view = inflaterPopUp.inflate(R.layout.report_popup, null);
            builder.setView(view);

            final EditText editText = view.findViewById(R.id.editText);
            Button okButton = view.findViewById(R.id.okButton);

            final AlertDialog dialog = builder.create();

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String enteredText = editText.getText().toString();
                    if(!enteredText.isEmpty()){
                        Report report=new Report();
                        report.setDate(new Date().toString());
                        report.setReportedEmail(firm.getId());
                        report.setReason(enteredText);
                        report.setStatus(ReportingStatus.REPORTED);
                        report.setReportedType("kompanija");
                        FirebaseAuth mAuth= FirebaseAuth.getInstance();
                        report.setReporterEmail(mAuth.getCurrentUser().getEmail());
                        ReportRepo.create(report);
                        Notification notification=new Notification();
                        notification.setDate(new Date().toString());
                        notification.setReceiverRole(UserType.ADMIN);
                        notification.setMessage("Company with name "+ firm.getName()+" is reported! Reason: "+enteredText);
                        notification.setStatus(NotificationStatus.NEW);
                        notification.setSenderId(mAuth.getCurrentUser().getUid());
                        NotificationRepo.create(notification);
                        Toast.makeText(getContext(), "Company is reported!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss(); // Zatvaranje dijaloga nakon klika na dugme
                }
            });

            dialog.show();
        });

        Button back = binding.getRoot().findViewById(R.id.buttonBack);
        back.setOnClickListener(e -> {
            //back
            getActivity().getSupportFragmentManager().popBackStack();
        });

        Button message = binding.getRoot().findViewById(R.id.buttonMessage);
        if(SharedPreferencesManager.getUserRole(getContext()).equals("ORGANIZER"))
        {
            message.setOnClickListener(e -> {

                ChooseUserDialog dialog = ChooseUserDialog.newInstance(users);
                dialog.show(getChildFragmentManager(), "CHAT");
            });
        }else{
            message.setVisibility(View.GONE);
        }



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

                TextView addr = binding.getRoot().findViewById(R.id.textViewAdress);
                addr.setText(address.getStreet() + " " + address.getStreetNumber() +", " +
                        address.getCity() + ", " + address.getCountry());
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

                TextView name = binding.getRoot().findViewById(R.id.textViewName);
                name.setText(firm.getName());

                TextView email = binding.getRoot().findViewById(R.id.textViewEmail);
                email.setText(firm.getEmail());

                TextView phone = binding.getRoot().findViewById(R.id.textViewPhone);
                phone.setText(firm.getPhoneNumber());

                TextView type = binding.getRoot().findViewById(R.id.textViewType);
                type.setText(firm.getType().toString());

                TextView desc = binding.getRoot().findViewById(R.id.textViewDescription);
                desc.setText(firm.getDescription());

                LinearLayout photoLinearLayout = binding.getRoot().findViewById(R.id.photoLinearLayout);

                for (String imageUrl : firm.getImages()) {
                    ImageView imageView = new ImageView(getContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            300, 300
                    );
                    layoutParams.setMargins(0, 0, 10, 0);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Picasso.get().load(imageUrl).into(imageView);
                    photoLinearLayout.addView(imageView);
                }

            }
        });
    }

    private void getRatings()
    {
        RatingRepo repo = new RatingRepo();
        repo.getByCompanyId(firm.getId(), new RatingRepo.RatingFetchCallback() {
            @Override
            public void onRatingFetch(ArrayList<Rating> r) {
                RatingRepo.RatingFetchCallback.super.onRatingFetch(r);
                ratings = r;
                setAverageRating();

                RatingAdapter adapter = new RatingAdapter(ratings, getActivity());
                RecyclerView listView = binding.getRoot().findViewById(R.id.lisViewRating);
                listView.setLayoutManager(new LinearLayoutManager(getActivity()));
                listView.setAdapter(adapter);
            }
        });
    }

    private void setAverageRating()
    {
        float rating_sum = 0;
        for(Rating r: ratings)
        {
            rating_sum = rating_sum + r.getRate();
        }
        float rating_average = 0;
        if(!ratings.isEmpty())
            rating_average =rating_sum/ratings.size();

        RatingBar ratingBar = binding.getRoot().findViewById(R.id.ratingBarItem);
        ratingBar.setRating(rating_average);
    }

    private void getOwner()
    {
        OwnerRepo.getByFirmId(firm.getId(), new OwnerRepo.OwnerFetchCallback() {
            @Override
            public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                users.clear();
                users.add(owner);
            }
        });
    }
}
