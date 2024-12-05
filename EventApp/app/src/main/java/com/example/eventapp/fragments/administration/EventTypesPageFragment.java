package com.example.eventapp.fragments.administration;

import androidx.fragment.app.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentEventTypesPageBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.EventType;

import java.util.ArrayList;

public class EventTypesPageFragment extends Fragment {

    public static ArrayList<EventType> types = new ArrayList<EventType>();
    private FragmentEventTypesPageBinding binding;


    public static EventTypesPageFragment newInstance() {
        return new EventTypesPageFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentEventTypesPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FragmentTransition.to(EventTypesListFragment.newInstance(types), getActivity(),
                false, R.id.scroll_event_types_list);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}