package com.example.eventapp.fragments.reports;

import android.os.Bundle;

import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.adapters.ReportsAdapter;
import com.example.eventapp.model.Report;
import com.example.eventapp.repositories.ReportRepo;

import java.util.ArrayList;

public class ReportsList extends ListFragment {

    private ReportRepo reportRepo;
    private ReportsAdapter adapter;
    public ReportsList() {
    }


    public static ReportsList newInstance() {
        ReportsList fragment = new ReportsList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportRepo=new ReportRepo();
        reportRepo.getAllUserReports(new ReportRepo.ReportFetchCallback() {
            @Override
            public void onReportFetch(ArrayList<Report> reports) {
                ReportRepo.ReportFetchCallback.super.onReportFetch(reports);
                adapter=new ReportsAdapter(getActivity(),reports);
                setListAdapter(adapter);

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports_list, container, false);
    }
}