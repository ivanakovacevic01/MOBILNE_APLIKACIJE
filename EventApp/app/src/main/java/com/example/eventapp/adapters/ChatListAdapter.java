package com.example.eventapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.eventapp.R;
import com.example.eventapp.fragments.ChatFragment;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.packages.PackageDetailsFragment;
import com.example.eventapp.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends ArrayAdapter<User> {
    private List<User> users = new ArrayList<>();
    private FragmentActivity context;

    public ChatListAdapter(FragmentActivity context, ArrayList<User> u){
        super(context, R.layout.chat_card, u);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_card,
                    parent, false);
        }

        //Add image

        TextView name = convertView.findViewById(R.id.name);
        name.setText(e.getFirstName() + " " + e.getLastName());

        ImageView mimageView= convertView.findViewById(R.id.image);
        Picasso.get().load(e.getImage()).into(mimageView);

        LinearLayout card = (LinearLayout) convertView.findViewById(R.id.card);
        card.setOnClickListener(v -> {

            FragmentTransition.to(ChatFragment.newInstance(e.getId()), context,
                    true, R.id.home_page_fragment);
        });

        return convertView;
    }
}
