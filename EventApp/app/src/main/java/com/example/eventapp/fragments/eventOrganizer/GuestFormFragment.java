package com.example.eventapp.fragments.eventOrganizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventapp.R;
import com.example.eventapp.databinding.AgendaActivityFormBinding;
import com.example.eventapp.databinding.GuestFormBinding;
import com.example.eventapp.model.AgendaActivity;
import com.example.eventapp.model.Guest;
import com.example.eventapp.model.SpecialRequest;
import com.example.eventapp.repositories.AgendaActivityRepo;
import com.example.eventapp.repositories.GuestRepo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GuestFormFragment extends DialogFragment {

    private GuestFormBinding binding;

    private Guest guest = new Guest();
    private String eventId;
    private GuestListFragment guestListFragment;
    private boolean isEditing = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = GuestFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if(isEditing)
            fillGuestInfo();
        else
        {
            RadioButton none = binding.getRoot().findViewById(R.id.none);
            none.setChecked(true);
        }

        //button back
        Button btnBack = root.findViewById(R.id.button_back);
        btnBack.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        //button submit
        Button btnSubmit = root.findViewById(R.id.button_submit);
        btnSubmit.setOnClickListener(v -> {
            if(fillData())
            {
                if(!isEditing)
                {
                    GuestRepo.create(guest);
                    Toast.makeText(getContext(), "New guest added!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    GuestRepo.update(guest);
                    Toast.makeText(getContext(), "Guest updated!", Toast.LENGTH_SHORT).show();
                }
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.event_fragment, GuestListFragment.newInstance(eventId));
                fragmentTransaction.commit();
            }else{
                Toast.makeText(getContext(), "All fields must be valid!", Toast.LENGTH_SHORT).show();
            }
        });

        return root;

    }

    public static GuestFormFragment newInstance(GuestListFragment f, String eventId, boolean isEditing, Guest guest) {
        GuestFormFragment fragment = new GuestFormFragment();
        fragment.guestListFragment = f;
        fragment.eventId = eventId;
        fragment.isEditing = isEditing;
        if(isEditing)
            fragment.guest = guest;
        return fragment;
    }

    private boolean fillData(){
        EditText name = binding.getRoot().findViewById(R.id.editTextName);

        EditText lastName = binding.getRoot().findViewById(R.id.editTextLastName);

        Spinner age = binding.getRoot().findViewById(R.id.spinner_age);

        CheckBox invited = binding.getRoot().findViewById(R.id.checkBox_invited);

        CheckBox confirmed = binding.getRoot().findViewById(R.id.checkBox_confirmed);

        RadioButton vegan = binding.getRoot().findViewById(R.id.vegan);
        RadioButton vegeterian = binding.getRoot().findViewById(R.id.vegetarian);

        if(name.getText() == null || lastName.getText() == null || age.getSelectedItem() == null)
            return false;

        guest.setEventId(eventId);
        guest.setName(name.getText().toString());
        guest.setLastName(lastName.getText().toString());
        guest.setAgeGroup(age.getSelectedItem().toString());
        guest.setInvited(invited.isChecked());
        guest.setConfirmed(confirmed.isChecked());
        if(vegan.isChecked())
            guest.setSpecialRequest(SpecialRequest.VEGAN);
        else if(vegeterian.isChecked())
            guest.setSpecialRequest(SpecialRequest.VEGETARIAN);
        else
            guest.setSpecialRequest(SpecialRequest.NONE);

        if(!guest.getName().isEmpty() && !guest.getLastName().isEmpty() && !guest.getAgeGroup().isEmpty())
            return true;
        return false;
    }

    private void fillGuestInfo()
    {
        EditText name = binding.getRoot().findViewById(R.id.editTextName);
        name.setText(guest.getName());

        EditText lastName = binding.getRoot().findViewById(R.id.editTextLastName);
        lastName.setText(guest.getLastName());

        Spinner age = binding.getRoot().findViewById(R.id.spinner_age);
        if(guest.getAgeGroup().equals("0 - 3"))
            age.setSelection(0);
        else if(guest.getAgeGroup().equals("3 - 10"))
            age.setSelection(1);
        else if(guest.getAgeGroup().equals("10 - 18"))
            age.setSelection(2);
        else if(guest.getAgeGroup().equals("18 - 30"))
            age.setSelection(3);
        else if(guest.getAgeGroup().equals("30 - 50"))
            age.setSelection(4);
        else if(guest.getAgeGroup().equals("50 - 70"))
            age.setSelection(5);
        else if(guest.getAgeGroup().equals("70+"))
            age.setSelection(6);

        CheckBox invited = binding.getRoot().findViewById(R.id.checkBox_invited);
        invited.setChecked(guest.isInvited());

        CheckBox confirmed = binding.getRoot().findViewById(R.id.checkBox_confirmed);
        confirmed.setChecked(guest.isConfirmed());

        RadioButton vegan = binding.getRoot().findViewById(R.id.vegan);
        RadioButton vegeterian = binding.getRoot().findViewById(R.id.vegetarian);
        RadioButton none = binding.getRoot().findViewById(R.id.none);

        if(guest.getSpecialRequest().equals(SpecialRequest.VEGAN))
            vegan.setChecked(true);
        else  if(guest.getSpecialRequest().equals(SpecialRequest.VEGETARIAN))
            vegeterian.setChecked(true);
        else
            none.setChecked(true);
    }

}
