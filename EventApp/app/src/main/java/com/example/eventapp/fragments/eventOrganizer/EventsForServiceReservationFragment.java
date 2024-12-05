package com.example.eventapp.fragments.eventOrganizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.adapters.employee.EmployeeListAdapter;
import com.example.eventapp.databinding.FragmentEventsForProductReservationBinding;
import com.example.eventapp.databinding.FragmentEventsForServiceReservationBinding;
import com.example.eventapp.fragments.administration.EditingCategoryFragment;
import com.example.eventapp.fragments.registration.RegistrationOwnerCompanyInfoFragment;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.WorkingTime;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.EventRepo;
import com.example.eventapp.repositories.ReservationRepo;
import com.example.eventapp.repositories.WorkingTimeRepo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsForServiceReservationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsForServiceReservationFragment extends DialogFragment {

    String startTimeStr = null;
    String endTimeStr = null;
    boolean validDate = false;
    RelativeLayout nextButton;
    TableLayout eventsTable;
    TableLayout employeesTable;
    ArrayList<Event> eventsToChoose;   //sve subkategorije
    ArrayList<Employee> employeesToChoose;
    private FragmentEventsForServiceReservationBinding binding;
    private Service service;
    private WorkingTime employeeWorkingTime;
    private Event selectedEvent = null;
    private Employee selectedEmployee = null;

    public EventsForServiceReservationFragment() {
        // Required empty public constructor
    }

    public static EventsForServiceReservationFragment newInstance(Service serviceToSend) {
        EventsForServiceReservationFragment fragment = new EventsForServiceReservationFragment();
        Bundle args = new Bundle();
        args.putParcelable("SERVICE_ID_RES", serviceToSend);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            service = getArguments().getParcelable("SERVICE_ID_RES");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventsForServiceReservationBinding.inflate(inflater, container, false);

        View view = binding.getRoot();
        nextButton = view.findViewById(R.id.nextServiceRegistrationButton);

        nextButton.setVisibility(View.GONE);

        nextButton.setOnClickListener(v -> {
            isDateValid();
            //isEmployeeWorking();


        });
        eventsTable = view.findViewById(R.id.eventsTable);
        employeesTable = view.findViewById(R.id.employeesTable);

        //tabela sa eventovima od organizatora koji kupuje, i da im datumi nisu u proslosti
        eventsToChoose = new ArrayList<>();
        EventRepo eventRepo = new EventRepo();
        eventRepo.getByOrganizer(new EventRepo.EventFetchCallback() {
            @Override
            public void onEventFetch(ArrayList<Event> events) {
                if(events != null) {
                    setEmployeesTable();
                    eventsToChoose = events;
                    eventsToChoose.removeIf(s -> s.getDate().before(new Date())); //nudim samo evente za buducnost

                    List<Button> buttons = new ArrayList<>();
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
                        buttons.add(addButton);
                        addButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Event");
                                builder.setMessage("Are you sure you want to choose this event?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                selectedEvent = event;
                                                addButton.setEnabled(false);

                                                for (Button button : buttons) {
                                                    if (button != addButton) {
                                                        button.setEnabled(true);
                                                    }
                                                }
                                                if(selectedEvent != null && selectedEmployee != null )
                                                    nextButton.setVisibility(View.VISIBLE);

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

    private void setEmployeesTable() {
        EmployeeRepo employeeRepo = new EmployeeRepo();
        employeeRepo.getAll(new EmployeeRepo.EmployeeFetchCallback() {
            @Override
            public void onEmployeeFetch(ArrayList<Employee> employees) {
                if (employees != null) {

                    employeesToChoose = employees;

                    List<Button> buttons = new ArrayList<>();
                    for (Employee employee : employeesToChoose) {
                        if(service.getAttendants().contains(employee.getId()) && employee.isActive()) {
                            TableRow row = new TableRow(getContext());
                            TextView textView = new TextView(getContext());
                            textView.setText(employee.getFirstName() + " " + employee.getLastName());

                            TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                            textView.setLayoutParams(params);
                            row.addView(textView);

                            Button addButton = new Button(getContext());
                            addButton.setText("Add");
                            buttons.add(addButton);
                            addButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Employee");
                                    builder.setMessage("Are you sure you want to choose this employee?")
                                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    selectedEmployee = employee;
                                                    addButton.setEnabled(false);

                                                    for (Button button : buttons) {
                                                        if (button != addButton) {
                                                            button.setEnabled(true);
                                                        }
                                                    }
                                                    if(selectedEvent != null && selectedEmployee != null )
                                                        nextButton.setVisibility(View.VISIBLE);

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
                            employeesTable.addView(row);
                        }


                    }
                }
            }
        });
    }

    private void isDateValid() {
        WorkingTimeRepo workingTimeRepo = new WorkingTimeRepo();

        workingTimeRepo.getByEmployee(selectedEmployee.getId(), new WorkingTimeRepo.WorkingTimeFetchCallback() {
            @Override
            public void onWorkingTimeFetch(ArrayList<WorkingTime> workingTimes) {
                if (workingTimes != null) {

                    for (WorkingTime workingTime : workingTimes) {
                        // Do something with each working time
                        if(isDateInWorkingRange(workingTime))
                        {
                            employeeWorkingTime = workingTime;
                            break;
                        }

                    }
                    if(employeeWorkingTime!=null)
                    {
                        validDate = true;
                        if(isEmployeeWorking()) {   //da li je event date u radnom vremenu zaposlenog

                                    FragmentManager fragmentManager = ((HomeActivity) getContext()).getSupportFragmentManager();
                                    ServiceCalendarFragment dialog1 = ServiceCalendarFragment.newInstance(service, selectedEvent, selectedEmployee, startTimeStr, endTimeStr);
                                    dialog1.show(fragmentManager, "ServiceCalendarFragment");
                                    //dialog.dismiss();


                        }
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setTitle("Error!");
                            builder.setMessage("Employee is not working.");

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }


                } else {
                    // Failed to fetch working times
                    Log.e("WORKING_TIME", "Failed to fetch working times");
                }
            }
        });


    }
    //da pronadjem working time employee-a za taj dan
    private boolean isDateInWorkingRange(WorkingTime time) {
        SimpleDateFormat dateFormatStartEnd = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateFormatDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);

        try {
            Date startDate = dateFormatStartEnd.parse(time.getStartDate());
            Date endDate = dateFormatStartEnd.parse(time.getEndDate());
            Date date = dateFormatDate.parse(selectedEvent.getDate().toString());

            if (date != null && startDate != null && endDate != null) {
                if (date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                Log.d("DATE_CHECK", "Invalid date format.");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isEmployeeWorking() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedEvent.getDate());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        startTimeStr = null;
        endTimeStr = null;

        switch (dayOfWeek) {
            case Calendar.MONDAY:
                startTimeStr = employeeWorkingTime.getMondayStartTime();
                endTimeStr = employeeWorkingTime.getMondayEndTime();
                break;
            case Calendar.TUESDAY:
                startTimeStr = employeeWorkingTime.getTuesdayStartTime();
                endTimeStr = employeeWorkingTime.getTuesdayEndTime();
                break;
            case Calendar.WEDNESDAY:
                startTimeStr = employeeWorkingTime.getWednesdayStartTime();
                endTimeStr = employeeWorkingTime.getWednesdayEndTime();
                break;
            case Calendar.THURSDAY:
                startTimeStr = employeeWorkingTime.getThursdayStartTime();
                endTimeStr = employeeWorkingTime.getThursdayEndTime();
                break;
            case Calendar.FRIDAY:
                startTimeStr = employeeWorkingTime.getFridayStartTime();
                endTimeStr = employeeWorkingTime.getFridayEndTime();
                break;
            case Calendar.SATURDAY:
                startTimeStr = employeeWorkingTime.getSaturdayStartTime();
                endTimeStr = employeeWorkingTime.getSaturdayEndTime();
                break;
            case Calendar.SUNDAY:
                startTimeStr = employeeWorkingTime.getSundayStartTime();
                endTimeStr = employeeWorkingTime.getSundayEndTime();
                break;
        }

        //ako mu je taj dan neradni
        if (startTimeStr == null || endTimeStr == null || startTimeStr.equals("") || endTimeStr.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle("Error!");
            builder.setMessage("Employee is not working at chosen time.");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }


        return true;

    }
}