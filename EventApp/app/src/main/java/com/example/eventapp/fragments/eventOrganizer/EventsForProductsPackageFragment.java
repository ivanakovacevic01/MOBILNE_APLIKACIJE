package com.example.eventapp.fragments.eventOrganizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.databinding.FragmentEventsForProductsPackageBinding;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.EventBudgetItem;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.ItemType;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.ReservationStatus;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.SubcategorySuggestion;
import com.example.eventapp.model.SubcategoryType;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.EventBudgetItemRepo;
import com.example.eventapp.repositories.EventBudgetRepo;
import com.example.eventapp.repositories.EventRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.FirmRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.PackageRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ReservationRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.example.eventapp.repositories.SubcategorySuggestionsRepo;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;


public class EventsForProductsPackageFragment extends DialogFragment {

    ArrayList<Event> eventsToChoose;   //sve subkategorije
    private FragmentEventsForProductsPackageBinding binding;
    private ArrayList<String> ProductIds;
    private Event selectedEvent;
    private String PackageId;
    private String firmId;

    public EventsForProductsPackageFragment() {
        // Required empty public constructor
    }

    public static EventsForProductsPackageFragment newInstance(ArrayList<String> productIds, String packageId) {
        EventsForProductsPackageFragment fragment = new EventsForProductsPackageFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("PRODUCT_ID_RES", productIds);
        args.putString("PACKAGE_ID_RES", packageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ProductIds = getArguments().getStringArrayList("PRODUCT_ID_RES");
            PackageId = getArguments().getString("PACKAGE_ID_RES");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEventsForProductsPackageBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        //tabela sa eventovima od organizatora koji kupuje, i da im datumi nisu u proslosti
        eventsToChoose = new ArrayList<>();

        PackageRepo packageRepo = new PackageRepo();
        packageRepo.getById(PackageId, new PackageRepo.PackageFetchCallback() {
            @Override
            public void onPackageObjectFetched(Package p, String errorMessage) {
                if (p != null && errorMessage == null) {
                    firmId = p.getFirmId();

                    EventRepo eventRepo = new EventRepo();
                    eventRepo.getByOrganizer(new EventRepo.EventFetchCallback() {
                        @Override
                        public void onEventFetch(ArrayList<Event> events) {
                            if(events != null) {
                                eventsToChoose = events;
                                eventsToChoose.removeIf(s -> s.getDate().before(new Date())); //nudim samo evente za buducnost


                                TableLayout eventsTable = view.findViewById(R.id.eventsTable);
                                for (Event event : eventsToChoose) {
                                    TableRow row = new TableRow(getContext());
                                    TextView textView = new TextView(getContext());
                                    textView.setText(event.getName());

                                    TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                                    textView.setLayoutParams(params);
                                    row.addView(textView);

                                    Button addButton = new Button(getContext());
                                    addButton.setText("Add");
                                    addButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("Event");
                                            builder.setMessage("Are you sure you want to choose this event?")
                                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            selectedEvent = event;
                                                            ReservationRepo.createReservations(createNewReservations(), new ReservationRepo.ReservationFetchCallback() {
                                                                @Override
                                                                public void onReservationObjectsFetched(ArrayList<Reservation> reservations, String errorMessage) {
                                                                    if (getActivity() != null) {
                                                                        getActivity().runOnUiThread(() -> {
                                                                            if (errorMessage == null) {
                                                                                Toast.makeText(getContext(), "Successfully reserved.", Toast.LENGTH_SHORT).show();
                                                                                dismiss();
                                                                            } else {
                                                                                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                                    }




                                                                }
                                                            });
                                                        }
                                                    })
                                                    .setNegativeButton("No", null);


                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    });
                                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    buttonLayoutParams.setMargins(0, 10, 0, 10);
                                    addButton.setLayoutParams(buttonLayoutParams);
                                    addButton.setBackgroundColor(getResources().getColor(R.color.add_green));
                                    row.addView(addButton);
                                    eventsTable.addView(row);

                                }


                            }

                        }

                    }, SharedPreferencesManager.getEmail(getContext()));


                }
            }});



        return view;
    }

    private ArrayList<Reservation> createNewReservations() {
        ArrayList<Reservation> reservations = new ArrayList<>();
        for(String id: ProductIds) {
            Reservation reservation = new Reservation();
            reservation.setStatus(ReservationStatus.ACCEPTED);
            reservation.setType(ItemType.Package);
            reservation.setProductId(id);
            reservation.setPackageId(PackageId);
            reservation.setServiceId("");
            reservation.setCreatedDate((new Date()).toString());
            reservation.setOrganizerEmail(SharedPreferencesManager.getEmail(getContext()));
            reservation.setEventId(selectedEvent.getId());
            reservation.setEmployees(new ArrayList<>());
            reservation.setEventDate(selectedEvent.getDate());
            reservation.setFirmId(firmId);
            reservations.add(reservation);
        }
        //rezervacija samog paketa
        Reservation fullPackage = new Reservation();
        fullPackage.setStatus(ReservationStatus.ACCEPTED);
        fullPackage.setType(ItemType.Package);
        fullPackage.setProductId("");
        fullPackage.setPackageId(PackageId);
        fullPackage.setServiceId("");
        fullPackage.setCreatedDate((new Date()).toString());
        fullPackage.setOrganizerEmail(SharedPreferencesManager.getEmail(getContext()));
        fullPackage.setEventId(selectedEvent.getId());
        fullPackage.setEmployees(new ArrayList<>());
        fullPackage.setEventDate(selectedEvent.getDate());
        fullPackage.setFirmId(firmId);
        reservations.add(fullPackage);


        return reservations;
    }
}