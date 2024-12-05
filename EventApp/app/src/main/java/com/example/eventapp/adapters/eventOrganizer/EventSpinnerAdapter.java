package com.example.eventapp.adapters.eventOrganizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventapp.R;
import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.fragments.eventOrganizer.BudgetingHomeFragment;
import com.example.eventapp.model.Event;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EventSpinnerAdapter  extends ArrayAdapter<Event> {
    private ArrayList<Event> events;

    public EventSpinnerAdapter(Context context, ArrayList<Event> items){
        super(context, R.layout.event_type_for_spinner, R.id.event_type_id, items);
        this.events = items;
    }

    @Override
    public int getCount() {
        return events.size();
    }


    @Nullable
    @Override
    public Event getItem(int position) {
        return events.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Event e = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_type_for_spinner,
                    parent, false);
        }

        TextView name = convertView.findViewById(R.id.event_type_id);
        name.setText(e.getName());
        name.setTextSize(18);

        TextView desc = convertView.findViewById(R.id.event_type_id_desc);
        desc.setVisibility(View.GONE);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        Event e = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_type_for_spinner,
                    parent, false);
        }

        TextView name = convertView.findViewById(R.id.event_type_id);
        name.setText(e.getName());
        name.setTextSize(18);
        name.setPadding(10,10,10,10);

        TextView desc = convertView.findViewById(R.id.event_type_id_desc);
        desc.setVisibility(View.GONE);

        return convertView;
    }
}
