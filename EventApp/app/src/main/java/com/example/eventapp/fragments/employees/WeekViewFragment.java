package com.example.eventapp.fragments.employees;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventapp.R;
import com.example.eventapp.adapters.events.CalendarAdapter;
import com.example.eventapp.adapters.events.CalendarUtils;
import com.example.eventapp.adapters.events.EventAdapter;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.WeeklyEvent;
import com.example.eventapp.repositories.WeeklyEventRepo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class WeekViewFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;
    public static ArrayList<Event> events = new ArrayList<Event>();
    private String employeeId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("event_added", false)) {
                setEventAdapter();
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mMessageReceiver);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.example.eventapp.R.layout.fragment_week_view, container, false);
        initWidgets(view);
        setWeekView();
        ImageView previousWeekBtn = view.findViewById(R.id.previousWeekAction);
        Bundle bundle=getArguments();
        employeeId=bundle.getString("employeeId");
        previousWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
                setWeekView();
            }
        });
        ImageView nextWeekBtn = view.findViewById(R.id.nextWeekAction);

        nextWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
                setWeekView();
            }
        });
        Button newEventBtn=view.findViewById(R.id.newEvent);
        newEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNonWorkingDay(CalendarUtils.selectedDate)) {
                    EventEditFragment dialog = new EventEditFragment();
                    Bundle bundle=new Bundle();
                    bundle.putString("employeeId",employeeId);
                    bundle.putString("date",CalendarUtils.selectedDate.toString());
                    dialog.setArguments(bundle);
                    dialog.show(requireActivity().getSupportFragmentManager(), "EventEditFragment");
                } else {
                    Toast.makeText(requireContext(), "You cannot add an event on a non-working day.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
    private boolean isNonWorkingDay(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
    public static WeekViewFragment newInstance() {
        WeekViewFragment fragment = new WeekViewFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView1);
        monthYearText = view.findViewById(R.id.monthYearTV);
        eventListView = view.findViewById(R.id.eventListView);
    }
    private void setWeekView() {
        Log.i("EventApp","WeekView" +CalendarUtils.selectedDate);

        monthYearText.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = CalendarUtils.daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this,employeeId);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setEventAdapter();
    }
    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }
    @Override
    public void onResume() {
        super.onResume();
        setEventAdapter();
    }
    public void setEventAdapter() {
        eventsForSelectedDate(CalendarUtils.selectedDate, new WeeklyEventCallback() {
            @Override
            public void onWeeklyEventsFetched(ArrayList<WeeklyEvent> dailyEvents) {
                EventAdapter eventAdapter = new EventAdapter(requireContext(), dailyEvents);
                eventListView.setAdapter(eventAdapter);
            }
        });
    }

    private void eventsForSelectedDate(LocalDate selectedDate, WeeklyEventCallback callback) {
        ArrayList<WeeklyEvent> dailyEvents = new ArrayList<>();
        WeeklyEventRepo weeklyEventRepo = new WeeklyEventRepo();
        weeklyEventRepo.getAll(new WeeklyEventRepo.WeeklyEventFetchCallback() {
            @Override
            public void onWeeklyEventFetch(ArrayList<WeeklyEvent> events) {
                for (WeeklyEvent event : events) {
                    if (event.getEmployeeId().equals(employeeId) && event.getDate().equals(selectedDate.toString())) {
                        dailyEvents.add(event);
                    }
                }
                callback.onWeeklyEventsFetched(dailyEvents);
            }
        });
    }
    public interface WeeklyEventCallback {
        void onWeeklyEventsFetched(ArrayList<WeeklyEvent> events);
    }

}
