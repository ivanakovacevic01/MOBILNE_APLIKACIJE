package com.example.eventapp.adapters.subcategories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.R;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Filters;
import com.example.eventapp.model.Subcategory;

import java.util.ArrayList;

public class SubcategoryCheckboxAdapter  extends ArrayAdapter<Subcategory> {

    private ArrayList<Subcategory> subcategories;
    private Filters filters;
    public SubcategoryCheckboxAdapter(Context context, ArrayList<Subcategory>subcategories, Filters filters){
        super(context, R.layout.checkbox_layout, subcategories);
        this.subcategories = subcategories;
        this.filters = filters;
    }

    @Override
    public int getCount() {
        if(subcategories == null)
            return 0;
        return subcategories.size();
    }


    @Nullable
    @Override
    public Subcategory getItem(int position) {
        return subcategories.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Subcategory subcategory = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.checkbox_layout,
                    parent, false);
        }
        CheckBox checkBox = convertView.findViewById(R.id.checkbox_layout_cb);
        checkBox.setText(subcategory.getName());

        if(filters.subcategories.stream().anyMatch(c -> c.getId().equals(subcategory.getId())))
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    if(filters.subcategories.stream().noneMatch(c -> c.getId().equals(subcategory.getId())))
                        filters.subcategories.add(subcategory);
                } else {
                    for(Subcategory t: filters.subcategories){
                        if(t.getId().equals(subcategory.getId())){
                            filters.subcategories.remove(t);
                            break;
                        }
                    }
                }
            }
        });
        return convertView;
    }

}
