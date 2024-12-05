package com.example.eventapp.fragments.administration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentCategoriesPageBinding;
import com.example.eventapp.databinding.FragmentOwnerRequestsPageBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.RegistrationOwnerRequest;

import java.util.ArrayList;


public class OwnerRequestsPageFragment extends Fragment {


    public static ArrayList<RegistrationOwnerRequest> requests = new ArrayList<RegistrationOwnerRequest>();
    private FragmentOwnerRequestsPageBinding binding;

    public static OwnerRequestsPageFragment newInstance() {
        return new OwnerRequestsPageFragment();
    }
    public OwnerRequestsPageFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentOwnerRequestsPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        FragmentTransition.to(OwnerRequestsListFragment.newInstance(requests), getActivity(),
                false, R.id.scroll_owner_requests_list);



        return root;
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}