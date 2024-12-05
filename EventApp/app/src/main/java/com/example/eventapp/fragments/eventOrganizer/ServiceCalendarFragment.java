package com.example.eventapp.fragments.eventOrganizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.eventapp.adapters.events.CalendarUtils.monthYearFromDate;

import static com.example.eventapp.adapters.events.CalendarUtils.daysInMonthArray;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.R;
import com.example.eventapp.adapters.CalendarSlotsListAdapter;
import com.example.eventapp.adapters.OwnerRequestsListAdapter;
import com.example.eventapp.adapters.events.CalendarAdapter;
import com.example.eventapp.adapters.events.CalendarUtils;
import com.example.eventapp.databinding.FragmentEventsForServiceReservationBinding;
import com.example.eventapp.databinding.FragmentServiceCalendarBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.CalendarSlot;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.NotificationStatus;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.ReservationStatus;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.model.WeeklyEvent;
import com.example.eventapp.model.WorkingTime;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.OrganizerRepo;
import com.example.eventapp.repositories.ReservationRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.UserRepo;
import com.example.eventapp.repositories.WeeklyEventRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ServiceCalendarFragment extends  DialogFragment {
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private CalendarSlotsListAdapter adapter;
    private Service service;
    private Event event;
    private Employee employee;
    private String startTime;
    private String endTime;
    private ArrayList<Reservation> serviceReservations;
    private ArrayList<Service> allServices;
    private ArrayList<CalendarSlot> slots;
    private ArrayList<WeeklyEvent> weeklyEvents;

    private FragmentServiceCalendarBinding binding;
    public ServiceCalendarFragment() {
        // Required empty public constructor
    }

    public static ServiceCalendarFragment newInstance(Service serviceToSend, Event eventToSend, Employee employeeToSend, String startTime, String endTime) {
        ServiceCalendarFragment fragment = new ServiceCalendarFragment();
        Bundle args = new Bundle();
        args.putParcelable("SERVICE", serviceToSend);
        args.putParcelable("EVENT", eventToSend);
        args.putParcelable("EMPLOYEE", employeeToSend);
        args.putString("START_TIME", startTime);
        args.putString("END_TIME", endTime);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            service = getArguments().getParcelable("SERVICE");
            event = getArguments().getParcelable("EVENT");
            employee = getArguments().getParcelable("EMPLOYEE");
            startTime = getArguments().getString("START_TIME"); //radno vrijeme employeea
            endTime = getArguments().getString("END_TIME");     //radno vrijeme employeea
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentServiceCalendarBinding.inflate(inflater, container, false);

        ReservationRepo.getNotFree(event.getDate(), employee.getId(), new ReservationRepo.ReservationFetchCallback() {
            @Override
            public void onReservationFetch(ArrayList<Reservation> reservations) {
                ReservationRepo.ReservationFetchCallback.super.onReservationFetch(reservations);
                serviceReservations = reservations;
                ServiceRepo serviceRepo = new ServiceRepo();
                serviceRepo.getAllServices(new ServiceRepo.ServiceFetchCallback() {
                    @Override
                    public void onServiceFetch(ArrayList<Service> services) {
                        allServices = services;
                        WeeklyEventRepo weeklyEventRepo = new WeeklyEventRepo();
                        weeklyEventRepo.getAll(new WeeklyEventRepo.WeeklyEventFetchCallback() {
                            @Override
                            public void onWeeklyEventFetch(ArrayList<WeeklyEvent> events) {
                                weeklyEvents = new ArrayList<>();
                                for (WeeklyEvent weeklyEvent : events) {
                                    if (weeklyEvent.getEmployeeId().equals(employee.getId()) && areDatesEqual(weeklyEvent.getDate(), event.getDate().toString()) && weeklyEvent.getEventType().equals(WeeklyEvent.EventType.BUSY)) {
                                        weeklyEvents.add(weeklyEvent);
                                    }
                                }
                                generateSlots();
                                ListView listView = binding.list;
                                CalendarSlotsListAdapter adapter = new CalendarSlotsListAdapter(getActivity(), slots, service, event, employee);
                                listView.setAdapter(adapter);

                            }
                        });



                    }

                });


            }
        });

        View view = binding.getRoot();
        return view;
    }

    public boolean areDatesEqual(String date1, String date2) {
        // Format za prvi datum "yyyy-MM-dd"
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

        // Format za drugi datum "MMMM d, yyyy 'at' h:mm:ss a z"
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
        try {
            // Parsirajte prvi datum
            Date parsedDate1 = simpleDateFormat1.parse(date1);

            // Parsirajte drugi datum
            Date parsedDate2 = simpleDateFormat2.parse(date2);

            // Formatirajte oba datuma u "yyyy-MM-dd"
            String formattedDate1 = simpleDateFormat1.format(parsedDate1);
            String formattedDate2 = simpleDateFormat1.format(parsedDate2);

            // Uporedite formatirane datume
            return formattedDate1.equals(formattedDate2);
        } catch (ParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            return false;
        }
    }
    private CalendarSlot createSlot(String fromTime, String toTime, String status, String name) {
        CalendarSlot slot = new CalendarSlot();
        slot.setFromTime(fromTime);
        slot.setToTime(toTime);
        slot.setStatus(status);
        slot.setName(name);
        return slot;
    }
    public ArrayList<CalendarSlot> generateSlots() {
        slots = new ArrayList<>();

        LocalTime start = LocalTime.parse(startTime, timeFormatter);
        LocalTime end = LocalTime.parse(endTime, timeFormatter);

        ArrayList<CalendarSlot> reservedSlots = new ArrayList<>();

        // Convert reservations and weekly events into CalendarSlot objects
        for (Reservation reservation : serviceReservations) {
            LocalTime reservationStart = LocalTime.parse(reservation.getFromTime(), timeFormatter);
            LocalTime reservationEnd = LocalTime.parse(reservation.getToTime(), timeFormatter);
            if (!reservationStart.isBefore(start) && !reservationEnd.isAfter(end)) {
                reservedSlots.add(createSlot(reservationStart.toString(), reservationEnd.toString(), "RESERVED", "Service reservation"));
            }
        }

        for (WeeklyEvent weeklyEvent : weeklyEvents) {
            LocalTime eventStart = LocalTime.parse(weeklyEvent.getFrom(), timeFormatter);
            LocalTime eventEnd = LocalTime.parse(weeklyEvent.getTo(), timeFormatter);
            if (!eventStart.isBefore(start) && !eventEnd.isAfter(end)) {
                reservedSlots.add(createSlot(eventStart.toString(), eventEnd.toString(), "RESERVED", weeklyEvent.getName()));
            }
        }

        // Sort reserved slots by start time
        Collections.sort(reservedSlots, Comparator.comparing(slot -> LocalTime.parse(slot.getFromTime(), timeFormatter)));

        // Generate free and reserved slots
        LocalTime currentTime = start;
        for (CalendarSlot reservedSlot : reservedSlots) {
            LocalTime reservedStart = LocalTime.parse(reservedSlot.getFromTime(), timeFormatter);
            LocalTime reservedEnd = LocalTime.parse(reservedSlot.getToTime(), timeFormatter);

            if (currentTime.isBefore(reservedStart)) {
                slots.add(createSlot(currentTime.toString(), reservedStart.toString(), "FREE", "FREE"));
            }
            slots.add(reservedSlot);
            currentTime = reservedEnd;
        }

        if (currentTime.isBefore(end)) {
            slots.add(createSlot(currentTime.toString(), end.toString(), "FREE", "FREE"));
        }

        return slots;
    }



}
