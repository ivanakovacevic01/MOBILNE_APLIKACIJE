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
import com.example.eventapp.activities.MainActivity;
import com.example.eventapp.adapters.eventOrganizer.EventListAdapter;
import com.example.eventapp.databinding.FragmentEventsForProductReservationBinding;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.EventBudgetItem;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.ItemType;
import com.example.eventapp.model.Notification;
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
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ReservationRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.example.eventapp.repositories.SubcategorySuggestionsRepo;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;


public class EventsForProductReservationFragment extends DialogFragment {

    ArrayList<Event> eventsToChoose;   //sve subkategorije
    private FragmentEventsForProductReservationBinding binding;
    private String ProductId;
    private Event selectedEvent;
    private Product chosen;

    public EventsForProductReservationFragment() {
        // Required empty public constructor
    }

    public static EventsForProductReservationFragment newInstance(String productId) {
        EventsForProductReservationFragment fragment = new EventsForProductReservationFragment();
        Bundle args = new Bundle();
        args.putString("PRODUCT_ID_RES", productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ProductId = getArguments().getString("PRODUCT_ID_RES");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEventsForProductReservationBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        //tabela sa eventovima od organizatora koji kupuje, i da im datumi nisu u proslosti
        eventsToChoose = new ArrayList<>();
        ProductRepo productRepo = new ProductRepo();
        productRepo.getById(new ProductRepo.ProductFetchCallback() {
            @Override
            public void onProductFetch(ArrayList<Product> products) {
                ProductRepo.ProductFetchCallback.super.onProductFetch(products);
                chosen = products.get(0);
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
                                                        ReservationRepo.createReservation(createNewReservation(), new ReservationRepo.ReservationFetchCallback() {
                                                            @Override
                                                            public void onReservationObjectFetched(Reservation reservation, String errorMessage) {
                                                                if (reservation != null) {
                                                                    //add to budgeting
                                                                    EventBudgetRepo eventBudgetRepo = new EventBudgetRepo();
                                                                    eventBudgetRepo.getByEventId(new EventBudgetRepo.EventBudgetFetchCallback() {
                                                                        @Override
                                                                        public void onEventBudgetFetch(ArrayList<EventBudget> budgets) {
                                                                        }

                                                                        @Override
                                                                        public void onEventBudgetFetchByEvent(EventBudget budget) {

                                                                            ProductRepo productRepo = new ProductRepo();
                                                                            productRepo.getById(new ProductRepo.ProductFetchCallback() {
                                                                                @Override
                                                                                public void onProductFetch(ArrayList<Product> products) {
                                                                                    ProductRepo.ProductFetchCallback.super.onProductFetch(products);
                                                                                    EventBudgetItemRepo itemRepo = new EventBudgetItemRepo();
                                                                                    itemRepo.getByBudgetAndSubcategory(new EventBudgetItemRepo.EventBudgetItemFetchCallback() {
                                                                                        @Override
                                                                                        public void onEventBudgetItemFetch(ArrayList<EventBudgetItem> budgets) {
                                                                                            if(budgets == null || budgets.isEmpty())
                                                                                            {
                                                                                                EventBudgetItem item = new EventBudgetItem();
                                                                                                item.setPlannedBudget(0);
                                                                                                ArrayList<String>ids = new ArrayList<>();
                                                                                                ids.add(ProductId);
                                                                                                item.setItemsIds(ids);
                                                                                                item.setSubcategoryId(products.get(0).getSubcategory());

                                                                                                itemRepo.create(new EventBudgetItemRepo.EventBudgetItemFetchCallback() {
                                                                                                    @Override
                                                                                                    public void onEventBudgetItemFetch(ArrayList<EventBudgetItem> budgets) {
                                                                                                        budget.getEventBudgetItemsIds().add(budgets.get(0).getId());
                                                                                                        EventBudgetRepo.update(budget);
                                                                                                    }
                                                                                                }, item);
                                                                                            }else{
                                                                                                EventBudgetItem item = budgets.get(0);
                                                                                                item.getItemsIds().add(ProductId);
                                                                                                EventBudgetItemRepo.update(item);
                                                                                            }
                                                                                        }
                                                                                    }, budget, products.get(0).getSubcategory());
                                                                                }
                                                                            }, ProductId);

                                                                        }
                                                                    }, event.getId());



                                                                    Toast.makeText(getContext(), "Successfully reserved.", Toast.LENGTH_SHORT).show();

                                                                    dismiss();

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

            }}, ProductId);





        return view;
    }

    private Reservation createNewReservation() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.ACCEPTED);
        reservation.setType(ItemType.Product);
        reservation.setProductId(ProductId);
        reservation.setPackageId("");
        reservation.setServiceId("");
        reservation.setCreatedDate((new Date()).toString());
        reservation.setOrganizerEmail(SharedPreferencesManager.getEmail(getContext()));
        reservation.setEventId(selectedEvent.getId());
        reservation.setEmployees(new ArrayList<>());
        reservation.setEventDate(selectedEvent.getDate());
        reservation.setFirmId(chosen.getFirmId());
        return reservation;
    }
}