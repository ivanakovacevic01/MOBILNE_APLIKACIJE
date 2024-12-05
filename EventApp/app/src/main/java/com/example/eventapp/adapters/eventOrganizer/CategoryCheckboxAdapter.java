package com.example.eventapp.adapters.eventOrganizer;

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
import com.example.eventapp.model.Filters;
import com.example.eventapp.model.Subcategory;

import java.util.ArrayList;

public class CategoryCheckboxAdapter extends ArrayAdapter<Category> {

    private ArrayList<Category> categories;
    private Filters filters;
    public CategoryCheckboxAdapter(Context context, ArrayList<Category>categories, Filters filters){
        super(context, R.layout.checkbox_layout, categories);
        this.categories = categories;
        this.filters = filters;
    }

    @Override
    public int getCount() {
        if(categories == null)
            return 0;
        return categories.size();
    }


    @Nullable
    @Override
    public Category getItem(int position) {
        return categories.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Category category = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.checkbox_layout,
                    parent, false);
        }
        CheckBox checkBox = convertView.findViewById(R.id.checkbox_layout_cb);
        checkBox.setText(category.getName());

        if(filters.categories.stream().anyMatch(c -> c.getId().equals(category.getId())))
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    if(filters.categories.stream().noneMatch(c -> c.getId().equals(category.getId())) && checkBox.isChecked())
                        filters.categories.add(category);
                } else {
                    for(Category t: filters.categories){
                        if(t.getId().equals(category.getId())){
                            filters.categories.remove(t);
                            break;
                        }
                    }
                }
            }
        });

        return convertView;
    }


}
