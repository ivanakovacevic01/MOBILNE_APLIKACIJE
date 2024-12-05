package com.example.eventapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.adapters.eventTypes.EventTypesListAdapter;
import com.example.eventapp.databinding.CompanyProfileBinding;
import com.example.eventapp.databinding.FragmentFirmDetailsBinding;
import com.example.eventapp.fragments.employees.EditCompanyFragment;
import com.example.eventapp.fragments.eventOrganizer.CatSubcatPopupDialogFragment;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.DialogType;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.FirmWorkingTime;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.model.WorkingTime;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.FirmRepo;
import com.example.eventapp.repositories.FirmWorkingTimeRepo;
import com.example.eventapp.repositories.UserRepo;
import com.example.eventapp.repositories.WorkingTimeRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FirmDetailsFragment extends DialogFragment {

    private Address address;
    private Firm firm = new Firm();
    private List<Category> categories = new ArrayList<>();
    private List<EventType> eventTypes = new ArrayList<>();
    private FirmWorkingTime time;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FragmentFirmDetailsBinding binding;


    public FirmDetailsFragment() {
        // Required empty public constructor
    }

    public static FirmDetailsFragment newInstance(String id) {
        FirmDetailsFragment fragment = new FirmDetailsFragment();
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
        // Inflate the layout for this fragment
        binding = FragmentFirmDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getFirm();
        getAddress();


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

                getWorkingTime();
                getCategories();
                getEventTypes();
                mAuth=FirebaseAuth.getInstance();
                user=mAuth.getCurrentUser();


            }
        });
    }
    private void getCategories()
    {
        CategoryRepo repo = new CategoryRepo();
        repo.getByIds(new CategoryRepo.CategoryFetchCallback() {
            @Override
            public void onCategoryFetch(ArrayList<Category> categories) {
                CategoryRepo.CategoryFetchCallback.super.onCategoryFetch(categories);

                Button catButton = binding.getRoot().findViewById(R.id.buttonCategories);

                catButton.setOnClickListener(e -> {
                    CatSubcatPopupDialogFragment f =  CatSubcatPopupDialogFragment.newInstance(true, DialogType.CategoriesForFirm, null, null, null, categories);
                    f.show(getChildFragmentManager(), "CATEGORIES");
                });
            }
        }, firm.getCategoriesIds());

    }

    private void getEventTypes() {
        EventTypeRepo repo = new EventTypeRepo();
        repo.getByIds(new EventTypeRepo.EventTypeFetchCallback() {
            @Override
            public void onEventTypeFetch(ArrayList<EventType> eventTypes) {
                LinearLayout linearLayout = binding.getRoot().findViewById(R.id.linearLayoutEventTypes);

                // Čišćenje prethodno dodanih elemenata, ako postoje
                linearLayout.removeAllViews();

                for (EventType eventType : eventTypes) {
                    TextView textView = new TextView(getContext());
                    textView.setText(eventType.getName());
                    textView.setTextSize(16); // Postavite veličinu teksta po želji
                    textView.setPadding(10, 10, 10, 10); // Postavite padding po želji

                    // Dodavanje TextView u LinearLayout
                    linearLayout.addView(textView);
                }
            }
        }, firm.getEventTypesIds());
    }

    private void getWorkingTime() {
        FirmWorkingTimeRepo firmWorkingTimeRepo = new FirmWorkingTimeRepo();
        firmWorkingTimeRepo.getAll(new FirmWorkingTimeRepo.FirmWorkingTimeFetchCallback() {
            @Override
            public void onFirmWorkingTimeFetch(ArrayList<FirmWorkingTime> firmWorkingTimes) {
                if (firmWorkingTimes != null) {
                    for (FirmWorkingTime firmWorkingTime1 : firmWorkingTimes) {
                        if (firmWorkingTime1.getFirmId().equals(firm.getId())) {
                            time = firmWorkingTime1;
                            updateWorkingTimeUI();
                            break;
                        }
                    }
                }
            }
        });
    }

    private void updateWorkingTimeUI() {
        if (time == null) return;

        View root = binding.getRoot();

        TextView mondayStartTime = root.findViewById(R.id.mondayStartTime);
        TextView mondayEndTime = root.findViewById(R.id.mondayEndTime);
        TextView tuesdayStartTime = root.findViewById(R.id.tuesdayStartTime);
        TextView tuesdayEndTime = root.findViewById(R.id.tuesdayEndTime);
        TextView wednesdayStartTime = root.findViewById(R.id.wednesdayStartTime);
        TextView wednesdayEndTime = root.findViewById(R.id.wednesdayEndTime);
        TextView thursdayStartTime = root.findViewById(R.id.thursdayStartTime);
        TextView thursdayEndTime = root.findViewById(R.id.thursdayEndTime);
        TextView fridayStartTime = root.findViewById(R.id.fridayStartTime);
        TextView fridayEndTime = root.findViewById(R.id.fridayEndTime);
        TextView saturdayStartTime = root.findViewById(R.id.saturdayStartTime);
        TextView saturdayEndTime = root.findViewById(R.id.saturdayEndTime);
        TextView sundayStartTime = root.findViewById(R.id.sundayStartTime);
        TextView sundayEndTime = root.findViewById(R.id.sundayEndTime);


        mondayStartTime.setText(time.getMondayStartTime());
        mondayEndTime.setText(time.getMondayEndTime());
        tuesdayStartTime.setText(time.getTuesdayStartTime());
        tuesdayEndTime.setText(time.getTuesdayEndTime());
        wednesdayStartTime.setText(time.getWednesdayStartTime());
        wednesdayEndTime.setText(time.getWednesdayEndTime());
        thursdayStartTime.setText(time.getThursdayStartTime());
        thursdayEndTime.setText(time.getThursdayEndTime());
        fridayStartTime.setText(time.getFridayStartTime());
        fridayEndTime.setText(time.getFridayEndTime());
        saturdayStartTime.setText(time.getSaturdayStartTime());
        saturdayEndTime.setText(time.getSaturdayEndTime());
        sundayStartTime.setText(time.getSundayStartTime());
        sundayEndTime.setText(time.getSundayEndTime());

    }


}