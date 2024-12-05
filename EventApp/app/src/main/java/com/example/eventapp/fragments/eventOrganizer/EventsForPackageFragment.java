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
import com.example.eventapp.databinding.FragmentEventsForPackageBinding;
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


public class EventsForPackageFragment extends DialogFragment {

    ArrayList<Event> eventsToChoose;   //sve subkategorije
    private FragmentEventsForPackageBinding binding;
    private ArrayList<String> ProductIds;
    private Event selectedEvent;
    public interface EventSelectionListener {
        void onEventSelected(Event selectedEvent);
    }

    public EventSelectionListener listener;

    public static EventsForPackageFragment newInstance() {
        EventsForPackageFragment fragment = new EventsForPackageFragment();

        return fragment;
    }

    public EventsForPackageFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ProductIds = getArguments().getStringArrayList("PRODUCT_ID_RES");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEventsForPackageBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        //tabela sa eventovima od organizatora koji kupuje, i da im datumi nisu u proslosti
        eventsToChoose = new ArrayList<>();
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
                                                if (listener != null) {
                                                    listener.onEventSelected(selectedEvent);
                                                }
                                                Toast.makeText(getContext(), "Event selected.", Toast.LENGTH_SHORT).show();
                                                dismiss();
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



        return view;
    }


}