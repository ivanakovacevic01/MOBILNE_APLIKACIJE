package com.example.eventapp.fragments.reports;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.adapters.ReportReviewsAdapter;
import com.example.eventapp.model.ReportReview;
import com.example.eventapp.repositories.ReportReviewRepo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportReviewsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportReviewsListFragment extends ListFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ReportReviewRepo reportReviewRepo;
    private ReportReviewsAdapter adapter;

    public ReportReviewsListFragment() {
        // Required empty public constructor
    }


    public static ReportReviewsListFragment newInstance() {
        ReportReviewsListFragment fragment = new ReportReviewsListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportReviewRepo=new ReportReviewRepo();
        Log.i("onCreate","uslo");
        reportReviewRepo.getAll(new ReportReviewRepo.ReportReviewFetchCallback() {
            @Override
            public void onReportReviewFetch(ArrayList<ReportReview> reportReviews) {
                ReportReviewRepo.ReportReviewFetchCallback.super.onReportReviewFetch(reportReviews);
                adapter=new ReportReviewsAdapter(getContext(),reportReviews, (AppCompatActivity) getActivity());
                setListAdapter(adapter);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_reviews_list, container, false);
    }
}