package com.example.eventapp.fragments.administration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.eventapp.R;
import com.example.eventapp.adapters.eventTypes.EventTypesListAdapter;
import com.example.eventapp.databinding.FragmentEventTypesListBinding;
import com.example.eventapp.model.EventType;
import com.example.eventapp.repositories.EventTypeRepo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class EventTypesListFragment extends ListFragment {
    private EventTypesListAdapter adapter;
    private ArrayList<EventType> mTypes;
    private FragmentEventTypesListBinding binding;
    private EventTypeRepo eventTypeRepository;

    public static EventTypesListFragment newInstance(ArrayList<EventType> types){
        EventTypesListFragment fragment = new EventTypesListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("TYPES_LIST", types);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventTypeRepository = new EventTypeRepo();
        eventTypeRepository.getAllEventTypes(new EventTypeRepo.EventTypeFetchCallback() {
            @Override
            public void onEventTypeFetch(ArrayList<EventType> eventTypes) {
                if (eventTypes != null) {
                    adapter = new EventTypesListAdapter(getActivity(), eventTypes);
                    setListAdapter(adapter);
                    //mozda zatreba ovo ispod posle
                    /*adapter.setEventTypeUpdateListener(new EventTypesListAdapter.EventTypeUpdateListener() {
                        @Override
                        public void onEventTypesUpdated(ArrayList<EventType> eventTypes) {
                            // Osvežite adapter sa novim podacima
                            adapter.clear();
                            adapter.addAll(eventTypes);
                            adapter.notifyDataSetChanged();
                        }
                    });*/
                }
            }
        });



    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("ShopApp", "onCreateView Event Type List Fragment");
        binding = FragmentEventTypesListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FloatingActionButton button = root.findViewById(R.id.floating_action_add_event_type_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddingEventTypeFragment dialog = new AddingEventTypeFragment();
                dialog.show(requireActivity().getSupportFragmentManager(), "AddingEventTypeFragment");            }
        });

        return root;


    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
