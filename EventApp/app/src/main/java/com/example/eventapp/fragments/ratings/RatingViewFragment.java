package com.example.eventapp.fragments.ratings;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.eventapp.R;
import com.example.eventapp.adapters.RatingAdapter;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.eventOrganizer.PopupDialogFragment;
import com.example.eventapp.model.DialogType;
import com.example.eventapp.model.Filters;
import com.example.eventapp.model.Rating;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.RatingRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RatingViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingViewFragment extends Fragment implements PopupDialogFragment.OnDateRangeSelectedListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DialogType type = DialogType.Date;
    private boolean isSelected=false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerViewCommentsRatings;
    private RatingAdapter adapter;
    private ArrayList<Rating> commentRatingList=new ArrayList<>();
    private ArrayList<Rating> filteredComments=new ArrayList<>();
    private String companyId;

    public RatingViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RatingViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RatingViewFragment newInstance(String param1, String param2) {
        RatingViewFragment fragment = new RatingViewFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_rating_view, container, false);
        commentRatingList = new ArrayList<>();
        this.setAdapter(rootView);
        Button btnDateRange = rootView.findViewById(R.id.button_set_date_range);
        btnDateRange.setOnClickListener(v2 -> {
            Bundle bundle1=new Bundle();
            bundle1.putString("companyId",companyId);
            Filters filters = new Filters();
            PopupDialogFragment popupDialog = PopupDialogFragment.newInstance(DialogType.RatingDate, filters);
            popupDialog.setArguments(bundle1);
            popupDialog.setOnDateRangeSelectedListener(RatingViewFragment.this);
            FragmentTransition.to(popupDialog,getActivity(),true, R.id.scroll_profile);

        });
        Button btnAll = rootView.findViewById(R.id.button_all);
        btnAll.setOnClickListener(v2 -> {
            isSelected=false;
            this.setAdapter(rootView);
        });
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        UserRepo.getUserById(user.getUid(), new UserRepo.UserFetchCallback() {
            @Override
            public void onUserObjectFetched(User user, String errorMessage) {
                UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                if(!user.getType().equals(UserType.OWNER)){
                    btnDateRange.setVisibility(View.GONE);
                    btnAll.setVisibility(View.GONE);
                }
            }
        });


        return rootView;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterCommentsByDateRange(Date startDate, Date endDate) {
        filteredComments = new ArrayList<>();
        for (Rating comment : commentRatingList) {
            Date commentDate = convertToDate(comment.getCreatedDate());
            if ((commentDate.equals(convertToDate(startDate.toString())) || commentDate.after(convertToDate(startDate.toString()))) &&
                    (commentDate.equals(convertToDate(endDate.toString())) || commentDate.before(convertToDate(endDate.toString())))) {
                filteredComments.add(comment);
            }
        }
    }
    public static Date convertToDate(String dateStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

        Date date = null;
        try {
            date = inputFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
    @Override
    public void onDateRangeSelected(Date startDate, Date endDate) {
        isSelected=true;
        filterCommentsByDateRange(startDate, endDate);
    }

    private void setAdapter(View root)
    {
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewCommentsRatings);
        Log.i("adapterRatingView","usao opet");
        RatingRepo ratingRepo=new RatingRepo();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Bundle bundle=getArguments();
        if(bundle!=null){
            companyId=bundle.getString("companyId");
        }
        ratingRepo.getByCompanyId(companyId,new RatingRepo.RatingFetchCallback() {
            @Override
            public void onRatingFetch(ArrayList<Rating> ratings) {
                commentRatingList.clear();
                commentRatingList.addAll(ratings);
                adapter = new RatingAdapter(commentRatingList,getActivity());
                recyclerView.setAdapter(adapter);
                if(isSelected)
                    filter(root);
            }
        });

    }

    private void filter(View root){
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewCommentsRatings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.i("filter","usao"+filteredComments.size());
        adapter = new RatingAdapter(filteredComments, getActivity());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


}