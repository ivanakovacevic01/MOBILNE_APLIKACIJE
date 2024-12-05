package com.example.eventapp.fragments.ratings;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.eventapp.R;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.NotificationStatus;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Rating;
import com.example.eventapp.model.RatingStatus;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.RatingRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RatingCompanyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingCompanyFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RatingBar ratingBar;
    private EditText commentEditText;
    private Button submitButton;
    private String companyId;
    private FirebaseUser currentUser;

    public RatingCompanyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RatingCompanyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RatingCompanyFragment newInstance(String param1, String param2) {
        RatingCompanyFragment fragment = new RatingCompanyFragment();
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
        View view = inflater.inflate(R.layout.fragment_rating_company, container, false);
        ratingBar = view.findViewById(R.id.ratingBar);
        commentEditText = view.findViewById(R.id.commentEditText);
        submitButton = view.findViewById(R.id.submitButton);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Bundle bundle=getArguments();
        if(bundle!=null){
            companyId=bundle.getString("companyId");
        }
        submitButton.setOnClickListener(v -> {
            int rating = (int) ratingBar.getRating();
            String comment = commentEditText.getText().toString();
            RatingRepo.create(createRating(rating,comment), new RatingRepo.RatingFetchCallback() {
                @Override
                public void onRatingObjectFetched(Rating rating, String errorMessage) {
                    RatingRepo.RatingFetchCallback.super.onRatingObjectFetched(rating, errorMessage);
                    //Intent intent = new Intent("custom-rating");
                    //intent.putExtra("rating_added", true);
                    //LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
                    createNotification();
                    dismiss();
                }
            });
        });

        return view;
    }
    private Rating createRating(int rating,String comment){
        Rating newRating = new Rating();
        newRating.rate=rating;
        newRating.createdDate=(new Date()).toString();
        newRating.companyId=companyId;
        newRating.comment=comment;
        newRating.userId=currentUser.getUid();
        newRating.status= RatingStatus.NOT_REPORTED;
        return newRating;
    }

    private void createNotification() {
        final Notification notification = new Notification();
        notification.setDate((new Date()).toString());
        notification.setReceiverRole(UserType.OWNER);
        notification.setMessage("Company rated by " + currentUser.getEmail() + " at " + (new Date()).toString() + ".");
        notification.setSenderId(currentUser.getUid());
        notification.setStatus(NotificationStatus.NEW);

        OwnerRepo.getByFirmId(companyId, new OwnerRepo.OwnerFetchCallback() {
            @Override
            public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                if(owner!=null){
                    notification.setReceiverId(owner.getId());
                    NotificationRepo.create(notification);
                }
            }
        });
    }
}