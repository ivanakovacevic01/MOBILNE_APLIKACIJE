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
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.NotificationStatus;
import com.example.eventapp.model.RejectingReason;
import com.example.eventapp.model.ReportReview;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.RejectingReasonRepo;
import com.example.eventapp.repositories.ReportReviewRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RejectingReasonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RejectingReasonFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText reasonEditText;
    private FirebaseUser currentUser;
    private String reportId;



    public RejectingReasonFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RejectingReasonFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RejectingReasonFragment newInstance(String param1, String param2) {
        RejectingReasonFragment fragment = new RejectingReasonFragment();
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
        View root= inflater.inflate(R.layout.fragment_rejecting_reason, container, false);
        reasonEditText = root.findViewById(R.id.etReason);
        Button submitButton = root.findViewById(R.id.btnSubmit);
        Button cancelButton = root.findViewById(R.id.btnCancel);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Bundle bundle=getArguments();
        if(bundle!=null){
            reportId=bundle.getString("reportId");
        }
        submitButton.setOnClickListener(v -> {
            String reason = reasonEditText.getText().toString();
            RejectingReasonRepo.create(createRejectingReason(reason), new RejectingReasonRepo.RejectingReasonFetchCallback() {
                @Override
                public void onRejectingReasonObjectFetched(RejectingReason rejectingReason, String errorMessage) {
                    RejectingReasonRepo.RejectingReasonFetchCallback.super.onRejectingReasonObjectFetched(rejectingReason, errorMessage);
                    createNotification(rejectingReason);
                    dismiss();
                }
            });
        });
        cancelButton.setOnClickListener(v -> {
            dismiss();
        });
        return root;
    }
    private RejectingReason createRejectingReason(String reason){
        RejectingReason rejectingReason=new RejectingReason();
        rejectingReason.reason=reason;
        rejectingReason.reportId=reportId;
        return rejectingReason;
    }

    private void createNotification(RejectingReason rejectingReason){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Notification notification=new Notification();
        notification.setMessage("Report is rejected. Rejecting reason: "+rejectingReason.getReason());
        notification.setStatus(NotificationStatus.NEW);
        notification.setReceiverRole(UserType.OWNER);
        notification.setSenderId(currentUser.getUid());
        notification.setDate((new Date().toString()));
        ReportReviewRepo.getById(reportId, new ReportReviewRepo.ReportReviewFetchCallback() {
            @Override
            public void onReportReviewObjectFetched(ReportReview reportReview, String errorMessage) {
                ReportReviewRepo.ReportReviewFetchCallback.super.onReportReviewObjectFetched(reportReview, errorMessage);
                UserRepo.getUserById(reportReview.getUserId(), new UserRepo.UserFetchCallback() {
                    @Override
                    public void onUserObjectFetched(User user, String errorMessage) {
                        UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                        notification.setReceiverId(user.getId());
                        NotificationRepo.create(notification);
                    }
                });
            }
        });

    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() instanceof FragmentActivity) {
            ((FragmentActivity) getActivity()).getSupportFragmentManager().setFragmentResult("requestKey", new Bundle());
        }
    }
}