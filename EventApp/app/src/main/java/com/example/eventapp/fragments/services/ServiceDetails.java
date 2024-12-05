package com.example.eventapp.fragments.services;

import android.os.Bundle;
import static android.content.Context.MODE_PRIVATE;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.databinding.FragmentServiceDetails2Binding;
import com.example.eventapp.fragments.ChooseUserDialog;
import com.example.eventapp.fragments.CompanyInfoFragment;
import com.example.eventapp.fragments.FavouritesFragment;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.eventOrganizer.EventsForProductReservationFragment;
import com.example.eventapp.fragments.eventOrganizer.EventsForServiceReservationFragment;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Favourites;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.FavouritesRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import android.content.DialogInterface;
import android.net.Uri;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.*;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceDetails extends Fragment {

   private FragmentServiceDetails2Binding binding;
    private List<Uri> selectedUriImages;
    private TextView name;
    private TextView description;
    private TextView category;
    private TextView event;
    private TextView pricePH;
    private TextView discount;
    private TextView discountPrice;
    private TextView performers;
    private TextView available;
    private TextView visible;
    private TextView cancelationPeriod;
    private TextView reservationPeriod;
    private TextView acceptance;
    private TextView location;
    private TextView specifics;
    private TextView minDuration;
    private TextView maxDuration;
    private Service servis;

    public boolean isHomePage = false;
    private ArrayList<User> users = new ArrayList<>();

    public ServiceDetails() {
        // Required empty public constructor
    }


    public static ServiceDetails newInstance(Service s) {
        ServiceDetails fragment = new ServiceDetails();
        Bundle args = new Bundle();
        args.putParcelable("SERVICE", s);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            servis = getArguments().getParcelable("SERVICE");
        }
        selectedUriImages = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServiceDetails2Binding.inflate(inflater, container, false);
        View root = binding.getRoot();
        LinearLayout photoLinearLayout = root.findViewById(R.id.photoLinearLayout);


        getEmployees();
        name=binding.name;
        description=binding.description;
        specifics=binding.specifics;
        event=binding.event;
        category=binding.category;
        location=binding.location;
        performers=binding.performers;
        visible=binding.visible;
        available=binding.available;
        cancelationPeriod=binding.cancelationDeadline;
        reservationPeriod=binding.reservationDeadline;
        acceptance=binding.acceptance;
        pricePH=binding.pricePH;
        minDuration=binding.minimumDuration;
        maxDuration=binding.maximumDuration;
        discount=binding.discount;
        discountPrice=binding.discountPrice;


        setData();


        Button btnEdit = (Button) root.findViewById(R.id.btnEdit);
        Button btnDelete = (Button) root.findViewById(R.id.btnDelete);

        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user != null)
        {
            UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                @Override
                public void onUserObjectFetched(User user, String errorResponse) {
                    if (user != null && user.isActive()) {
                        if(user.getType()!= UserType.OWNER) {
                            btnDelete.setVisibility(View.GONE);
                            btnEdit.setVisibility(View.GONE);
                        }

                    }
                }
            });
        }else{
            btnDelete.setVisibility(View.GONE);
            btnEdit.setVisibility(View.GONE);
        }



        btnEdit.setOnClickListener(v -> {
            FragmentTransition.to(ServiceFormFragment.newInstance(servis), getActivity(),
                    true, R.id.scroll_products_list_2);
        });

        btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("Are you sure you want to delete this service?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ServiceRepo.deleteService(servis.getId());
                            FragmentTransition.to(ServicesPageFragment.newInstance(),getActivity() ,
                                    true, R.id.scroll_products_list_2);
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = dialog.create();
            alert.show();
        });

        for (String imageUrl : servis.getImageUris()) {
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

        //dugme za company info
        Button btnCompanyInfo = (Button) root.findViewById(R.id.btnCompanyInfo);
        if(isHomePage)
        {
            btnCompanyInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransition.to(CompanyInfoFragment.newInstance(servis.getFirmId()), getActivity(),
                            false, R.id.home_page_fragment);
                }
            });
        }else{
            btnCompanyInfo.setVisibility(View.GONE);
        }

        //dugme za rezervaciju
        Button btnReservation = (Button) root.findViewById(R.id.btnReserve);
        if(!SharedPreferencesManager.getUserRole(getContext()).equals(UserType.ORGANIZER.toString()) || !servis.getAvailable())
            btnReservation.setVisibility(View.GONE);
        btnReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventsForServiceReservationFragment dialog = EventsForServiceReservationFragment.newInstance(servis);
                dialog.show(requireActivity().getSupportFragmentManager(), "EventsForServiceReservationFragment");            }
        });

        //messaging
        Button btnMessage = (Button) binding.getRoot().findViewById(R.id.btnMessage);
        if(isHomePage && SharedPreferencesManager.getUserRole(getContext()).equals("ORGANIZER"))
        {
            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChooseUserDialog dialog = ChooseUserDialog.newInstance(users);
                    dialog.show(getChildFragmentManager(), "CHAT");
                }
            });
        }else{
            btnMessage.setVisibility(View.GONE);
        }

        Button btnFavourite = binding.getRoot().findViewById(R.id.btnFavourite);
        if(isHomePage) {
            btnFavourite.setOnClickListener(v -> {
                    Favourites newFavourites = new Favourites();
                    newFavourites.getServices().add(servis);
                    newFavourites.setUserEmail(SharedPreferencesManager.getEmail(getContext()));
                    Toast.makeText(getContext(), "Added to favourites!", Toast.LENGTH_SHORT).show();
                    FavouritesRepo favouritesRepo = new FavouritesRepo();
                    favouritesRepo.getByOrganizer(new FavouritesRepo.FavouriteFetchCallback() {
                        @Override
                        public void onFavouriteFetch(ArrayList<Favourites> favourites) {
                            if(favourites == null || favourites.isEmpty())
                                FavouritesRepo.create(newFavourites);
                            else
                            {
                                favourites.get(0).getServices().add(servis);
                                FavouritesRepo.update(favourites.get(0));
                            }
                        }
                    }, SharedPreferencesManager.getEmail(getContext()));

                });
        }else
            btnFavourite.setVisibility(View.GONE);

        return  root;

    }

    private void setData(){

        name.setText(servis.getName());
        description.setText(servis.getDescription());
        location.setText(servis.getLocation());
        specifics.setText(servis.getLocation());
        CategoryRepo.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
            @Override
            public void onCategoryFetch(ArrayList<Category> categories) {
                for(Category c:categories)
                    if(c.getId().equals(servis.getCategory()))
                        SubcategoryRepo.getAllSubcategories(new SubcategoryRepo.SubcategoryFetchCallback() {
                            @Override
                            public void onSubcategoryFetch(ArrayList<Subcategory> subcategories) {
                                for(Subcategory s:subcategories)
                                    if(s.getId().equals(servis.getSubcategory())) {
                                        category.setText(c.getName()+" - "+s.getName());
                                    }

                            }
                        });
            }
        });

        EventTypeRepo eventTypeRepo=new EventTypeRepo();
        eventTypeRepo.getAllActicateEventTypes(new EventTypeRepo.EventTypeFetchCallback() {
            @Override
            public void onEventTypeFetch(ArrayList<EventType> eventTypes) {
                String events = "";

                if (eventTypes != null) {
                    for(EventType e:eventTypes)
                        for(String id:servis.getType())
                            if(e.getId().equals(id))
                                events +=e.getName()+"   ";
                    event.setText(events);

                }
            }
        });

        EmployeeRepo employeeRepo=new EmployeeRepo();
        employeeRepo.getAll(new EmployeeRepo.EmployeeFetchCallback() {
            @Override
            public void onEmployeeFetch(ArrayList<Employee> employees) {
                String attendants = "";

                if (employees != null) {
                    for(Employee e:employees)
                        for(String id:servis.getAttendants())
                            if(e.getId().equals(id))
                                attendants +=e.getFirstName()+" "+e.getLastName()+"   ";
                    performers.setText(attendants);

                }
            }
        });
        if(servis.getVisible())
            visible.setText("Da");
        else
            visible.setText("Ne");
        if(servis.getAvailable())
            available.setText("Da");
        else
            available.setText("Ne");
        if(servis.getManualConfirmation())
            acceptance.setText("Ručno");
        else
            acceptance.setText("Automatski");
        cancelationPeriod.setText(servis.getCancellationDeadline()+" d");
        reservationPeriod.setText(servis.getReservationDeadline()+" d");
        minDuration.setText(servis.getMinDuration()+" h");
        maxDuration.setText(servis.getMaxDuration()+" h");
        pricePH.setText(servis.getPricePerHour()+" din");
        discount.setText(servis.getDiscount()+" %");
        discountPrice.setText(Math.round(servis.getPricePerHour()*(1- servis.getDiscount()/100))+" din");
    }


    private void getEmployees()
    {
       EmployeeRepo.getByIds(servis.getAttendants(), new EmployeeRepo.EmployeeFetchCallback() {
           @Override
           public void onEmployeeFetch(ArrayList<Employee> employees) {
               EmployeeRepo.EmployeeFetchCallback.super.onEmployeeFetch(employees);
               users = new ArrayList<>(employees);
           }
       });
    }
}