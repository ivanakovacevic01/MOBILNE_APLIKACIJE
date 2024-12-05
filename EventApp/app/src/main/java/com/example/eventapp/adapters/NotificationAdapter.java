package com.example.eventapp.adapters;

import android.content.Context;
import android.util.Log;
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

import com.example.eventapp.R;
import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.products.ProductDetails;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.NotificationStatus;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.UserRepo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends ArrayAdapter<Notification> {
    private ArrayList<Notification> aNotifications;

    public NotificationAdapter(Context context, ArrayList<Notification> notifications){
        super(context, R.layout.notification_card, notifications);
        aNotifications = notifications;
    }

    @Override
    public int getCount() {
        return aNotifications.size();
    }


    @Nullable
    @Override
    public Notification getItem(int position) {
        return aNotifications.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Notification notification = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_card,
                    parent, false);
        }
        TextView sender = convertView.findViewById(R.id.sender);
        TextView reason = convertView.findViewById(R.id.reason);
        TextView date=convertView.findViewById(R.id.notification_date);
        ImageView imageView=convertView.findViewById(R.id.user_image);
        Button onMarkAsSeen=convertView.findViewById(R.id.markAsSeen);
        reason.setText(notification.getMessage());
        date.setText(notification.getDate().substring(0, notification.getDate().indexOf("GMT")).trim());


        if(notification != null){
            UserRepo.getUserById(notification.getSenderId(), new UserRepo.UserFetchCallback() {
                @Override
                public void onUserObjectFetched(User user, String errorMessage) {
                    UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                    sender.setText(user.getEmail());
                    if(user.getImage()!=null)
                        Picasso.get().load(user.getImage()).into(imageView);
                }
            });
            if(notification.getStatus().equals(NotificationStatus.SEEN))
                onMarkAsSeen.setVisibility(View.GONE);
            onMarkAsSeen.setOnClickListener(v->{
                NotificationRepo.updateNotificationStatus(notification.getId());
                onMarkAsSeen.setVisibility(View.GONE);
            });



        }

        return convertView;
    }
}
