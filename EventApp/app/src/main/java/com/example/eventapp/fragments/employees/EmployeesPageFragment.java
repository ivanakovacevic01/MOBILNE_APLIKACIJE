package com.example.eventapp.fragments.employees;

import androidx.fragment.app.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventapp.R;
import com.example.eventapp.databinding.FragmentEmployeesPageBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Employee;

import java.util.ArrayList;

public class EmployeesPageFragment extends Fragment{

    public static ArrayList<Employee> employees = new ArrayList<Employee>();
    private FragmentEmployeesPageBinding binding;
    private RegistrationEmployeeFragment fragment;

    public static EmployeesPageFragment newInstance() {
        return new EmployeesPageFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragment=new RegistrationEmployeeFragment();
        binding = FragmentEmployeesPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FragmentTransition.to(EmployeesListFragment.newInstance(employees), getActivity(),
                false, R.id.scroll_employees_list);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}