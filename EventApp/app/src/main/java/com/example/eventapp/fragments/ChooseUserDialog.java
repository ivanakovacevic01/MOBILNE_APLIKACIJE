package com.example.eventapp.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventapp.R;
import com.example.eventapp.adapters.ChooseUserAdapter;
import com.example.eventapp.databinding.CheckboxesMenuBinding;
import com.example.eventapp.databinding.ChooseUserBinding;
import com.example.eventapp.fragments.eventOrganizer.PopupDialogFragment;
import com.example.eventapp.model.DialogType;
import com.example.eventapp.model.Filters;
import com.example.eventapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChooseUserDialog extends DialogFragment {

    private ChooseUserBinding binding;
    private List<User> users;
    public static ChooseUserDialog newInstance(List<User> userList) {
        ChooseUserDialog fragment = new ChooseUserDialog();
        fragment.users = userList;
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ChooseUserBinding binding = ChooseUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button back = binding.getRoot().findViewById(R.id.backButton);
        back.setOnClickListener(e -> {
            //back
            this.dismiss();
        });

        ArrayAdapter<User> adapter = new ChooseUserAdapter(getActivity(), new ArrayList<>(users));
        ListView listView = binding.getRoot().findViewById(R.id.userList);
        listView.setAdapter(adapter);

        return root;
    }


}
