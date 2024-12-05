package com.example.eventapp.fragments.eventOrganizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventapp.R;
import com.example.eventapp.databinding.AgendaActivityFormBinding;
import com.example.eventapp.databinding.CheckboxesMenuBinding;
import com.example.eventapp.model.AgendaActivity;
import com.example.eventapp.model.DialogType;
import com.example.eventapp.model.Filters;
import com.example.eventapp.repositories.AgendaActivityRepo;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CreateAgendaActivityFragment extends DialogFragment {
    private AgendaActivityFormBinding binding;

    private AgendaActivity activity = new AgendaActivity();
    private String eventId;
    private CreateAgendaFragment createAgendaFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = AgendaActivityFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //button back
        Button btnBack = root.findViewById(R.id.button_back);
        btnBack.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        //button submit
        Button btnSubmit = root.findViewById(R.id.button_submit);
        btnSubmit.setOnClickListener(v -> {
            try {
                if(fillActivityData())
                {
                    if(!isAlreadyBusy())
                    {
                        AgendaActivityRepo.create(activity);
                        Toast.makeText(getContext(), "New activity created!", Toast.LENGTH_SHORT).show();
                        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.event_fragment, CreateAgendaFragment.newInstance(eventId));
                        fragmentTransaction.commit();
                    }else {
                        Toast.makeText(getContext(), "End time cannot be before start!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(!isTimeValid())
                        Toast.makeText(getContext(), "End time cannot be before start!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), "All fields must be valid!", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        return root;

    }

    public static CreateAgendaActivityFragment newInstance(CreateAgendaFragment f, String eventId) {
        CreateAgendaActivityFragment fragment = new CreateAgendaActivityFragment();
        fragment.createAgendaFragment = f;
        fragment.eventId = eventId;
        return fragment;
    }

    private boolean fillActivityData() throws ParseException {
        EditText name = binding.getRoot().findViewById(R.id.editTextName);

        EditText desc = binding.getRoot().findViewById(R.id.editTextTextMultiLine);

        TimePicker startTimePicker = binding.getRoot().findViewById(R.id.startTimePicker);
        int hour = startTimePicker.getCurrentHour();
        int minute = startTimePicker.getCurrentMinute();
        activity.setStartTime(hour + ":" + minute);

        TimePicker endTimePicker = binding.getRoot().findViewById(R.id.endTimePicker);
        int hour2 = endTimePicker.getCurrentHour();
        int minute2 = endTimePicker.getCurrentMinute();
        activity.setEndTime(hour2 + ":" + minute2);

        EditText location = binding.getRoot().findViewById(R.id.editTextLocation);

        if(name.getText() == null || desc.getText() == null || location.getText() == null)
            return false;
        activity.setName(name.getText().toString());
        activity.setDescription(desc.getText().toString());
        activity.setLocation(location.getText().toString());

        activity.setEventId(eventId);
        return !activity.getName().isEmpty() && !activity.getDescription().isEmpty() && !activity.getLocation().isEmpty()
                && isTimeValid();
    }

    private boolean isTimeValid() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date1 = format.parse(activity.getStartTime());
        Date date2 = format.parse(activity.getEndTime());

        return date2.getTime() > date1.getTime();
    }

    private boolean isAlreadyBusy()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try {
            Date newStartTime = dateFormat.parse(activity.getStartTime());
            Date newEndTime = dateFormat.parse(activity.getEndTime());
            for (AgendaActivity activity : createAgendaFragment.activities) {
                Date existingStartTime = dateFormat.parse(activity.getStartTime());
                Date existingEndTime = dateFormat.parse(activity.getEndTime());
                if ((newStartTime.after(existingStartTime) && newStartTime.before(existingEndTime)) ||
                        (newEndTime.after(existingStartTime) && newEndTime.before(existingEndTime)) ||
                        (newStartTime.before(existingStartTime) && newEndTime.after(existingEndTime))) {
                    // There is a time conflict
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // No time conflict found
        return false;
    }
    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }
}

