package com.example.eventapp.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.databinding.FragmentNotificationsPageBinding;
import com.example.eventapp.databinding.FragmentUserProfileBinding;
import com.example.eventapp.fragments.products.ProductDetails;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.NotificationStatus;
import com.example.eventapp.repositories.NotificationRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.itextpdf.layout.element.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class NotificationsPage extends Fragment implements SensorEventListener {
    private TextView notifications_type;
    private Button onAll;
    private Button onNew;
    private Button onSeen;
    private FragmentNotificationsPageBinding binding;
    private SensorManager sensorManager;
    private long lastUpdate;
    private static final int SHAKE_THRESHOLD = 800;

    private float last_x;
    private float last_y;
    private float last_z;


    public NotificationsPage() {
        // Required empty public constructor
    }

    public static NotificationsPage newInstance() {
        NotificationsPage fragment = new NotificationsPage();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) {
            Log.e("Sensor", "Akcelerometar nije dostupan na ovom uređaju.");
        } else {
            Log.i("Sensor", "Akcelerometar je dostupan na ovom uređaju.");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        onAll=binding.onALL;
        onNew=binding.onNEW;
        onSeen=binding.onSEEN;
        notifications_type=binding.notificationsType;
        notifications_type.setText("ALL NOTIFICATINS");
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        String role= SharedPreferencesManager.getUserRole(getContext());


        onAll.setOnClickListener(a->{
            notifications_type.setText("ALL NOTIFICATINS");

            if(role.equals("ADMIN")){
                NotificationRepo.getAllForAdmin(new NotificationRepo.NotificationFetchCallback() {
                    @Override
                    public void onNotificationFetch(ArrayList<Notification> notifications) {
                        sortNotificationsByDate(notifications);
                        FragmentTransition.to(NotificationsList.newInstance(notifications),getActivity() ,
                                true, R.id.scroll_notifications_list);
                    }
                });
            }else{
                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                NotificationRepo.getAllByReceiverId(mAuth.getCurrentUser().getUid(), new NotificationRepo.NotificationFetchCallback() {
                    @Override
                    public void onNotificationFetch(ArrayList<Notification> notifications) {
                        sortNotificationsByDate(notifications);
                        FragmentTransition.to(NotificationsList.newInstance(notifications),getActivity() ,
                                true, R.id.scroll_notifications_list);
                    }
                });
            }
        });

        onNew.setOnClickListener(n->{
            notifications_type.setText("NEW NOTIFICATINS");

            if(role.equals("ADMIN")){
                NotificationRepo.getNewsForAdmin(new NotificationRepo.NotificationFetchCallback() {
                    @Override
                    public void onNotificationFetch(ArrayList<Notification> notifications) {
                        sortNotificationsByDate(notifications);
                        FragmentTransition.to(NotificationsList.newInstance(notifications),getActivity() ,
                                true, R.id.scroll_notifications_list);
                    }
                });
            }else{
                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                NotificationRepo.getAllByReceiverId(mAuth.getCurrentUser().getUid(), new NotificationRepo.NotificationFetchCallback() {
                    @Override
                    public void onNotificationFetch(ArrayList<Notification> notifications) {
                        ArrayList<Notification> newNotifications=new ArrayList<>();
                        for(Notification n:notifications)
                            if(n.getStatus().equals(NotificationStatus.NEW))
                                newNotifications.add(n);
                        sortNotificationsByDate(newNotifications);
                        FragmentTransition.to(NotificationsList.newInstance(newNotifications),getActivity() ,
                                true, R.id.scroll_notifications_list);
                    }
                });
            }
        });

        onSeen.setOnClickListener(s->{
            notifications_type.setText("SEEN NOTIFICATINS");

            if(role.equals("ADMIN")){
                NotificationRepo.getAllForAdmin(new NotificationRepo.NotificationFetchCallback() {
                    @Override
                    public void onNotificationFetch(ArrayList<Notification> notifications) {
                        ArrayList<Notification> seenNotifications=new ArrayList<>();
                        for(Notification n:notifications)
                            if(n.getStatus().equals(NotificationStatus.SEEN))
                                seenNotifications.add(n);

                        sortNotificationsByDate(seenNotifications);
                        FragmentTransition.to(NotificationsList.newInstance(seenNotifications),getActivity() ,
                                true, R.id.scroll_notifications_list);
                    }
                });
            }else{
                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                NotificationRepo.getAllByReceiverId(mAuth.getCurrentUser().getUid(), new NotificationRepo.NotificationFetchCallback() {
                    @Override
                    public void onNotificationFetch(ArrayList<Notification> notifications) {
                        ArrayList<Notification> seenNotifications=new ArrayList<>();
                        for(Notification n:notifications)
                            if(n.getStatus().equals(NotificationStatus.SEEN))
                                seenNotifications.add(n);

                        sortNotificationsByDate(seenNotifications);
                        FragmentTransition.to(NotificationsList.newInstance(seenNotifications),getActivity() ,
                                true, R.id.scroll_notifications_list);
                    }
                });
            }
        });


        if(role.equals("ADMIN")){
            NotificationRepo.getAllForAdmin(new NotificationRepo.NotificationFetchCallback() {
                @Override
                public void onNotificationFetch(ArrayList<Notification> notifications) {
                    sortNotificationsByDate(notifications);
                    FragmentTransition.to(NotificationsList.newInstance(notifications),getActivity() ,
                            true, R.id.scroll_notifications_list);
                }
            });
        }else{
            FirebaseAuth mAuth= FirebaseAuth.getInstance();
            Log.i("KKKK",mAuth.getCurrentUser().getUid());
            NotificationRepo.getAllByReceiverId(mAuth.getCurrentUser().getUid(), new NotificationRepo.NotificationFetchCallback() {
                @Override
                public void onNotificationFetch(ArrayList<Notification> notifications) {
                    Log.i("KKKK",notifications.size()+" ");
                    sortNotificationsByDate(notifications);
                    FragmentTransition.to(NotificationsList.newInstance(notifications),getActivity() ,
                            true, R.id.scroll_notifications_list);
                }
            });
        }

        return root;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        String role= SharedPreferencesManager.getUserRole(getContext());

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float[] values = sensorEvent.values;
                float x = values[0];
                float y = values[1];
                float z = values[2];

                float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000;
                Log.d("Shake", "Brzina trešenja: " + speed+" "+notifications_type.getText());

                if (speed > 1000) {
                    if(notifications_type.getText().equals("ALL NOTIFICATINS")){
                        Log.d("Shake", "USAO");

                        notifications_type.setText("NEW NOTIFICATINS");

                        if(role.equals("ADMIN")){
                            NotificationRepo.getNewsForAdmin(new NotificationRepo.NotificationFetchCallback() {
                                @Override
                                public void onNotificationFetch(ArrayList<Notification> notifications) {
                                    sortNotificationsByDate(notifications);
                                    FragmentTransition.to(NotificationsList.newInstance(notifications),getActivity() ,
                                            true, R.id.scroll_notifications_list);
                                }
                            });
                        }else{
                            FirebaseAuth mAuth= FirebaseAuth.getInstance();
                            NotificationRepo.getAllByReceiverId(mAuth.getCurrentUser().getUid(), new NotificationRepo.NotificationFetchCallback() {
                                @Override
                                public void onNotificationFetch(ArrayList<Notification> notifications) {
                                    ArrayList<Notification> newNotifications=new ArrayList<>();
                                    for(Notification n:notifications)
                                        if(n.getStatus().equals(NotificationStatus.NEW))
                                            newNotifications.add(n);

                                    sortNotificationsByDate(newNotifications);
                                    FragmentTransition.to(NotificationsList.newInstance(newNotifications),getActivity() ,
                                            true, R.id.scroll_notifications_list);
                                }
                            });
                        }
                    }else if(notifications_type.getText().equals("NEW NOTIFICATINS")){
                        notifications_type.setText("SEEN NOTIFICATINS");

                        if(role.equals("ADMIN")){
                            NotificationRepo.getAllForAdmin(new NotificationRepo.NotificationFetchCallback() {
                                @Override
                                public void onNotificationFetch(ArrayList<Notification> notifications) {
                                    ArrayList<Notification> seenNotifications=new ArrayList<>();
                                    for(Notification n:notifications)
                                        if(n.getStatus().equals(NotificationStatus.SEEN))
                                            seenNotifications.add(n);

                                    sortNotificationsByDate(seenNotifications);
                                    FragmentTransition.to(NotificationsList.newInstance(seenNotifications),getActivity() ,
                                            true, R.id.scroll_notifications_list);
                                }
                            });
                        }else{
                            FirebaseAuth mAuth= FirebaseAuth.getInstance();
                            NotificationRepo.getAllByReceiverId(mAuth.getCurrentUser().getUid(), new NotificationRepo.NotificationFetchCallback() {
                                @Override
                                public void onNotificationFetch(ArrayList<Notification> notifications) {
                                    ArrayList<Notification> seenNotifications=new ArrayList<>();
                                    for(Notification n:notifications)
                                        if(n.getStatus().equals(NotificationStatus.SEEN))
                                            seenNotifications.add(n);

                                    sortNotificationsByDate(seenNotifications);
                                    FragmentTransition.to(NotificationsList.newInstance(seenNotifications),getActivity() ,
                                            true, R.id.scroll_notifications_list);
                                }
                            });
                        }
                    }else if(notifications_type.getText().equals("SEEN NOTIFICATINS")){
                        notifications_type.setText("ALL NOTIFICATINS");

                        if(role.equals("ADMIN")){
                            NotificationRepo.getAllForAdmin(new NotificationRepo.NotificationFetchCallback() {
                                @Override
                                public void onNotificationFetch(ArrayList<Notification> notifications) {
                                    sortNotificationsByDate(notifications);
                                    FragmentTransition.to(NotificationsList.newInstance(notifications),getActivity() ,
                                            true, R.id.scroll_notifications_list);
                                }
                            });
                        }else{
                            FirebaseAuth mAuth= FirebaseAuth.getInstance();
                            NotificationRepo.getAllByReceiverId(mAuth.getCurrentUser().getUid(), new NotificationRepo.NotificationFetchCallback() {
                                @Override
                                public void onNotificationFetch(ArrayList<Notification> notifications) {
                                    sortNotificationsByDate(notifications);
                                    FragmentTransition.to(NotificationsList.newInstance(notifications),getActivity() ,
                                            true, R.id.scroll_notifications_list);
                                }
                            });
                        }
                    }
                }else{
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }


        }
    }

    public static void sortNotificationsByDate(List<Notification> notifications) {
        Collections.sort(notifications, new Comparator<Notification>() {
            @Override
            public int compare(Notification notification1, Notification notification2) {
                // Convert date strings to Date objects
                Date date1 = getDateFromString(notification1.getDate());
                Date date2 = getDateFromString(notification2.getDate());

                // If any of the dates is null, compare them as strings
                if (date1 == null || date2 == null) {
                    return notification1.getDate().compareTo(notification2.getDate());
                }

                // Compare dates
                return date1.compareTo(date2); // older dates come first
            }
        });
    }
    private static Date getDateFromString(String dateString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}