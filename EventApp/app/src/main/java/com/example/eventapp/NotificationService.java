package com.example.eventapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.example.eventapp.model.Notification;
import com.example.eventapp.model.ShowStatus;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.NotificationRepo;

import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationService extends Service {
    public NotificationService() {
    }
    private static final String TAG = "NotificationService";
    private static final int INTERVAL = 3000; // 3 sekunde
    private Handler handler;
    private Runnable runnable;
    private String role=null;
    private FirebaseUser currentUser;
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.createNotificationChannel(this, "SUGGESTIONS", "SUGGESTIONS", "SUGGESTIONS");
        //role= SharedPreferencesManager.getUserRole(this);
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();



        Context context=this;

        handler = new Handler();
        currentUser = mAuth.getCurrentUser();

        runnable = new Runnable() {
            @Override
            public void run() {
                fetchNotifications(context);
                handler.postDelayed(this, INTERVAL);

            }
        };
        handler.post(runnable);
    }

    private void fetchNotifications(Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                FirebaseUser user=mAuth.getCurrentUser();

                if(user != null)
                {
                    UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                        @Override
                        public void onUserObjectFetched(User user, String errorResponse) {
                            if (user != null && user.isActive()) {
                                role=user.getType().toString();
                                Log.i("DDDDDDDDDDD",role);
                                if(role!=null) {
                                    if (role.equals("ADMIN")) {
                                        NotificationRepo.getNewsForAdmin(new NotificationRepo.NotificationFetchCallback() {
                                            @Override
                                            public void onNotificationFetch(ArrayList<Notification> notifications) {
                                                for (Notification n : notifications) {
                                                    if(n.getShowStatus()== ShowStatus.UNSHOWED) {
                                                        NotificationHelper.showNotification(context, "EVENT APP", n.getMessage(), "SUGGESTIONS", n.getId());
                                                        NotificationRepo.updateShowingStatus(n.getId());
                                                    }
                                                }
                                            }
                                        });
                                    } else if (role.equals("OWNER")) {
                                        NotificationRepo notificationRepo = new NotificationRepo();
                                        notificationRepo.getNewsForOwnerById(currentUser.getUid(),new NotificationRepo.NotificationFetchCallback() {
                                            @Override
                                            public void onNotificationFetch(ArrayList<Notification> notifications) {
                                                for (Notification n : notifications) {
                                                    if(n.getShowStatus()==ShowStatus.UNSHOWED) {
                                                        NotificationHelper.showNotification(context, "EVENT APP", n.getMessage(), "SUGGESTIONS", n.getId());
                                                        NotificationRepo.updateShowingStatus(n.getId());
                                                    }
                                                }
                                            }
                                        });
                                    }  else if (role.equals("EMPLOYEE")) {
                                        NotificationRepo notificationRepo = new NotificationRepo();
                                        notificationRepo.getNewsForEmployee(currentUser.getUid(), new NotificationRepo.NotificationFetchCallback() {
                                            @Override
                                            public void onNotificationFetch(ArrayList<Notification> notifications) {
                                                for (Notification n : notifications) {
                                                    if(n.getShowStatus()==ShowStatus.UNSHOWED) {
                                                        NotificationHelper.showNotification(context, "EVENT APP", n.getMessage(), "SUGGESTIONS", n.getId());
                                                        NotificationRepo.updateShowingStatus(n.getId());

                                                    }

                                                }

                                            }
                                        });
                                    }
                                    else if (role.equals("ORGANIZER")) {
                                        NotificationRepo notificationRepo = new NotificationRepo();
                                        notificationRepo.getNewsForOrganizer(currentUser.getUid(), new NotificationRepo.NotificationFetchCallback() {
                                            @Override
                                            public void onNotificationFetch(ArrayList<Notification> notifications) {
                                                for (Notification n : notifications) {
                                                    if(n.getShowStatus()==ShowStatus.UNSHOWED) {

                                                        //za notifikacije 1h prije rezervacije
                                                        if (n.getMessage().contains("in 1 hour")) {
                                                            String message = n.getMessage();
                                                            String[] mainParts = message.split("\\.");

                                                            if (mainParts.length >= 2) {
                                                                String reservationDetails = mainParts[1].trim();
                                                                String[] reservationParts = reservationDetails.split("-");

                                                                if (reservationParts.length >= 3) {
                                                                    String reservationDateTimeStr = reservationParts[0].trim();
                                                                    String reservationTimeStr = reservationParts[1].trim();
                                                                    String reservationId = reservationParts[2].trim();

                                                                    try {
                                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                                                                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

                                                                        // Parsiranje datuma i vremena
                                                                        Date date = dateFormat.parse(reservationDateTimeStr);
                                                                        Date time = timeFormat.parse(reservationTimeStr);

                                                                        // Kombinovanje datuma i vremena
                                                                        Calendar calendar = Calendar.getInstance();
                                                                        calendar.setTime(date);
                                                                        Calendar timeCalendar = Calendar.getInstance();
                                                                        timeCalendar.setTime(time);

                                                                        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
                                                                        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

                                                                        LocalDateTime reservationDateTime = LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
                                                                        LocalDateTime currentDateTime = LocalDateTime.now();

                                                                        // Provera da li je trenutni datum i vreme unutar 1 sata pre rezervacije
                                                                        if (currentDateTime.isAfter(reservationDateTime.minusHours(1))) {
                                                                            NotificationHelper.showNotification(context, "EVENT APP", n.getMessage(), "SUGGESTIONS", n.getId());
                                                                            NotificationRepo.updateShowingStatus(n.getId());
                                                                        }
                                                                    } catch (ParseException e) {
                                                                        Log.e("Date Parsing", "Error parsing date/time", e);
                                                                    }

                                                                    Log.d("Reservation ID", "ID: " + reservationId);
                                                                } else {
                                                                    Log.e("Reservation ID", "Could not parse reservation details from message: " + reservationDetails);
                                                                }
                                                            } else {
                                                                Log.e("Message Parsing", "Could not parse message: " + message);
                                                            }
                                                        } else {
                                                            NotificationHelper.showNotification(context, "EVENT APP", n.getMessage(), "SUGGESTIONS", n.getId());
                                                            NotificationRepo.updateShowingStatus(n.getId());
                                                        }
                                                    }

                                                }

                                            }
                                        });
                                    }


                                }
                            }
                        }
                    });
                }

                /*
                Log.i("DDDDDDDDDDD",role);
                if(role!=null) {
                    if (role.equals("ADMIN")) {
                        NotificationRepo.getNewsForAdmin(new NotificationRepo.NotificationFetchCallback() {
                            @Override
                            public void onNotificationFetch(ArrayList<Notification> notifications) {
                                for (Notification n : notifications) {
                                    if(n.getShowStatus()== ShowStatus.UNSHOWED) {
                                        NotificationHelper.showNotification(context, "EVENT APP", n.getMessage(), "SUGGESTIONS", n.getId());
                                        NotificationRepo.updateShowingStatus(n.getId());
                                    }
                                }
                            }
                        });
                    } else if (role.equals("OWNER")) {
                        NotificationRepo notificationRepo = new NotificationRepo();
                        notificationRepo.getNewsForOwnerById(currentUser.getUid(),new NotificationRepo.NotificationFetchCallback() {
                            @Override
                            public void onNotificationFetch(ArrayList<Notification> notifications) {
                                for (Notification n : notifications) {
                                    if(n.getShowStatus()==ShowStatus.UNSHOWED) {
                                        NotificationHelper.showNotification(context, "EVENT APP", n.getMessage(), "SUGGESTIONS", n.getId());
                                        NotificationRepo.updateShowingStatus(n.getId());
                                    }
                                }
                            }
                        });
                    }  else if (role.equals("EMPLOYEE")) {
                        NotificationRepo notificationRepo = new NotificationRepo();
                        notificationRepo.getNewsForEmployee(currentUser.getUid(), new NotificationRepo.NotificationFetchCallback() {
                            @Override
                            public void onNotificationFetch(ArrayList<Notification> notifications) {
                                for (Notification n : notifications) {
                                    if(n.getShowStatus()==ShowStatus.UNSHOWED) {
                                        NotificationHelper.showNotification(context, "EVENT APP", n.getMessage(), "SUGGESTIONS", n.getId());
                                        NotificationRepo.updateShowingStatus(n.getId());

                                    }

                                }

                            }
                        });
                    }
                    else if (role.equals("ORGANIZER")) {
                        NotificationRepo notificationRepo = new NotificationRepo();
                        notificationRepo.getNewsForOrganizer(currentUser.getUid(), new NotificationRepo.NotificationFetchCallback() {
                            @Override
                            public void onNotificationFetch(ArrayList<Notification> notifications) {
                                for (Notification n : notifications) {
                                    if(n.getShowStatus()==ShowStatus.UNSHOWED) {

                                        //za notifikacije 1h prije rezervacije
                                        if (n.getMessage().contains("in 1 hour")) {
                                            String message = n.getMessage();
                                            String[] mainParts = message.split("\\.");

                                            if (mainParts.length >= 2) {
                                                String reservationDetails = mainParts[1].trim();
                                                String[] reservationParts = reservationDetails.split("-");

                                                if (reservationParts.length >= 3) {
                                                    String reservationDateTimeStr = reservationParts[0].trim();
                                                    String reservationTimeStr = reservationParts[1].trim();
                                                    String reservationId = reservationParts[2].trim();

                                                    try {
                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                                                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

                                                        // Parsiranje datuma i vremena
                                                        Date date = dateFormat.parse(reservationDateTimeStr);
                                                        Date time = timeFormat.parse(reservationTimeStr);

                                                        // Kombinovanje datuma i vremena
                                                        Calendar calendar = Calendar.getInstance();
                                                        calendar.setTime(date);
                                                        Calendar timeCalendar = Calendar.getInstance();
                                                        timeCalendar.setTime(time);

                                                        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
                                                        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

                                                        LocalDateTime reservationDateTime = LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
                                                        LocalDateTime currentDateTime = LocalDateTime.now();

                                                        // Provera da li je trenutni datum i vreme unutar 1 sata pre rezervacije
                                                        if (currentDateTime.isAfter(reservationDateTime.minusHours(1))) {
                                                            NotificationHelper.showNotification(context, "EVENT APP", n.getMessage(), "SUGGESTIONS", n.getId());
                                                            NotificationRepo.updateShowingStatus(n.getId());
                                                        }
                                                    } catch (ParseException e) {
                                                        Log.e("Date Parsing", "Error parsing date/time", e);
                                                    }

                                                    Log.d("Reservation ID", "ID: " + reservationId);
                                                } else {
                                                    Log.e("Reservation ID", "Could not parse reservation details from message: " + reservationDetails);
                                                }
                                            } else {
                                                Log.e("Message Parsing", "Could not parse message: " + message);
                                            }
                                        }
                                        else {
                                            NotificationHelper.showNotification(context, "EVENT APP", n.getMessage(), "SUGGESTIONS", n.getId());
                                            NotificationRepo.updateShowingStatus(n.getId());
                                        }

                                    }

                                }

                            }
                        });
                    }


                }
*/

            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(runnable);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}