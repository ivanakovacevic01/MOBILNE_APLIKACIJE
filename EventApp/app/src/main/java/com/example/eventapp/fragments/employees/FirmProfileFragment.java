package com.example.eventapp.fragments.employees;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.eventapp.R;
import com.example.eventapp.databinding.CompanyProfileBinding;
import com.example.eventapp.fragments.ratings.RatingCompanyFragment;
import com.example.eventapp.fragments.ratings.RatingViewFragment;
import com.example.eventapp.adapters.eventTypes.EventTypesListAdapter;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.eventOrganizer.CatSubcatPopupDialogFragment;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Deadline;
import com.example.eventapp.model.DialogType;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.DeadlineRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.FirmRepo;
import com.squareup.picasso.Picasso;
import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FirmProfileFragment extends Fragment {

    private CompanyProfileBinding binding;
    private Address address;
    private Firm firm = new Firm();
    private List<Category> categories = new ArrayList<>();
    private List<EventType> eventTypes = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String reservationStatus="";

    public FirmProfileFragment() {
        // Required empty public constructor
    }


    public static FirmProfileFragment newInstance(String id,String status) {
        FirmProfileFragment fragment = new FirmProfileFragment();
        fragment.firm.setId(id);
        fragment.reservationStatus=status;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = CompanyProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getFirm();
        getAddress();
        rateCompany();
        ratingsView();

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

                getCategories();
                getEventTypes();
                mAuth=FirebaseAuth.getInstance();
                user=mAuth.getCurrentUser();
                Button edit = binding.getRoot().findViewById(R.id.buttonEdit);
                UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                    @Override
                    public void onUserObjectFetched(User user, String errorMessage) {
                        UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                        if(user!=null){
                            if(user.getType().equals(UserType.ADMIN))
                                edit.setVisibility(View.GONE);
                        }
                    }
                });
                edit.setOnClickListener(e -> {
                    FragmentTransition.to(EditCompanyFragment.newInstance(firm.getId()), getActivity(),
                            false, R.id.scroll_profile);
                });
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

    private void getEventTypes()
    {
        EventTypeRepo repo = new EventTypeRepo();
        repo.getByIds(new EventTypeRepo.EventTypeFetchCallback() {
            @Override
            public void onEventTypeFetch(ArrayList<EventType> eventTypes) {
                ListView listView = binding.getRoot().findViewById(R.id.lisViewEventTypes);
                EventTypesListAdapter adapter = new EventTypesListAdapter(getContext(), eventTypes);
                adapter.firmId = firm.getId();
                listView.setAdapter(adapter);
            }
        }, firm.getEventTypesIds());
    }

    private void rateCompany(){
        Button rate = binding.getRoot().findViewById(R.id.buttonRate);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
            @Override
            public void onUserObjectFetched(User user, String errorMessage) {
                UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                if(user!=null) {
                    if (!user.getType().equals(UserType.ORGANIZER)) {
                        rate.setVisibility(View.GONE);
                    }else{
                        DeadlineRepo deadlineRepo=new DeadlineRepo();
                        deadlineRepo.getByOrganizer(user.getEmail(), new DeadlineRepo.DeadlineFetchCallback() {
                            @Override
                            public void onDeadlineFetch(ArrayList<Deadline> deadlines) {
                                DeadlineRepo.DeadlineFetchCallback.super.onDeadlineFetch(deadlines);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                                try {
                                    Log.i("EventApp","Deadline"+deadlines.size());
                                    if(deadlines.size()>0){
                                        Date creationDate = dateFormat.parse(deadlines.get(0).getDate());
                                        Date today = new Date();
                                        long diffInMillies = Math.abs(today.getTime() - creationDate.getTime());
                                        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                                        if (diff > 5) {
                                            rate.setVisibility(View.GONE);
                                        }
                                    }else{
                                        rate.setVisibility(View.GONE);
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    rate.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                    if (!(reservationStatus.equals("REALIZED") || reservationStatus.equals("PUP_REJECTED")))
                        rate.setVisibility(View.GONE);
                }
            }
        });
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRejectedOrRealized()) {
                    RatingCompanyFragment dialog = new RatingCompanyFragment();
                    Bundle bundle=new Bundle();
                    bundle.putString("companyId",firm.getId());
                    dialog.setArguments(bundle);
                    dialog.show(requireActivity().getSupportFragmentManager(), "RatingCompanyFragment");
                } else {
                    Toast.makeText(requireContext(), "You cannot rate this company.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean isRejectedOrRealized(){
        return true;
    }
    private void ratingsView(){
        Button btnRatingView=binding.buttonRatingsView;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        btnRatingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("companyId",firm.getId());
                RatingViewFragment fragment=new RatingViewFragment();
                fragment.setArguments(bundle);
                UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                    @Override
                    public void onUserObjectFetched(User user, String errorMessage) {
                        UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                        if(user!=null){
                            if(user.getType().equals(UserType.ADMIN))
                                FragmentTransition.to(fragment, getActivity(),
                                        true, R.id.scroll_reports);
                            else if(user.getType().equals(UserType.ORGANIZER) || user.getType().equals(UserType.EMPLOYEE))
                                FragmentTransition.to(fragment, getActivity(),
                                        true, R.id.scroll_reservations_list);
                            else
                                FragmentTransition.to(fragment, getActivity(),
                                        true, R.id.scroll_profile);
                        }
                    }
                });

            }
        });
    }
}
