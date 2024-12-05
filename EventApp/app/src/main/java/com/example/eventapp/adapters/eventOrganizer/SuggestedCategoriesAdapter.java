package com.example.eventapp.adapters.eventOrganizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.R;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Subcategory;

import java.util.ArrayList;

public class SuggestedCategoriesAdapter extends ArrayAdapter<Category> {
    private ArrayList<Category> categories;
    private boolean readOnly;

    public SuggestedCategoriesAdapter(Context context, ArrayList<Category> categories, boolean readOnly){
        super(context, R.layout.subcategory_item_card, categories);
        this.categories = categories;
        this.readOnly = readOnly;
    }

    @Override
    public int getCount() {
        return categories.size();
    }


    @Nullable
    @Override
    public Category getItem(int position) {
        return categories.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Category s = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.subcategory_item_card,
                    parent, false);
        }
        TextView subcat = convertView.findViewById(R.id.subcat_name);
        subcat.setText(s.getName());

        if(this.readOnly){
            ImageView sug = convertView.findViewById(R.id.subcat_suggested);
            sug.setVisibility(View.INVISIBLE);

        }

        return convertView;
    }
}
