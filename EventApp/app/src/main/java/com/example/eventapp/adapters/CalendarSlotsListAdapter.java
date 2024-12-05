package com.example.eventapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.fragments.administration.QwnerRequestDetailsFragment;
import com.example.eventapp.fragments.administration.RejectRequestReasonFragment;
import com.example.eventapp.fragments.eventOrganizer.EventsForServiceReservationFragment;
import com.example.eventapp.fragments.reservations.ServiceReservationTimeFragment;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.CalendarSlot;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.LinkExpiration;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.OwnerRequestStatus;
import com.example.eventapp.model.RegistrationOwnerRequest;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.LinkExpirationRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.RegistrationOwnerRequestRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class CalendarSlotsListAdapter extends ArrayAdapter<CalendarSlot> {
    private ArrayList<CalendarSlot> aSlots;
    private TextView status;
    private TextView name;
    private TextView fromToTime;

    private Service service;
    private Event event;
    private Employee employee;

    public CalendarSlotsListAdapter(Context context, ArrayList<CalendarSlot> slots, Service serviceToSend, Event eventToSend, Employee employeeToSend) {
        super(context, R.layout.calendar_slot_card, slots);
        aSlots = slots;
        service = serviceToSend;
        event = eventToSend;
        employee = employeeToSend;
    }
    @Override
    public int getCount() {
        return aSlots.size();
    }


    @Nullable
    @Override
    public CalendarSlot getItem(int position) {
        return aSlots.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        CalendarSlot slot = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.calendar_slot_card,
                    parent, false);
        }
        ConstraintLayout clotCard = convertView.findViewById(R.id.calendar_slot_item);
        status = convertView.findViewById(R.id.status);
        fromToTime = convertView.findViewById(R.id.slot_time);
        name = convertView.findViewById(R.id.slot_name);
        if (slot != null) {
            status.setText(slot.getStatus());
            fromToTime.setText(slot.getFromTime() + " - " + slot.getToTime());
            name.setText(slot.getName());
            if ("free".equalsIgnoreCase(slot.getStatus())) {
                clotCard.setBackgroundColor(getContext().getResources().getColor(R.color.free));
            } else {
                // Ako slot nije slobodan, ostavljamo pozadinu kakva jeste
                clotCard.setBackgroundColor(getContext().getResources().getColor(R.color.not_free));
            }
        }
        clotCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("free".equalsIgnoreCase(slot.getStatus())) {
                    // Perform the desired action when the slot is free
                    // For example, display a toast or navigate to another activity/fragment
                    String fromTime = slot.getFromTime();
                    String toTime = slot.getToTime();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    LocalTime from = LocalTime.parse(fromTime, formatter);
                    LocalTime to = LocalTime.parse(toTime, formatter);

                    long slotDurationInMinutes = java.time.Duration.between(from, to).toMinutes();



                    if(service.getDuration()!=-1)//ima to
                    {
                        double serviceDurationInMinutes = service.getDuration() * 60;

                        if (serviceDurationInMinutes <= slotDurationInMinutes) {
                            FragmentManager fragmentManager = ((HomeActivity) getContext()).getSupportFragmentManager();
                            ServiceReservationTimeFragment dialog = ServiceReservationTimeFragment.newInstance(slot, service, event, employee);
                            dialog.show(fragmentManager, "ServiceReservationTimeFragment");



                    } else {
                            Toast.makeText(getContext(), "Service duration exceeds the slot time.", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else //ima min i max angazovanje
                    {
                        double minEngagementInMinutes = service.getMinDuration() * 60;
                        double maxEngagementInMinutes = service.getMaxDuration() * 60;
                        if (minEngagementInMinutes <= slotDurationInMinutes && maxEngagementInMinutes <= slotDurationInMinutes) {

                            FragmentManager fragmentManager = ((HomeActivity) getContext()).getSupportFragmentManager();
                            ServiceReservationTimeFragment dialog = ServiceReservationTimeFragment.newInstance(slot, service, event, employee);
                            dialog.show(fragmentManager, "ServiceReservationTimeFragment");

                        } else {
                            Toast.makeText(getContext(), "Engagement time exceeds the slot time.", Toast.LENGTH_SHORT).show();
                        }
                    }


                    // Example of starting a new activity or fragment:
                    // Intent intent = new Intent(getContext(), DesiredActivity.class);
                    // getContext().startActivity(intent);

                    // Example of navigating to a fragment (if in a FragmentActivity):
                    // FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                    // fragmentManager.beginTransaction()
                    //         .replace(R.id.fragment_container, new DesiredFragment())
                    //         .addToBackStack(null)
                    //         .commit();
                } else {
                    Toast.makeText(getContext(), "Slot is not available.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return convertView;
    }



}