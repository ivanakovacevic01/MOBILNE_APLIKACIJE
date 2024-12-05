package com.example.eventapp.fragments.eventOrganizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.adapters.eventOrganizer.EventListAdapter;
import com.example.eventapp.databinding.EventsManagingBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.EventType;
import com.example.eventapp.repositories.EventRepo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

public class EventListFragment extends Fragment {
    private EventsManagingBinding binding;


    public static EventListFragment newInstance() {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EventsManagingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        EventRepo eventRepo = new EventRepo();
        final EventListAdapter[] adapter = new EventListAdapter[1];
        ListView listView = root.findViewById(R.id.event_listView);
        eventRepo.getByOrganizer(new EventRepo.EventFetchCallback() {
            @Override
            public void onEventFetch(ArrayList<Event> events) {
                adapter[0] = new EventListAdapter(getActivity(), events);
                listView.setAdapter(adapter[0]);
            }

        }, SharedPreferencesManager.getEmail(getContext()));


        FloatingActionButton btnAdd = (FloatingActionButton) root.findViewById(R.id.floating_action_add_event_button);
        btnAdd.setOnClickListener(v -> {
            FragmentTransition.to(EventFormFragment.newInstance(), getActivity(), true, R.id.event_fragment);
        });
        return root;

    }

    public static ArrayList<Event> createEvents() {
        ArrayList<Event> exampleEvents = new ArrayList<>();

        // Example event 1
        Event event1 = new Event("Venčanje T i M", new Date(1707622400000L));
        event1.setType(new EventType("11", "Svadba", "Proslava venčanja", false));
        event1.setDescription("Opis venčanja T i M");
        event1.setMaxNumberOfParticipans(200);
        event1.setPrivate(true);
        event1.setLocation("Novi Sad");
        event1.setMaxKms(50.0);
        exampleEvents.add(event1);

        // Add more example events if needed...

        return exampleEvents;
    }
}
