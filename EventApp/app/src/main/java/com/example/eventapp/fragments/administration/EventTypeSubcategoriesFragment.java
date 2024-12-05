package com.example.eventapp.fragments.administration;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.repositories.SubcategoryRepo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExistingSubcategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventTypeSubcategoriesFragment extends DialogFragment {

    ArrayList<String> storedSubcategoriesIds;
    ArrayList<Subcategory> storedSubcategories;

    public EventTypeSubcategoriesFragment() {
        // Required empty public constructor

    }

    public static EventTypeSubcategoriesFragment newInstance(ArrayList<String> subcategoriesIds) {
        EventTypeSubcategoriesFragment fragment = new EventTypeSubcategoriesFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("SUBCATEGORIES", subcategoriesIds);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            storedSubcategoriesIds = getArguments().getStringArrayList("SUBCATEGORIES");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_type_subcategories, container, false);


        //dodavanje dinamicko u tabeku
        TableLayout subcategoryTable = view.findViewById(R.id.eventTypeSubcategoriesTable);
        storedSubcategories = new ArrayList<>();



        //dobavim sve objekte subkategorija sa dobavljenim id-jevima
        SubcategoryRepo.getSubcategoriesByIds(storedSubcategoriesIds, new SubcategoryRepo.SubcategoryFetchCallback() {
            @Override
            public void onSubcategoryFetch(ArrayList<Subcategory> subcategoriesByIds) {
                if (subcategoriesByIds != null) {
                    storedSubcategories = subcategoriesByIds;

                    for (Subcategory subcateg : storedSubcategories) {

                        TableRow row = new TableRow(getContext());
                        TextView textView = new TextView(getContext());
                        textView.setText(subcateg.getName());

                        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                        textView.setLayoutParams(params);
                        row.addView(textView);
                        subcategoryTable.addView(row);
                    }
                }
            }

        });
        return view;
    }
}