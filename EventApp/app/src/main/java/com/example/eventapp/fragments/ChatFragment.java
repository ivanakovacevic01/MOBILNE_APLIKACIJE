package com.example.eventapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.adapters.MessageAdapter;
import com.example.eventapp.databinding.ChatBinding;
import com.example.eventapp.fragments.products.ProductDetails;
import com.example.eventapp.model.Message;
import com.example.eventapp.model.MessageStatus;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.MessageRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ChatFragment extends Fragment {

    private ChatBinding binding;
    private User recipient = new User();
    private User sender = new User();
    private ArrayList<Message> messages = new ArrayList<>();
    public ChatFragment() {
        // Required empty public constructor

    }


    public static ChatFragment newInstance(String recipientId) {
        ChatFragment fragment = new ChatFragment();
        fragment.recipient.setId(recipientId);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getSender();
        getRecipient();
       // if(sender.getType()==UserType.OWNER) {
            Button profile = binding.getRoot().findViewById(R.id.profile);
            profile.setOnClickListener(v -> {
                FragmentTransition.to(UserProfile.newInstance(recipient.getEmail()), getActivity(),
                        true, R.id.home_page_fragment);
            });
       // }

        Button back = binding.getRoot().findViewById(R.id.buttonBack);
        back.setOnClickListener(e -> {
            //back
            getActivity().getSupportFragmentManager().popBackStack();
        });

        EditText text = binding.getRoot().findViewById(R.id.content);
        Button send = binding.getRoot().findViewById(R.id.sendButton);
        send.setOnClickListener(e -> {
           // MessageRepo repo = new MessageRepo();
            Message m = new Message();
            m.setDate(new Date());
            LocalTime currentTime = LocalTime.now();
            m.setTime(currentTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            m.setText(text.getText().toString());
            m.setRecipientId(recipient.getId());
            m.setSenderId(sender.getId());
            m.setStatus(MessageStatus.NOT_OPENED);
            text.setText("");

            messages.add(m);

            //Notification
            Notification newNotification = new Notification();
            newNotification.setMessage("New message from " + sender.getFirstName() + " " + sender.getLastName());
            newNotification.setReceiverRole(recipient.getType());
            newNotification.setDate(new Date().toString());
            newNotification.setSenderId(sender.getId());
            newNotification.setReceiverId(recipient.getId());
            NotificationRepo.create(newNotification);

            // Define a comparator for comparing messages by date and time
            Comparator<Message> messageComparator = new Comparator<Message>() {
                @Override
                public int compare(Message m1, Message m2) {
                    // First, compare dates
                    int dateComparison = m1.getDate().compareTo(m2.getDate());
                    if (dateComparison != 0) {
                        return dateComparison;
                    }
                    // If dates are equal, compare times
                    return m1.getTime().compareTo(m2.getTime());
                }
            };
            Collections.sort(messages, messageComparator);

            MessageAdapter adapter;
            if(sender.getEmail().equals(SharedPreferencesManager.getEmail(getContext())))
                adapter = new MessageAdapter(getActivity(), messages, sender, recipient);
            else
                adapter = new MessageAdapter(getActivity(), messages, recipient, sender);
            ListView listView = binding.getRoot().findViewById(R.id.messages);

            listView.setAdapter(adapter);
            int lastItemPosition = messages.size() - 1;
            listView.setSelection(lastItemPosition);
            MessageRepo.create(m, new MessageRepo.MessageFetchCallback() {
                @Override
                public void onMessageFetch(ArrayList<Message> messages) {
                    MessageRepo.MessageFetchCallback.super.onMessageFetch(messages);

                }
            });
        });

        return root;

    }

    private void getRecipient()
    {
        UserRepo.getUserById(recipient.getId(), new UserRepo.UserFetchCallback() {
            @Override
            public void onUserObjectFetched(User user, String errorMessage) {
                UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                recipient = user;

                TextView name = binding.getRoot().findViewById(R.id.recipientName);
                name.setText(recipient.getFirstName() + " " + recipient.getLastName());

                ImageView mimageView=binding.getRoot().findViewById(R.id.profile_image);
                Picasso.get().load(recipient.getImage()).into(mimageView);
            }
        });
    }
    private void getSender()
    {
        UserRepo.getUserByEmail(SharedPreferencesManager.getEmail(getContext()), new UserRepo.UserFetchCallback() {
            @Override
            public void onUserObjectFetched(User user, String errorMessage) {
                UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                sender = user;
                getMessages();
            }
        });
    }

    private void getMessages()
    {
        MessageRepo repo = new MessageRepo();
        repo.getBy2Users(sender.getId(), recipient.getId(), new MessageRepo.MessageFetchCallback() {
            @Override
            public void onMessageFetch(ArrayList<Message> m) {
                MessageRepo.MessageFetchCallback.super.onMessageFetch(m);
                messages = m;

                // Define a comparator for comparing messages by date and time
                Comparator<Message> messageComparator = new Comparator<Message>() {
                    @Override
                    public int compare(Message m1, Message m2) {
                        // First, compare dates
                        int dateComparison = m1.getDate().compareTo(m2.getDate());
                        if (dateComparison != 0) {
                            return dateComparison;
                        }
                        // If dates are equal, compare times
                        return m1.getTime().compareTo(m2.getTime());
                    }
                };
                Collections.sort(messages, messageComparator);

                ListView listView = binding.getRoot().findViewById(R.id.messages);
                MessageAdapter adapter;
                adapter = new MessageAdapter(getActivity(), messages, sender, recipient);
                listView.setAdapter(adapter);
                int lastItemPosition = messages.size() - 1;
                listView.setSelection(lastItemPosition);
            }
        });
    }

    final Handler handler = new Handler();
    private Runnable refreshRunnable;

    @Override
    public void onResume() {
        super.onResume();
        startRefreshing();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRefreshing();
    }

    private void startRefreshing() {
            refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(this, 5000);
                    if(isVisible())
                        getMessages();
                }
            };
            handler.postDelayed(refreshRunnable, 5000);
    }

    private void stopRefreshing() {
        handler.removeCallbacks(refreshRunnable);
    }

}
