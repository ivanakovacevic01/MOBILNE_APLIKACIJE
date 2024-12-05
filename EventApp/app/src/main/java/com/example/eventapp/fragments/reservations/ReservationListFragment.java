package com.example.eventapp.fragments.reservations;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.adapters.ItemListAdapter;
import com.example.eventapp.adapters.ReservationListAdapter;
import com.example.eventapp.adapters.employee.EmployeeListAdapter;
import com.example.eventapp.databinding.FragmentReservationListBinding;
import com.example.eventapp.model.Employee;

import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.ReservationFilters;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.OrganizerRepo;
import com.example.eventapp.repositories.PackageRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ReservationRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ReservationListFragment extends ListFragment {
    private ReservationListAdapter adapter;
    private static final String ARG_PARAM = "param";
    private ArrayList<Reservation> mReservations;
    private FragmentReservationListBinding binding;
    private ReservationRepo reservationRepo;
    private ReservationFilters filters = new ReservationFilters();
    private ArrayList<Employee> employees = new ArrayList<>();
    private FirebaseUser currentUser;
    private UserRepo userRepo;
    private SearchView searchName;
    private ArrayList<Reservation> allReservations;
    private boolean isOwner = false;
    private ArrayList<Organizer> organizers=new ArrayList<>();

    public static ReservationListFragment newInstance(ArrayList<Reservation> reservations) {
        ReservationListFragment fragment = new ReservationListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM, reservations);
        fragment.setArguments(args);
        fragment.filters.status = "ALL";
        fragment.filters.text = "";
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("EventApp", "onCreate Reservation List Fragment");
        mReservations = new ArrayList<>();
        reservationRepo = new ReservationRepo();
        allReservations = new ArrayList<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRepo = new UserRepo();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("EventApp", "onCreateView Reservation List Fragment");
        binding = FragmentReservationListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        searchName = binding.searchText;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRepo.getAll(new UserRepo.UserFetchCallback() {
                @Override
                public void onUserFetch(ArrayList<User> users) {
                    UserRepo.UserFetchCallback.super.onUserFetch(users);
                    if (users != null) {
                        for (User u : users) {
                            if (u.getEmail().equals(currentUser.getEmail())) {
                                if (u.getType().toString().equals("OWNER")) {
                                    fetchOwnerReservations();
                                } else if (u.getType().toString().equals("ORGANIZER")) {
                                    searchName.setVisibility(View.GONE);
                                    fetchOrganizerReservations(currentUser.getEmail());
                                } else if (u.getType().toString().equals("EMPLOYEE")) {
                                    //searchName.setVisibility(View.GONE);
                                    fetchEmployeeReservations(currentUser.getUid());
                                }
                                break;
                            }
                        }
                    }
                }
            });
            EmployeeRepo employeeRepo=new EmployeeRepo();
            employeeRepo.getAll(new EmployeeRepo.EmployeeFetchCallback() {
                @Override
                public void onEmployeeFetch(ArrayList<Employee> types) {
                    employees.clear();
                    employees.addAll(types);
                }
            });
            OrganizerRepo.getAll(new OrganizerRepo.OrganizerFetchCallback() {
                @Override
                public void onOrganizerFetch(ArrayList<Organizer> types) {
                    OrganizerRepo.OrganizerFetchCallback.super.onOrganizerFetch(types);
                    organizers.clear();
                    organizers.addAll(types);
                }
            });

        }

        setupFilters(root);
        setupSearch();

        return root;
    }
    private void fetchOwnerReservations() {
        reservationRepo.getAll(new ReservationRepo.ReservationFetchCallback() {
            @Override
            public void onReservationFetch(ArrayList<Reservation> reservations) {
                if (reservations != null) {
                    mReservations = reservations;
                    allReservations.clear();
                    allReservations.addAll(reservations);
                    isOwner = true;
                    updateAdapter();
                }
            }
        });
    }

    private void fetchOrganizerReservations(String email) {
        ReservationRepo.getByEmail(email, new ReservationRepo.ReservationFetchCallback() {
            @Override
            public void onReservationFetch(ArrayList<Reservation> reservations) {
                if (reservations != null) {
                    mReservations = reservations;
                    allReservations.clear();
                    allReservations.addAll(reservations);
                    Log.i("Organzatorske rezervacije",""+allReservations.size());
                    updateAdapter();
                }
            }
        });
    }

    private void fetchEmployeeReservations(String email) {
        ReservationRepo.getByEmployeeId(email, new ReservationRepo.ReservationFetchCallback() {
            @Override
            public void onReservationFetch(ArrayList<Reservation> reservations) {
                if (reservations != null) {
                    mReservations = reservations;
                    allReservations.clear();
                    allReservations.addAll(reservations);
                    updateAdapter();
                }
            }
        });
    }

    private void updateAdapter() {
        if (adapter == null) {
            adapter = new ReservationListAdapter(getContext(), allReservations, (AppCompatActivity) getActivity());
            setListAdapter(adapter);
        } else {
            adapter.updateReservations(allReservations);
            adapter.notifyDataSetChanged();
        }
    }

    private void setupFilters(View root) {
        Button btnFilters = root.findViewById(R.id.btnFilters);
        btnFilters.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.FullScreenBottomSheetDialog);
            View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter_reservations, null);
            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.show();

            RadioGroup radioGroup = bottomSheetDialog.findViewById(R.id.radio_group);

            if (filters != null) {
                switch (filters.status) {
                    case "NEW":
                        radioGroup.check(R.id.new1);
                        break;
                    case "PUP_REJECTED":
                        radioGroup.check(R.id.pup_rejected);
                        break;
                    case "ADMIN_REJECTED":
                        radioGroup.check(R.id.admin_rejected);
                        break;
                    case "ORGANIZER_REJECTED":
                        radioGroup.check(R.id.od_rejected);
                        break;
                    case "ACCEPTED":
                        radioGroup.check(R.id.accepted);
                        break;
                    case "REALIZED":
                        radioGroup.check(R.id.realized);
                        break;
                    case "ALL":
                        radioGroup.check(R.id.all);
                        break;
                }
            }

            RadioButton acceptedButton = bottomSheetDialog.findViewById(R.id.accepted);
            RadioButton newButton = bottomSheetDialog.findViewById(R.id.new1);
            RadioButton admRejectedButton = bottomSheetDialog.findViewById(R.id.admin_rejected);
            RadioButton pupRejectedButton = bottomSheetDialog.findViewById(R.id.pup_rejected);
            RadioButton odRejectedButton = bottomSheetDialog.findViewById(R.id.od_rejected);
            RadioButton realizedButton = bottomSheetDialog.findViewById(R.id.realized);
            Button btnApply = bottomSheetDialog.findViewById(R.id.apply_filters);
            btnApply.setOnClickListener(v3 -> {
                if (acceptedButton.isChecked()) filters.status = "ACCEPTED";
                else if (newButton.isChecked()) filters.status = "NEW";
                else if (admRejectedButton.isChecked()) filters.status = "ADMIN_REJECTED";
                else if (pupRejectedButton.isChecked()) filters.status = "PUP_REJECTED";
                else if (odRejectedButton.isChecked()) filters.status = "ORGANIZER_REJECTED";
                else if (realizedButton.isChecked()) filters.status = "REALIZED";
                else filters.status = "ALL";
                ArrayList<Reservation> filteredReservations = filterReservationsByStatus(filters.status);
                adapter.updateReservations(filteredReservations);
                bottomSheetDialog.dismiss();
            });
        });
    }

    private void setupSearch() {
        searchName = binding.searchText;

        searchName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchReservations(newText);
                return true;
            }
        });

        searchName.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter.updateReservations(allReservations);
                searchName.setQuery("", false);
                return false;
            }
        });
    }

    private void searchReservations(String newText) {
        ArrayList<Reservation> filteredReservations = new ArrayList<>();
        AtomicInteger latch = new AtomicInteger(mReservations.size());

        for (Reservation r : mReservations) {
            if (!r.getServiceId().isEmpty()) {
                fetchService(r, newText, filteredReservations, latch);
            } else if (!r.getPackageId().isEmpty()) {
                fetchPackage(r, newText, filteredReservations, latch);
            } else {
                latch.decrementAndGet();
            }
        }
        checkLatchAndUpdate(latch, filteredReservations);
    }

    private void fetchService(Reservation r, String newText, ArrayList<Reservation> filteredReservations, AtomicInteger latch) {
        ServiceRepo serviceRepo = new ServiceRepo();
        serviceRepo.getById(r.getServiceId(), new ServiceRepo.ServiceFetchCallback() {
            @Override
            public void onServiceObjectFetched(Service service, String errorMessage) {
                if (service != null && errorMessage == null) {
                    if (service.getName().toLowerCase().contains(newText.toLowerCase()) && !filteredReservations.contains(r)) {
                        filteredReservations.add(r);
                    }
                    if (isOwner) {
                        if(isEmployee(service.getAttendants(),newText) && !filteredReservations.contains(r))
                            filteredReservations.add(r);
                        if(isOrganizer(r.getOrganizerEmail(),newText) && !filteredReservations.contains(r)){
                            filteredReservations.add(r);
                        }
                    }else{
                        if(isOrganizer(r.getOrganizerEmail(),newText) && !filteredReservations.contains(r)){
                            filteredReservations.add(r);
                        }
                    }
                }
                checkLatchAndUpdate(latch,filteredReservations);
            }
        });
    }

    private void fetchPackage(Reservation r, String newText, ArrayList<Reservation> filteredReservations, AtomicInteger latch) {
        PackageRepo packageRepo = new PackageRepo();
        packageRepo.getById(r.getPackageId(), new PackageRepo.PackageFetchCallback() {
            @Override
            public void onPackageObjectFetched(Package p, String errorMessage) {
                if (p != null && errorMessage == null) {
                    AtomicInteger serviceLatch = new AtomicInteger(p.getServices().size());
                    for (String serviceId : p.getServices()) {
                        fetchService(r, newText, filteredReservations, serviceLatch);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private ArrayList<Reservation> filterReservationsByStatus(String status) {
        ArrayList<Reservation> filteredReservations = new ArrayList<>();

        if (mReservations != null) {
            for (Reservation reservation : mReservations) {
                if (reservation.getStatus().toString().equals(status)) {
                    filteredReservations.add(reservation);
                }
            }
            if (status.equals("ALL"))
                return mReservations;
        }
        return filteredReservations;
    }
    private void checkLatchAndUpdate(AtomicInteger latch, ArrayList<Reservation> filteredReservations) {
        if (latch.decrementAndGet() == 0) {
            getActivity().runOnUiThread(() -> {
                adapter.updateReservations(filteredReservations);
                adapter.notifyDataSetChanged();
            });
        }
    }
    private boolean isEmployee(List<String> employeeIds, String newText)
    {
        for(String id: employeeIds)
        {
            if(!newText.isEmpty())
            {
                for(Employee e: employees)
                {
                    if(e.getId().equals(id)){
                        if(e.getFirstName().toLowerCase().contains(newText.toLowerCase()) ||
                                e.getLastName().toLowerCase().contains(newText.toLowerCase()))
                            return true;
                    }
                }
            }else
                return true;
        }
        return false;
    }
    private boolean isOrganizer(String email, String newText)
    {
        if(!newText.isEmpty())
        {
            for(Organizer o: organizers)
            {
                if(o.getEmail().equals(email)){
                    if(o.getFirstName().toLowerCase().contains(newText.toLowerCase()) ||
                            o.getLastName().toLowerCase().contains(newText.toLowerCase()))
                        return true;
                }
            }
        }else
            return true;
        return false;
    }

}