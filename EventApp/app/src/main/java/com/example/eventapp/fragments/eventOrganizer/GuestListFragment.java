package com.example.eventapp.fragments.eventOrganizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventapp.AgendaPdfExporter;
import com.example.eventapp.GuestListPdfExporter;
import com.example.eventapp.R;
import com.example.eventapp.adapters.eventOrganizer.AgendaActivityAdapter;
import com.example.eventapp.adapters.eventOrganizer.GuestListAdapter;
import com.example.eventapp.databinding.AgendaFormBinding;
import com.example.eventapp.databinding.GuestListBinding;
import com.example.eventapp.model.AgendaActivity;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.Guest;
import com.example.eventapp.repositories.AgendaActivityRepo;
import com.example.eventapp.repositories.EventRepo;
import com.example.eventapp.repositories.GuestRepo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public class GuestListFragment extends Fragment {

    private GuestListBinding binding;
    private Event event = new Event();
    public ArrayList<Guest> guests = new ArrayList<>();
    public static GuestListFragment newInstance(String eventId) {
        GuestListFragment f = new GuestListFragment();
        f.event.setId(eventId);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = GuestListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        getEvent();
        getGuests();

        //button back
        Button btnBack = root.findViewById(R.id.button_back);
        btnBack.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        //button new guest
        Button btnNewGuest = root.findViewById(R.id.button_new_guest);
        btnNewGuest.setOnClickListener(v -> {
            GuestFormFragment popup = GuestFormFragment.newInstance(this, event.getId(), false, null);
            popup.show(getChildFragmentManager(), "new_activity");
        });


        //button pdf
        Button btnPdf = root.findViewById(R.id.button_pdf);
        btnPdf.setOnClickListener(v -> {
            GuestListPdfExporter.exportToPdf(guests, event);
            Toast.makeText(getContext(), "Pdf report generated!", Toast.LENGTH_SHORT).show();
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void getEvent(){
        EventRepo.getById(event.getId(), new EventRepo.EventFetchCallback() {
            @Override
            public void onEventObjectFetched(Event e, String errorMessage) {
                EventRepo.EventFetchCallback.super.onEventObjectFetched(e, errorMessage);
                event = e;

                TextView name = binding.getRoot().findViewById(R.id.event_name);
                name.setText(event.getName());

                TextView date = binding.getRoot().findViewById(R.id.event_date);
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, e.getDate().getYear() + 1900);
                c.set(Calendar.MONTH, e.getDate().getMonth());
                c.set(Calendar.DAY_OF_MONTH, e.getDate().getDate());
                LocalDate localDate = c.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                date.setText(localDate.format(formatter));
            }
        });
    }


    public void getGuests()
    {
        GuestListFragment tempF = this;
        GuestRepo.getByEventId(event.getId(), new GuestRepo.GuestFetchCallback() {
            @Override
            public void onGuestFetch(ArrayList<Guest> g) {
                GuestRepo.GuestFetchCallback.super.onGuestFetch(guests);
                guests = g;

                GuestListAdapter adapter = new GuestListAdapter(getContext(), guests, tempF);
                ListView guestListView = binding.getRoot().findViewById(R.id.listViewGuests);
                guestListView.setAdapter(adapter);
            }
        });
    }
}
