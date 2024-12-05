package com.example.eventapp.adapters.eventOrganizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.R;
import com.example.eventapp.model.EventType;

import java.util.ArrayList;

public class EventTypeAdapter extends ArrayAdapter<EventType> {

    private ArrayList<EventType> eventTypes;

    public EventTypeAdapter(Context context, ArrayList<EventType> types){
        super(context, R.layout.event_type_for_spinner, R.id.event_type_id, types);
        this.eventTypes = types;
    }

    @Override
    public int getCount() {
        return eventTypes.size();
    }


    @Nullable
    @Override
    public EventType getItem(int position) {
        return eventTypes.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        EventType e = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_type_for_spinner,
                    parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.event_type_id);
        textViewName.setText(e.getName());
        TextView textViewDesc = convertView.findViewById(R.id.event_type_id_desc);
        textViewDesc.setText(e.getDescription());
        return convertView;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        EventType e = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_type_for_spinner,
                    parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.event_type_id);
        textViewName.setText(e.getName());
        TextView textViewDesc = convertView.findViewById(R.id.event_type_id_desc);
        textViewDesc.setText(e.getDescription());
        return convertView;
    }
}
