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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.eventapp.R;
import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.UserProfile;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.NotificationStatus;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Report;
import com.example.eventapp.model.ReportingStatus;
import com.example.eventapp.model.Reservation;
import com.example.eventapp.model.ReservationStatus;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.model.WeeklyEvent;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.EventRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.OrganizerRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.PackageRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ReportRepo;
import com.example.eventapp.repositories.ReservationRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.UserRepo;
import com.example.eventapp.repositories.WeeklyEventRepo;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;

public class ReportsAdapter extends ArrayAdapter<Report> {
    private ArrayList<Report> aReports;
    private TextView reporterName;
    private TextView reporterEmail;
    private TextView repotredName;
    private TextView reportedEmail;
    private TextView reportingReason;
    private TextView date;
    private TextView status;
    private Button accept;
    private Button reject;
    private LinearLayout reporterLinearLayout;
    private LinearLayout reportedLinearLayout;

    public ReportsAdapter(Context context, ArrayList<Report> reports){
        super(context, R.layout.report_card, reports);
        aReports = reports;



    }
    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
    @Override
    public int getCount() {
        return aReports.size();
    }


    @Nullable
    @Override
    public Report getItem(int position) {
        return aReports.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Report report = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.report_card,
                    parent, false);
        }

        reporterName = convertView.findViewById(R.id.reporter_name);
        reporterEmail = convertView.findViewById(R.id.reporter_email);
        repotredName = convertView.findViewById(R.id.reported_name);
        reportedEmail = convertView.findViewById(R.id.reported_email);
        reportingReason =  convertView.findViewById(R.id.report_reason);
        accept = convertView.findViewById(R.id.acceptReport);
        reject = convertView.findViewById(R.id.rejectReport);
        reporterLinearLayout=convertView.findViewById(R.id.reporter);
        reportedLinearLayout=convertView.findViewById(R.id.reported);
        status=convertView.findViewById(R.id.status);
        date=convertView.findViewById(R.id.report_date);
        reportingReason.setText(report.getReason());
        reportedEmail.setText(report.getReportedEmail());
        reporterEmail.setText(report.getReporterEmail());
        date.setText(report.getDate().substring(0, report.getDate().indexOf("GMT")).trim());

        reportedLinearLayout.setOnClickListener(pr->{
            HomeActivity activity = (HomeActivity) getContext();
            FragmentTransition.to(UserProfile.newInstance(report.getReportedEmail()),activity ,
                    true, R.id.scroll_reports_list);
        });

        reporterLinearLayout.setOnClickListener(pri->{
            HomeActivity activity = (HomeActivity) getContext();
            FragmentTransition.to(UserProfile.newInstance(report.getReporterEmail()),activity ,
                    true, R.id.scroll_reports_list);

        });
        String reportedEmail=report.getReportedEmail();
        String reporterEmail=report.getReporterEmail();

        UserRepo.getUserByEmail(reportedEmail, new UserRepo.UserFetchCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onUserObjectFetched(User user, String errorMessage) {
                if (user != null) {


                repotredName.setText(user.getFirstName() + " " + user.getLastName());

                UserRepo.getUserByEmail(reporterEmail, new UserRepo.UserFetchCallback() {
                    @Override
                    public void onUserObjectFetched(User user1, String errorMessage) {
                        reporterName.setText(user1.getFirstName() + " " + user1.getLastName());
                        if (report.getStatus().equals(ReportingStatus.REPORTED)) {
                            accept.setVisibility(View.VISIBLE);
                            reject.setVisibility(View.VISIBLE);
                            status.setVisibility(View.GONE);

                        } else {
                            accept.setVisibility(View.GONE);
                            reject.setVisibility(View.GONE);
                            status.setText(report.getStatus().toString());

                        }

                        accept.setOnClickListener(a -> {


                            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                            dialog.setMessage("Are you sure you want to accept this report?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if (user.getType().equals(UserType.ORGANIZER)) {
                                                ReservationRepo.getByEmail(user.getEmail(), new ReservationRepo.ReservationFetchCallback() {
                                                    @Override
                                                    public void onReservationFetch(ArrayList<Reservation> reservations) {
                                                        ReservationRepo.ReservationFetchCallback.super.onReservationFetch(reservations);
                                                        for (Reservation r : reservations) {
                                                            EventRepo.getById(r.getEventId(), new EventRepo.EventFetchCallback() {
                                                                @Override
                                                                public void onEventFetch(ArrayList<Event> events) {

                                                                }

                                                                @Override
                                                                public void onEventObjectFetched(Event event, String errorMessage) {
                                                                    EventRepo.EventFetchCallback.super.onEventObjectFetched(event, errorMessage);
                                                                    if (event.getDate().after(new Date()) && (r.getStatus().equals(ReservationStatus.ACCEPTED) || r.getStatus().equals(ReservationStatus.NEW))) {
                                                                        r.setStatus(ReservationStatus.ADMIN_REJECTED);
                                                                        ReservationRepo.update(r);
                                                                        WeeklyEventRepo.getAll(new WeeklyEventRepo.WeeklyEventFetchCallback() {
                                                                            @Override
                                                                            public void onWeeklyEventFetch(ArrayList<WeeklyEvent> events) {
                                                                                WeeklyEventRepo.WeeklyEventFetchCallback.super.onWeeklyEventFetch(events);
                                                                                for(WeeklyEvent weeklyEvent:events)
                                                                                    if(weeklyEvent.reservationId.equals(r.getId()))
                                                                                        WeeklyEventRepo.delete(weeklyEvent.getId());
                                                                            }
                                                                        });
                                                                        EmployeeRepo.getByFirmId(r.getFirmId(), new EmployeeRepo.EmployeeFetchCallback() {
                                                                            @Override
                                                                            public void onEmployeeFetch(ArrayList<Employee> employees) {
                                                                                EmployeeRepo.EmployeeFetchCallback.super.onEmployeeFetch(employees);
                                                                                for (Employee e : employees) {
                                                                                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                                                                    Notification notification = new Notification();
                                                                                    notification.setDate(new Date().toString());
                                                                                    notification.setReceiverId(e.getId());
                                                                                    notification.setReceiverRole(UserType.EMPLOYEE);
                                                                                    notification.setMessage("Cancelled reservation: Organizer " + user.getEmail() + " is reported. Reservation " + r.getId() + " canceled.");
                                                                                    notification.setStatus(NotificationStatus.NEW);
                                                                                    notification.setSenderId(mAuth.getCurrentUser().getUid());
                                                                                    NotificationRepo.create(notification);
                                                                                }
                                                                            }
                                                                        });
                                                                    }

                                                                }
                                                            });

                                                        }
                                                    }
                                                });
                                            }


                                            if (user.getType().equals(UserType.OWNER)) {
                                                OwnerRepo.get(user.getId(), new OwnerRepo.OwnerFetchCallback() {
                                                    @Override
                                                    public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                                        OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);

                                                        ReservationRepo.getAllAcceptedAndNewByFirmId(owner.getFirmId(), new ReservationRepo.ReservationFetchCallback() {
                                                            @Override
                                                            public void onReservationFetch(ArrayList<Reservation> reservations) {
                                                                ReservationRepo.ReservationFetchCallback.super.onReservationFetch(reservations);
                                                                for (Reservation r : reservations) {
                                                                    r.setStatus(ReservationStatus.ADMIN_REJECTED);
                                                                    ReservationRepo.update(r);
                                                                    WeeklyEventRepo.getAll(new WeeklyEventRepo.WeeklyEventFetchCallback() {
                                                                        @Override
                                                                        public void onWeeklyEventFetch(ArrayList<WeeklyEvent> events) {
                                                                            WeeklyEventRepo.WeeklyEventFetchCallback.super.onWeeklyEventFetch(events);
                                                                            for(WeeklyEvent weeklyEvent:events)
                                                                                if(weeklyEvent.reservationId.equals(r.getId()))
                                                                                    WeeklyEventRepo.delete(weeklyEvent.getId());
                                                                        }
                                                                    });
                                                                    OrganizerRepo.getByEmail(r.getOrganizerEmail(), new OrganizerRepo.OrganizerFetchCallback() {
                                                                        @Override
                                                                        public void onOrganizerObjectFetched(Organizer organizer, String errorMessage) {
                                                                            OrganizerRepo.OrganizerFetchCallback.super.onOrganizerObjectFetched(organizer, errorMessage);
                                                                            Notification notification = new Notification();
                                                                            notification.setDate(new Date().toString());
                                                                            notification.setReceiverId(organizer.getId());
                                                                            notification.setReceiverRole(UserType.ORGANIZER);
                                                                            notification.setMessage("Rejected reservation: " + r.getId() + " because owner is reported");
                                                                            notification.setStatus(NotificationStatus.NEW);
                                                                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                                                            notification.setSenderId(mAuth.getCurrentUser().getUid());
                                                                            NotificationRepo.create(notification);
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                        PackageRepo.getByFirmId(owner.getFirmId(), new PackageRepo.PackageFetchCallback() {
                                                            @Override
                                                            public void onPackageFetch(ArrayList<Package> packages) {
                                                                PackageRepo.PackageFetchCallback.super.onPackageFetch(packages);
                                                                for (Package p : packages) {
                                                                    p.setAvailable(false);
                                                                    PackageRepo.updatePackage(p);
                                                                }
                                                            }
                                                        });
                                                        ServiceRepo.getByFirmId(owner.getFirmId(), new ServiceRepo.ServiceFetchCallback() {
                                                            @Override
                                                            public void onServiceFetch(ArrayList<Service> services) {
                                                                ServiceRepo.ServiceFetchCallback.super.onServiceFetch(services);
                                                                for (Service s : services) {
                                                                    s.setAvailable(false);
                                                                    ServiceRepo.updateService(s);
                                                                }

                                                            }
                                                        });
                                                        ProductRepo.getByFirmId(owner.getFirmId(), new ProductRepo.ProductFetchCallback() {
                                                            @Override
                                                            public void onProductFetch(ArrayList<Product> products) {
                                                                ProductRepo.ProductFetchCallback.super.onProductFetch(products);
                                                                for (Product p : products) {
                                                                    p.setAvailable(false);
                                                                    ProductRepo.updateProduct(p);
                                                                }
                                                            }
                                                        });
                                                        EmployeeRepo.getByFirmId(owner.getFirmId(), new EmployeeRepo.EmployeeFetchCallback() {
                                                            @Override
                                                            public void onEmployeeFetch(ArrayList<Employee> employees) {
                                                                EmployeeRepo.EmployeeFetchCallback.super.onEmployeeFetch(employees);
                                                                for (Employee employee : employees) {
                                                                    employee.setBlocked(true);
                                                                    EmployeeRepo.update(employee);
                                                                }
                                                            }
                                                        });


                                                    }
                                                });

                                            }
                                            user.setBlocked(true);
                                            UserRepo.update(user);
                                            report.setStatus(ReportingStatus.ACCEPTED);
                                            accept.setVisibility(View.GONE);
                                            reject.setVisibility(View.GONE);
                                            status.setText(report.getStatus().toString());
                                            status.setVisibility(View.VISIBLE);
                                            ReportRepo.update(report);
                                            Toast.makeText(getContext(), "Report is accepted!", Toast.LENGTH_SHORT).show();
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

                        reject.setOnClickListener(r -> {


                            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                            dialog.setMessage("Are you sure you want to reject this report?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                                            LayoutInflater inflater = LayoutInflater.from(getContext());
                                            View view = inflater.inflate(R.layout.report_popup, null);
                                            builder.setView(view);

                                            final EditText editText = view.findViewById(R.id.editText);
                                            Button okButton = view.findViewById(R.id.okButton);

                                            final android.app.AlertDialog dialog1 = builder.create();

                                            okButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    String enteredText = editText.getText().toString();
                                                    if (!enteredText.isEmpty()) {

                                                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                                        Notification notification = new Notification();
                                                        notification.setDate(new Date().toString());
                                                        notification.setReceiverId(user1.getId());
                                                        notification.setReceiverRole(user1.getType());
                                                        notification.setMessage("Rejected report: " + enteredText);
                                                        notification.setStatus(NotificationStatus.NEW);
                                                        notification.setSenderId(mAuth.getCurrentUser().getUid());
                                                        NotificationRepo.create(notification);

                                                    }
                                                    dialog1.dismiss(); // Zatvaranje dijaloga nakon klika na dugme
                                                }
                                            });

                                            dialog1.show();

                                            report.setStatus(ReportingStatus.REJECTED);
                                            accept.setVisibility(View.GONE);
                                            reject.setVisibility(View.GONE);
                                            status.setText(report.getStatus().toString());
                                            status.setVisibility(View.VISIBLE);
                                            ReportRepo.update(report);
                                            Toast.makeText(getContext(), "Report is rejected!", Toast.LENGTH_SHORT).show();
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
                });
            }



            }
        });










        return convertView;
    }
}
