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

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.R;
import com.example.eventapp.fragments.eventOrganizer.BudgetingHomeFragment;
import com.example.eventapp.fragments.eventOrganizer.CreateAgendaFragment;
import com.example.eventapp.fragments.eventOrganizer.GuestListFragment;
import com.example.eventapp.model.Event;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class EventListAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;

    public EventListAdapter(Context context, ArrayList<Event> items){
        super(context, R.layout.event_card, items);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_card,
                    parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.event_name);
        textViewName.setText(e.getName());
        TextView textViewDescription = convertView.findViewById(R.id.event_desc);
        textViewDescription.setText(e.getDescription());

        TextView textViewDate = convertView.findViewById(R.id.event_date);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, e.getDate().getYear());
        c.set(Calendar.MONTH, e.getDate().getMonth());
        c.set(Calendar.DAY_OF_MONTH, e.getDate().getDate());
        LocalDate localDate = c.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        textViewDate.setText(localDate.format(formatter));

        Button btnBudget = convertView.findViewById(R.id.button_manage_budgeting);
        btnBudget.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = ((HomeActivity) getContext()).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.event_fragment, BudgetingHomeFragment.newInstance(e.getId())).addToBackStack("BUDGET");
            fragmentTransaction.commit();
        });

        Button btnAgenda = convertView.findViewById(R.id.agenda_button);
        btnAgenda.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = ((HomeActivity) getContext()).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.event_fragment, CreateAgendaFragment.newInstance(e.getId())).addToBackStack("AGENDA");
            fragmentTransaction.commit();
        });

        Button btnGuestList = convertView.findViewById(R.id.guest_list_button);
        btnGuestList.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = ((HomeActivity) getContext()).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.event_fragment, GuestListFragment.newInstance(e.getId())).addToBackStack("GUEST_LIST");
            fragmentTransaction.commit();
        });
        return convertView;
    }
}
