package com.example.eventapp.adapters.eventOrganizer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventapp.R;
import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.fragments.eventOrganizer.CreateAgendaFragment;
import com.example.eventapp.fragments.eventOrganizer.GuestFormFragment;
import com.example.eventapp.fragments.eventOrganizer.GuestListFragment;
import com.example.eventapp.model.AgendaActivity;
import com.example.eventapp.model.Guest;
import com.example.eventapp.repositories.AgendaActivityRepo;
import com.example.eventapp.repositories.GuestRepo;

import java.util.ArrayList;

public class GuestListAdapter extends ArrayAdapter<Guest> {
    private ArrayList<Guest> guests;
    private GuestListFragment fragment;
    public GuestListAdapter(Context context, ArrayList<Guest> items, GuestListFragment f){
        super(context, R.layout.guest_card, items);
        this.guests = items;
        this.fragment = f;
    }

    @Override
    public int getCount() {
        return guests.size();
    }


    @Nullable
    @Override
    public Guest getItem(int position) {
        return guests.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Guest e = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.guest_card,
                    parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.activity_name);
        textViewName.setText(e.getName() + " " + e.getLastName());

        TextView textViewAge = convertView.findViewById(R.id.activity_desc);
        textViewAge.setText(e.getAgeGroup());

        TextView invited = convertView.findViewById(R.id.isInvited);
        if(e.isInvited())
            invited.setText("✔");
        else
            invited.setText("❌");
        TextView confirmed = convertView.findViewById(R.id.confirmed);
        if(e.isConfirmed())
            confirmed.setText("✔");
        else
            confirmed.setText("❌");

        TextView specialRequest = convertView.findViewById(R.id.special_requests);
        specialRequest.setText(e.getSpecialRequest().toString());

        ImageView btnDelete = convertView.findViewById(R.id.imageViewDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure?")
                        .setTitle("Delete guest")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                fragment.getGuests();
                                GuestRepo.delete(e.getId());
                                Toast.makeText(getContext(), "Successfully deleted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("NO", null);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        ImageView btnEdit = convertView.findViewById(R.id.imageViewEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = ((HomeActivity) getContext()).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.event_fragment, GuestFormFragment.newInstance(fragment, e.getEventId(), true, e)).addToBackStack("GUEST");
                fragmentTransaction.commit();
            }
        });

        return convertView;
    }
}
