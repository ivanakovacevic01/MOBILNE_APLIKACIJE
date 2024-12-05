package com.example.eventapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.adapters.NotificationAdapter;
import com.example.eventapp.model.Notification;

import java.util.ArrayList;


public class NotificationsList extends ListFragment {



    private static ArrayList<Notification> notifikacije;

    public NotificationsList() {
        // Required empty public constructor
    }


    public static NotificationsList newInstance(ArrayList<Notification> notifications) {
        Log.i("KKK",notifications.size()+" M");
        NotificationsList fragment = new NotificationsList();
        notifikacije=notifications;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            NotificationAdapter adapter=new NotificationAdapter(getContext(),notifikacije);
            setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_notifications, container, false);
    }
}