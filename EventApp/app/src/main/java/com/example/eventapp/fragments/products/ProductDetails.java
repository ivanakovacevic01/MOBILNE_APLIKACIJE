package com.example.eventapp.fragments.products;

import static android.content.Context.MODE_PRIVATE;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.databinding.FragmentProductDetailsBinding;
import com.example.eventapp.fragments.ChooseUserDialog;
import com.example.eventapp.fragments.CompanyInfoFragment;
import com.example.eventapp.fragments.FavouritesFragment;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.administration.AddingEventTypeFragment;
import com.example.eventapp.fragments.employees.OwnerProfileFragment;
import com.example.eventapp.fragments.eventOrganizer.EventsForProductReservationFragment;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Favourites;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.FavouritesRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ProductDetails extends Fragment {

    private TextView productName;
    private TextView productDescription;
    private TextView productPrice;
    private TextView productDiscount;
    private TextView productDiscountPrice;
    private TextView productCategory;
    private TextView productSubcategory;
    private TextView productVisible;
    private TextView productAvailable;
    private TextView productEventType;

    private Product proizvod;

    private List<Uri> selectedUriImages;
    private FragmentProductDetailsBinding binding;
    public boolean isHomePage = false;
    private ArrayList<User> users = new ArrayList<>();
    public ProductDetails() {
        // Required empty public constructor
    }


    public static ProductDetails newInstance(Product p) {
        ProductDetails fragment = new ProductDetails();
        Bundle args = new Bundle();
        args.putParcelable("PROIZVOD", p);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            proizvod = getArguments().getParcelable("PROIZVOD");
        }
        selectedUriImages = new ArrayList<>();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        LinearLayout photoLinearLayout = root.findViewById(R.id.photoLinearLayout);

        getOwner();

        Button btnEdit = (Button) root.findViewById(R.id.btnEdit);
        Button btnDelete = (Button) root.findViewById(R.id.btnDelete);
        productName=binding.nameDetail;
        productDescription=binding.descriptionDetail;
        productCategory=binding.categoryDetail;
        productSubcategory=binding.subcategoryDetail;
        productDiscount=binding.discountDetail;
        productEventType=binding.eventDetail;
        productPrice=binding.priceDetail;
        productDiscountPrice=binding.discountPriceDetail;
        productAvailable=binding.availableDetail;
        productVisible=binding.visibleDetail;

        //*****************************ROLES**************************************
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


        //*************************************************************************


        SetData();

        if(!isHomePage)
        {
            btnEdit.setOnClickListener(v -> {
                if(!isHomePage)
                    FragmentTransition.to(ProductForm.newInstance(proizvod), getActivity(),
                            true, R.id.scroll_products_list);
            });

            btnDelete.setOnClickListener(v -> {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("Are you sure you want to delete this product?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ProductRepo.deleteProduct(proizvod.getId());
                                FragmentTransition.to(ProductsListFragment.newInstance(new ArrayList<>()),getActivity() ,
                                        true, R.id.scroll_products_list);
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
        }


        for (String imageUrl : proizvod.getImageUris()) {
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


        //dugme za rezervaciju
        Button btnReservation = (Button) root.findViewById(R.id.btnReserve);
        if(!SharedPreferencesManager.getUserRole(getContext()).equals(UserType.ORGANIZER.toString()) || !proizvod.getAvailable())
            btnReservation.setVisibility(View.GONE);
        btnReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventsForProductReservationFragment dialog = EventsForProductReservationFragment.newInstance(proizvod.getId());
                dialog.show(requireActivity().getSupportFragmentManager(), "EventsForProductReservationFragment");            }
        });


        //dugme za company info
        Button btnCompanyInfo = (Button) root.findViewById(R.id.btnCompanyInfo);
        if(isHomePage)
        {
            btnCompanyInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransition.to(CompanyInfoFragment.newInstance(proizvod.getFirmId()), getActivity(),
                            false, R.id.home_page_fragment);
                }
            });
        }else{
            btnCompanyInfo.setVisibility(View.GONE);
        }

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
                newFavourites.getProducts().add(proizvod);
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
                            favourites.get(0).getProducts().add(proizvod);
                            FavouritesRepo.update(favourites.get(0));
                        }
                    }
                }, SharedPreferencesManager.getEmail(getContext()));

            });
        }else
            btnFavourite.setVisibility(View.GONE);
        return  root;
    }

    private void SetData(){
        productName.setText(proizvod.getName());
        productDescription.setText(proizvod.getDescription());
        CategoryRepo.getAllCategories( new CategoryRepo.CategoryFetchCallback() {
            @Override
            public void onCategoryFetch(ArrayList<Category> categories) {
                for(Category c:categories)
                    if(c.getId().equals(proizvod.getCategory()))
                        productCategory.setText(c.getName());
            }
        });
        SubcategoryRepo.getAllSubcategories(new SubcategoryRepo.SubcategoryFetchCallback() {
            @Override
            public void onSubcategoryFetch(ArrayList<Subcategory> subcategories) {
                for(Subcategory s:subcategories)
                    if(s.getId().equals(proizvod.getSubcategory())) {
                        productSubcategory.setText(s.getName());
                    }

            }
        });
        productPrice.setText(proizvod.getPrice()+" din");
        productDiscount.setText(proizvod.getDiscount()+ " %");
        double discountPrice=proizvod.getPrice()*(1-proizvod.getDiscount()/100);
        productDiscountPrice.setText(Math.round(discountPrice) +" din");
        if(proizvod.getAvailable())
            productAvailable.setText("Da");
        else
            productAvailable.setText("Ne");
        if(proizvod.getVisible())
            productVisible.setText("Da");
        else
            productVisible.setText("Ne");
        EventTypeRepo eventTypeRepo=new EventTypeRepo();
        eventTypeRepo.getAllActicateEventTypes(new EventTypeRepo.EventTypeFetchCallback() {
            @Override
            public void onEventTypeFetch(ArrayList<EventType> eventTypes) {
                String events = "";
                if (eventTypes != null) {
                    for(EventType e:eventTypes)
                        for(String id:proizvod.getType())
                            if(e.getId().equals(id))
                                events +=e.getName()+"   ";
                    productEventType.setText(events);

                }
            }
        });

    }

    private void getOwner()
    {
        OwnerRepo.getByFirmId(proizvod.getFirmId(), new OwnerRepo.OwnerFetchCallback() {
            @Override
            public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                users.clear();
                users.add(owner);
            }
        });
    }
}