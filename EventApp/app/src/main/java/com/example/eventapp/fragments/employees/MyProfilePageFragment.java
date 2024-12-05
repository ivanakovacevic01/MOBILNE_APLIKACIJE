package com.example.eventapp.fragments.employees;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.databinding.FragmentEmployeeProfileBinding;
import com.example.eventapp.databinding.FragmentEmployeesPageBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Owner;
import com.example.eventapp.repositories.OrganizerRepo;
import com.example.eventapp.repositories.OwnerRepo;

import java.util.ArrayList;

public class MyProfilePageFragment extends Fragment {
    private FragmentEmployeeProfileBinding binding;

    public static MyProfilePageFragment newInstance() {
        return new MyProfilePageFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEmployeeProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if(SharedPreferencesManager.getUserRole(getContext()).equals("EMPLOYEE"))
        {
            FragmentTransition.to(EmployeeProfileFragment.newInstance(), getActivity(),
                    true, R.id.scroll_profile);
        }else if(SharedPreferencesManager.getUserRole(getContext()).equals("OWNER"))
        {
            OwnerRepo.getByEmail(SharedPreferencesManager.getEmail(getContext()), new OwnerRepo.OwnerFetchCallback() {
                @Override
                public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                    OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                    FragmentTransition.to(OwnerProfileFragment.newInstance(owner), getActivity(),
                            true, R.id.scroll_profile);
                }
            });

        }else if(SharedPreferencesManager.getUserRole(getContext()).equals("ORGANIZER"))
        {
            OrganizerRepo.getByEmail(SharedPreferencesManager.getEmail(getContext()), new OrganizerRepo.OrganizerFetchCallback() {
                @Override
                public void onOrganizerObjectFetched(Organizer organizer, String errorMessage) {
                    OrganizerRepo.OrganizerFetchCallback.super.onOrganizerObjectFetched(organizer, errorMessage);
                    FragmentTransition.to(OrganizerProfileFragment.newInstance(organizer), getActivity(),
                            true, R.id.scroll_profile);
                }
            });

        }


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
