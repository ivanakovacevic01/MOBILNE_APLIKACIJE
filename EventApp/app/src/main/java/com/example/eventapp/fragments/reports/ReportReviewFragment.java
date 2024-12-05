package com.example.eventapp.fragments.reports;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.eventapp.R;
import com.example.eventapp.model.ReportReview;
import com.example.eventapp.model.ReportReviewType;
import com.example.eventapp.repositories.ReportReviewRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportReviewFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText reasonEditText;
    private FirebaseUser currentUser;
    private String ratingId;

    public ReportReviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportReviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportReviewFragment newInstance(String param1, String param2) {
        ReportReviewFragment fragment = new ReportReviewFragment();
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
        View root= inflater.inflate(R.layout.fragment_report_review, container, false);
        reasonEditText = root.findViewById(R.id.etReason);
        Button submitButton = root.findViewById(R.id.btnSubmit);
        Button cancelButton = root.findViewById(R.id.btnCancel);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Bundle bundle=getArguments();
        if(bundle!=null){
            ratingId=bundle.getString("ratingId");
        }
        submitButton.setOnClickListener(v -> {
            String reason = reasonEditText.getText().toString();
            ReportReviewRepo.create(createReportReview(reason), new ReportReviewRepo.ReportReviewFetchCallback() {
                @Override
                public void onReportReviewObjectFetched(ReportReview reportReview, String errorMessage) {
                    ReportReviewRepo.ReportReviewFetchCallback.super.onReportReviewObjectFetched(reportReview, errorMessage);
                    Bundle result = new Bundle();
                    result.putBoolean("submitted", true);
                    getParentFragmentManager().setFragmentResult("reportReviewResult", result);
                    dismiss();
                }
            });
        });
        cancelButton.setOnClickListener(v -> {
            dismiss();
        });
        return root;
    }

    private ReportReview createReportReview(String reason){
        ReportReview reportReview=new ReportReview();
        reportReview.reason=reason;
        reportReview.date=(new Date()).toString();
        reportReview.type= ReportReviewType.REPORTED;
        reportReview.userId=currentUser.getUid();
        reportReview.ratingId=ratingId;
        return reportReview;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() instanceof FragmentActivity) {
            ((FragmentActivity) getActivity()).getSupportFragmentManager().setFragmentResult("requestKey", new Bundle());
        }
    }
}