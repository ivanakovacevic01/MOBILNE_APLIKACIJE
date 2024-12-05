package com.example.eventapp.adapters.eventOrganizer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventapp.R;
import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.fragments.eventOrganizer.BudgetingHomeFragment;
import com.example.eventapp.fragments.eventOrganizer.CreateAgendaActivityFragment;
import com.example.eventapp.fragments.eventOrganizer.CreateAgendaFragment;
import com.example.eventapp.model.AgendaActivity;
import com.example.eventapp.model.Event;
import com.example.eventapp.repositories.AgendaActivityRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.SubcategoryRepo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AgendaActivityAdapter extends ArrayAdapter<AgendaActivity> {
    private ArrayList<AgendaActivity> activities;
    private CreateAgendaFragment fragment;
    public AgendaActivityAdapter(Context context, ArrayList<AgendaActivity> items, CreateAgendaFragment f){
        super(context, R.layout.agenda_activity_card, items);
        this.activities = items;
        this.fragment = f;
    }

    @Override
    public int getCount() {
        return activities.size();
    }


    @Nullable
    @Override
    public AgendaActivity getItem(int position) {
        return activities.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AgendaActivity e = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.agenda_activity_card,
                    parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.activity_name);
        textViewName.setText(e.getName());
        TextView textViewDescription = convertView.findViewById(R.id.activity_desc);
        textViewDescription.setText(e.getDescription());

        TextView textViewDate = convertView.findViewById(R.id.start_time);
        textViewDate.setText(e.getStartTime());

        TextView textViewDate2 = convertView.findViewById(R.id.end_time);
        textViewDate2.setText(e.getEndTime());

        ImageView btnDelete = convertView.findViewById(R.id.imageViewDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure?")
                        .setTitle("Delete activity")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                fragment.getActivities();
                                AgendaActivityRepo.delete(e.getId());
                                Toast.makeText(getContext(), "Successfully deleted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("NO", null);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        return convertView;
    }
}
