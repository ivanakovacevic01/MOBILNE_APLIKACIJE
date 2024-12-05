package com.example.eventapp.adapters.eventOrganizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.R;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Filters;

import java.util.ArrayList;

public class EventTypeCheckboxAdapter extends ArrayAdapter<EventType> {

    private ArrayList<EventType> eventTypes;
    private Filters filters;
    public EventTypeCheckboxAdapter(Context context, ArrayList<EventType>eventTypes, Filters filters){
        super(context, R.layout.checkbox_layout, eventTypes);
        this.eventTypes = eventTypes;
        this.filters = filters;
    }

    @Override
    public int getCount() {
        if(eventTypes == null)
            return 0;
        return eventTypes.size();
    }


    @Nullable
    @Override
    public EventType getItem(int position) {
        return eventTypes.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        EventType subcategory = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.checkbox_layout,
                    parent, false);
        }
        CheckBox checkBox = convertView.findViewById(R.id.checkbox_layout_cb);
        checkBox.setText(subcategory.getName());

        if(filters.types.stream().anyMatch(c -> c.getId().equals(subcategory.getId())))
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    if(filters.types.stream().noneMatch(c -> c.getId().equals(subcategory.getId())) && checkBox.isChecked())
                        filters.types.add(subcategory);
                } else {
                    for(EventType t: filters.types){
                        if(t.getId().equals(subcategory.getId())){
                            filters.types.remove(t);
                            break;
                        }
                    }
                }
            }
        });

        return convertView;
    }

}
