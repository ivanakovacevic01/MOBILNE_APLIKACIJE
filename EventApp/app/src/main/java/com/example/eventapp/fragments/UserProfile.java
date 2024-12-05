package com.example.eventapp.fragments;

import android.app.AlertDialog;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentServiceDetails2Binding;
import com.example.eventapp.databinding.FragmentUserProfileBinding;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.NotificationStatus;
import com.example.eventapp.model.Report;
import com.example.eventapp.model.ReportingStatus;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.ReportRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfile extends Fragment {

    private FragmentUserProfileBinding binding;
    private TextView email;
    private TextView name;
    private TextView phone;
    private TextView type;
    private ImageView image;
    private Button onReport;
    private static String user;

    public UserProfile() {
    }

    public static UserProfile newInstance(String userEmail) {
        UserProfile fragment = new UserProfile();
        user=userEmail;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        email=binding.profileEmail;
        name=binding.profileName;
        phone=binding.profilePhone;
        type=binding.profileType;
        onReport=binding.profileReport;
        image=binding.userImage;

        UserRepo.getUserByEmail(user, new UserRepo.UserFetchCallback() {
            @Override
            public void onUserObjectFetched(User user, String errorMessage) {
                email.setText(user.getEmail());
                name.setText(user.getFirstName()+" "+user.getLastName());
                phone.setText(user.getPhoneNumber());
                type.setText(user.getType().toString());
                if(user.getImage()!=null)
                    Picasso.get().load(user.getImage()).into(image);

                onReport.setOnClickListener(v->{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    View view = inflater.inflate(R.layout.report_popup, null);
                    builder.setView(view);

                    final EditText editText = view.findViewById(R.id.editText);
                    Button okButton = view.findViewById(R.id.okButton);

                    final AlertDialog dialog = builder.create();

                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String enteredText = editText.getText().toString();
                            if(!enteredText.isEmpty()){
                                Report report=new Report();
                                report.setDate(new Date().toString());
                                report.setReportedEmail(user.getEmail());
                                report.setReason(enteredText);
                                report.setStatus(ReportingStatus.REPORTED);
                                report.setReportedType("korisnik");
                                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                                report.setReporterEmail(mAuth.getCurrentUser().getEmail());
                                ReportRepo.create(report);
                                Notification notification=new Notification();
                                notification.setDate(new Date().toString());
                                notification.setReceiverRole(UserType.ADMIN);
                                notification.setMessage("New user report: "+enteredText);
                                notification.setStatus(NotificationStatus.NEW);
                                notification.setSenderId(mAuth.getCurrentUser().getUid());
                                NotificationRepo.create(notification);
                                Toast.makeText(getContext(), "User is reported!", Toast.LENGTH_SHORT).show();

                            }
                            dialog.dismiss(); // Zatvaranje dijaloga nakon klika na dugme
                        }
                    });

                    dialog.show();
                });


            }
        });





        return root;
    }
}