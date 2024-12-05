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
import com.example.eventapp.model.Subcategory;

import java.util.ArrayList;

public class SuggestedSubcategoriesAdapter extends ArrayAdapter<Subcategory> {


    private ArrayList<Subcategory> subcategories;
    private boolean readOnly;

    public SuggestedSubcategoriesAdapter(Context context, ArrayList<Subcategory> subcategories, boolean readOnly){
        super(context, R.layout.subcategory_item_card, subcategories);
        this.subcategories = subcategories;
        this.readOnly = readOnly;
    }

    @Override
    public int getCount() {
        return subcategories.size();
    }


    @Nullable
    @Override
    public Subcategory getItem(int position) {
        return subcategories.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Subcategory s = getItem(position);
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
