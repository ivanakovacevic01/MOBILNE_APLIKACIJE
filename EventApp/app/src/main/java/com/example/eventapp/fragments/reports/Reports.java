package com.example.eventapp.fragments.reports;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.fragments.FragmentTransition;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Reports#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Reports extends Fragment {

    public Reports() {
    }
    public static Reports newInstance() {
        Reports fragment = new Reports();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentTransition.to(ReportsList.newInstance(), getActivity(),
                false, R.id.scroll_reports_list);
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }
}