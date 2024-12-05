package com.example.eventapp.fragments.eventOrganizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;

import com.example.eventapp.R;
import com.example.eventapp.adapters.eventOrganizer.CategoryCheckboxAdapter;
import com.example.eventapp.adapters.eventOrganizer.EventTypeCheckboxAdapter;

import com.example.eventapp.adapters.subcategories.SubcategoryCheckboxAdapter;
import com.example.eventapp.databinding.CheckboxesMenuBinding;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.DialogType;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Filters;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PopupDialogFragment extends DialogFragment {
    private DialogType type = DialogType.Date;
    public Filters filters = new Filters();
    /*
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(type == DialogType.Date) {
            this.DatePickerdialog();
        }
        else
        {
            View view = getActivity().getLayoutInflater().inflate(R.layout.checkboxes_menu, null);
            builder.setView(view);
            /*
            if(type == DialogType.Caterogies)
            {
                CheckboxesMenuBinding binding = CheckboxesMenuBinding.inflate(getActivity().getLayoutInflater(), (ViewGroup) view, false);
                View root = binding.getRoot();
                if(type == DialogType.Caterogies){
                    CategoryCheckboxAdapter adapter = new CategoryCheckboxAdapter(getContext(), createCategories());
                    ListView listView = root.findViewById(R.id.listView_dialog);
                    listView.setAdapter(adapter);
                    listView.invalidate();
                }
            }


            builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    getChildFragmentManager().popBackStack();
                }
            });

            builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancels the dialog.
            }
        });
        }

        // Create the AlertDialog object and return it.
        return builder.create();
    }
    */

    //getActivity().getLayoutInflater()
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (type == DialogType.Date) {
            this.DatePickerdialog();
            return null;
        }
        else if (type == DialogType.RatingDate) {
            this.DatePickerdialog1();
            return null;
        }
        else {
            CheckboxesMenuBinding binding = CheckboxesMenuBinding.inflate(inflater, container, false);
            View root = binding.getRoot();
            TextView tv = root.findViewById(R.id.checkbox_menu_title);
            ListView listView = root.findViewById(R.id.listView_dialog);
            if (type == DialogType.Caterogies) {
                tv.setText("Categories");
                createCategories(listView);
            } else if (type == DialogType.SubCategories) {
                tv.setText("Subcategories");
                createSubcategories(listView);
            } else if (type == DialogType.EventTypes) {
                tv.setText("Event Types");
                createEventTypeList(listView);
            }

            Button exit = root.findViewById(R.id.checkbox_menu_exit);
            exit.setOnClickListener(e -> {
                dismiss();
            });

            Button apply = root.findViewById(R.id.checkbox_menu_apply);
            apply.setOnClickListener(e -> {
                dismiss();
            });

            return root;

        }

    }

    public static PopupDialogFragment newInstance(DialogType type, Filters filters) {
        PopupDialogFragment fragment = new PopupDialogFragment();
        fragment.type = type;
        fragment.filters = filters;
        return fragment;
    }

    private void DatePickerdialog() {
        // Creating a MaterialDatePicker builder for selecting a date range
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");
        // create the calendar constraint builder
        CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();

        // set the validator point forward from june
        // this mean the all the dates before the June month
        // are blocked
        calendarConstraintBuilder.setValidator(DateValidatorPointForward.now());

        builder.setCalendarConstraints(calendarConstraintBuilder.build());


        if(this.filters.startDate != null && this.filters.endDate != null){
            Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone(String.valueOf(Locale.getDefault())));
            if(this.filters.startDate.getYear() < 2000)
                cal1.set(Calendar.YEAR, this.filters.startDate.getYear() + 1900);
            else
                cal1.set(Calendar.YEAR, this.filters.startDate.getYear());
            cal1.set(Calendar.MONTH, this.filters.startDate.getMonth());
            cal1.set(Calendar.DAY_OF_MONTH, this.filters.startDate.getDate());
            Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone(String.valueOf(Locale.getDefault())));
            if(this.filters.startDate.getYear() < 2000)
                cal2.set(Calendar.YEAR, this.filters.endDate.getYear() + 1900);
            else
                cal2.set(Calendar.YEAR, this.filters.endDate.getYear());
            cal2.set(Calendar.MONTH, this.filters.endDate.getMonth());
            cal2.set(Calendar.DAY_OF_MONTH, this.filters.endDate.getDate());
            builder.setSelection(new Pair<>(cal1.getTimeInMillis(), cal2.getTimeInMillis()));
        }
        builder.setPositiveButtonText("Apply");
        // Building the date picker dialog
        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(selection.first));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            filters.startDate = new Date(calendar.getTimeInMillis());
            calendar.setTime(new Date(selection.second));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            filters.endDate = new Date(calendar.getTimeInMillis());

            //filters.startDate =  new Date(selection.first);
            //filters.endDate = new Date(selection.second);
            getActivity().getSupportFragmentManager().popBackStack();
            dismiss();
        });

        datePicker.addOnNegativeButtonClickListener(selection -> {
            getActivity().getSupportFragmentManager().popBackStack();
            dismiss();
        });

        // Showing the date picker dialog
        datePicker.show(getActivity().getSupportFragmentManager(), "DATE_PICKER");
    }

    private void DatePickerdialog1() {
        // Creating a MaterialDatePicker builder for selecting a date range
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");

        // Building the date picker dialog
        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(selection.first);
            calendar1.set(Calendar.HOUR_OF_DAY, 0);
            Date startDate = new Date(calendar1.getTimeInMillis());

            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTimeInMillis(selection.second);
            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            Date endDate = new Date(calendar2.getTimeInMillis());

            if (listener != null) {
                listener.onDateRangeSelected(startDate, endDate);
            }
            getActivity().getSupportFragmentManager().popBackStack();
            dismiss();
        });

        datePicker.addOnNegativeButtonClickListener(selection -> {
            getActivity().getSupportFragmentManager().popBackStack();
            dismiss();
        });

        // Showing the date picker dialog
        datePicker.show(getActivity().getSupportFragmentManager(), "DATE_PICKER");
    }



    private void createCategories(ListView listView) {
        CategoryRepo repo = new CategoryRepo();
        repo.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
            @Override
            public void onCategoryFetch(ArrayList<Category> types) {
                CategoryCheckboxAdapter adapter = new CategoryCheckboxAdapter(getActivity(), types, filters);
                listView.setAdapter(adapter);
            }
        });
    }

    public void createSubcategories(ListView listView) {
        SubcategoryRepo repo = new SubcategoryRepo();
        repo.getAllSubcategories(new SubcategoryRepo.SubcategoryFetchCallback() {
            @Override
            public void onSubcategoryFetch(ArrayList<Subcategory> types) {
                SubcategoryCheckboxAdapter adapter = new SubcategoryCheckboxAdapter(getActivity(), types, filters);
                listView.setAdapter(adapter);
            }
        });
    }

    private void createEventTypeList(ListView listView) {
        EventTypeRepo repo = new EventTypeRepo();
        repo.getAllEventTypes(new EventTypeRepo.EventTypeFetchCallback() {
            @Override
            public void onEventTypeFetch(ArrayList<EventType> types) {
                EventTypeCheckboxAdapter adapter = new EventTypeCheckboxAdapter(getActivity(), types, filters);
                listView.setAdapter(adapter);
            }
        });
    }
    public interface OnDateRangeSelectedListener {
        void onDateRangeSelected(Date startDate, Date endDate);
    }

    private OnDateRangeSelectedListener listener;

    public void setOnDateRangeSelectedListener(OnDateRangeSelectedListener listener) {
        this.listener = listener;
    }

}

