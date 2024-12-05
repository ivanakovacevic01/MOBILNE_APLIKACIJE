package com.example.eventapp.fragments.reservations;

import androidx.fragment.app.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentReservationPageBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Reservation;

import java.util.ArrayList;

public class ReservationPageFragment extends Fragment{

    public static ArrayList<Reservation> reservations = new ArrayList<Reservation>();
    private FragmentReservationPageBinding binding;

    public static ReservationPageFragment newInstance() {
        return new ReservationPageFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentReservationPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FragmentTransition.to(ReservationListFragment.newInstance(reservations), getActivity(),
                false, R.id.scroll_reservations_list);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}