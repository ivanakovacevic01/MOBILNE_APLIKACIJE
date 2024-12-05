package com.example.eventapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.eventapp.R;
import com.example.eventapp.fragments.ChatFragment;
import com.example.eventapp.fragments.ChooseUserDialog;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.employees.EditCompanyFragment;
import com.example.eventapp.fragments.employees.EmployeeDetailsFragment;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChooseUserAdapter extends ArrayAdapter<User> {
    private List<User> users = new ArrayList<>();
    private FragmentActivity context;

    public ChooseUserAdapter(FragmentActivity context, ArrayList<User> u){
        super(context, R.layout.user_detail_card, u);
        this.users = u;
        this.context = context;
    }

    @Override
    public int getCount() {
        return users.size();
    }


    @Nullable
    @Override
    public User getItem(int position) {
        return users.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User e = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_detail_card,
                    parent, false);
        }

        if(e != null)
        {
            TextView name = convertView.findViewById(R.id.name);
            name.setText(e.getFirstName() + " " + e.getLastName());

            TextView role = convertView.findViewById(R.id.role);
            role.setText(e.getType().toString());

            Button message = convertView.findViewById(R.id.addButton);
            message.setOnClickListener(s -> {

                FragmentTransition.to(ChatFragment.newInstance(e.getId()), context,
                        true, R.id.home_page_fragment);
            });
        }


        return convertView;
    }
}
