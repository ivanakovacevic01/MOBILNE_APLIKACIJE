package com.example.eventapp.fragments.employees;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.adapters.ReservationListAdapter;
import com.example.eventapp.adapters.services.ServiceRecyclerViewAdapter;
import com.example.eventapp.databinding.FragmentEmployeeDetailsBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.model.WeeklyEvent;
import com.example.eventapp.model.WorkingTime;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.UserRepo;
import com.example.eventapp.repositories.WeeklyEventRepo;
import com.example.eventapp.repositories.WorkingTimeRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EmployeeDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentEmployeeDetailsBinding binding;
    private WorkingTimeRepo workingTimeRepo;
    private AddressRepo addressRepo;
    private ServiceRecyclerViewAdapter adapter;
    private Employee employee;
    private ServiceRepo serviceRepo;

    // TODO: Rename and change types of parameters
    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView mondayWorkingTime;
    private TextView tuesdayWorkingTime;
    private TextView wednesdayWorkingTime;
    private TextView thursdayWorkingTime;
    private TextView fridayWorkingTime;
    private TextView saturdayWorkingTime;
    private TextView sundayWorkingTime;
    private TextView employeeAddress;
    private FirebaseUser currentUser;

    public EmployeeDetailsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static EmployeeDetailsFragment newInstance(Employee e) {
        EmployeeDetailsFragment fragment = new EmployeeDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("EMPLOYEE", e);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }
    private void displayEmployeeProfile() {
        ImageView mimageView=binding.employeeImage;
        name=binding.name;
        email=binding.email;
        phone=binding.phone;
        mondayWorkingTime=binding.mondayWorkingTime;
        tuesdayWorkingTime=binding.tuesdayWorkingTime;
        wednesdayWorkingTime=binding.wednesdayWorkingTime;
        thursdayWorkingTime=binding.thursdayWorkingTime;
        fridayWorkingTime=binding.fridayWorkingTime;
        saturdayWorkingTime=binding.saturdayWorkingTime;
        sundayWorkingTime=binding.sundayWorkingTime;
        employeeAddress=binding.employeeAddress;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        workingTimeRepo=new WorkingTimeRepo();
        workingTimeRepo.getAll(new WorkingTimeRepo.WorkingTimeFetchCallback() {
            @Override
            public void onWorkingTimeFetch(ArrayList<WorkingTime> workingTimes) {
                for(WorkingTime wt: workingTimes){
                    if(wt.getUserId()!=null){
                        if(wt.getUserId().equals(employee.getId())){
                            mondayWorkingTime.setText(wt.getMondayStartTime()+" - "+wt.getMondayEndTime());
                            tuesdayWorkingTime.setText(wt.getTuesdayStartTime()+" - "+wt.getTuesdayEndTime());
                            wednesdayWorkingTime.setText(wt.getWednesdayStartTime()+" - "+wt.getWednesdayEndTime());
                            thursdayWorkingTime.setText(wt.getThursdayStartTime()+" - "+wt.getThursdayEndTime());
                            fridayWorkingTime.setText(wt.getFridayStartTime()+" - "+wt.getFridayEndTime());
                            saturdayWorkingTime.setText(wt.getSaturdayStartTime()+" - "+wt.getSaturdayEndTime());
                            sundayWorkingTime.setText(wt.getSundayStartTime()+" - "+wt.getSundayEndTime());
                        }
                    }

                }
            }
        });

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

        serviceRepo=new ServiceRepo();
        ArrayList<Service> employeeServices=new ArrayList<>();
        serviceRepo.getAllServices(new ServiceRepo.ServiceFetchCallback() {
            @Override
            public void onServiceFetch(ArrayList<Service> services) {
                if (services != null) {
                    for (Service service : services) {
                        if (service.getAttendants() != null) {
                            for (String s : service.getAttendants()) {
                                if (s.equals(employee.getId())) {
                                    employeeServices.add(service);
                                }
                            }
                        }
                    }
                }
                RecyclerView recyclerView = binding.recyclerView;
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new ServiceRecyclerViewAdapter(getActivity(), employeeServices);
                recyclerView.setAdapter(adapter);

            }
        });



        name.setText(employee.getFirstName()+" "+employee.getLastName());
        email.setText(employee.getEmail());
        phone.setText(employee.getPhoneNumber());

        WeeklyEventRepo.getByEmployeeId(employee.getId(), new WeeklyEventRepo.WeeklyEventFetchCallback() {
            @Override
            public void onWeeklyEventFetch(ArrayList<WeeklyEvent> events) {
                WeeklyEventRepo.WeeklyEventFetchCallback.super.onWeeklyEventFetch(events);

            }
        });

        Button btn2 = binding.editWorkingTime;
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        workingTimeRepo=new WorkingTimeRepo();
        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
            @Override
            public void onUserObjectFetched(User user, String errorResponse) {
                if (user != null && user.isActive()) {
                    if (user.getType() == UserType.EMPLOYEE)
                        btn2.setVisibility(View.GONE);
                    else{
                        workingTimeRepo.getByEmployee(employee.getId(), new WorkingTimeRepo.WorkingTimeFetchCallback() {
                            @Override
                            public void onWorkingTimeFetch(ArrayList<WorkingTime> workingTimes) {
                                WorkingTimeRepo.WorkingTimeFetchCallback.super.onWorkingTimeFetch(workingTimes);
                                if(workingTimes!=null){
                                    hasEventsInPeriod(employee.getId(), workingTimes.get(0).getStartDate(), workingTimes.get(0).getEndDate(), new hasEvent() {
                                        @Override
                                        public void onCheckComplete(boolean hasEvent) {
                                            if(hasEvent)
                                                btn2.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }
                        });
                    }
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
        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                300, 300
        );
        layoutParams.setMargins(0, 0, 10, 0);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get().load(employee.getImage()).into(mimageView);

        Button buttonEdit = binding.getRoot().findViewById(R.id.button_edit_profile);

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransition.to(EditProfileFragment.newInstance(employee), getActivity(), true, R.id.scroll_profile);
            }
        });


    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmployeeDetailsBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        employee=new Employee();
        if(getArguments()!=null)
            employee = getArguments().getParcelable("EMPLOYEE");
        EmployeeRepo.getByEmail(currentUser.getEmail(), new EmployeeRepo.EmployeeFetchCallback() {
            @Override
            public void onEmployeeObjectFetched(Employee user, String errorMessage) {
                if(user!=null) {
                    employee=user;
                    displayEmployeeProfile();
                }
                else{
                    if (getArguments() != null) {
                        employee = getArguments().getParcelable("EMPLOYEE");
                        displayEmployeeProfile();
                    }
                }
            }
        });


        return root;
    }
    public void hasEventsInPeriod(String employeeId, String startDateStr, String endDateStr, EmployeeDetailsFragment.hasEvent callback) {
        SimpleDateFormat eventFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date start = sdf.parse(startDateStr);
            Date end = sdf.parse(endDateStr);

            WeeklyEventRepo.getByEmployeeId(employeeId, new WeeklyEventRepo.WeeklyEventFetchCallback() {
                @Override
                public void onWeeklyEventFetch(ArrayList<WeeklyEvent> events) {
                    WeeklyEventRepo.WeeklyEventFetchCallback.super.onWeeklyEventFetch(events);
                    Boolean hasEvent = false;

                    for (WeeklyEvent event : events) {
                        try {
                            Date eventDate = eventFormatter.parse(event.getDate());
                            if ((eventDate.equals(start) || eventDate.after(start)) &&
                                    (eventDate.equals(end) || eventDate.before(end))) {
                                hasEvent = true;
                                break;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onCheckComplete(hasEvent);
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the error, maybe notify the callback with a failure
            callback.onCheckComplete(false); // or some error state
        }
    }
    interface hasEvent {
        void onCheckComplete(boolean hasEvent);
    }

}