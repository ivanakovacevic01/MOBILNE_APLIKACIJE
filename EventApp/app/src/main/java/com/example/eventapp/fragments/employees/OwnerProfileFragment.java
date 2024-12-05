package com.example.eventapp.fragments.employees;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.activities.MainActivity;
import com.example.eventapp.databinding.CompanyProfileBinding;
import com.example.eventapp.databinding.FragmentEmployeeDetailsBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.PackageRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ReservationRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OwnerProfileFragment extends Fragment {
    private FragmentEmployeeDetailsBinding binding;
    private Owner employee;

    private AddressRepo addressRepo;
    private ServiceRepo serviceRepo;

    // TODO: Rename and change types of parameters
    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView employeeAddress;
    private FirebaseUser currentUser;

    public OwnerProfileFragment() {
        // Required empty public constructor
    }


    public static OwnerProfileFragment newInstance(Owner e) {
        OwnerProfileFragment fragment = new OwnerProfileFragment();
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
        binding = FragmentEmployeeDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        displayEmployeeProfile();


        return root;

    }

    private void displayEmployeeProfile() {
        ImageView mimageView=binding.getRoot().findViewById(R.id.employee_image);
        LinearLayout employeeLayout = (LinearLayout) binding.getRoot().findViewById(R.id.employee_layout);
        employeeLayout.setVisibility(View.GONE);
        name=binding.name;
        email=binding.email;
        phone=binding.phone;
        employeeAddress=binding.employeeAddress;

        ImageView imageView = new ImageView(getContext());

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get().load(employee.getImage()).into(mimageView);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        addressRepo=new AddressRepo();
        addressRepo.getAll(new AddressRepo.AddressFetchCallback() {
            @Override
            public void onAddressFetch(ArrayList<Address> addresses) {
                for(Address address:addresses){
                    if(address.getUserId().equals(employee.getId())){
                        employeeAddress.setText(address.getStreet()+" "+address.getStreetNumber()+", "+address.getCity()+", "+address.getCountry());
                    }
                }
            }
        });


        name.setText(employee.getFirstName()+" "+employee.getLastName());
        email.setText(employee.getEmail());
        phone.setText(employee.getPhoneNumber());

        Button btn2 = binding.editWorkingTime;
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();


        Button buttonEdit = binding.getRoot().findViewById(R.id.button_edit_profile);
        Button company = binding.getRoot().findViewById(R.id.button_company);
        company.setVisibility(View.VISIBLE);
        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
            @Override
            public void onUserObjectFetched(User user, String errorResponse) {
                if (user != null && user.isActive()) {
                    if (user.getType() == UserType.EMPLOYEE)
                        btn2.setVisibility(View.GONE);
                    if (user.getType() == UserType.ADMIN) {
                        buttonEdit.setVisibility(View.GONE);
                        company.setVisibility(View.GONE);
                    }
                }
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransition.to(EditProfileFragment.newInstance(employee), getActivity(), true, R.id.scroll_profile);
            }
        });



        company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransition.to(FirmProfileFragment.newInstance(employee.getFirmId(),""), getActivity(), true, R.id.scroll_profile);
            }
        });

        //DEACTIVATE PROFILE
        Button buttonDeactivate = binding.getRoot().findViewById(R.id.button_deactivate);

        buttonDeactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReservationRepo.getByFirmId(employee.getFirmId(), new ReservationRepo.ReservationFetchCallback() {
                    @Override
                    public void onReservationFetch(ArrayList<Reservation> reservations) {
                        ReservationRepo.ReservationFetchCallback.super.onReservationFetch(reservations);
                        if(reservations != null && !reservations.isEmpty())
                        {
                            employee.setDeactivated(true);
                            UserRepo.update(employee);
                            deactivate();
                        }else{
                            Toast.makeText(getContext(), "Can't deactivate this account!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void deactivate()
    {
        EmployeeRepo.getByFirmId(employee.getFirmId(), new EmployeeRepo.EmployeeFetchCallback() {
            @Override
            public void onEmployeeFetch(ArrayList<Employee> employees) {
                EmployeeRepo.EmployeeFetchCallback.super.onEmployeeFetch(employees);
                if(employees!= null)
                {
                    for(Employee e: employees)
                    {
                        e.setDeactivated(true);
                        UserRepo.update(e);
                    }
                }

                ProductRepo.getByFirmId(employee.getFirmId(), new ProductRepo.ProductFetchCallback() {
                    @Override
                    public void onProductFetch(ArrayList<Product> products) {
                        ProductRepo.ProductFetchCallback.super.onProductFetch(products);
                        if(products!= null)
                        {
                            for(Product p: products)
                            {
                                p.setVisible(false);
                                ProductRepo.updateProduct(p);
                            }
                        }

                        ServiceRepo.getByFirmId(employee.getFirmId(), new ServiceRepo.ServiceFetchCallback() {
                            @Override
                            public void onServiceFetch(ArrayList<Service> services) {
                                ServiceRepo.ServiceFetchCallback.super.onServiceFetch(services);
                                if(services!= null)
                                {
                                    for(Service s: services)
                                    {
                                        s.setVisible(false);
                                        ServiceRepo.updateService(s);
                                    }
                                }

                                PackageRepo.getByFirmId(employee.getFirmId(), new PackageRepo.PackageFetchCallback() {
                                    @Override
                                    public void onPackageFetch(ArrayList<Package> packages) {
                                        PackageRepo.PackageFetchCallback.super.onPackageFetch(packages);
                                        if(packages!= null)
                                        {
                                            for(Package p: packages)
                                            {
                                                p.setVisible(false);
                                                PackageRepo.updatePackage(p);
                                            }
                                        }

                                        Toast.makeText(getContext(), "Account deactivated!", Toast.LENGTH_SHORT).show();
                                        Intent myIntent = new Intent(getContext(), MainActivity.class);
                                        startActivity(myIntent);
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                });
                            }
                        });
                    }
                });

            }
        });

    }
}
