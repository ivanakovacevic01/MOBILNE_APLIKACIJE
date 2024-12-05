package com.example.eventapp.fragments.employees;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.eventapp.adapters.events.CalendarUtils.monthYearFromDate;

import static com.example.eventapp.adapters.events.CalendarUtils.daysInMonthArray;

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.R;
import com.example.eventapp.adapters.events.CalendarAdapter;
import com.example.eventapp.adapters.events.CalendarUtils;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.ArrayList;


public class CalendarFragment extends  DialogFragment implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private View view;
    private String employeeId;
    private FirebaseUser currentUser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        initWidgets(rootView);
        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();
        ImageView nextMonthBtn = rootView.findViewById(R.id.nextMonthAction);
        Bundle bundle = getArguments();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        employeeId = bundle.getString("employeeId");
        if(employeeId==null){
            employeeId=currentUser.getUid();
        }
        nextMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
                setMonthView();
            }
        });

        ImageView previousMonthBtn = rootView.findViewById(R.id.previousMonthAction);

        previousMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
                setMonthView();
            }
        });
        view=rootView;
        return rootView;
    }
    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
    private void initWidgets(View rootView) {
        calendarRecyclerView = rootView.findViewById(R.id.calendarRecyclerView);
        monthYearText = rootView.findViewById(R.id.monthYearTV);
    }
    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);
        employeeId = getArguments().getString("employeeId");
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this,employeeId);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }
    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            Bundle bundle = new Bundle();
            bundle.putString("employeeId", employeeId);
            Fragment weekViewFragment = new WeekViewFragment();
            weekViewFragment.setArguments(bundle);
            UserRepo.getUserByEmail(currentUser.getEmail(), new UserRepo.UserFetchCallback() {
                @Override
                public void onUserObjectFetched(User user, String errorMessage) {
                    if (user.getType().equals(UserType.EMPLOYEE))
                        FragmentTransition.to(weekViewFragment, getActivity(), true, R.id.scroll_profile);
                    else
                        FragmentTransition.to(weekViewFragment, getActivity(), true, R.id.scroll_employees_list);
                }
            });
        }
    }
}
