package com.example.eventapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.databinding.ActivityMainBinding;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.User;

import com.example.eventapp.model.LinkExpiration;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.FirmRepo;
import com.example.eventapp.repositories.LinkExpirationRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.RegistrationOwnerRequestRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private boolean activityIsRunning = false;  //potrebna radi alerta, pa prelaza na drugu aktivnost
    private FirebaseUser loggedFirebaseUser;
    private User loggedUser;
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activityIsRunning = true;   //
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        email = binding.emailEditText;
        password = binding.passwordEditText;

        mAuth = FirebaseAuth.getInstance();
        //izloguj
        mAuth.signOut();
        //brisem token
        SharedPreferencesManager.clearToken(this);
        SharedPreferencesManager.clearEmail(this);
        SharedPreferencesManager.clearUserRole(this);


        //dugme za: reg se kao vlasnik
        Button registerOwnerButton = findViewById(R.id.signUpProviderButton);
        registerOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pokrecem HomeActivity
                Intent intent = new Intent(MainActivity.this, RegistrationOwnerActivity.class);
                startActivity(intent);
            }
        });

        //dugme za: reg se kao organizator
        Button registerOrganizerButton = findViewById(R.id.signUpOrganizerButton);
        registerOrganizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pokrecem HomeActivity
                Intent intent = new Intent(MainActivity.this, RegistrationOrganizerActivity.class);
                startActivity(intent);
            }
        });



        //dugme za: nastavi kao NK
        Button continueWithoutRegistrationButton = findViewById(R.id.continueWithoutRegistrationButton);
        continueWithoutRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pokrecem HomeActivity
                SharedPreferencesManager.saveUserRole(MainActivity.this, "NOT_LOGGED_IN");
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


        Button ownerButton = findViewById(R.id.ownerButton);
        ownerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pokrecem HomeActivity
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                editor.putString("key_string", "OWNER");
                editor.apply();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


        Button employeeButton = findViewById(R.id.employeeButton);
        employeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pokrecem HomeActivity
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                editor.putString("key_string", "EMPLOYEE");
                editor.apply();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        Button adminButton = findViewById(R.id.adminButton);
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pokrecem HomeActivity
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                editor.putString("key_string", "ADMIN");
                editor.apply();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            login();


        });


    }

    @Override
    public void onStart() {
        super.onStart();

    }
    private void login() {
        if(areFieldsValid()) {
            //provjera da li je aktivan
            progressDialog.show();
            UserRepo.getUserByEmail(email.getText().toString(), new UserRepo.UserFetchCallback() {
                @Override
                public void onUserObjectFetched(User user, String errorResponse) {
                    if (user != null && !user.isDeactivated()) {
                        if(!user.isBlocked()) {
                            loggedUser = user;

                            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("ulogovao_se", "signInWithEmail:success");
                                                loggedFirebaseUser = mAuth.getCurrentUser();
                                                manageFirstLoginSettings();

                                            } else {
                                                progressDialog.dismiss();
                                                Log.w("nije_se_ulogovao", "signInWithEmail:failure", task.getException());
                                                Toast.makeText(MainActivity.this, "Invalid credentials ",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Blocked user. ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Not found user. ",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    private boolean areFieldsValid() {
        if(email.getText().toString().isEmpty()) {
            email.setError("Email is required.");
            return false;
        }
        if(password.getText().toString().isEmpty()){
            password.setError("Password is required.");
            return false;
        }
        return true;
    }

    private void continueNext(User user) {
        loggedFirebaseUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            String idToken = task.getResult().getToken();
                            //prikazem mu token i sacuvam ga u sharedPreferences
                            showTokenAlert(idToken);
                            SharedPreferencesManager.saveToken(MainActivity.this, idToken);
                            SharedPreferencesManager.saveUserRole(MainActivity.this, user.getType().toString());
                            SharedPreferencesManager.saveEmail(MainActivity.this, user.getEmail());
                        }
                        progressDialog.dismiss();
                    }
                });



    }
    private void showTokenAlert(String token) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Firebase ID Token");
        builder.setMessage("Save your token: " + token);
        builder.setPositiveButton("OK", (dialog, which) -> {

            dialog.dismiss();
            activityIsRunning = false;
            if(!activityIsRunning) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
               // intent.putExtra("USER_EXTRA", loggedUser);    //saljem u glavnu aktivnost ulogovanog usera!
                startActivity(intent);
                finish();//DA SE NE MOZE VRATITI NA LOGIN NA DUGME BACK
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void manageFirstLoginSettings() {
        if(loggedUser.getType().toString().equals("ADMIN")) {
            Log.i("adminko", "admin");
            continueNext(loggedUser);
        } else if (loggedUser.getType().toString().equals("OWNER")) {
            OwnerRepo.get(loggedUser.getId(), new OwnerRepo.OwnerFetchCallback() {
                @Override
                public void onOwnerObjectFetched(Owner stored, String errorResponse) {
                    if (stored != null) {


                        if(!loggedFirebaseUser.isEmailVerified()) {
                            //provjera da li je proslo 24h
                            LinkExpirationRepo.getLinkExpirationByEmail(loggedFirebaseUser.getEmail(), new LinkExpirationRepo.LinkFetchCallback() {
                                @Override
                                public void onLinkObjectFetched(LinkExpiration link, String errorMessage) {
                                    LinkExpirationRepo.LinkFetchCallback.super.onLinkObjectFetched(link, errorMessage);
                                    long hoursDifference = calculateHoursDifference(link);
                                    if(hoursDifference > 24) {

                                        Log.i("provjera", stored.getFirmId());

                                                Toast.makeText(MainActivity.this, "Your activation link has expired. Register again. ",
                                                        Toast.LENGTH_SHORT).show();
                                                loggedFirebaseUser.delete();
                                                progressDialog.dismiss();
                                                mAuth.signOut();
                                                Log.d("REZ_DB", "Firm deleted successfully");
                                                UserRepo.deleteUserByEmail(loggedFirebaseUser.getEmail());
                                                LinkExpirationRepo.deleteByEmail(loggedFirebaseUser.getEmail());
                                           

                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Confirm activation link. ",
                                                Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    }
                                }
                            });

                        }
                        else {
                            if(!loggedUser.isActive()) {
                                loggedUser.setActive(true); //nasa provjera
                                Log.i("neaktivan ali emailovan", "d");
                                UserRepo.activateUserByEmail(loggedUser.getEmail(), new UserRepo.UserUpdateCallback() {
                                    @Override
                                    public void onUpdateSuccess() {
                                        Log.d("Update", "Organizer successfully updated.");
                                        FirmRepo.activateFirm(stored.getFirmId(), new FirmRepo.FirmFetchCallback() {
                                            @Override
                                            public void onFirmObjectFetched(Firm firm, String errorMessage) {
                                                if (errorMessage == null) {
                                                    continueNext(loggedUser);
                                                } else {
                                                    Log.e("ActivateFirmError", errorMessage);
                                                }
                                            }
                                        });


                                    }
                                    @Override
                                    public void onUpdateFailure(String errorMessage) {
                                        Log.e("Update", "Error updating organizer: " + errorMessage);
                                    }
                                });
                            }
                            else {
                                Log.i("aktivan i emailovan", "d");
                                continueNext(loggedUser);
                            }
                        }
                    }
                }
            });


        }
        else {
            if(!loggedFirebaseUser.isEmailVerified()) {
                //provjera da li je proslo 24h
                LinkExpirationRepo.getLinkExpirationByEmail(loggedFirebaseUser.getEmail(), new LinkExpirationRepo.LinkFetchCallback() {
                    @Override
                    public void onLinkObjectFetched(LinkExpiration link, String errorMessage) {
                        LinkExpirationRepo.LinkFetchCallback.super.onLinkObjectFetched(link, errorMessage);
                        long hoursDifference = calculateHoursDifference(link);
                        if(hoursDifference > 24) {
                            Toast.makeText(MainActivity.this, "Your activation link has expired. Register again. ",
                                    Toast.LENGTH_SHORT).show();
                            loggedFirebaseUser.delete();
                            progressDialog.dismiss();
                            mAuth.signOut();
                            UserRepo.deleteUserByEmail(loggedFirebaseUser.getEmail());
                            LinkExpirationRepo.deleteByEmail(loggedFirebaseUser.getEmail());
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Confirm activation link. ",
                                    Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    }
                });

            }
            else {
                if(!loggedUser.isActive()) {
                    loggedUser.setActive(true); //nasa provjera
                    Log.i("neaktivan ali emailovan", "d");
                    UserRepo.activateUserByEmail(loggedUser.getEmail(), new UserRepo.UserUpdateCallback() {
                        @Override
                        public void onUpdateSuccess() {
                            Log.d("Update", "Organizer successfully updated.");
                            continueNext(loggedUser);

                        }
                        @Override
                        public void onUpdateFailure(String errorMessage) {
                            Log.e("Update", "Error updating organizer: " + errorMessage);
                        }
                    });
                }
                else {
                    Log.i("aktivan i emailovan", "d");
                    continueNext(loggedUser);
                }
            }
        }

    }

    private long calculateHoursDifference(LinkExpiration link) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime formattedDateTime = LocalDateTime.parse(link.getSentTime(), formatter);
        long hoursDifference = ChronoUnit.HOURS.between(formattedDateTime, LocalDateTime.now());
        return  hoursDifference;
    }

}










