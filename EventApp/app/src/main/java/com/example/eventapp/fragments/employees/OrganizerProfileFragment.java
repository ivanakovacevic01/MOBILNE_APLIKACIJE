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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.activities.MainActivity;
import com.example.eventapp.adapters.services.ServiceRecyclerViewAdapter;
import com.example.eventapp.databinding.EditPersonalInfoBinding;
import com.example.eventapp.databinding.FragmentEmployeeDetailsBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.model.WorkingTime;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.ReservationRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.UserRepo;
import com.example.eventapp.repositories.WorkingTimeRepo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firestore.v1.UpdateDocumentRequest;
import com.google.firestore.v1.UpdateDocumentRequestOrBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrganizerProfileFragment extends Fragment {
    private FragmentEmployeeDetailsBinding binding;
    private Organizer employee;

    private AddressRepo addressRepo;
    private ServiceRepo serviceRepo;

    // TODO: Rename and change types of parameters
    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView employeeAddress;
    private FirebaseUser currentUser;

    public OrganizerProfileFragment() {
        // Required empty public constructor
    }


    public static OrganizerProfileFragment newInstance(Organizer e) {
        OrganizerProfileFragment fragment = new OrganizerProfileFragment();
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
        binding = FragmentEmployeeDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        displayEmployeeProfile();


        return root;

    }

    private void displayEmployeeProfile() {
        ImageView mimageView=binding.employeeImage;
        LinearLayout employeeLayout = (LinearLayout) binding.getRoot().findViewById(R.id.employee_layout);
        employeeLayout.setVisibility(View.GONE);
        name=binding.name;
        email=binding.email;
        phone=binding.phone;
        employeeAddress=binding.employeeAddress;
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
        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
            @Override
            public void onUserObjectFetched(User user, String errorResponse) {
                if (user != null && user.isActive()) {
                    if (user.getType() == UserType.EMPLOYEE)
                        btn2.setVisibility(View.GONE);
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("employeeId",employee.getId());
                Fragment registerWorkingTimeFragment = new RegisterEmployeWorkingTimeFragment();
                registerWorkingTimeFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.scroll_employees_list, registerWorkingTimeFragment).addToBackStack("details");
                fragmentTransaction.commit();
            }
        });
        Button btn3 = binding.editCalendar;

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("employeeId", employee.getId());
                Fragment calendarFragment = new CalendarFragment();
                calendarFragment.setArguments(bundle);
                Log.i("EventApp","id: "+currentUser.getUid()+" "+employee.getId()+"email "+currentUser.getEmail()+" "+employee.getEmail());

                if(currentUser.getEmail().equals(employee.getEmail())) {
                    FragmentTransition.to(calendarFragment, getActivity(), true, R.id.scroll_profile);
                }
                else
                    FragmentTransition.to(calendarFragment,getActivity(),true,R.id.scroll_employees_list);


            }
        });

        Picasso.get().load(employee.getImage()).into(mimageView);

        Button buttonEdit = binding.getRoot().findViewById(R.id.button_edit_profile);

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransition.to(EditProfileFragment.newInstance(employee), getActivity(), true, R.id.scroll_profile);
            }
        });

        //DEACTIVATE PROFILE
        Button buttonDeactivate = binding.getRoot().findViewById(R.id.button_deactivate);

        buttonDeactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReservationRepo.getByEmail(SharedPreferencesManager.getEmail(getContext()), new ReservationRepo.ReservationFetchCallback() {
                    @Override
                    public void onReservationFetch(ArrayList<Reservation> reservations) {
                        ReservationRepo.ReservationFetchCallback.super.onReservationFetch(reservations);
                        if(reservations != null && !reservations.isEmpty())
                        {
                            employee.setDeactivated(true);
                            UserRepo.update(employee);
                            Toast.makeText(getContext(), "Account deactivated!", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            Intent myIntent = new Intent(getContext(), MainActivity.class);
                            startActivity(myIntent);
                        }else{
                            Toast.makeText(getContext(), "Can't deactivate this account!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}
