package com.example.eventapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.adapters.ItemListAdapter;
import com.example.eventapp.adapters.MessageAdapter;
import com.example.eventapp.databinding.ChatBinding;
import com.example.eventapp.databinding.FavouritesLayoutBinding;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Favourites;
import com.example.eventapp.model.Message;
import com.example.eventapp.model.MessageStatus;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.FavouritesRepo;
import com.example.eventapp.repositories.MessageRepo;
import com.example.eventapp.repositories.PackageRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.UserRepo;

import org.bouncycastle.util.Pack;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class FavouritesFragment extends Fragment {
    private FavouritesLayoutBinding binding;
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Service> services = new ArrayList<>();
    private ArrayList<Package> packages = new ArrayList<>();
    private ArrayList<Message> messages = new ArrayList<>();
    private ItemListAdapter adapter;
    public FavouritesFragment() {
        // Required empty public constructor
    }


    public static FavouritesFragment newInstance() {
        FavouritesFragment fragment = new FavouritesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FavouritesLayoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.setAdapter(root);

        return root;

    }

    private void setAdapter(View root)
    {
        RecyclerView recyclerView = root.findViewById(R.id.favouritesList);

        ProductRepo productRepo = new ProductRepo();
        ServiceRepo serviceRepo = new ServiceRepo();
        PackageRepo packageRepo = new PackageRepo();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FavouritesRepo repo = new FavouritesRepo();
        repo.getByOrganizer(new FavouritesRepo.FavouriteFetchCallback() {
            @Override
            public void onFavouriteFetch(ArrayList<Favourites> favourites) {

                Favourites f;
                if(favourites.isEmpty())
                    f = new Favourites();
                else
                    f = favourites.get(0);
                productRepo.getAllProducts(new ProductRepo.ProductFetchCallback() {
                    @Override
                    public void onProductFetch(ArrayList<Product> types) {
                        products.clear();
                        for(Product p: types)
                        {
                            if(p.getVisible() && !p.getDeleted() && f.getProducts().stream().anyMatch(i -> i.getId().equals(p.getId())))
                                products.add(p);
                        }
                        adapter = new ItemListAdapter(packages, services, products, getActivity(), f);
                        recyclerView.setAdapter(adapter);
                    }
                });
                packageRepo.getAllPackages(new PackageRepo.PackageFetchCallback() {
                    @Override
                    public void onPackageFetch(ArrayList<Package> types) {
                        packages.clear();
                        for(Package p: types)
                        {
                            if(p.getVisible() && !p.getDeleted()  && f.getPackages().stream().anyMatch(i -> i.getId().equals(p.getId())))
                                packages.add(p);
                        }

                        adapter = new ItemListAdapter(packages, services, products, getActivity(), f);
                        recyclerView.setAdapter(adapter);
                    }
                });
                serviceRepo.getAllServices(new ServiceRepo.ServiceFetchCallback() {
                    @Override
                    public void onServiceFetch(ArrayList<Service> types) {
                        services.clear();

                        for(Service p: types)
                        {
                            if(p.getVisible() && !p.getDeleted() && f.getServices().stream().anyMatch(i -> i.getId().equals(p.getId())))
                                services.add(p);
                        }
                        adapter = new ItemListAdapter(packages, services, products, getActivity(), f);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }

        }, SharedPreferencesManager.getEmail(getContext()));
}
}
