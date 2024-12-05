package com.example.eventapp.adapters.events;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.R;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.WeeklyEvent;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends ArrayAdapter<WeeklyEvent>
{
    private ArrayList<WeeklyEvent> aEvents;
    public EventAdapter(@NonNull Context context, ArrayList<WeeklyEvent> events)
    {
        super(context, 0, events);
        aEvents=events;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        WeeklyEvent event = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_card2, parent, false);

        TextView eventName = convertView.findViewById(R.id.event_name);
        TextView eventTime=convertView.findViewById(R.id.event_time);
        TextView eventStatus=convertView.findViewById(R.id.status);
        eventName.setText(event.getName());
        eventTime.setText(event.getFrom()+"-"+event.getTo());
        eventStatus.setText(event.getEventType().toString());
        return convertView;
    }

}