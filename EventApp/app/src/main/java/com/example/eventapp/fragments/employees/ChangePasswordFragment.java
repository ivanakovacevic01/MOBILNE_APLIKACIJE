package com.example.eventapp.fragments.employees;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.databinding.ChangePasswordBinding;
import com.example.eventapp.databinding.EditPersonalInfoBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.OrganizerRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ChangePasswordFragment extends Fragment {
    private ChangePasswordBinding binding;
    private User employee;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }


    public static ChangePasswordFragment newInstance(User e) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        fragment.employee = e;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ChangePasswordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Button save = binding.getRoot().findViewById(R.id.saveButton);

        save.setOnClickListener(new View.OnClickListener() {

            EditText oldPassword = root.findViewById(R.id.editEmployeeTextOldPass);
            EditText newPassword = root.findViewById(R.id.editEmployeeTextNewPass);
            EditText newPassword2 = root.findViewById(R.id.editEmployeeTextNewPass2);
            @Override
            public void onClick(View v) {

                if (isValid()) {

                    AuthCredential credential = EmailAuthProvider.getCredential(employee.getEmail(), oldPassword.getText().toString());
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    // Re-authenticate the user with the provided credential
                    user.reauthenticate(credential)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Password reauthentication successful
                                    // Now update the user's password with the new password
                                    user.updatePassword(newPassword.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Password updated successfully
                                                    Log.d("ChangePassword", "Password updated successfully");
                                                    FragmentTransition.to(EditProfileFragment.newInstance(employee), getActivity(),
                                                            false, R.id.scroll_profile);
                                                    Toast.makeText(getContext(), "Password changed!", Toast.LENGTH_SHORT).show();
                                                    UserRepo.getUserByEmail(SharedPreferencesManager.getEmail(getContext()), new UserRepo.UserFetchCallback() {
                                                        @Override
                                                        public void onUserObjectFetched(User user, String errorMessage) {
                                                            UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                                                            UserRepo.updatePassword(newPassword.getText().toString(), user.getId());
                                                        }
                                                    });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Failed to update password
                                                    Log.d("ChangePassword", "Failed to update password: " + e.getMessage());

                                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();}
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Password reauthentication failed
                                    Log.d("ChangePassword", "Password reauthentication failed: " + e.getMessage());
                                    Toast.makeText(getContext(), "Old password is incorrect!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else if(newPassword.getText().toString().isEmpty() || newPassword2.getText().toString().isEmpty() || oldPassword.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(), "Field cannot be empty!", Toast.LENGTH_SHORT).show();
                }
                else if (!isValid()) {
                    Toast.makeText(getContext(), "Confirmation password must be the same!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Old password is incorrect!", Toast.LENGTH_SHORT).show();
                }


            }});

        return root;

    }

    private boolean isValid()
    {
        EditText oldPassword = binding.getRoot().findViewById(R.id.editEmployeeTextOldPass);
        EditText newPassword = binding.getRoot().findViewById(R.id.editEmployeeTextNewPass);
        EditText newPassword2 = binding.getRoot().findViewById(R.id.editEmployeeTextNewPass2);
        return !newPassword.getText().toString().isEmpty() && !newPassword2.getText().toString().isEmpty() && !oldPassword.getText().toString().isEmpty() && newPassword.getText().toString().equals(newPassword2.getText().toString());
    }
}
