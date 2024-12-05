package com.example.eventapp.fragments.employees;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentEventEditBinding;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.model.WeeklyEvent;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.UserRepo;
import com.example.eventapp.repositories.WeeklyEventRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventEditFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String firmId;
    private FragmentEventEditBinding binding;
    private EditText name;
    private TimePicker startPicker;
    private TimePicker endPicker;
    private WeeklyEventRepo weeklyEventRepo;
    private WeeklyEvent event;
    private String eventName;
    private String start;
    private String end;
    private String employeeId;
    private String date;
    private ArrayList<WeeklyEvent> dailyEvents;
    private FirebaseUser currentUser;
    public EventEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventEditFragment newInstance(String param1, String param2) {
        EventEditFragment fragment = new EventEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventEditBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Log.i("EventApp","currentUser"+currentUser.getEmail());
        name=binding.editTextEventName;
        startPicker=binding.startPicker;
        endPicker=binding.endPicker;

        Button btn=root.findViewById(R.id.addEvent);
        Bundle bundle=getArguments();
        employeeId=bundle.getString("employeeId");
        date=bundle.getString("date");
        dailyEvents=new ArrayList<>();
        weeklyEventRepo=new WeeklyEventRepo();
        EmployeeRepo.getById(employeeId, new EmployeeRepo.EmployeeFetchCallback() {
            @Override
            public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                if (errorMessage != null) {
                    return;
                }
                firmId = employee.getFirmId();
                WeeklyEventRepo.getByFirmId(firmId, new WeeklyEventRepo.WeeklyEventFetchCallback() {
                    @Override
                    public void onWeeklyEventFetch(ArrayList<WeeklyEvent> events) {
                        dailyEvents = events;
                    }
                });
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addEvent()){
                    if (!isTimeAvailable(startPicker.getHour(), startPicker.getMinute(), endPicker.getHour(), endPicker.getMinute())) {
                        Toast.makeText(requireContext(), "Selected time is not available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    WeeklyEventRepo.create(event, new WeeklyEventRepo.WeeklyEventFetchCallback() {
                        @Override
                        public void onWeeklyEventObjectFetched(WeeklyEvent event, String errorMessage) {
                            WeeklyEventRepo.WeeklyEventFetchCallback.super.onWeeklyEventObjectFetched(event, errorMessage);
                            Intent intent = new Intent("custom-event-name");
                            intent.putExtra("event_added", true);
                            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
                            dismiss();
                            createNotification();
                        }
                    });
                }else{
                    Toast.makeText(requireContext(), "End time must be after start time.", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });

        return root;
    }
    private void createNotification(){
        UserRepo.getUserByEmail(currentUser.getEmail(), new UserRepo.UserFetchCallback() {
            @Override
            public void onUserObjectFetched(User user, String errorMessage) {
                if(user.getType().equals(UserType.EMPLOYEE)){
                    EmployeeRepo.getById(user.getId(), new EmployeeRepo.EmployeeFetchCallback() {
                        @Override
                        public void onEmployeeObjectFetched(Employee employee, String errorMessage) {

                            EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                            OwnerRepo.getByFirmId(employee.getFirmId(), new OwnerRepo.OwnerFetchCallback() {
                                @Override
                                public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                    OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                    Notification newNotification = new Notification();
                                    newNotification.setDate(new Date().toString());
                                    newNotification.setMessage("Event added by employee.");
                                    newNotification.setReceiverId(owner.getId());
                                    newNotification.setReceiverRole(UserType.OWNER);
                                    newNotification.setSenderId(event.getEmployeeId());
                                    NotificationRepo.create(newNotification);
                                }
                            });
                        }
                    });


                }else{
                    Notification newNotification = new Notification();
                    newNotification.setDate(new Date().toString());
                    newNotification.setMessage("Event added by owner.");
                    newNotification.setReceiverId(event.getEmployeeId());
                    newNotification.setReceiverRole(UserType.EMPLOYEE);
                    newNotification.setSenderId(user.getId());
                    NotificationRepo.create(newNotification);

                }
            }
        });


    }
    private boolean addEvent() {
        boolean isValid = false;
        eventName=name.getText().toString();
        start=startPicker.getHour()+":"+startPicker.getMinute();
        end=endPicker.getHour()+":"+endPicker.getMinute();
        event=new WeeklyEvent();
        event.reservationId="";
        if(isValidWorkingTime(start,end)){
            event.setName(eventName);
            event.setFrom(start);
            event.setTo(end);
            event.setEventType(WeeklyEvent.EventType.BUSY);
            event.setEmployeeId(employeeId);
            event.setDate(date);
            event.setFirmId(firmId);
            isValid =true;
        }
        return isValid;
    }

    private boolean isTimeAvailable(int startHour, int startMinute, int endHour, int endMinute){
        if(dailyEvents!=null){
            for (WeeklyEvent existingEvent : dailyEvents) {
                String[] existingStartParts = existingEvent.getFrom().split(":");
                String[] existingEndParts = existingEvent.getTo().split(":");
                int existingStartHour = Integer.parseInt(existingStartParts[0]);
                int existingStartMinute = Integer.parseInt(existingStartParts[1]);
                int existingEndHour = Integer.parseInt(existingEndParts[0]);
                int existingEndMinute = Integer.parseInt(existingEndParts[1]);
                if (!((endHour < existingStartHour) ||
                        (endHour == existingStartHour && endMinute <= existingStartMinute) ||
                        (startHour > existingEndHour) ||
                        (startHour == existingEndHour && startMinute >= existingEndMinute))) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean isValidWorkingTime(String startTime, String endTime) {
        if (startTime == null || endTime == null || startTime.isEmpty() || endTime.isEmpty()) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setLenient(false);

        try {
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            if (start.after(end)) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}