package com.example.eventapp.adapters.events;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventapp.R;
import com.example.eventapp.fragments.employees.WeekViewFragment;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.ReservationStatus;
import com.example.eventapp.model.WeeklyEvent;
import com.example.eventapp.repositories.ReservationRepo;
import com.example.eventapp.repositories.WeeklyEventRepo;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;
    private boolean exist;
    private String employeeId;

    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener,String id)
    {
        this.days = days;
        this.onItemListener = onItemListener;
        this.employeeId=id;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendarcell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        exist=false;
        if(days.size() > 15) //month view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        else // week view
            layoutParams.height = (int) parent.getHeight();

        return new CalendarViewHolder(view, onItemListener, days);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        final LocalDate date = days.get(position);
        if(date == null)
            holder.dayOfMonth.setText("");
        else
        {
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if(date.equals(CalendarUtils.selectedDate))
                holder.parentView.setBackgroundColor(Color.LTGRAY);
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(employeeId==null)
            employeeId = mAuth.getCurrentUser().getUid();

        final LocalDate datum = days.get(position);
        if (datum == null) {
            holder.dayOfMonth.setText("");
            holder.dayOfMonth.setBackgroundColor(Color.TRANSPARENT);
        } else {
            holder.dayOfMonth.setText(String.valueOf(datum.getDayOfMonth()));
            Log.i("EventApp","datum "+datum);
            hasEventForDate(date, employeeId, new OnEventCheckListener() {
                @Override
                public void onEventCheck(boolean hasEvent) {
                    if (hasEvent) {
                        holder.dayOfMonth.setBackgroundColor(Color.RED);
                    } else {
                        holder.dayOfMonth.setBackgroundColor(Color.WHITE);
                    }
                }
            });
            hasReservationForDate(date, employeeId, new OnEventCheckListener() {
                @Override
                public void onEventCheck(boolean hasEvent) {
                    if (hasEvent) {
                        holder.dayOfMonth.setBackgroundColor(Color.RED);
                    } else {
                        holder.dayOfMonth.setBackgroundColor(Color.WHITE);
                    }
                }
            });
        }
    }
    private void hasEventForDate(LocalDate date, String id, OnEventCheckListener listener) {
        WeeklyEventRepo.getByEmployeeId(id, new WeeklyEventRepo.WeeklyEventFetchCallback() {
            @Override
            public void onWeeklyEventFetch(ArrayList<WeeklyEvent> events) {
                for (WeeklyEvent event : events) {
                    LocalDate eventDate = LocalDate.parse(event.getDate());
                    if (eventDate.equals(date)) {
                        listener.onEventCheck(true);
                        return;
                    }
                }
                listener.onEventCheck(false);
            }
        });
    }

    private void hasReservationForDate(LocalDate date, String id, OnEventCheckListener listener) {
        ReservationRepo.getByEmployeeId(id, new ReservationRepo.ReservationFetchCallback() {
            @Override
            public void onReservationFetch(ArrayList<Reservation> reservations) {
                ReservationRepo.ReservationFetchCallback.super.onReservationFetch(reservations);
                for (Reservation r : reservations) {
                    Date eventDate = r.getEventDate();
                    LocalDate localEventDate = eventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    Log.i("LocalDate","event "+localEventDate+" "+date);
                    if (localEventDate.equals(date) && r.getStatus().equals(ReservationStatus.ACCEPTED)) {
                        listener.onEventCheck(true);
                        return;
                    }
                }
                listener.onEventCheck(false);
            }
        });
    }
    interface OnEventCheckListener {
        void onEventCheck(boolean hasEvent);
    }

    @Override
    public int getItemCount()
    {
        return days.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, LocalDate date);
    }
}