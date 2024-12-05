package com.example.eventapp.fragments.reservations;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.databinding.FragmentEventsForServiceReservationBinding;
import com.example.eventapp.databinding.FragmentServiceReservationTimeBinding;
import com.example.eventapp.fragments.eventOrganizer.EmployeesForPackageReservationFragment;
import com.example.eventapp.fragments.eventOrganizer.EventsForPackageFragment;
import com.example.eventapp.fragments.eventOrganizer.EventsForServiceReservationFragment;
import com.example.eventapp.fragments.eventOrganizer.PackageCalendarFragment;
import com.example.eventapp.fragments.eventOrganizer.ServiceCalendarFragment;
import com.example.eventapp.fragments.packages.PackageDetailsFragment;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.CalendarSlot;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.EventBudgetItem;
import com.example.eventapp.model.ItemType;

import com.example.eventapp.model.Notification;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.ReservationStatus;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.ShowStatus;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.NotificationRepo;

import com.example.eventapp.model.Product;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.ReservationStatus;
import com.example.eventapp.model.Service;
import com.example.eventapp.repositories.EventBudgetItemRepo;
import com.example.eventapp.repositories.EventBudgetRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ReservationRepo;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class PackageReservationTimeFragment extends DialogFragment {

    private FragmentServiceReservationTimeBinding binding;
    private Service service;
    private CalendarSlot slot;
    private Event event;
    private Employee employee;
    private TimePicker toTimePicker;
    private TimePicker fromTimePicker;
    private Button submitButton;
    private FirebaseAuth mAuth;
    private String PackageId;

    public interface ReservationSelectionListener {
        void onReservationSelected(Reservation selectedReservation);
    }

    public PackageReservationTimeFragment.ReservationSelectionListener listener;
    private static PackageDetailsFragment packageDetailsFragment;


    public PackageReservationTimeFragment() {
        // Required empty public constructor
    }


    public static PackageReservationTimeFragment newInstance(CalendarSlot slotToSend, Service serviceToSend, Event eventToSend, Employee employeeToSend, String packageId, PackageDetailsFragment fragmentToSend) {
        PackageReservationTimeFragment fragment = new PackageReservationTimeFragment();
        packageDetailsFragment = fragmentToSend;
        Bundle args = new Bundle();
        args.putParcelable("SERVICE_ID_RES", serviceToSend);
        args.putParcelable("SLOT_ID_RES", slotToSend);
        args.putParcelable("EVENT_ID_RES", eventToSend);
        args.putParcelable("EMPLOYEE_ID_RES", employeeToSend);
        args.putString("PACK", packageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            service = getArguments().getParcelable("SERVICE_ID_RES");
            slot = getArguments().getParcelable("SLOT_ID_RES");
            event = getArguments().getParcelable("EVENT_ID_RES");
            employee = getArguments().getParcelable("EMPLOYEE_ID_RES");
            PackageId = getArguments().getString("PACK");
        }
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentServiceReservationTimeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        fromTimePicker = binding.timePickerFrom;
        toTimePicker = binding.timePickerTo;
        fromTimePicker.setIs24HourView(true);
        toTimePicker.setIs24HourView(true);
        LocalTime slotStartTime = LocalTime.parse(slot.getFromTime());
        LocalTime slotEndTime = LocalTime.parse(slot.getToTime());
        fromTimePicker.setHour(slotStartTime.getHour());
        fromTimePicker.setMinute(slotStartTime.getMinute());
        toTimePicker.setHour(slotEndTime.getHour());
        toTimePicker.setMinute(slotEndTime.getMinute());



        if(service.getDuration()!=-1)   //ima trajanje, disablovati toTimePicker jer se automatski racuna
        {
            binding.textViewToLabel.setVisibility(View.GONE);
            toTimePicker.setVisibility(View.GONE);
        }
        else
        {
            toTimePicker.setVisibility(View.VISIBLE);
            binding.textViewToLabel.setVisibility(View.VISIBLE);
            toTimePicker.setEnabled(true);
        }

        fromTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // Učitaj trenutne sate i minute iz fromTimePicker-a

                if(service.getDuration()!=-1) {
                    int fromHour = fromTimePicker.getHour();
                    int fromMinute = fromTimePicker.getMinute();

                    double duration = service.getDuration();
                    int hours = (int) duration;
                    int minutes = (int) ((duration - hours) * 60);

                    LocalTime fromTime = LocalTime.of(fromHour, fromMinute);
                    LocalTime toTime = fromTime.plusHours(hours).plusMinutes(minutes);

                    toTimePicker.setHour(toTime.getHour());
                    toTimePicker.setMinute(toTime.getMinute());
                }

            }
        });
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        submitButton = binding.submitTimeButton;
        submitButton.setOnClickListener(v -> {
            int fromHour = fromTimePicker.getHour();
            int fromMinute = fromTimePicker.getMinute();
            int toHour = toTimePicker.getHour();
            int toMinute = toTimePicker.getMinute();

            if (fromHour < toHour || (fromHour == toHour && fromMinute < toMinute)) {
                boolean valid = false;

                LocalTime fromTime = LocalTime.of(fromHour, fromMinute);
                LocalTime toTime = LocalTime.of(toHour, toMinute);

                if (fromTime.isAfter(slotStartTime) || fromTime.equals(slotStartTime)) {
                    if (toTime.isBefore(slotEndTime) || toTime.equals(slotEndTime)) {
                        long duration = java.time.Duration.between(fromTime, toTime).toMinutes();
                        long slotDuration = java.time.Duration.between(slotStartTime, slotEndTime).toMinutes();

                        if (duration <= slotDuration) {

                            //jos provjera za duration i minmax
                            if(service.getDuration() != -1){
                                double serviceDurationInMinutes = service.getDuration() * 60;
                                if (serviceDurationInMinutes <= slotDuration && serviceDurationInMinutes == duration) {

                                    //ok
                                    valid = true;



                                } else {
                                    Toast.makeText(getContext(), "Service duration exceeds the slot or selected time.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                double minEngagementInMinutes = service.getMinDuration() * 60;
                                double maxEngagementInMinutes = service.getMaxDuration() * 60;
                                if (minEngagementInMinutes <= slotDuration && maxEngagementInMinutes <= slotDuration && minEngagementInMinutes <= duration && maxEngagementInMinutes <= duration) {

                                    //ok
                                    valid = true;


                                } else {
                                    Toast.makeText(getContext(), "Engagement time exceeds the slot or selected time.", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
                            Toast.makeText(getContext(), "Invalid term. Maybe too long term.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Termin nije unutar slota
                        Toast.makeText(getContext(), "Term is out of slot.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Termin nije unutar slota
                    Toast.makeText(getContext(), "Term is out of slot.", Toast.LENGTH_SHORT).show();
                }

                if(valid) {
                    //provjera roka za rezervaciju
                    // Dobijanje trenutnog kalendarskog datuma i vremena
                    Calendar currentCalendar = Calendar.getInstance();

                    Date reservationDate = event.getDate();
                    Calendar reservationCalendar = Calendar.getInstance();
                    reservationCalendar.setTime(reservationDate);
                    reservationCalendar.set(Calendar.HOUR_OF_DAY, fromTimePicker.getHour());
                    reservationCalendar.set(Calendar.MINUTE, fromTimePicker.getMinute());

                    double deadline = service.getReservationDeadline();
                    Calendar deadlineCalendar = (Calendar) reservationCalendar.clone();
                    deadlineCalendar.add(Calendar.HOUR_OF_DAY, -(int) deadline);

                    if (currentCalendar.compareTo(deadlineCalendar) >= 0) {
                        Toast.makeText(getContext(), "Reservation deadline expired.", Toast.LENGTH_SHORT).show();
                    } else {
                        //kreiram rezervaciju
                        Reservation newRes = createNewReservation();
                        listener.onReservationSelected(newRes);
                        //Toast.makeText(getContext(), "Successfully reserved.", Toast.LENGTH_SHORT).show();

                        // U unutrasnjem fragmentu
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        List<Fragment> fragments = fragmentManager.getFragments();
                        for (Fragment fragment : fragments) {
                            if (fragment instanceof PackageCalendarFragment) {
                                ((PackageCalendarFragment) fragment).dismiss();
                            }
                            if (fragment instanceof EmployeesForPackageReservationFragment) {
                                ((EmployeesForPackageReservationFragment) fragment).dismiss();
                            }
                        }
                        dismiss();





                    }




                }
            } else {
                Toast.makeText(getContext(), "Start time must be before end time.", Toast.LENGTH_SHORT).show();
            }


        });



        return view;
    }

    private Reservation createNewReservation() {    //ovo za usluge paketa
        Reservation reservation = new Reservation();
        reservation.setFirmId(service.getFirmId());
        reservation.setStatus(ReservationStatus.NEW);
        reservation.setType(ItemType.Package);
        reservation.setProductId("");
        reservation.setPackageId(PackageId);
        reservation.setServiceId(service.getId());
        String fromHour = String.format("%02d", fromTimePicker.getHour());
        String fromMinute = String.format("%02d", fromTimePicker.getMinute());
        reservation.setFromTime(fromHour + ":" + fromMinute);
        String toHour = String.format("%02d", toTimePicker.getHour());
        String toMinute = String.format("%02d", toTimePicker.getMinute());
        reservation.setToTime(toHour + ":" + toMinute);
        reservation.setCreatedDate((new Date()).toString());
        reservation.setOrganizerEmail(SharedPreferencesManager.getEmail(getContext()));
        reservation.setEventId(event.getId());
        reservation.setEmployees(new ArrayList<>());
        reservation.setEventDate(event.getDate());
        ArrayList<String> employees = new ArrayList<>();
        employees.add(employee.getId());
        reservation.setEmployees(employees);
        return reservation;
    }
}