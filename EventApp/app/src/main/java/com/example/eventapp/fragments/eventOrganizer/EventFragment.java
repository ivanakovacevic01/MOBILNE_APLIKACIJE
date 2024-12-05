package com.example.eventapp.fragments.eventOrganizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentEventBinding;
import com.example.eventapp.fragments.FragmentTransition;

public class EventFragment extends Fragment {

    private FragmentEventBinding binding;


    public EventFragment() {
        // Required empty public constructor
    }


    public static EventFragment newInstance() {
        EventFragment fragment = new EventFragment();
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
        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        FragmentTransition.to(EventListFragment.newInstance(), getActivity(),
                false, R.id.event_fragment);


        return root;

    }
}
