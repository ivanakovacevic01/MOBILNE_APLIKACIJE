package com.example.eventapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.fragments.reservations.ReservationDetailsFragment;
import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Deadline;
import com.example.eventapp.model.Employee;

import com.example.eventapp.R;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.EventBudgetItem;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.ReservationStatus;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.model.WeeklyEvent;
import com.example.eventapp.repositories.DeadlineRepo;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.EventBudgetItemRepo;
import com.example.eventapp.repositories.EventBudgetRepo;
import com.example.eventapp.repositories.EventRepo;
import com.example.eventapp.repositories.FirmRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.OrganizerRepo;
import com.example.eventapp.repositories.PackageRepo;
import com.example.eventapp.repositories.ReservationRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.UserRepo;
import com.example.eventapp.repositories.WeeklyEventRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;


public class ReservationListAdapter extends ArrayAdapter<Reservation> {
    private ArrayList<Reservation> aReservations;
    private AppCompatActivity mActivity;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userType="";
    private PackageRepo packageRepo;
    private ServiceRepo serviceRepo;
    private ReservationRepo reservationRepo;
    private User currentUser;
    private ReservationListAdapter adapter;
    private String lastState;
    private boolean isAccepted;
    public ReservationListAdapter(Context context, ArrayList<Reservation> reservations,AppCompatActivity activity){
        super(context, R.layout.reservation_card, reservations);
        aReservations = reservations;
        mActivity=activity;
    }
    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
    @Override
    public int getCount() {
        return aReservations.size();
    }


    @Nullable
    @Override
    public Reservation getItem(int position) {
        return aReservations.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Reservation reservation = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.reservation_card,
                    parent, false);
        }

        //initialize
        TextView employeeName = convertView.findViewById(R.id.res_employee);
        TextView organizerName = convertView.findViewById(R.id.res_user);
        TextView serviceName = convertView.findViewById(R.id.service_name);
        TextView companyName = convertView.findViewById(R.id.company_name);
        TextView date = convertView.findViewById(R.id.res_created_date);
        TextView status =convertView.findViewById(R.id.res_status);
        Button rejectBtn=convertView.findViewById(R.id.reject_button);
        Button acceptBtn=convertView.findViewById(R.id.accept_button);
        LinearLayout reservationCard = convertView.findViewById(R.id.reservation_card);


        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        getFirm(reservation.getFirmId(),companyName);
        //getUser(user.getEmail());

        if(reservation != null) {
            date.setText(formatDateString(reservation.getCreatedDate()));
            status.setText(reservation.getStatus().toString());
            reservationCard.setOnClickListener(v -> {
                if(getContext() instanceof HomeActivity){
                    HomeActivity activity = (HomeActivity) getContext();
                    FragmentTransition.to(ReservationDetailsFragment.newInstance(reservation),activity,true,R.id.scroll_reservations_list);
                }
            });
            getOrganizer(reservation,serviceName,employeeName,organizerName);
            initializeButtons(reservation, acceptBtn,rejectBtn);
            Date eventDate = reservation.getEventDate();
            if(reservation.getStatus().equals(ReservationStatus.ACCEPTED) && eventDate!=null){
                LocalDate localEventDate = eventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                WeeklyEventRepo.getByReservation(reservation.getId(), new WeeklyEventRepo.WeeklyEventFetchCallback() {
                    @Override
                    public void onWeeklyEventObjectFetched(WeeklyEvent event, String errorMessage) {
                        WeeklyEventRepo.WeeklyEventFetchCallback.super.onWeeklyEventObjectFetched(event, errorMessage);
                        if(event!=null && (localEventDate.isEqual(LocalDate.now()) || localEventDate.isBefore(LocalDate.now())) && isCurrentTimeAfter(event.getTo())){
                            reservation.setStatus(ReservationStatus.REALIZED);
                            ReservationRepo.update(reservation, new ReservationRepo.ReservationFetchCallback() {
                                @Override
                                public void onReservationObjectsFetched(ArrayList<Reservation> reservations, String errorMessage) {
                                    ReservationRepo.ReservationFetchCallback.super.onReservationObjectsFetched(reservations, errorMessage);
                                    createNotification(reservation);
                                }
                            });

                        }
                    }
                });
            }

           rejectBtn.setOnClickListener(v -> {
                HomeActivity activity = (HomeActivity) getContext();
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setMessage("Are you sure you want to reject this reservation?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
                                    @Override
                                    public void onUserObjectFetched(User user, String errorMessage) {
                                        UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                                        if(user.getType().toString().equals("ORGANIZER")){
                                            reservation.setStatus(ReservationStatus.ORGANIZER_REJECTED);
                                            //ako odbija paket
                                            if((reservation.getServiceId().isEmpty() && !reservation.getPackageId().isEmpty()))
                                                rejectAllServices(reservation.getPackageId());
                                            //ako odbija paket ili servis
                                            ReservationRepo.update(reservation, new ReservationRepo.ReservationFetchCallback() {
                                                @Override
                                                public void onUpdateSuccess() {
                                                    ReservationRepo.ReservationFetchCallback.super.onUpdateSuccess();
                                                    WeeklyEventRepo.delete(reservation.getId());
                                                    status.setText(reservation.getStatus().toString());
                                                    rejectBtn.setVisibility(View.GONE);
                                                    acceptBtn.setVisibility(View.GONE);
                                                    if(reservation.getEmployees().size()==1)
                                                        createNotificationByOrganizer(reservation,"");
                                                    else{
                                                        for(String id:reservation.getEmployees()){
                                                            createNotificationByOrganizer(reservation,id);
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                        else if(user.getType().toString().equals("ADMIN")){
                                            reservation.setStatus(ReservationStatus.ADMIN_REJECTED);
                                            ReservationRepo.update(reservation, new ReservationRepo.ReservationFetchCallback() {
                                                @Override
                                                public void onUpdateSuccess() {
                                                    ReservationRepo.ReservationFetchCallback.super.onUpdateSuccess();
                                                    status.setText(reservation.getStatus().toString());
                                                    rejectBtn.setVisibility(View.GONE);
                                                    acceptBtn.setVisibility(View.GONE);
                                                    createNotification(reservation);
                                                }
                                            });
                                        }
                                        else{
                                            reservation.setStatus(ReservationStatus.PUP_REJECTED);
                                            //ako je paket
                                            if(reservation.getServiceId().isEmpty() && !reservation.getPackageId().isEmpty()) {
                                                rejectAllServicesByPup(reservation.getPackageId());
                                                ReservationRepo.update(reservation, new ReservationRepo.ReservationFetchCallback() {
                                                    @Override
                                                    public void onUpdateSuccess() {
                                                        ReservationRepo.ReservationFetchCallback.super.onUpdateSuccess();
                                                        WeeklyEventRepo.delete(reservation.getId());
                                                        status.setText(reservation.getStatus().toString());
                                                        rejectBtn.setVisibility(View.GONE);
                                                        acceptBtn.setVisibility(View.GONE);
                                                        createNotification(reservation);
                                                    }
                                                });
                                            }
                                            //ako je servis u paketu
                                            else if(!reservation.getServiceId().isEmpty() & !reservation.getPackageId().isEmpty()){
                                                rejectAllServicesByPup(reservation.getPackageId());
                                                ReservationRepo.getByPackageId(reservation.getPackageId(), new ReservationRepo.ReservationFetchCallback() {
                                                    @Override
                                                    public void onReservationObjectFetched(Reservation reservation, String errorMessage) {
                                                        ReservationRepo.ReservationFetchCallback.super.onReservationObjectFetched(reservation, errorMessage);
                                                        reservation.setStatus(ReservationStatus.PUP_REJECTED);
                                                        ReservationRepo.update(reservation, new ReservationRepo.ReservationFetchCallback() {
                                                            @Override
                                                            public void onUpdateSuccess() {
                                                                ReservationRepo.ReservationFetchCallback.super.onUpdateSuccess();
                                                                WeeklyEventRepo.delete(reservation.getId());
                                                                status.setText(reservation.getStatus().toString());
                                                                rejectBtn.setVisibility(View.GONE);
                                                                acceptBtn.setVisibility(View.GONE);
                                                                createNotification(reservation);
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                            //ako je obican servis
                                            else{
                                                reservation.setStatus(ReservationStatus.PUP_REJECTED);
                                                ReservationRepo.update(reservation, new ReservationRepo.ReservationFetchCallback() {
                                                    @Override
                                                    public void onUpdateSuccess() {
                                                        ReservationRepo.ReservationFetchCallback.super.onUpdateSuccess();
                                                        WeeklyEventRepo.delete(reservation.getId());
                                                        status.setText(reservation.getStatus().toString());
                                                        rejectBtn.setVisibility(View.GONE);
                                                        acceptBtn.setVisibility(View.GONE);
                                                        createNotification(reservation);
                                                        createDeadline(reservation.getOrganizerEmail(),reservation.getFirmId());
                                                    }
                                                });
                                            }


                                        }
                                    }
                                });

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = dialog.create();
                alert.show();
            });





            acceptBtn.setOnClickListener(v -> {
                HomeActivity activity = (HomeActivity) getContext();
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setMessage("Are you sure you want to accept this reservation?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                reservation.setStatus(ReservationStatus.ACCEPTED);
                                if (!reservation.getServiceId().isEmpty() && reservation.getPackageId().isEmpty()) { // kada je samo servis u pitanju
                                    updateReservationAndUI(reservation,status,acceptBtn);

                                    serviceRepo.getById(reservation.getServiceId(), new ServiceRepo.ServiceFetchCallback() {
                                        @Override
                                        public void onServiceObjectFetched(Service service, String mess) {
                                            ServiceRepo.ServiceFetchCallback.super.onServiceObjectFetched(service, mess);

                                            //add to budgeting
                                            EventBudgetRepo eventBudgetRepo = new EventBudgetRepo();
                                            eventBudgetRepo.getByEventId(new EventBudgetRepo.EventBudgetFetchCallback() {
                                                @Override
                                                public void onEventBudgetFetch(ArrayList<EventBudget> budgets) {
                                                }

                                                @Override
                                                public void onEventBudgetFetchByEvent(EventBudget budget) {
                                                    EventBudgetItemRepo itemRepo = new EventBudgetItemRepo();
                                                    itemRepo.getByBudgetAndSubcategory(new EventBudgetItemRepo.EventBudgetItemFetchCallback() {
                                                        @Override
                                                        public void onEventBudgetItemFetch(ArrayList<EventBudgetItem> budgets) {
                                                            if(budgets == null || budgets.isEmpty())
                                                            {
                                                                EventBudgetItem item = new EventBudgetItem();
                                                                item.setPlannedBudget(0);
                                                                ArrayList<String>ids = new ArrayList<>();
                                                                ids.add(service.getId());
                                                                item.setItemsIds(ids);
                                                                item.setSubcategoryId(service.getSubcategory());

                                                                itemRepo.create(new EventBudgetItemRepo.EventBudgetItemFetchCallback() {
                                                                    @Override
                                                                    public void onEventBudgetItemFetch(ArrayList<EventBudgetItem> budgets) {
                                                                        budget.getEventBudgetItemsIds().add(budgets.get(0).getId());
                                                                        EventBudgetRepo.update(budget);
                                                                    }
                                                                }, item);
                                                            }else{
                                                                EventBudgetItem item = budgets.get(0);
                                                                item.getItemsIds().add(service.getId());
                                                                EventBudgetItemRepo.update(item);
                                                            }
                                                        }
                                                    }, budget, service.getSubcategory());
                                                }
                                            }, reservation.getEventId());
                                        }
                                    });

                                } else if (!reservation.getServiceId().isEmpty() && !reservation.getPackageId().isEmpty()) { // kada je paket u pitanju
                                    updateReservationAndUI(reservation,status,acceptBtn);
                                    checkAllServicesAccepted(reservation.getPackageId(), isAccepted -> {
                                        Log.i("check", "" + isAccepted);
                                        if (isAccepted) {
                                            ReservationRepo.getByPackageId(reservation.getPackageId(), new ReservationRepo.ReservationFetchCallback() {
                                                @Override
                                                public void onReservationObjectFetched(Reservation reservation, String errorMessage) {
                                                    ReservationRepo.ReservationFetchCallback.super.onReservationObjectFetched(reservation, errorMessage);
                                                    if(reservation!=null){
                                                        reservation.setStatus(ReservationStatus.ACCEPTED);
                                                        updateReservationAndUI(reservation,status,acceptBtn);
                                                        createWeeklyEventsForPackage(reservation.getPackageId());


                                                        serviceRepo.getById(reservation.getServiceId(), new ServiceRepo.ServiceFetchCallback() {
                                                            @Override
                                                            public void onServiceObjectFetched(Service service, String mess) {
                                                                ServiceRepo.ServiceFetchCallback.super.onServiceObjectFetched(service, mess);

                                                                //add to budgeting
                                                                EventBudgetRepo eventBudgetRepo = new EventBudgetRepo();
                                                                eventBudgetRepo.getByEventId(new EventBudgetRepo.EventBudgetFetchCallback() {
                                                                    @Override
                                                                    public void onEventBudgetFetch(ArrayList<EventBudget> budgets) {
                                                                    }

                                                                    @Override
                                                                    public void onEventBudgetFetchByEvent(EventBudget budget) {
                                                                        EventBudgetItemRepo itemRepo = new EventBudgetItemRepo();
                                                                        itemRepo.getByBudgetAndSubcategory(new EventBudgetItemRepo.EventBudgetItemFetchCallback() {
                                                                            @Override
                                                                            public void onEventBudgetItemFetch(ArrayList<EventBudgetItem> budgets) {
                                                                                if(budgets == null || budgets.isEmpty())
                                                                                {
                                                                                    EventBudgetItem item = new EventBudgetItem();
                                                                                    item.setPlannedBudget(0);
                                                                                    ArrayList<String>ids = new ArrayList<>();
                                                                                    ids.add(service.getId());
                                                                                    item.setItemsIds(ids);
                                                                                    item.setSubcategoryId(service.getSubcategory());

                                                                                    itemRepo.create(new EventBudgetItemRepo.EventBudgetItemFetchCallback() {
                                                                                        @Override
                                                                                        public void onEventBudgetItemFetch(ArrayList<EventBudgetItem> budgets) {
                                                                                            budget.getEventBudgetItemsIds().add(budgets.get(0).getId());
                                                                                            EventBudgetRepo.update(budget);
                                                                                        }
                                                                                    }, item);
                                                                                }else{
                                                                                    EventBudgetItem item = budgets.get(0);
                                                                                    item.getItemsIds().add(service.getId());
                                                                                    EventBudgetItemRepo.update(item);
                                                                                }
                                                                            }
                                                                        }, budget, service.getSubcategory());
                                                                    }
                                                                }, reservation.getEventId());
                                                            }
                                                        });

                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = dialog.create();
                alert.show();
            });

        }


        return convertView;
    }
    public void updateReservations(ArrayList<Reservation> newReservations) {
        this.aReservations = newReservations;
        notifyDataSetChanged();
    }
    @SuppressLint("SetTextI18n")
    public void setEmployees(TextView employeeName,Reservation reservation){
        if(!reservation.getEmployees().isEmpty()){
            EmployeeRepo.getById(reservation.getEmployees().get(0), new EmployeeRepo.EmployeeFetchCallback() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                    EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                    employeeName.setText(employee.getFirstName()+" "+employee.getLastName());
                }
            });
        }
    }
    public void setServices(Package p, TextView serviceName, TextView employeeName, Reservation reservation){
        ServiceRepo serviceRepo=new ServiceRepo();
        for(String id:p.getServices()){
            serviceRepo.getById(id, new ServiceRepo.ServiceFetchCallback() {
                @Override
                public void onServiceObjectFetched(Service service, String errorMessage) {
                    ServiceRepo.ServiceFetchCallback.super.onServiceObjectFetched(service, errorMessage);
                    serviceName.setText(service.getName());
                    setEmployees(employeeName, reservation);


                }
            });
        }
    }

    private void createNotification(Reservation reservation){
        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
            @Override
            public void onUserObjectFetched(User user, String errorMessage) {
                OrganizerRepo.getByEmail(reservation.getOrganizerEmail(), new OrganizerRepo.OrganizerFetchCallback() {
                    @Override
                    public void onOrganizerObjectFetched(Organizer organizer, String errorMessage) {
                        OrganizerRepo.OrganizerFetchCallback.super.onOrganizerObjectFetched(organizer, errorMessage);
                        Notification newNotification = new Notification();
                        newNotification.setMessage(reservation.getOrganizerEmail()+" reservation "+reservation.getStatus()+". You can rate this company.");
                        newNotification.setDate(new Date().toString());
                        newNotification.setReceiverId(organizer.getId());
                        newNotification.setReceiverRole(UserType.ORGANIZER);
                        newNotification.setSenderId(user.getId());
                        newNotification.setDate((new Date()).toString());
                        NotificationRepo.create(newNotification);
                    }
                });
            }
        });


    }

    private void createNotificationByOrganizer(Reservation reservation, String id){
        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
            @Override
            public void onUserObjectFetched(User user, String errorMessage) {
                Notification newNotification = new Notification();
                newNotification.setMessage("Reservation for "+reservation.getType()+" rejected by "+reservation.getOrganizerEmail()+". Event date: "+reservation.getEventDate());
                if(id.isEmpty())
                    newNotification.setReceiverId(reservation.getEmployees().get(0));
                else
                    newNotification.setReceiverId(id);
                newNotification.setReceiverRole(UserType.EMPLOYEE);
                newNotification.setSenderId(user.getId());
                newNotification.setDate((new Date()).toString());
                NotificationRepo.create(newNotification);
            }
        });
    }
    private String formatDateString(String dateString) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        try {
            Date date = originalFormat.parse(dateString);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }
    private void getFirm(String id,TextView companyName){
        FirmRepo firmRepo=new FirmRepo();
        firmRepo.getById(id, new FirmRepo.FirmFetchCallback() {
            @Override
            public void onFirmFetch(ArrayList<Firm> firms) {
                FirmRepo.FirmFetchCallback.super.onFirmFetch(firms);
                companyName.setText(firms.get(0).getName());
            }
        });
    }
    public static Date convertToDate(String dateStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        Date date = null;
        try {
            date = inputFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static boolean isCancellationPeriod(Date date,double cancellationDeadline) {
        Date today = new Date();
        Log.i("Date",""+date);
        long diffInMillis = Math.abs(today.getTime() - date.getTime());
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        return diffInDays >= cancellationDeadline;
    }
    private void createWeeklyEvent(Reservation reservation){
        WeeklyEvent weeklyEvent=new WeeklyEvent();
        weeklyEvent.firmId=reservation.getFirmId();
        weeklyEvent.eventType=WeeklyEvent.EventType.BUSY;
        Log.i("res","id "+reservation.getEventId());
        weeklyEvent.from=reservation.getFromTime();
        weeklyEvent.to=reservation.getToTime();
        weeklyEvent.employeeId=reservation.getEmployees().get(0); //ISPRAVITI
        weeklyEvent.reservationId=reservation.getId();
        EventRepo.getById(reservation.getEventId(), new EventRepo.EventFetchCallback() {
            @Override
            public void onEventObjectFetched(Event event, String errorMessage) {
                EventRepo.EventFetchCallback.super.onEventObjectFetched(event, errorMessage);
                weeklyEvent.name=event.getName();
                Log.i("res","id "+event);
                Log.i("res","id "+event.getDate());
                weeklyEvent.date=convertToYYYYMMDD(event.getDate().toString());
                WeeklyEventRepo.create(weeklyEvent, new WeeklyEventRepo.WeeklyEventFetchCallback() {
                    @Override
                    public void onWeeklyEventObjectFetched(WeeklyEvent event, String errorMessage) {
                        WeeklyEventRepo.WeeklyEventFetchCallback.super.onWeeklyEventObjectFetched(event, errorMessage);
                    }
                });
            }
        });

    }
    public static String convertToYYYYMMDD(String dateStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        Date date = null;
        try {
            date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
    private void createDeadline(String email, String id){
        Deadline deadline=new Deadline();
        deadline.date=(new Date()).toString();
        deadline.firmId=id;
        deadline.organizerEmail=email;
        DeadlineRepo.create(deadline, new DeadlineRepo.DeadlineFetchCallback() {
            @Override
            public void onDeadlineObjectFetched(Deadline deadline, String errorMessage) {
                DeadlineRepo.DeadlineFetchCallback.super.onDeadlineObjectFetched(deadline, errorMessage);
            }
        });
    }

    private void rejectAllServices(String packageId){
        packageRepo=new PackageRepo();

        ReservationRepo.getServiesByPackageId(packageId, new ReservationRepo.ReservationFetchCallback() {
            @Override
            public void onReservationFetch(ArrayList<Reservation> reservations) {
                ReservationRepo.ReservationFetchCallback.super.onReservationFetch(reservations);
                if(reservations!=null){
                    for(Reservation r:reservations)
                    {
                        lastState=r.getStatus().toString();
                        r.setStatus(ReservationStatus.ORGANIZER_REJECTED);
                        ReservationRepo.update(r, new ReservationRepo.ReservationFetchCallback() {
                            @Override
                            public void onUpdateSuccess() {
                                ReservationRepo.ReservationFetchCallback.super.onUpdateSuccess();
                                if(lastState.equals("ACCEPTED"))
                                    WeeklyEventRepo.delete(r.getId());
                            }
                        });
                    }
                }

            }
        });
    }

    private void rejectAllServicesByPup(String packageId) {
        packageRepo = new PackageRepo();
        ArrayList<Reservation> mReservations=new ArrayList<>();
        ReservationRepo.getServiesByPackageId(packageId, new ReservationRepo.ReservationFetchCallback() {
            @Override
            public void onReservationFetch(ArrayList<Reservation> reservations) {
                ReservationRepo.ReservationFetchCallback.super.onReservationFetch(reservations);
                if(reservations!=null){
                    for(Reservation r:reservations)
                    {
                        r.setStatus(ReservationStatus.PUP_REJECTED);
                        ReservationRepo.update(r, new ReservationRepo.ReservationFetchCallback() {
                            @Override
                            public void onUpdateSuccess() {
                                ReservationRepo.ReservationFetchCallback.super.onUpdateSuccess();
                                WeeklyEventRepo.delete(r.getId());
                                mReservations.add(r);
                                updateReservations(mReservations);
                            }
                        });
                    }
                }

            }
        });
    }

    private void checkAllServicesAccepted(String packageId, CheckAllServicesCallback callback) {
        ReservationRepo.getServiesByPackageId(packageId, new ReservationRepo.ReservationFetchCallback() {
            @Override
            public void onReservationFetch(ArrayList<Reservation> reservations) {
                boolean isAccepted = true;
                for (Reservation r : reservations) {
                    if (!r.getStatus().equals(ReservationStatus.ACCEPTED)) {
                        isAccepted = false;
                        break;
                    }
                }
                callback.onCheckComplete(isAccepted);
            }
        });
    }
    interface CheckAllServicesCallback {
        void onCheckComplete(boolean isAccepted);
    }

    @SuppressLint("SetTextI18n")
    private void updateReservationAndUI(Reservation reservation, TextView status, Button acceptBtn) {
        ReservationRepo.update(reservation, new ReservationRepo.ReservationFetchCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onUpdateSuccess() {
                status.setText("ACCEPTED");
                acceptBtn.setVisibility(View.GONE);
                createWeeklyEvent(reservation);
            }
        });
    }

    private void createWeeklyEventsForPackage(String packageId) {
        ReservationRepo.getServiesByPackageId(packageId, new ReservationRepo.ReservationFetchCallback() {
            @Override
            public void onReservationFetch(ArrayList<Reservation> reservations) {
                for (Reservation r : reservations) {
                    createWeeklyEvent(r);
                }
            }
        });
    }
    private void getOrganizer(Reservation reservation,TextView serviceName, TextView employeeName, TextView organizerName){
        OrganizerRepo.getByEmail(reservation.getOrganizerEmail(), new OrganizerRepo.OrganizerFetchCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onOrganizerObjectFetched(Organizer organizer, String errorMessage) {
                OrganizerRepo.OrganizerFetchCallback.super.onOrganizerObjectFetched(organizer, errorMessage);
                if(organizer!=null){
                    organizerName.setText(organizer.getFirstName()+" "+organizer.getLastName());
                    if(!reservation.getServiceId().isEmpty() && reservation.getPackageId().isEmpty()){
                        serviceRepo=new ServiceRepo();
                        serviceRepo.getById(reservation.getServiceId(), new ServiceRepo.ServiceFetchCallback() {
                            @Override
                            public void onServiceObjectFetched(Service service, String errorMessage) {
                                ServiceRepo.ServiceFetchCallback.super.onServiceObjectFetched(service, errorMessage);
                                serviceName.setText(service.getName());
                                setEmployees(employeeName,reservation);

                            }
                        });
                    }
                    else if(!reservation.getPackageId().isEmpty() && reservation.getServiceId().isEmpty()){
                        packageRepo=new PackageRepo();
                        packageRepo.getById(reservation.getPackageId(), new PackageRepo.PackageFetchCallback() {
                            @Override
                            public void onPackageObjectFetched(Package p, String errorMessage) {
                                PackageRepo.PackageFetchCallback.super.onPackageObjectFetched(p, errorMessage);
                                if(p!=null){
                                    serviceName.setText(p.getName());
                                    setEmployees(employeeName,reservation);
                                }
                            }
                        });
                    }else if(!reservation.getPackageId().isEmpty() && !reservation.getServiceId().isEmpty()){
                        packageRepo=new PackageRepo();
                        packageRepo.getById(reservation.getPackageId(), new PackageRepo.PackageFetchCallback() {
                            @Override
                            public void onPackageObjectFetched(Package p, String errorMessage) {
                                PackageRepo.PackageFetchCallback.super.onPackageObjectFetched(p, errorMessage);
                                if(p!=null){
                                    setServices(p,serviceName,employeeName, reservation);
                                }
                            }
                        });
                    }
                }

            }
        });
    }

    private void initializeButtons(Reservation reservation, Button acceptBtn, Button rejectBtn) {
        HomeActivity activity = (HomeActivity) getContext();
        serviceRepo=new ServiceRepo();
        UserRepo.getUserByEmail(user.getEmail(), new UserRepo.UserFetchCallback() {
            @Override
            public void onUserObjectFetched(User user, String errorMessage) {
                if (user == null) {
                    Log.e("UserFetch", "Error fetching user: " + errorMessage);
                    return;
                }

                // Postavljanje vidljivosti za acceptBtn
                if (reservation.getStatus().equals(ReservationStatus.NEW)) {
                    if (!reservation.getServiceId().isEmpty() && reservation.getPackageId().isEmpty()) {
                        serviceRepo.getById(reservation.getServiceId(), new ServiceRepo.ServiceFetchCallback() {
                            @Override
                            public void onServiceObjectFetched(Service service, String errorMessage) {
                                if (service != null && service.getManualConfirmation() && user.getType() != UserType.ORGANIZER) {
                                    acceptBtn.setVisibility(View.VISIBLE);
                                } else {
                                    acceptBtn.setVisibility(View.GONE);
                                }
                            }
                        });
                    } else if (!reservation.getPackageId().isEmpty() && reservation.getServiceId().isEmpty()) {
                        PackageRepo packageRepo = new PackageRepo();
                        packageRepo.getById(reservation.getPackageId(), new PackageRepo.PackageFetchCallback() {
                            @Override
                            public void onPackageObjectFetched(Package p, String errorMessage) {
                                if (p != null && p.getManualConfirmation() && user.getType() != UserType.ORGANIZER) {
                                    acceptBtn.setVisibility(View.VISIBLE);
                                } else {
                                    acceptBtn.setVisibility(View.GONE);
                                }
                            }
                        });
                    }else{
                        if(user.getType().equals(UserType.ORGANIZER)){
                            acceptBtn.setVisibility(View.GONE);
                            rejectBtn.setVisibility(View.GONE);
                        }
                    }
                } else {
                    acceptBtn.setVisibility(View.GONE);
                }

                if (reservation.getStatus() == ReservationStatus.ADMIN_REJECTED ||
                        reservation.getStatus() == ReservationStatus.ORGANIZER_REJECTED ||
                        reservation.getStatus() == ReservationStatus.PUP_REJECTED ||
                        reservation.getStatus() == ReservationStatus.REALIZED) {
                    rejectBtn.setVisibility(View.GONE);
                } else {
                    if (!reservation.getServiceId().isEmpty() && reservation.getPackageId().isEmpty()) {
                        serviceRepo.getById(reservation.getServiceId(), new ServiceRepo.ServiceFetchCallback() {
                            @Override
                            public void onServiceObjectFetched(Service service, String errorMessage) {
                                if (service != null) {
                                    Date eventDate = convertToDate(reservation.getEventDate().toString());
                                    if (!isCancellationPeriod(eventDate, service.getCancellationDeadline()) && user.getType() == UserType.ORGANIZER) {
                                        rejectBtn.setVisibility(View.GONE);
                                    } else {
                                        rejectBtn.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
                    } else if (!reservation.getPackageId().isEmpty() && reservation.getServiceId().isEmpty()) {
                        PackageRepo packageRepo = new PackageRepo();
                        packageRepo.getById(reservation.getPackageId(), new PackageRepo.PackageFetchCallback() {
                            @Override
                            public void onPackageObjectFetched(Package p, String errorMessage) {
                                if (p != null) {
                                    Date eventDate = convertToDate(reservation.getEventDate().toString());
                                    if (!isCancellationPeriod(eventDate, p.getCancellationDeadline()) && user.getType() == UserType.ORGANIZER) {
                                        rejectBtn.setVisibility(View.GONE);
                                    } else {
                                        rejectBtn.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    public static boolean isCurrentTimeAfter(String timeStr) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime givenTime = LocalTime.parse(timeStr, timeFormatter);
        LocalTime currentTime = LocalTime.now();
        return currentTime.isAfter(givenTime);
    }

}

