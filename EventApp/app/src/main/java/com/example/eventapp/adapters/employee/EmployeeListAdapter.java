package com.example.eventapp.adapters.employee;

import android.content.Context;
import android.content.DialogInterface;
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

import com.example.eventapp.fragments.employees.EmployeeDetailsFragment;
import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Employee;

import com.example.eventapp.R;
import com.example.eventapp.model.LinkExpiration;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.LinkExpirationRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class EmployeeListAdapter extends ArrayAdapter<Employee> {
    private ArrayList<Employee> aEmployees;
    private AppCompatActivity mActivity;
    private String state="deactivate";
    private FirebaseAuth mAuth;
    private FirebaseUser owner;
    private String idForChange;


    public EmployeeListAdapter(Context context, ArrayList<Employee> employees,AppCompatActivity activity){
        super(context, R.layout.employee_card, employees);
        aEmployees = employees;
        mActivity=activity;
    }
    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
    @Override
    public int getCount() {
        return aEmployees.size();
    }


    @Nullable
    @Override
    public Employee getItem(int position) {
        return aEmployees.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Employee employee = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.employee_card,
                    parent, false);
        }
        LinearLayout employeeCard = convertView.findViewById(R.id.employee_card);
        ImageView imageView = convertView.findViewById(R.id.employee_image);
        TextView name = convertView.findViewById(R.id.employee_name);
        TextView email = convertView.findViewById(R.id.employee_email);
        Button switch1 =convertView.findViewById(R.id.employee_switch);
        mAuth = FirebaseAuth.getInstance();
        owner=mAuth.getCurrentUser();
        if(employee != null){
            if (employee.getImage()!=null && employee.getImage()!="") {
                Picasso.get().load(employee.getImage()).into(imageView);
            }

            name.setText(employee.getFirstName()+" "+employee.getLastName());
            email.setText(employee.getEmail());
            employeeCard.setOnClickListener(v -> {
                if(getContext() instanceof HomeActivity){
                    HomeActivity activity = (HomeActivity) getContext();
                    FragmentTransition.to(EmployeeDetailsFragment.newInstance(employee),activity,true,R.id.scroll_employees_list);

                }
            });
            if(employee.isDeactivated()){
                switch1.setText("Activate");
                state="activate";
            }else{
                switch1.setText("Deactivate");
                state="deactivate";
            }
            switch1.setOnClickListener(v -> {
                if (getContext() instanceof HomeActivity) {
                    idForChange=employee.getId();
                    HomeActivity activity = (HomeActivity) getContext();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

                    if (switch1.getText() == "Activate") {
                        dialog.setMessage("Are you sure you want to " + state + " this account?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Log.i("pass","pass email "+employee.getEmail()+" "+employee.getPassword());


                                        mAuth.createUserWithEmailAndPassword(employee.getEmail(), employee.getPassword())
                                                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            employee.setId(mAuth.getUid());
                                                            UserRepo.create((User) employee, new UserRepo.UserFetchCallback() {
                                                                @Override
                                                                public void onUserObjectFetched(User user, String errorMessage) {
                                                                    Log.i("TAB","user ulog "+mAuth.getCurrentUser().getEmail());
                                                                    sendEmailVerification(mAuth.getCurrentUser(), employee);
                                                                    mAuth.updateCurrentUser(owner);
                                                                    switch1.setText("Deactivate");
                                                                    state = "deactivate";
                                                                    employee.setId(user.getId());
                                                                    employee.setDeactivated(false);
                                                                    EmployeeRepo.updateEmployee(employee,idForChange, new EmployeeRepo.EmployeeFetchCallback() {
                                                                        @Override
                                                                        public void onUpdateSuccess() {
                                                                            Log.i("eventapp","uspjesno");
                                                                            EmployeeRepo.EmployeeFetchCallback.super.onUpdateSuccess();
                                                                        }

                                                                        @Override
                                                                        public void onUpdateFailure(String errorMessage) {
                                                                            EmployeeRepo.EmployeeFetchCallback.super.onUpdateFailure(errorMessage);
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        } else {
                                                        }
                                                    }
                                                });



                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        switch1.setText("Activate");
                                        state = "activate";
                                        employee.setDeactivated(true);
                                        /*EmployeeRepo.updateEmployee(employee, idForChange, new EmployeeRepo.EmployeeFetchCallback() {
                                            @Override
                                            public void onUpdateSuccess() {
                                                Log.i("eventapp","uspjesno");
                                                EmployeeRepo.EmployeeFetchCallback.super.onUpdateSuccess();
                                            }

                                            @Override
                                            public void onUpdateFailure(String errorMessage) {
                                                EmployeeRepo.EmployeeFetchCallback.super.onUpdateFailure(errorMessage);
                                            }
                                        });*/
                                    }
                                });
                        AlertDialog alert = dialog.create();
                        alert.show();
                    } else {
                        dialog.setMessage("Are you sure you want to " + state + " this account?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Log.i("pass","pass email "+employee.getEmail()+" "+employee.getPassword());

                                        mAuth.signInWithEmailAndPassword(employee.getEmail(), employee.getPassword())
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> signInTask) {
                                                        if (signInTask.isSuccessful()) {
                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                            if (user != null) {
                                                                user.delete()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Log.d("EventApp", "User successfully deleted.");
                                                                                    mAuth.updateCurrentUser(owner);
                                                                                    UserRepo.deleteUser(employee.getEmail(), new UserRepo.DeleteUserCallback() {
                                                                                        @Override
                                                                                        public void onUserDeleted(boolean success, String errorMessage) {
                                                                                            switch1.setText("Activate");
                                                                                            state = "activate";
                                                                                            employee.setDeactivated(true);
                                                                                            EmployeeRepo.updateEmployee(employee,idForChange, new EmployeeRepo.EmployeeFetchCallback() {
                                                                                                @Override
                                                                                                public void onUpdateSuccess() {
                                                                                                    EmployeeRepo.EmployeeFetchCallback.super.onUpdateSuccess();
                                                                                                }

                                                                                                @Override
                                                                                                public void onUpdateFailure(String errorMessage) {
                                                                                                    EmployeeRepo.EmployeeFetchCallback.super.onUpdateFailure(errorMessage);
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    });

                                                                                } else {
                                                                                    Log.w("EventApp", "Failed to delete user.", task.getException());
                                                                                }
                                                                            }
                                                                        });
                                                            } else {
                                                                Log.d("EventApp", "The user is not logged in.");
                                                            }
                                                        } else {
                                                            Log.w("EventApp", "Login failed.", signInTask.getException());
                                                        }
                                                    }
                                                });

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        switch1.setText("Deactivate");
                                        state = "deactivate";
                                        employee.setDeactivated(false);
                                        /*EmployeeRepo.updateEmployee(employee, idForChange,new EmployeeRepo.EmployeeFetchCallback() {
                                            @Override
                                            public void onUpdateSuccess() {
                                                EmployeeRepo.EmployeeFetchCallback.super.onUpdateSuccess();
                                            }

                                            @Override
                                            public void onUpdateFailure(String errorMessage) {
                                                EmployeeRepo.EmployeeFetchCallback.super.onUpdateFailure(errorMessage);
                                            }
                                        });*/
                                    }
                                });
                        AlertDialog alert = dialog.create();
                        alert.show();

                    }
                }
            });
        }
        return convertView;
    }


    private LinkExpiration createLinkExpiration(Employee e){
        LinkExpiration link = new LinkExpiration();
        link.setEmail(e.getEmail());
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        link.setSentTime(formattedDateTime);
        return link;
    }

    private void sendEmailVerification(FirebaseUser user,Employee e) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("EventApp", "Email sent.");
                            LinkExpiration link = createLinkExpiration(e);
                            LinkExpirationRepo.createLinkExpiration(link, new LinkExpirationRepo.LinkFetchCallback() {
                                @Override
                                public void onLinkObjectFetched(LinkExpiration link, String errorMessage) {
                                    LinkExpirationRepo.LinkFetchCallback.super.onLinkObjectFetched(link, errorMessage);
                                }
                            });
                        } else {
                            Log.e("EventApp", "Failed to send verification email", task.getException());
                        }
                    });
        }
    }
}

