package com.example.eventapp.fragments.registration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventapp.activities.MainActivity;
import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentRegisterOwnerWorkingTimeBinding;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.FirmWorkingTime;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.OwnerRequestStatus;
import com.example.eventapp.model.RegistrationOwnerHelper;
import com.example.eventapp.model.RegistrationOwnerRequest;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.FirmRepo;
import com.example.eventapp.repositories.FirmWorkingTimeRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.RegistrationOwnerRequestRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterOwnerWorkingTimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterOwnerWorkingTimeFragment extends Fragment {

    private CheckBox checkBoxMonday;
    private CheckBox checkBoxTuesday;
    private CheckBox checkBoxWednesday;
    private CheckBox checkBoxThursday;
    private CheckBox checkBoxFriday;
    private CheckBox checkBoxSaturday;
    private CheckBox checkBoxSunday;

    private RegistrationOwnerHelper helper;
    private RegistrationOwnerRequest newRequest;
    private FragmentRegisterOwnerWorkingTimeBinding binding;
    private FirmWorkingTime workingTime;
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
    //ZA REGISTRACIJU
    private FirebaseAuth mAuth;
    public RegisterOwnerWorkingTimeFragment() {
        // Required empty public constructor
    }

    public static RegisterOwnerWorkingTimeFragment newInstance(RegistrationOwnerHelper helper) {
        RegisterOwnerWorkingTimeFragment fragment = new RegisterOwnerWorkingTimeFragment();
        Bundle args = new Bundle();
        args.putParcelable("END_HELPER", helper);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            helper = getArguments().getParcelable("END_HELPER");
        }
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterOwnerWorkingTimeBinding.inflate(inflater, container, false);

        View view = binding.getRoot();



        //dugme za: reg se kao owner
        Button registerOwnerButton = view.findViewById(R.id.submitOwnerRegistrationButton);
        registerOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidAllPickersInput()) {
                    FirmRepo.createFirm(helper.getFirm(), new FirmRepo.FirmFetchCallback() {
                        @Override
                        public void onFirmObjectFetched(Firm firm, String errorMessage) {
                            if (firm != null) {


                                //sad imam id firme u "firm", treba ga staviti u adresu firme, i u ownera
                                Owner toCreate = updateOwnerObject(firm.getId());


                                toCreate.setId("owner_" + generateUniqueString());  //privremeni string
                                OwnerRepo.createOwner(toCreate, new OwnerRepo.OwnerFetchCallback() {
                                    @Override
                                    public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                        if (owner != null) {
                                            createNewRequestObject(owner.getId());
                                            addWorkingTime();
                                            workingTime.setFirmId(firm.getId());
                                            Address ownerAddress = updateOwnerAddress(owner.getId());
                                            Address firmAddress = updateFirmAddress(firm.getId());

                                            //kreiram i adrese
                                            AddressRepo.create(ownerAddress, new AddressRepo.AddressFetchCallback() {
                                                @Override
                                                public void onAddressObjectFetched(Address address, String errorMessage) {
                                                    AddressRepo.AddressFetchCallback.super.onAddressObjectFetched(address, errorMessage);
                                                    AddressRepo.create(firmAddress, new AddressRepo.AddressFetchCallback() {
                                                        @Override
                                                        public void onAddressObjectFetched(Address address, String errorMessage) {
                                                            AddressRepo.AddressFetchCallback.super.onAddressObjectFetched(address, errorMessage);
                                                            RegistrationOwnerRequestRepo.create(newRequest);
                                                            FirmWorkingTimeRepo.createFirmWorkingTime(workingTime);
                                                            Notification newNotification = new Notification();
                                                            newNotification.setMessage("1 new owner registration request - " + toCreate.getEmail());
                                                            newNotification.setReceiverRole(UserType.ADMIN);
                                                            newNotification.setSenderId(toCreate.getId());
                                                            newNotification.setDate(new Date().toString());
                                                            NotificationRepo.create(newNotification);
                                                            mAuth.signOut();
                                                            Toast.makeText(getContext(), "Request sent to admin.", Toast.LENGTH_SHORT).show();

                                                            // Pokrecem HomeActivity
                                                            Intent intent = new Intent(getContext(), MainActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            });


                                        } else {
                                            Log.w("REZ_DB", "Error creating owner: " + errorMessage);
                                        }
                                    }
                                });
                            }

                            else {
                                Log.w("REZ_DB", "Error creating firm: " + errorMessage);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(), "Invalid working time.", Toast.LENGTH_SHORT).show();
                }



            }
        });


        checkBoxMonday = view.findViewById(R.id.checkbox_work_mon);
        TimePicker mondayStartPicker = view.findViewById(R.id.mondayStartPicker);
        mondayStartPicker.setIs24HourView(true);

        mondayStartTime = String.valueOf(String.format("%02d", mondayStartPicker.getHour()))+":"+String.valueOf(String.format("%02d", mondayStartPicker.getMinute()));
        mondayStartPicker.setOnTimeChangedListener((mondayStart, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            mondayStartTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));

        });

        TimePicker mondayEndPicker = view.findViewById(R.id.mondayEndPicker);
        mondayEndPicker.setIs24HourView(true);
        mondayEndTime = String.valueOf(String.format("%02d", mondayEndPicker.getHour()))+":"+String.valueOf(String.format("%02d", mondayEndPicker.getMinute()));
        mondayEndPicker.setOnTimeChangedListener((mondayEnd, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;

            mondayEndTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));

        });

        checkBoxMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Ako je CheckBox označen
                if (isChecked) {

                    mondayStartPicker.setEnabled(true);
                    mondayEndPicker.setEnabled(true);
                } else {

                    mondayStartPicker.setEnabled(false);
                    mondayEndPicker.setEnabled(false);
                }
            }
        });


        checkBoxTuesday = view.findViewById(R.id.checkbox_work_tue);
        TimePicker tuesdayStartPicker = view.findViewById(R.id.tuesdayStartPicker);
        tuesdayStartPicker.setIs24HourView(true);
        tuesdayStartTime = String.valueOf(String.format("%02d", tuesdayStartPicker.getHour()))+":"+String.valueOf(String.format("%02d", tuesdayStartPicker.getMinute()));
        tuesdayStartPicker.setOnTimeChangedListener((tuesdayStart, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;

            tuesdayStartTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));

        });

        TimePicker tuesdayEndPicker = view.findViewById(R.id.tuesdayEndPicker);
        tuesdayEndPicker.setIs24HourView(true);
        tuesdayEndTime = String.valueOf(String.format("%02d", tuesdayEndPicker.getHour()))+":"+String.valueOf(String.format("%02d", tuesdayEndPicker.getMinute()));
        tuesdayEndPicker.setOnTimeChangedListener((tuesdayEnd, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;

            tuesdayEndTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));

        });



        checkBoxTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    tuesdayStartPicker.setEnabled(true);
                    tuesdayEndPicker.setEnabled(true);
                } else {

                    tuesdayStartPicker.setEnabled(false);
                    tuesdayEndPicker.setEnabled(false);
                }
            }
        });

        checkBoxWednesday = view.findViewById(R.id.checkbox_work_wed);
        TimePicker wednesdayStartPicker = view.findViewById(R.id.wednesdayStartPicker);
        wednesdayStartPicker.setIs24HourView(true);
        wedStartTime = String.valueOf(String.format("%02d", wednesdayStartPicker.getHour()))+":"+String.valueOf(String.format("%02d", wednesdayStartPicker.getMinute()));

        wednesdayStartPicker.setOnTimeChangedListener((wednesdayStart, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;

            wedStartTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));

        });



        TimePicker wednesdayEndPicker = view.findViewById(R.id.wednesdayEndPicker);
        wednesdayEndPicker.setIs24HourView(true);
        wedEndTime = String.valueOf(String.format("%02d", wednesdayEndPicker.getHour()))+":"+String.valueOf(String.format("%02d", wednesdayEndPicker.getMinute()));

        wednesdayEndPicker.setOnTimeChangedListener((wednesdayEnd, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;

            wedEndTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));

        });


        checkBoxWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    wednesdayStartPicker.setEnabled(true);
                    wednesdayEndPicker.setEnabled(true);
                } else {

                    wednesdayStartPicker.setEnabled(false);
                    wednesdayEndPicker.setEnabled(false);
                }
            }
        });


        checkBoxThursday = view.findViewById(R.id.checkbox_work_thu);
        TimePicker thursdayStartPicker = view.findViewById(R.id.thursdayStartPicker);
        thursdayStartPicker.setIs24HourView(true);
        thurStartTime = String.valueOf(String.format("%02d", thursdayStartPicker.getHour()))+":"+String.valueOf(String.format("%02d", thursdayStartPicker.getMinute()));

        thursdayStartPicker.setOnTimeChangedListener((thursdayStart, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            thurStartTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
        });


        TimePicker thursdayEndPicker = view.findViewById(R.id.thursdayEndPicker);
        thursdayEndPicker.setIs24HourView(true);
        thurEndTime = String.valueOf(String.format("%02d", thursdayEndPicker.getHour()))+":"+String.valueOf(String.format("%02d", thursdayEndPicker.getMinute()));

        thursdayEndPicker.setOnTimeChangedListener((thursdayEnd, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            thurEndTime =String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
        });



        checkBoxThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    thursdayStartPicker.setEnabled(true);
                    thursdayEndPicker.setEnabled(true);
                } else {

                    thursdayStartPicker.setEnabled(false);
                    thursdayEndPicker.setEnabled(false);
                }
            }
        });

        checkBoxFriday = view.findViewById(R.id.checkbox_work_fri);
        TimePicker fridayStartPicker = view.findViewById(R.id.fridayStartPicker);
        fridayStartPicker.setIs24HourView(true);
        fridStartTime = String.valueOf(String.format("%02d", fridayStartPicker.getHour()))+":"+String.valueOf(String.format("%02d", fridayStartPicker.getMinute()));

        fridayStartPicker.setOnTimeChangedListener((fridayStart, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            fridStartTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
        });


        TimePicker fridayEndPicker = view.findViewById(R.id.fridayEndPicker);
        fridayEndPicker.setIs24HourView(true);
        fridEndTime = String.valueOf(String.format("%02d", fridayEndPicker.getHour()))+":"+String.valueOf(String.format("%02d", fridayEndPicker.getMinute()));

        fridayEndPicker.setOnTimeChangedListener((fridayEnd, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            fridEndTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));

        });

        checkBoxFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    fridayStartPicker.setEnabled(true);
                    fridayEndPicker.setEnabled(true);
                } else {

                    fridayStartPicker.setEnabled(false);
                    fridayEndPicker.setEnabled(false);
                }
            }
        });

        checkBoxSaturday = view.findViewById(R.id.checkbox_work_sat);
        TimePicker saturdayStartPicker = view.findViewById(R.id.saturdayStartPicker);
        saturdayStartPicker.setIs24HourView(true);
        satStartTime = String.valueOf(String.format("%02d", saturdayStartPicker.getHour()))+":"+String.valueOf(String.format("%02d", saturdayStartPicker.getMinute()));

        saturdayStartPicker.setOnTimeChangedListener((saturdayStart, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            satStartTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));

        });


        TimePicker saturdayEndPicker = view.findViewById(R.id.saturdayEndPicker);
        saturdayEndPicker.setIs24HourView(true);
        satEndTime = String.valueOf(String.format("%02d", saturdayEndPicker.getHour()))+":"+String.valueOf(String.format("%02d", saturdayEndPicker.getMinute()));

        saturdayEndPicker.setOnTimeChangedListener((saturdayEnd, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            satEndTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));

        });


        checkBoxSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    saturdayStartPicker.setEnabled(true);
                    saturdayEndPicker.setEnabled(true);
                } else {

                    saturdayStartPicker.setEnabled(false);
                    saturdayEndPicker.setEnabled(false);
                }
            }
        });

        checkBoxSunday = view.findViewById(R.id.checkbox_work_sun);
        TimePicker sundayStartPicker = view.findViewById(R.id.sundayStartPicker);
        sundayStartPicker.setIs24HourView(true);
        sunStartTime = String.valueOf(String.format("%02d", sundayStartPicker.getHour()))+":"+String.valueOf(String.format("%02d", sundayStartPicker.getMinute()));

        sundayStartPicker.setOnTimeChangedListener((sundayStart, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            sunStartTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));
        });


        TimePicker sundayEndPicker = view.findViewById(R.id.sundayEndPicker);
        sundayEndPicker.setIs24HourView(true);
        sunEndTime = String.valueOf(String.format("%02d", sundayEndPicker.getHour()))+":"+String.valueOf(String.format("%02d", sundayEndPicker.getMinute()));

        sundayEndPicker.setOnTimeChangedListener((sundayEnd, hourOfDay, minute) -> {
            int selectedHour = hourOfDay;
            int selectedMinute = minute;
            sunEndTime = String.valueOf(String.format("%02d", selectedHour))+":"+String.valueOf(String.format("%02d", selectedMinute));

        });


        checkBoxSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    sundayStartPicker.setEnabled(true);
                    sundayEndPicker.setEnabled(true);
                } else {

                    sundayStartPicker.setEnabled(false);
                    sundayEndPicker.setEnabled(false);
                }
            }
        });



        return view;
    }




    private void addWorkingTime() {
        workingTime = new FirmWorkingTime();

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

        workingTime.setFirmId(""); //treba dodati ovo
    }

    private Owner updateOwnerObject(String firmId) {
        Owner owner = helper.getOwner();
        owner.setFirmId(firmId);
        return owner;
    }
    private Address updateOwnerAddress(String ownerId) {
        Address address = helper.getOwnerAddress();
        address.setUserId(ownerId);
        address.setFirmId("");
        return address;
    }
    private Address updateFirmAddress(String firmId) {
        Address address = helper.getFirmAddress();
        address.setFirmId(firmId);
        address.setUserId("");
        return address;
    }
    private void createNewRequestObject(String ownerId) {
        newRequest = new RegistrationOwnerRequest();
        newRequest.setOwnerId(ownerId);
        newRequest.setStatus(OwnerRequestStatus.PENDING);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        newRequest.setCreatedDate(dateFormat.format(date));
    }

    private boolean isValidWorkingTime(String startTime, String endTime, CheckBox checkBox) {
        if(checkBox.isChecked()) {
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
        return true;

    }

    private boolean isValidAllPickersInput() {
        return (isValidWorkingTime(mondayStartTime, mondayEndTime, checkBoxMonday) &&
                isValidWorkingTime(tuesdayStartTime, tuesdayEndTime, checkBoxTuesday) &&
                isValidWorkingTime(wedStartTime, wedEndTime, checkBoxWednesday) &&
                isValidWorkingTime(thurStartTime, thurEndTime, checkBoxThursday) &&
                isValidWorkingTime(fridStartTime, fridEndTime, checkBoxFriday) &&
                isValidWorkingTime(satStartTime, satEndTime, checkBoxSaturday) &&
                isValidWorkingTime(sunStartTime, sunEndTime, checkBoxSunday));
    }

    private static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        String timestampString = Long.toString(timestamp);

        Random random = new Random();
        int randomInt = random.nextInt(10000);

        return timestampString + "_" + randomInt;
    }

}