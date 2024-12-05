package com.example.eventapp.fragments.administration;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.eventapp.EmailSender;
import com.example.eventapp.R;
import com.example.eventapp.adapters.OwnerRequestsListAdapter;
import com.example.eventapp.databinding.FragmentQwnerRequestDetailsBinding;
import com.example.eventapp.databinding.FragmentRejectRequestReasonBinding;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.OwnerRequestStatus;
import com.example.eventapp.model.RegistrationOwnerRequest;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.FirmRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.RegistrationOwnerRequestRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class RejectRequestReasonFragment extends DialogFragment {

    private OwnerRequestsListAdapter adapter;
    private RegistrationOwnerRequest toRemoveFromAdapter;
    private RegistrationOwnerRequest requestToReject;
   private Owner toReject;
   private FragmentRejectRequestReasonBinding binding;

    public RejectRequestReasonFragment() {
        // Required empty public constructor
    }


    public static RejectRequestReasonFragment newInstance(OwnerRequestsListAdapter adapter, int position, RegistrationOwnerRequest request) {
        RejectRequestReasonFragment fragment = new RejectRequestReasonFragment();
        fragment.adapter = adapter;
        Bundle args = new Bundle();
        args.putParcelable("TO_REJECT", request);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requestToReject = getArguments().getParcelable("TO_REJECT");
            toRemoveFromAdapter = getArguments().getParcelable("TO_REJECT");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRejectRequestReasonBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        EditText reason = binding.rejectionRequestReason;

        //dobavim tog vlasnika
        OwnerRepo ownerRepo = new OwnerRepo();
        FirmRepo firmRepo = new FirmRepo();
        ownerRepo.get(requestToReject.getOwnerId(), new OwnerRepo.OwnerFetchCallback() {
                    @Override
                    public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                        OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                        if (owner != null) {
                            toReject = owner;
                            Button btnConfirm = (Button) binding.confirmButton;
                            btnConfirm.setOnClickListener(v -> {
                                if (reason.getText() != null && !reason.getText().toString().equals("")) {
                                    String recipientEmail = toReject.getEmail();
                                    String emailBody = "<html><body><p>" + reason.getText().toString() + "</p></body></html>";


                                    //azuriram request status
                                    requestToReject.setStatus(OwnerRequestStatus.REJECTED);
                                    RegistrationOwnerRequestRepo.updateRequest(requestToReject, new RegistrationOwnerRequestRepo.RequestFetchCallback() {
                                        @Override
                                        public void onUpdateSuccess() {

                                            adapter.remove(toRemoveFromAdapter);
                                                    UserRepo.deleteUserByEmail(owner.getEmail());

                                                    new EmailSender(recipientEmail, emailBody).execute();
                                                    dismiss();


                                        }
                                        @Override
                                        public void onUpdateFailure(String errorMessage) {

                                        }

                                    });

                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage("Invalid reason")
                                            .setTitle("Rejecting request")
                                            .setPositiveButton("OK", null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });
                        }
                    }
                });









        return view;
    }
}