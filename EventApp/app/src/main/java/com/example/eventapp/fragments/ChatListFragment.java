package com.example.eventapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.adapters.ChatListAdapter;
import com.example.eventapp.adapters.MessageAdapter;
import com.example.eventapp.databinding.ChatListBinding;
import com.example.eventapp.model.Message;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.MessageRepo;
import com.example.eventapp.repositories.UserRepo;

import java.util.ArrayList;

public class ChatListFragment extends Fragment {

    private ChatListBinding binding;
    private User sender = new User();
    private ArrayList<User> users = new ArrayList<>();
    public ChatListFragment() {
        // Required empty public constructor
    }


    public static ChatListFragment newInstance() {
        ChatListFragment fragment = new ChatListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ChatListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getUsers();



        return root;

    }

    private void getUsers()
    {
        UserRepo repo = new UserRepo();
        repo.getAll(new UserRepo.UserFetchCallback() {
            @Override
            public void onUserFetch(ArrayList<User> u) {
                UserRepo.UserFetchCallback.super.onUserFetch(u);
                users = u;
                for(User user: users)
                {
                    if(user.getEmail().equals(SharedPreferencesManager.getEmail(getContext())))
                    {
                        sender = user;
                        break;
                    }
                }
                getMessages();
            }
        });
    }

    private void getMessages()
    {
        MessageRepo repo = new MessageRepo();
        ArrayList<User> usersToShow = new ArrayList<>();
        repo.getByUser(sender.getId(), new MessageRepo.MessageFetchCallback() {
            @Override
            public void onMessageFetch(ArrayList<Message> m) {
                MessageRepo.MessageFetchCallback.super.onMessageFetch(m);
                for(Message mess: m)
                {
                    for(User u: users)
                    {
                        if((mess.getRecipientId().equals(u.getId()) || mess.getSenderId().equals(u.getId())) && usersToShow.stream().noneMatch(s -> s.getId().equals(u.getId())))
                        {
                            if(!u.getId().equals(sender.getId()))
                            {
                                usersToShow.add(u);
                                break;
                            }
                        }
                    }
                }
                ListView listView = binding.getRoot().findViewById(R.id.chat_list_id);
                ChatListAdapter adapter = new ChatListAdapter(getActivity(), usersToShow);
                listView.setAdapter(adapter);
            }
        });
    }
}
