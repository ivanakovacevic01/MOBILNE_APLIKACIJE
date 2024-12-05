package com.example.eventapp.fragments.employees;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentRegisterEmployeWorkingTimeBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.WorkingTime;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.WorkingTimeRepo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterEmployeWorkingTimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterEmployeWorkingTimeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatePicker start;
    private DatePicker end;
    private int startDay;
    private int startMonth;
    private int startYear;
    private int endDay;
    private int endMonth;
    private int endYear;
    private FragmentRegisterEmployeWorkingTimeBinding binding;
    private WorkingTime workingTime;
    private String startDate;
    private String endDate;
    private String mondayStartTime;
    private String mondayEndTime;
    private String tuesdayStartTime;

    private String tuesdayEndTime;

    private String wedStartTime;

    private String wedEndTime;
    private String thurStartTime;

    private String thurEndTime;
    private String fridStartTime;

    private String fridEndTime;
    private String satStartTime;

    private String satEndTime;
    private String sunStartTime;

    private String sunEndTime;
    private WorkingTimeRepo workingTimeRepo;
    private ArrayList<WorkingTime> workingTimes;
    private String employeeId;
    private Employee employee1;
    private CheckBox checkBoxMonday;
    private CheckBox checkBoxTuesday;
    private CheckBox checkBoxWednesday;
    private CheckBox checkBoxThursday;
    private CheckBox checkBoxFriday;
    private CheckBox checkBoxSaturday;
    private CheckBox checkBoxSunday;
    public RegisterEmployeWorkingTimeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterEmployeWorkingTimeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterEmployeWorkingTimeFragment newInstance(String param1, String param2) {
        RegisterEmployeWorkingTimeFragment fragment = new RegisterEmployeWorkingTimeFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegisterEmployeWorkingTimeBinding.inflate(inflater, container, false);

        View view = binding.getRoot();
        workingTimes=new ArrayList<>();
        Bundle bundle=getArguments();
        boolean isVisited=bundle.getBoolean("visitedWorkingTimePage");
        employeeId=bundle.getString("employeeId");
        startDate="";
        endDate="";
        start = view.findViewById(R.id.startDatePicker);
        end = view.findViewById(R.id.endDatePicker);
        startDay=start.getDayOfMonth();
        startMonth=start.getMonth()+1;
        startYear=start.getYear();

        startDate = startDay+"/"+startMonth+"/"+startYear;
        start.setOnDateChangedListener((startDatePicker, year, monthOfYear, dayOfMonth) -> {
            startDay=dayOfMonth;
            startMonth=monthOfYear+1;
            startYear=year;

            startDate = startDay+"/"+startMonth+"/"+startYear;
            bundle.putBoolean("visitedWorkingTimePage",true);
        });
        startDate = startDay+"/"+startMonth+"/"+startYear;
        Log.i("EventApp","START: "+startDate);

        endDay=end.getDayOfMonth();
        endMonth=end.getMonth()+1;
        endYear=end.getYear();
        endDate = endDay+"/"+endMonth+"/"+endYear;
        end.setOnDateChangedListener((endDatePicker, year, monthOfYear, dayOfMonth) -> {
            endDay=dayOfMonth;
            endMonth=monthOfYear+1;
            endYear=year;

            endDate = endDay+"/"+endMonth+"/"+endYear;
            bundle.putBoolean("visitedWorkingTimePage",true);
        });
        checkBoxMonday = view.findViewById(R.id.checkbox_work_mon);
        TimePicker mondayStart = view.findViewById(R.id.mondayStartPicker);
        TimePicker mondayEnd = view.findViewById(R.id.mondayEndPicker);
        mondayStart.setIs24HourView(true);
        mondayEnd.setIs24HourView(true);
        mondayStartTime = String.valueOf(String.format("%02d", mondayStart.getHour()))+":"+String.valueOf(String.format("%02d", mondayStart.getMinute()));

        mondayStart.setOnTimeChangedListener((mondayStartPicker, hourOfDay
                , minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            mondayStartTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
            bundle.putBoolean("visitedWorkingTimePage",true);

        });
        mondayEndTime = String.valueOf(String.format("%02d", mondayEnd.getHour()))+":"+String.valueOf(String.format("%02d", mondayEnd.getMinute()));

        mondayEnd.setOnTimeChangedListener((mondayEndPicker, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;

            mondayEndTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
            bundle.putBoolean("visitedWorkingTimePage",true);

        });
        checkBoxMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Ako je CheckBox označen
                if (isChecked) {

                    mondayStart.setEnabled(true);
                    mondayEnd.setEnabled(true);
                } else {

                    mondayStart.setEnabled(false);
                    mondayEnd.setEnabled(false);
                }
            }
        });

        checkBoxTuesday = view.findViewById(R.id.checkbox_work_tue);
        TimePicker tuesdayStart = view.findViewById(R.id.tuesdayStartPicker);
        TimePicker tuesdayEnd = view.findViewById(R.id.tuesdayEndPicker);
        tuesdayStart.setIs24HourView(true);
        tuesdayEnd.setIs24HourView(true);
        tuesdayStartTime = String.valueOf(String.format("%02d", tuesdayStart.getHour()))+":"+String.valueOf(String.format("%02d", tuesdayStart.getMinute()));

        tuesdayStart.setOnTimeChangedListener((tuesdayStartPicker, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;

            tuesdayStartTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
            bundle.putBoolean("visitedWorkingTimePage",true);

        });
        tuesdayEndTime = String.valueOf(String.format("%02d", tuesdayEnd.getHour()))+":"+String.valueOf(String.format("%02d", tuesdayEnd.getMinute()));

        tuesdayEnd.setOnTimeChangedListener((tuesdayEndPicker, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;

            tuesdayEndTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
            bundle.putBoolean("visitedWorkingTimePage",true);
        });

        checkBoxTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    tuesdayStart.setEnabled(true);
                    tuesdayEnd.setEnabled(true);
                } else {

                    tuesdayStart.setEnabled(false);
                    tuesdayEnd.setEnabled(false);
                }
            }
        });
        checkBoxWednesday = view.findViewById(R.id.checkbox_work_wed);
        TimePicker wednesdayStart = view.findViewById(R.id.wednesdayStartPicker);
        TimePicker wednesdayEnd = view.findViewById(R.id.wednesdayEndPicker);
        wednesdayStart.setIs24HourView(true);
        wednesdayEnd.setIs24HourView(true);
        wedStartTime = String.valueOf(String.format("%02d", wednesdayStart.getHour()))+":"+String.valueOf(String.format("%02d", wednesdayStart.getMinute()));

        wednesdayStart.setOnTimeChangedListener((wednesdayStartPicker, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;

            wedStartTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
            bundle.putBoolean("visitedWorkingTimePage",true);

        });
        wedEndTime = String.valueOf(String.format("%02d", wednesdayEnd.getHour()))+":"+String.valueOf(String.format("%02d", wednesdayEnd.getMinute()));

        wednesdayEnd.setOnTimeChangedListener((wednesdayEndPicker, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;

            wedEndTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
            bundle.putBoolean("visitedWorkingTimePage",true);

        });
        checkBoxWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    wednesdayStart.setEnabled(true);
                    wednesdayEnd.setEnabled(true);
                } else {

                    wednesdayStart.setEnabled(false);
                    wednesdayEnd.setEnabled(false);
                }
            }
        });

        checkBoxThursday = view.findViewById(R.id.checkbox_work_thu);
        TimePicker thursdayStart = view.findViewById(R.id.thursdayStartPicker);
        TimePicker thursdayEnd = view.findViewById(R.id.thursdayEndPicker);
        thursdayStart.setIs24HourView(true);
        thursdayEnd.setIs24HourView(true);
        thurStartTime = String.valueOf(String.format("%02d", thursdayStart.getHour()))+":"+String.valueOf(String.format("%02d", thursdayStart.getMinute()));

        thursdayStart.setOnTimeChangedListener((thursdayStartPicker, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            thurStartTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
            bundle.putBoolean("visitedWorkingTimePage",true);
        });
        thurEndTime = String.valueOf(String.format("%02d", thursdayEnd.getHour()))+":"+String.valueOf(String.format("%02d", thursdayEnd.getMinute()));

        thursdayEnd.setOnTimeChangedListener((thursdayEndPicker, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            thurEndTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
            bundle.putBoolean("visitedWorkingTimePage",true);
        });
        checkBoxThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    thursdayStart.setEnabled(true);
                    thursdayEnd.setEnabled(true);
                } else {

                    thursdayStart.setEnabled(false);
                    thursdayEnd.setEnabled(false);
                }
            }
        });

        checkBoxFriday = view.findViewById(R.id.checkbox_work_fri);
        TimePicker fridayStart = view.findViewById(R.id.fridayStartPicker);
        TimePicker fridayEnd = view.findViewById(R.id.fridayEndPicker);
        fridayStart.setIs24HourView(true);
        fridayEnd.setIs24HourView(true);
        fridStartTime = String.valueOf(String.format("%02d", fridayStart.getHour()))+":"+String.valueOf(String.format("%02d", fridayStart.getMinute()));

        fridayStart.setOnTimeChangedListener((fridayStartPicker, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            fridStartTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
            bundle.putBoolean("visitedWorkingTimePage",true);
        });
        fridEndTime = String.valueOf(String.format("%02d", fridayEnd.getHour()))+":"+String.valueOf(String.format("%02d", fridayEnd.getMinute()));

        fridayEnd.setOnTimeChangedListener((fridayEndPicker, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            fridEndTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
            bundle.putBoolean("visitedWorkingTimePage",true);
        });
        checkBoxFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    fridayStart.setEnabled(true);
                    fridayEnd.setEnabled(true);
                } else {

                    fridayStart.setEnabled(false);
                    fridayEnd.setEnabled(false);
                }
            }
        });

        checkBoxSaturday = view.findViewById(R.id.checkbox_work_sat);
        TimePicker saturdayStart = view.findViewById(R.id.saturdayStartPicker);
        TimePicker saturdayEnd = view.findViewById(R.id.saturdayEndPicker);
        saturdayStart.setIs24HourView(true);
        saturdayEnd.setIs24HourView(true);
        satStartTime = String.valueOf(String.format("%02d", saturdayStart.getHour()))+":"+String.valueOf(String.format("%02d", saturdayStart.getMinute()));

        saturdayStart.setOnTimeChangedListener((saturdayStartPicker, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            satStartTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
            bundle.putBoolean("visitedWorkingTimePage",true);
        });
        satEndTime = String.valueOf(String.format("%02d", saturdayEnd.getHour()))+":"+String.valueOf(String.format("%02d", saturdayEnd.getMinute()));

        saturdayEnd.setOnTimeChangedListener((saturdayEndPicker, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            satEndTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
            bundle.putBoolean("visitedWorkingTimePage",true);
        });
        checkBoxSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    saturdayStart.setEnabled(true);
                    saturdayEnd.setEnabled(true);
                } else {

                    saturdayStart.setEnabled(false);
                    saturdayEnd.setEnabled(false);
                }
            }
        });

        checkBoxSunday = view.findViewById(R.id.checkbox_work_sun);
        TimePicker sundayStart = view.findViewById(R.id.sundayStartPicker);
        TimePicker sundayEnd = view.findViewById(R.id.sundayEndPicker);
        sundayStart.setIs24HourView(true);
        sundayEnd.setIs24HourView(true);
        sunStartTime = String.valueOf(String.format("%02d", sundayStart.getHour()))+":"+String.valueOf(String.format("%02d", sundayStart.getMinute()));

        sundayStart.setOnTimeChangedListener((sundayStartPicker, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            sunStartTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
            bundle.putBoolean("visitedWorkingTimePage",true);
        });
        sunEndTime = String.valueOf(String.format("%02d", sundayEnd.getHour()))+":"+String.valueOf(String.format("%02d", sundayEnd.getMinute()));

        sundayEnd.setOnTimeChangedListener((sundayEndPicker, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            sunEndTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
            bundle.putBoolean("visitedWorkingTimePage",true);
        });
        checkBoxSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    sundayStart.setEnabled(true);
                    sundayEnd.setEnabled(true);
                } else {

                    sundayStart.setEnabled(false);
                    sundayEnd.setEnabled(false);
                }
            }
        });

        workingTimeRepo=new WorkingTimeRepo();
        workingTimeRepo.getAll(new WorkingTimeRepo.WorkingTimeFetchCallback() {
            @Override
            public void onWorkingTimeFetch(ArrayList<WorkingTime> workingTime) {
                workingTimes=workingTime;
            }
        });
        EmployeeRepo employeeRepo=new EmployeeRepo();
        employeeRepo.getAll(new EmployeeRepo.EmployeeFetchCallback() {
            @Override
            public void onEmployeeFetch(ArrayList<Employee> employees) {
                for(Employee e: employees){
                    if(e.getId().equals(employeeId)){
                        employee1=e;
                    }
                }
            }
        });

        Button submitWorkingTime = view.findViewById(R.id.submitWorkingTime);
        submitWorkingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getArguments();
                Bundle resultBundle = new Bundle();
                resultBundle.putString("firstName", bundle.getString("firstName"));
                resultBundle.putString("lastName", bundle.getString("lastName"));
                resultBundle.putString("email", bundle.getString("email"));
                resultBundle.putString("street", bundle.getString("street"));
                resultBundle.putString("streetn", bundle.getString("streetn"));
                resultBundle.putString("city", bundle.getString("city"));
                resultBundle.putString("country", bundle.getString("country"));
                resultBundle.putString("phone", bundle.getString("phone"));
                resultBundle.putString("pass", bundle.getString("pass"));
                resultBundle.putString("confPass", bundle.getString("confPass"));
                resultBundle.putString("image",bundle.getString("image"));
                resultBundle.putBoolean("visitedWorkingTimePage",true);

                Fragment registrationEmployeeFragment = new RegistrationEmployeeFragment();
                registrationEmployeeFragment.setArguments(resultBundle);


                int fm=getActivity().getSupportFragmentManager().getBackStackEntryCount()-1;
                FragmentManager.BackStackEntry backEntry=getFragmentManager().getBackStackEntryAt(fm);
                String tag=backEntry.getName();
                Log.i("EventApp","tag "+tag);
                if(tag.equals("details")) {
                    FragmentTransition.to(EmployeeDetailsFragment.newInstance(employee1), getActivity(), true, R.id.scroll_employees_list);
                    for(WorkingTime wt:workingTimes){
                        if(wt.getUserId()!=null){
                            if(wt.getUserId().equals(employeeId)){
                                if(updateValidation(wt))
                                    WorkingTimeRepo.update(wt);
                            }
                        }

                    }
                }
                else {
                    FragmentTransition.to(registrationEmployeeFragment, getActivity(), true, R.id.scroll_employees_list);
                    if(addWorkingTime()){
                        workingTime.setUserId("");
                        WorkingTimeRepo.create(workingTime, new WorkingTimeRepo.WorkingTimeFetchCallback() {
                            @Override
                            public void onWorkingTimeObjectFetched(WorkingTime workingTime, String errorMessage) {
                                WorkingTimeRepo.WorkingTimeFetchCallback.super.onWorkingTimeObjectFetched(workingTime, errorMessage);
                            }
                        });
                    }

                }
            }
        });

        return view;
    }
    private boolean updateValidation(WorkingTime wt){
        if (startDate != null && endDate != null && !endDate.isEmpty() && !startDate.isEmpty()) {
            String validationError = validateDateRange(startDate, endDate);
            if (validationError != null) {
                Toast.makeText(getContext(), validationError, Toast.LENGTH_SHORT).show();
                return false;
            }else{
                wt.setStartDate(startDate);
                wt.setEndDate(endDate);
            }
        }

        if (isValidWorkingTime(mondayStartTime, mondayEndTime) &&
                isValidWorkingTime(tuesdayStartTime, tuesdayEndTime) &&
                isValidWorkingTime(wedStartTime, wedEndTime) &&
                isValidWorkingTime(thurStartTime, thurEndTime) &&
                isValidWorkingTime(fridStartTime, fridEndTime) &&
                isValidWorkingTime(satStartTime, satEndTime) &&
                isValidWorkingTime(sunStartTime, sunEndTime)) {
            if(checkBoxMonday.isChecked()) {
                wt.setMondayStartTime(mondayStartTime);
                wt.setMondayEndTime(mondayEndTime);
            }
            else {
                wt.setMondayStartTime("");
                wt.setMondayEndTime("");
            }
            if(checkBoxTuesday.isChecked()) {
                wt.setTuesdayStartTime(tuesdayStartTime);
                wt.setTuesdayEndTime(tuesdayEndTime);
            }
            else {
                wt.setTuesdayStartTime("");
                wt.setTuesdayEndTime("");
            }
            if(checkBoxWednesday.isChecked()) {
                wt.setWednesdayStartTime(wedStartTime);
                wt.setWednesdayEndTime(wedEndTime);
            }
            else {
                wt.setWednesdayStartTime("");
                wt.setWednesdayEndTime("");
            }
            if(checkBoxThursday.isChecked()) {
                wt.setThursdayStartTime(thurStartTime);
                wt.setThursdayEndTime(thurEndTime);
            }
            else {
                wt.setThursdayStartTime("");
                wt.setThursdayEndTime("");
            }
            if(checkBoxFriday.isChecked()) {
                wt.setFridayStartTime(fridStartTime);
                wt.setFridayEndTime(fridEndTime);
            }
            else {
                wt.setFridayStartTime("");
                wt.setFridayEndTime("");
            }
            if(checkBoxSaturday.isChecked()) {
                wt.setSaturdayStartTime(satStartTime);
                wt.setSaturdayEndTime(satEndTime);
            }
            else {
                wt.setSaturdayStartTime("");
                wt.setSaturdayEndTime("");
            }
            if(checkBoxSunday.isChecked()) {
                wt.setSundayStartTime(sunStartTime);
                wt.setSundayEndTime(sunEndTime);
            }
            else {
                wt.setSundayStartTime("");
                wt.setSundayEndTime("");
            }

            return true;
        }else {
            Toast.makeText(getContext(), "Invalid working time. Please check the time format.", Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    private boolean addWorkingTime() {
        workingTime = new WorkingTime();
        workingTime.setStartDate(startDate);
        workingTime.setEndDate(endDate);
        if (startDate != null && endDate != null && !endDate.isEmpty() && !startDate.isEmpty()) {
            String validationError = validateDateRange(startDate, endDate);
            if (validationError != null) {
                Toast.makeText(getContext(), validationError, Toast.LENGTH_SHORT).show();
                return false;
            }else{
                workingTime.setStartDate(startDate);
                workingTime.setEndDate(endDate);
            }
        }

        if (isValidWorkingTime(mondayStartTime, mondayEndTime) &&
                isValidWorkingTime(tuesdayStartTime, tuesdayEndTime) &&
                isValidWorkingTime(wedStartTime, wedEndTime) &&
                isValidWorkingTime(thurStartTime, thurEndTime) &&
                isValidWorkingTime(fridStartTime, fridEndTime) &&
                isValidWorkingTime(satStartTime, satEndTime) &&
                isValidWorkingTime(sunStartTime, sunEndTime)) {
            if(checkBoxMonday.isChecked()) {
                workingTime.setMondayStartTime(mondayStartTime);
                workingTime.setMondayEndTime(mondayEndTime);
            }
            else {
                workingTime.setMondayStartTime("");
                workingTime.setMondayEndTime("");
            }
            if(checkBoxTuesday.isChecked()) {
                workingTime.setTuesdayStartTime(tuesdayStartTime);
                workingTime.setTuesdayEndTime(tuesdayEndTime);
            }
            else {
                workingTime.setTuesdayStartTime("");
                workingTime.setTuesdayEndTime("");
            }
            if(checkBoxWednesday.isChecked()) {
                workingTime.setWednesdayStartTime(wedStartTime);
                workingTime.setWednesdayEndTime(wedEndTime);
            }
            else {
                workingTime.setWednesdayStartTime("");
                workingTime.setWednesdayEndTime("");
            }
            if(checkBoxThursday.isChecked()) {
                workingTime.setThursdayStartTime(thurStartTime);
                workingTime.setThursdayEndTime(thurEndTime);
            }
            else {
                workingTime.setThursdayStartTime("");
                workingTime.setThursdayEndTime("");
            }
            if(checkBoxFriday.isChecked()) {
                workingTime.setFridayStartTime(fridStartTime);
                workingTime.setFridayEndTime(fridEndTime);
            }
            else {
                workingTime.setFridayStartTime("");
                workingTime.setFridayEndTime("");
            }
            if(checkBoxSaturday.isChecked()) {
                workingTime.setSaturdayStartTime(satStartTime);
                workingTime.setSaturdayEndTime(satEndTime);
            }
            else {
                workingTime.setSaturdayStartTime("");
                workingTime.setSaturdayEndTime("");
            }
            if(checkBoxSunday.isChecked()) {
                workingTime.setSundayStartTime(sunStartTime);
                workingTime.setSundayEndTime(sunEndTime);
            }
            else {
                workingTime.setSundayStartTime("");
                workingTime.setSundayEndTime("");
            }
            workingTime.setUserId(""); //treba dodati ovo
            return true;
        }else {
            Toast.makeText(getContext(), "Invalid working time. Please check the time format.", Toast.LENGTH_SHORT).show();
            return false;
        }


    }
    public static String validateDateRange(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            if (end.before(start)) {
                return "End date must be after start date.";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date format.";
        }
        return null;
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