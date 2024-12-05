package com.example.eventapp.fragments.employees;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.databinding.FragmentEmployeeProfileBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Employee;
import com.example.eventapp.model.Organizer;
import com.example.eventapp.model.Owner;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.OrganizerRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmployeeProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmployeeProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentEmployeeProfileBinding binding;
    private boolean isFirstSelection = true;
    private EmployeeProfileFragment fragment;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EmployeeProfileFragment() {
        // Required empty public constructor
    }

    public static EmployeeProfileFragment newInstance() {
        return new EmployeeProfileFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEmployeeProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(SharedPreferencesManager.getUserRole(getContext()).equals("EMPLOYEE"))
        {
            Employee e=new Employee();
            EmployeeRepo.getByEmail(SharedPreferencesManager.getEmail(getContext()), new EmployeeRepo.EmployeeFetchCallback() {
                @Override
                public void onEmployeeObjectFetched(Employee employee, String errorMessage) {
                    EmployeeRepo.EmployeeFetchCallback.super.onEmployeeObjectFetched(employee, errorMessage);
                    FragmentTransition.to(EmployeeDetailsFragment.newInstance(employee), getActivity(),
                            false, R.id.scroll_profile);

                }
            });
        }else if(SharedPreferencesManager.getUserRole(getContext()).equals("ORGANIZER"))
        {
            Organizer e=new Organizer();
            OrganizerRepo repo = new OrganizerRepo();
            repo.getByEmail(SharedPreferencesManager.getEmail(getContext()), new OrganizerRepo.OrganizerFetchCallback() {
                @Override
                public void onOrganizerObjectFetched(Organizer employee, String errorMessage) {
                    OrganizerRepo.OrganizerFetchCallback.super.onOrganizerObjectFetched(employee, errorMessage);
                    FragmentTransition.to(OrganizerProfileFragment.newInstance(employee), getActivity(),
                            false, R.id.scroll_profile);

                }
            });
        }else if(SharedPreferencesManager.getUserRole(getContext()).equals("OWNER"))
        {
            Owner e=new Owner();
            OwnerRepo repo = new OwnerRepo();
            repo.getByEmail(SharedPreferencesManager.getEmail(getContext()), new OwnerRepo.OwnerFetchCallback() {
                @Override
                public void onOwnerObjectFetched(Owner employee, String errorMessage) {
                    OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(employee, errorMessage);
                    FragmentTransition.to(OwnerProfileFragment.newInstance(employee), getActivity(),
                            false, R.id.scroll_profile);

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