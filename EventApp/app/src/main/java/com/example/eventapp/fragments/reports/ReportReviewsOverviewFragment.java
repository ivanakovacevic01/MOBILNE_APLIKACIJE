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
 * Use the {@link ReportReviewsOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportReviewsOverviewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReportReviewsOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportReviewsOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportReviewsOverviewFragment newInstance(String param1, String param2) {
        ReportReviewsOverviewFragment fragment = new ReportReviewsOverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentTransition.to(ReportReviewsListFragment.newInstance(), getActivity(),
                false, R.id.scroll_report_reviews_list);
        return inflater.inflate(R.layout.fragment_report_reviews_overview, container, false);
    }
}