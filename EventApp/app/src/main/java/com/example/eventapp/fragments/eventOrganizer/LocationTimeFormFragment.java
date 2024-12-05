package com.example.eventapp.fragments.eventOrganizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.databinding.LocationTimeFormBinding;
import com.example.eventapp.model.Event;
import com.example.eventapp.repositories.EventRepo;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class LocationTimeFormFragment extends Fragment {

    private LocationTimeFormBinding binding;
    private EventRepo eventRepo;
    private Event newEvent;

    public static LocationTimeFormFragment newInstance(Event event) {
        LocationTimeFormFragment f=  new LocationTimeFormFragment();
        f.newEvent = event;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NotNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = LocationTimeFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        eventRepo = new EventRepo();

        RelativeLayout relativeLayout = root.findViewById(R.id.relative_layout);
        DatePicker datePicker = (DatePicker)root.findViewById(R.id.date_picker);
        datePicker.setMinDate(new Date().getTime());
        //button submit
        Button btnSubmit = relativeLayout.findViewById(R.id.button_submit);
        btnSubmit.setOnClickListener(v3 -> {
            this.fillEventFields(root);
            if(this.isValid())
            {
                eventRepo.create(newEvent);

                Toast.makeText(getContext(), "New event created!", Toast.LENGTH_SHORT).show();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.event_fragment, EventListFragment.newInstance());
                fragmentTransaction.commit();
            }
            else{
                //error message
                Toast.makeText(getContext(), "All fields must be valid!", Toast.LENGTH_SHORT).show();
            }
        });

        //button back
        Button btnBack = root.findViewById(R.id.button_back);
        btnBack.setOnClickListener(v3 -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private boolean isValid(){
        return !newEvent.getLocation().isEmpty() && newEvent.getMaxKms()>0 && newEvent.getDate().after(new Date());
    }

    private void fillEventFields(View root){
        LinearLayout linearLayout = root.findViewById(R.id.location_and_time);
        LinearLayout location = linearLayout.findViewById(R.id.location);
        LinearLayout time = linearLayout.findViewById(R.id.time);
        this.newEvent.setLocation(((EditText)location.findViewById(R.id.editTextLocation)).getText().toString());
        if(!((EditText)location.findViewById(R.id.editTextNumberDecimal)).getText().toString().isEmpty())
            this.newEvent.setMaxKms(Double.parseDouble(((EditText)location.findViewById(R.id.editTextNumberDecimal)).getText().toString()));
        int mYear = ((DatePicker)time.findViewById(R.id.date_picker)).getYear();
        int mMonth = ((DatePicker)time.findViewById(R.id.date_picker)).getMonth();
        int mDay = ((DatePicker)time.findViewById(R.id.date_picker)).getDayOfMonth();
        this.newEvent.setDate(new Date(mYear, mMonth, mDay));
        this.newEvent.setOrganizerEmail(SharedPreferencesManager.getEmail(getContext()));
    }
}
