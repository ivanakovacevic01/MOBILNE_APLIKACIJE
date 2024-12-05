package com.example.eventapp.fragments.eventOrganizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventapp.R;
import com.example.eventapp.adapters.eventOrganizer.EventTypeAdapter;
import com.example.eventapp.databinding.EventFormBinding;
import com.example.eventapp.model.DialogType;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.SubcategoryRepo;

import java.util.ArrayList;

public class EventFormFragment extends Fragment {

    private EventFormBinding eventFormBinding;
    private Event newEvent;
    private ArrayList<EventType> eventTypes = new ArrayList<>();
    private EventType selectedType = new EventType();
    public static EventFormFragment newInstance() {
        return new EventFormFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        eventFormBinding = EventFormBinding.inflate(inflater, container, false);
        View root = eventFormBinding.getRoot();

        EventTypeRepo eventTypeRepo = new EventTypeRepo();
        LinearLayout linearLayout = root.findViewById(R.id.event_form);
        final EventTypeAdapter[] typeAdapter = new EventTypeAdapter[1];
        Spinner typeSpinner = linearLayout.findViewById(R.id.spinner_type);
        eventTypeRepo.getAllEventTypes(new EventTypeRepo.EventTypeFetchCallback() {
            @Override
            public void onEventTypeFetch(ArrayList<EventType> types) {
                eventTypes.addAll(types);
                EventType otherType = new EventType();
                otherType.setId("0");
                otherType.setName("Other");
                otherType.setSuggestedSubcategoriesIds(new ArrayList<>());
                eventTypes.add(otherType);
                typeAdapter[0] = new EventTypeAdapter(getContext(), eventTypes);
                typeSpinner.setAdapter(typeAdapter[0]);
                selectedType = eventTypes.get(0);
            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            selectedType = eventTypes.get(position);
         }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        newEvent = new Event();

        //button next
        Button btnNext = linearLayout.findViewById(R.id.button_next);
        btnNext.setOnClickListener(v -> {
            this.fillEventFields(root);
            if(this.isValid())
            {
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.event_fragment, LocationTimeFormFragment.newInstance(this.newEvent)).addToBackStack(null);
                fragmentTransaction.commit();
            }else{
                //error message
                Toast.makeText(getContext(), "All fields must be valid!", Toast.LENGTH_SHORT).show();
            }
        });

        //button manage budget
        Button btnBudget = linearLayout.findViewById(R.id.button_budgeting);
        btnBudget.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.event_fragment, BudgetingHomeFragment.newInstance(this.newEvent.getId())).addToBackStack("BUDGET");
            fragmentTransaction.commit();
        });

        //button categories
        Button btnCategories = linearLayout.findViewById(R.id.spinner_category);
        btnCategories.setOnClickListener(v -> {
            CatSubcatPopupDialogFragment popup = CatSubcatPopupDialogFragment.newInstance(true, DialogType.Caterogies, null, selectedType, null, null);
            popup.show(getChildFragmentManager(), "cat");
        });

        //button subcategories
        Button btnSubcategories = linearLayout.findViewById(R.id.spinner_subcategory);
        btnSubcategories.setOnClickListener(v -> {
            CatSubcatPopupDialogFragment popup = CatSubcatPopupDialogFragment.newInstance(true, DialogType.SubCategories, null, selectedType, null, null);
            popup.show(getChildFragmentManager(), "subcat");
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventFormBinding = null;
    }

    private boolean isValid(){
        return !this.newEvent.getEventTypeId().isEmpty() && !this.newEvent.getName().isEmpty() && !this.newEvent.getDescription().isEmpty()
                && this.newEvent.getMaxNumberOfParticipans()>0;
    }

    private void fillEventFields(View root){
        LinearLayout linearLayout = root.findViewById(R.id.event_form);
        Spinner s = (Spinner)linearLayout.findViewById(R.id.spinner_type);
        //Object p = s.getSelectedItem();
        //EventType id = (EventType) s.getSelectedItem();
        this.newEvent.setEventTypeId(((EventType)s.getSelectedItem()).getId());
        this.newEvent.setName(((EditText)linearLayout.findViewById(R.id.editTextName)).getText().toString());
        this.newEvent.setDescription(((EditText)linearLayout.findViewById(R.id.editTextTextMultiLine)).getText().toString());
        if(!((EditText) linearLayout.findViewById(R.id.editTextNumberSigned)).getText().toString().isEmpty())
         this.newEvent.setMaxNumberOfParticipans(Integer.parseInt(((EditText)linearLayout.findViewById(R.id.editTextNumberSigned)).getText().toString()));
        this.newEvent.setPrivate(((CheckBox)linearLayout.findViewById(R.id.checkBox)).isChecked());
    }

    private ArrayList<EventType> getEventTypes(){
        EventTypeRepo eventTypeRepo = new EventTypeRepo();
        ArrayList<EventType> eventTypes = new ArrayList<>();
        eventTypeRepo.getAllActicateEventTypes(new EventTypeRepo.EventTypeFetchCallback() {
            @Override
            public void onEventTypeFetch(ArrayList<EventType> types) {
                eventTypes.addAll(types);
            }
        });
        return eventTypes;
    }

}
