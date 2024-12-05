package com.example.eventapp.fragments.reservations;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.adapters.ReservationListAdapter;
import com.example.eventapp.adapters.services.ServiceRecyclerViewAdapter;
import com.example.eventapp.databinding.FragmentReservationDetailsBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.employees.FirmProfileFragment;
import com.example.eventapp.model.Firm;
import com.example.eventapp.fragments.UserProfile;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.Service;
import com.example.eventapp.repositories.FirmRepo;
import com.example.eventapp.repositories.OrganizerRepo;
import com.example.eventapp.repositories.PackageRepo;
import com.example.eventapp.repositories.ReservationRepo;
import com.example.eventapp.repositories.ServiceRepo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservationDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservationDetailsFragment extends ListFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentReservationDetailsBinding binding;
    private ServiceRecyclerViewAdapter adapter;
    private Reservation reservation;
    private ArrayList<Service> reservationServices;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReservationDetailsFragment() {
        // Required empty public constructor
    }


    public static ReservationDetailsFragment newInstance(Reservation reservation) {
        ReservationDetailsFragment fragment = new ReservationDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("RESERVATION", reservation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReservationDetailsBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        reservationServices=new ArrayList<>();
        TextView status=binding.status;
        TextView reservedBy=binding.reservedBy;
        TextView createdDate=binding.createdDate;
        TextView companyName = binding.companyName;
        reservation=new Reservation();
        if(getArguments()!=null)
            reservation = getArguments().getParcelable("RESERVATION");
        if(reservation!=null) {
            status.setText(reservation.getStatus().toString());
            createdDate.setText(formatDateString(reservation.getCreatedDate()));
            OrganizerRepo.getByEmail(reservation.getOrganizerEmail(), new OrganizerRepo.OrganizerFetchCallback() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onOrganizerObjectFetched(Organizer organizer, String errorMessage) {
                    OrganizerRepo.OrganizerFetchCallback.super.onOrganizerObjectFetched(organizer, errorMessage);

                    if(organizer!=null){
                        reservedBy.setText(organizer.getFirstName()+" "+organizer.getLastName());
                    }

                    reservedBy.setOnClickListener(v->{
                        FragmentTransition.to(UserProfile.newInstance(organizer.getEmail()), getActivity(),
                                true, R.id.scroll_reservations_list);
                    });
                    if(!reservation.getServiceId().isEmpty() && reservation.getPackageId().isEmpty())
                        setServices();
                    else if(!reservation.getPackageId().isEmpty() && reservation.getServiceId().isEmpty())
                        setServiceReservations();
                    setFirm(reservation.getFirmId(),companyName);
                }
            });
        }
        if(SharedPreferencesManager.getUserRole(getContext())!=null && !SharedPreferencesManager.getUserRole(getContext()).equals("OWNER")){
            companyName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransition.to(FirmProfileFragment.newInstance(reservation.getFirmId(),reservation.getStatus().toString()),getActivity(),true, R.id.scroll_reservations_list);
                }
            });
        }

        return root;
    }
    private void setServices(){
        ServiceRepo serviceRepo=new ServiceRepo();
        reservationServices=new ArrayList<>();
        serviceRepo.getAllServices(new ServiceRepo.ServiceFetchCallback() {
            @Override
            public void onServiceFetch(ArrayList<Service> services) {
                if (services != null) {
                    if(!reservation.getServiceId().isEmpty()){
                        for(Service s:services){
                            if(s.getId().equals(reservation.getServiceId()))
                                reservationServices.add(s);
                        }
                    }
                    else if(!reservation.getPackageId().isEmpty()){
                        PackageRepo packageRepo=new PackageRepo();
                        packageRepo.getById(reservation.getPackageId(), new PackageRepo.PackageFetchCallback() {
                            @Override
                            public void onPackageObjectFetched(Package p, String errorMessage) {
                                PackageRepo.PackageFetchCallback.super.onPackageObjectFetched(p, errorMessage);
                                for(Service s:services){
                                    for(String id:p.getServices()){
                                        if(s.getId().equals(id) && !reservationServices.contains(s)){
                                            reservationServices.add(s);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
                RecyclerView recyclerView = binding.recyclerView;
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new ServiceRecyclerViewAdapter(getActivity(), reservationServices);
                recyclerView.setAdapter(adapter);

            }
        });
    }
    private String formatDateString(String dateString) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        try {
            Date date = originalFormat.parse(dateString);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }
    private void setFirm(String id,TextView companyName){
        FirmRepo firmRepo=new FirmRepo();
        firmRepo.getById(id, new FirmRepo.FirmFetchCallback() {
            @Override
            public void onFirmFetch(ArrayList<Firm> firms) {
                FirmRepo.FirmFetchCallback.super.onFirmFetch(firms);
                companyName.setText(firms.get(0).getName());
            }
        });
    }

    private void setServiceReservations(){
        ReservationRepo.getServiesByPackageId(reservation.getPackageId(),new ReservationRepo.ReservationFetchCallback() {
            @Override
            public void onReservationFetch(ArrayList<Reservation> storedReservations) {
                if(storedReservations!=null){
                    ReservationListAdapter adapter = new ReservationListAdapter(getContext(), storedReservations, (AppCompatActivity) getActivity());
                    setListAdapter(adapter);
                }
            }
        });
    }

}