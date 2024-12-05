package com.example.eventapp.fragments.employees;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.ListFragment;

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.R;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Employee;
import com.example.eventapp.adapters.employee.EmployeeListAdapter;
import com.example.eventapp.databinding.FragmentEmployeesListBinding;

import com.example.eventapp.model.Owner;
import com.example.eventapp.repositories.EmployeeRepo;
import com.example.eventapp.repositories.OwnerRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EmployeesListFragment extends ListFragment implements RegistrationEmployeeFragment.OnEmployeeAddedListener {
    private EmployeeListAdapter adapter;
    private static final String ARG_PARAM = "param";
    private ArrayList<Employee> mEmployees;
    private ArrayList<Employee> allEmployees;
    private FragmentEmployeesListBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EmployeeRepo employeeRepo;
    private String firmId;
    private SearchView searchName;
    private OwnerRepo ownerRepo;
    private FirebaseUser currentUser;
    private String storedOwnerId;

    public static EmployeesListFragment newInstance(ArrayList<Employee> employees){
        EmployeesListFragment fragment = new EmployeesListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM, employees);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("EventApp", "onCreate Employees List Fragment");
        //setListByOwner();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mEmployees = new ArrayList<>();
        allEmployees=new ArrayList<>();
        employeeRepo = new EmployeeRepo();
        ownerRepo = new OwnerRepo();
        storedOwnerId="";
       // Log.i("EventApp","current user: "+currentUser.getUid());

    }
    // Ostatak koda iz prethodnog primjera..

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("ShopApp", "onCreateView Employees List Fragment");
        binding = FragmentEmployeesListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button registerButton = root.findViewById(com.example.eventapp.R.id.register_button);

        if (currentUser != null) {
            String ownerId = currentUser.getUid();
            if(storedOwnerId.equals("")){
                storedOwnerId=ownerId;
            }
            Log.i("EventApp","current user: "+currentUser.getUid()+" "+storedOwnerId);


            ownerRepo.get(storedOwnerId, new OwnerRepo.OwnerFetchCallback() {
                @Override
                public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                    OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                    if (owner != null) {
                        firmId = owner.getFirmId();
                        employeeRepo.getByFirmId(firmId, new EmployeeRepo.EmployeeFetchCallback() {
                            @Override
                            public void onEmployeeFetch(ArrayList<Employee> employees) {
                                if (employees != null) {
                                    allEmployees.clear();
                                    allEmployees.addAll(employees);
                                    Log.i("EventApp", "firm" + allEmployees.size());
                                    adapter = new EmployeeListAdapter(getContext(), allEmployees, (AppCompatActivity) getActivity());
                                    setListAdapter(adapter);
                                }
                            }
                        });

                    }
                }
            });
        }



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null && getActivity() instanceof HomeActivity) {
                    FragmentTransition.to(RegistrationEmployeeFragment.newInstance(null),getActivity(),true,R.id.scroll_employees_list);
                }
            }
        });
        searchName=binding.searchText;
        //po imenu, prezimenu i emailu
        searchName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                EmployeeRepo.getByFirmId(firmId,new EmployeeRepo.EmployeeFetchCallback(){
                    @Override
                    public void onEmployeeFetch(ArrayList<Employee> employees) {
                        if (employees != null) {
                            allEmployees=new ArrayList<>();
                            for(Employee e:employees){
                                if (e.getFirstName().toLowerCase().contains(newText.toLowerCase()) && !allEmployees.contains(e))
                                    allEmployees.add(e);
                                if (e.getLastName().toLowerCase().contains(newText.toLowerCase()) && !allEmployees.contains(e))
                                    allEmployees.add(e);
                                if (e.getEmail().split("@")[0].toLowerCase().contains(newText.toLowerCase()) && !allEmployees.contains(e))
                                    allEmployees.add(e);

                            }
                            adapter=new EmployeeListAdapter(getContext(),allEmployees, (AppCompatActivity) getActivity());
                            setListAdapter(adapter);
                        }
                    }
                });

                return true;
            }
        });

        searchName.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                EmployeeRepo.getByFirmId(firmId,new EmployeeRepo.EmployeeFetchCallback(){
                    @Override
                    public void onEmployeeFetch(ArrayList<Employee> employees) {
                        if (employees != null) {
                            adapter=new EmployeeListAdapter(getContext(),allEmployees, (AppCompatActivity) getActivity());
                            setListAdapter(adapter);
                        }
                    }
                });
                searchName.setQuery("", false);
                return false;
            }
        });
        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onEmployeeAdded(Employee employee) {
        allEmployees.add(employee);
        adapter = new EmployeeListAdapter(getContext(), allEmployees, (AppCompatActivity) getActivity());
        setListAdapter(adapter);
    }




}
