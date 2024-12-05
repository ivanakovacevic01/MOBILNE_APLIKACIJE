package com.example.eventapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.eventapp.EmailSender;
import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.activities.MainActivity;
import com.example.eventapp.activities.RegistrationOrganizerActivity;
import com.example.eventapp.fragments.FirmDetailsFragment;
import com.example.eventapp.fragments.administration.EditingEventTypeFragment;
import com.example.eventapp.fragments.administration.EventTypeSubcategoriesFragment;
import com.example.eventapp.fragments.administration.QwnerRequestDetailsFragment;
import com.example.eventapp.fragments.administration.RejectRequestReasonFragment;
import com.example.eventapp.model.Address;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Firm;
import com.example.eventapp.model.LinkExpiration;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.OwnerRequestStatus;
import com.example.eventapp.model.RegistrationOwnerRequest;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.AddressRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.FirmRepo;
import com.example.eventapp.repositories.LinkExpirationRepo;
import com.example.eventapp.repositories.OrganizerRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.RegistrationOwnerRequestRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class OwnerRequestsListAdapter extends ArrayAdapter<RegistrationOwnerRequest> {
    private ArrayList<RegistrationOwnerRequest> aRequests;
    private FirebaseAuth mAuth;
    private ArrayList<Owner> storedOwners;
    private ArrayList<Firm> storedFirms;
    private TextView ownerName;
    private TextView ownerEmail;
    private TextView firmName;
    private TextView firmEmail;


    public OwnerRequestsListAdapter(Context context, ArrayList<RegistrationOwnerRequest> requests, ArrayList<Owner> owners, ArrayList<Firm> firms) {
        super(context, R.layout.request_card, requests);
        aRequests = requests;
        storedOwners = owners;
        storedFirms = firms;


    }

    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
    @Override
    public int getCount() {
        return aRequests.size();
    }


    @Nullable
    @Override
    public RegistrationOwnerRequest getItem(int position) {
        return aRequests.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        mAuth = FirebaseAuth.getInstance();
        RegistrationOwnerRequest request = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.request_card,
                    parent, false);
        }
        LinearLayout requestCart = convertView.findViewById(R.id.request_cart_item);

        TextView requestStatus = convertView.findViewById(R.id.textViewStatusLabel);
        ownerName = convertView.findViewById(R.id.textViewName);
        ownerEmail = convertView.findViewById(R.id.textViewOwnerEmail);
        firmEmail = convertView.findViewById(R.id.textViewFirmEmail);
        firmName = convertView.findViewById(R.id.textViewFirm);

        ImageView acceptRequestBtn = convertView.findViewById(R.id.imageViewAcceptRequest);
        Button showDetailsButton = convertView.findViewById(R.id.buttonDetails);
        Button showFirmButton = convertView.findViewById(R.id.buttonFirm);
        ImageView rejectRequestBtn = convertView.findViewById(R.id.imageViewRejectRequest);


        if (request != null) {


            requestStatus.setText(request.getStatus().toString());


            if(!request.getStatus().toString().equals(OwnerRequestStatus.PENDING.toString()))
            {
                acceptRequestBtn.setVisibility(View.GONE);
                rejectRequestBtn.setVisibility(View.GONE);
            }


            String ownerId = request.getOwnerId();

            Owner owner = getOwnerById(ownerId);
            Firm firm = getFirmById(owner.getFirmId());

            ownerName.setText(owner.getFirstName() + " " + owner.getLastName());
            ownerEmail.setText(owner.getEmail());
            firmName.setText(firm.getName());
            firmEmail.setText(firm.getEmail());





            rejectRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getContext() instanceof HomeActivity) {
                        FragmentManager fragmentManager = ((HomeActivity) getContext()).getSupportFragmentManager();
                        RejectRequestReasonFragment dialog = RejectRequestReasonFragment.newInstance(OwnerRequestsListAdapter.this, position, request);
                        dialog.show(fragmentManager, "RejectRequestReasonFragment");
                    }
                }
            });

            acceptRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserRepo.getUserByEmail(SharedPreferencesManager.getEmail(getContext()), new UserRepo.UserFetchCallback() {
                        @Override
                        public void onUserObjectFetched(User user, String errorMessage) {
                            User admin = user;    //da ga vratim da bude ulogovan on
                            OwnerRepo.get(request.getOwnerId(), new OwnerRepo.OwnerFetchCallback() {
                                @Override
                                public void onOwnerObjectFetched(Owner stored, String errorResponse) {
                                    if (stored != null) {
                                        String oldId = stored.getId();
                                        // On ima email i password
                                        mAuth.createUserWithEmailAndPassword(stored.getEmail(), stored.getPassword())
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            // Sign in success, update UI with the signed-in user's information
                                                            Log.d("uspjesno", "createUserWithEmail:success");
                                                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                                            firebaseUser.sendEmailVerification()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            // Email sent
                                                                            if (task.isSuccessful()) {
                                                                                stored.setId(firebaseUser.getUid());
                                                                                UserRepo.deleteUserByEmail(firebaseUser.getEmail());

                                                                                LinkExpiration link = createLinkExpiration(stored);
                                                                                OwnerRepo.createOwner(stored, new OwnerRepo.OwnerFetchCallback() {
                                                                                    @Override
                                                                                    public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                                                                        if (owner != null) {
                                                                                            LinkExpirationRepo.createLinkExpiration(link, new LinkExpirationRepo.LinkFetchCallback() {
                                                                                                @Override
                                                                                                public void onLinkObjectFetched(LinkExpiration returned, String errorMessage) {
                                                                                                    mAuth.signOut();

                                                                                                    //ulogujem onog admina
                                                                                                    Log.i("adminnn", admin.getEmail() +  admin.getPassword());
                                                                                                    mAuth.signInWithEmailAndPassword(admin.getEmail(), admin.getPassword())
                                                                                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                                                    if (task.isSuccessful()) {
                                                                                                                        Log.d("ulogovao_se", "signInWithEmail:success");
                                                                                                                        request.setStatus(OwnerRequestStatus.ACCEPTED);
                                                                                                                        request.setOwnerId(stored.getId());
                                                                                                                        RegistrationOwnerRequestRepo.updateRequest(request, new RegistrationOwnerRequestRepo.RequestFetchCallback() {
                                                                                                                            @Override
                                                                                                                            public void onUpdateSuccess() {

                                                                                                                                AddressRepo.updateUserIdByOldUserId(oldId, stored.getId(), new AddressRepo.AddressFetchCallback() {
                                                                                                                                    @Override
                                                                                                                                    public void onAddressObjectFetched(Address address, String errorMessage) {
                                                                                                                                        if (address != null) {

                                                                                                                                            notifyDataSetChanged();

                                                                                                                                           /* FirmRepo.activateFirm(stored.getFirmId(), new FirmRepo.FirmFetchCallback() {
                                                                                                                                                @Override
                                                                                                                                                public void onFirmObjectFetched(Firm firm, String errorMessage) {
                                                                                                                                                    if (errorMessage == null) {

                                                                                                                                                    } else {
                                                                                                                                                        Log.e("ActivateFirmError", errorMessage);
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            });*/

                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                });

                                                                                                                            }
                                                                                                                            @Override
                                                                                                                            public void onUpdateFailure(String errorMessage) {

                                                                                                                            }

                                                                                                                        });


                                                                                                                    }
                                                                                                                }
                                                                                                            });
                                                                                                }
                                                                                            });
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
                                }
                            });
                        }
                    });
                }
            });










            showDetailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getContext() instanceof HomeActivity) {
                        FragmentManager fragmentManager = ((HomeActivity) getContext()).getSupportFragmentManager();


                        QwnerRequestDetailsFragment dialog = QwnerRequestDetailsFragment.newInstance(request);
                        dialog.show(fragmentManager, "QwnerRequestDetailsFragment");
                    }
                }
            });

            showFirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getContext() instanceof HomeActivity) {
                        FragmentManager fragmentManager = ((HomeActivity) getContext()).getSupportFragmentManager();


                        FirmDetailsFragment dialog = FirmDetailsFragment.newInstance(firm.getId());
                        dialog.show(fragmentManager, "FirmDetailsFragment");
                    }
                }
            });




        }


        return convertView;
    }

private LinkExpiration createLinkExpiration(Owner owner){
        LinkExpiration link = new LinkExpiration();
        link.setEmail(owner.getEmail());
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Formatiranje datuma i vremena
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        link.setSentTime(formattedDateTime);
        return link;
        }
    private Owner getOwnerById(String id) {
        for (Owner owner : storedOwners) {
            if (owner.getId().equals(id)) {
                return owner;
            }
        }
        return null;
    }
    private Firm getFirmById(String id) {
        for (Firm firm : storedFirms) {
            if (firm.getId().equals(id)) {
                return firm;
            }
        }
        return null;
    }


}

