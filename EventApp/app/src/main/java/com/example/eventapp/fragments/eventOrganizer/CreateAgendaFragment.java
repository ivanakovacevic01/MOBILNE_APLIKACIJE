package com.example.eventapp.fragments.eventOrganizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventapp.AgendaPdfExporter;
import com.example.eventapp.R;
import com.example.eventapp.adapters.eventOrganizer.AgendaActivityAdapter;
import com.example.eventapp.databinding.AgendaFormBinding;
import com.example.eventapp.model.AgendaActivity;
import com.example.eventapp.model.DialogType;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.EventType;
import com.example.eventapp.repositories.AgendaActivityRepo;
import com.example.eventapp.repositories.EventRepo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public class CreateAgendaFragment extends Fragment {

    private AgendaFormBinding binding;
    private Event event = new Event();
    public ArrayList<AgendaActivity> activities = new ArrayList<>();
    public static CreateAgendaFragment newInstance(String eventId) {
        CreateAgendaFragment f = new CreateAgendaFragment();
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
        binding = AgendaFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        getEvent();
        getActivities();

        //button back
        Button btnBack = root.findViewById(R.id.button_back);
        btnBack.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        //button new activity
        Button btnNewActivity = root.findViewById(R.id.button_new_activity);
        btnNewActivity.setOnClickListener(v -> {
            CreateAgendaActivityFragment popup = CreateAgendaActivityFragment.newInstance(this, event.getId());
            popup.show(getChildFragmentManager(), "new_activity");
        });

        //button pdf
        Button btnPdf = root.findViewById(R.id.button_pdf);
        btnPdf.setOnClickListener(v -> {
            AgendaPdfExporter.exportToPdf(activities, event);
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


    public void getActivities()
    {
        CreateAgendaFragment tempF = this;
        AgendaActivityRepo.getByEventId(event.getId(), new AgendaActivityRepo.AgendaActivityFetchCallback() {
            @Override
            public void onAgendaActivityFetch(ArrayList<AgendaActivity> a) {
                activities = a;

                activities.sort(Comparator.comparing(activity -> {
                    try {
                        return new SimpleDateFormat("HH:mm").parse(activity.getStartTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return null;
                    }
                }));
                AgendaActivityAdapter adapter = new AgendaActivityAdapter(getActivity(), activities, tempF);
                ListView listView = binding.getRoot().findViewById(R.id.listViewActivities);
                listView.setAdapter(adapter);
            }
        });
    }
}
