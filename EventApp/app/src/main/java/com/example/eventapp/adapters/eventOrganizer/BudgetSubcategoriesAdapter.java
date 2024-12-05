package com.example.eventapp.adapters.eventOrganizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.R;
import com.example.eventapp.fragments.eventOrganizer.BudgetingDetailsFragment;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.EventBudgetItem;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.Subcategory;

import java.util.ArrayList;

public class BudgetSubcategoriesAdapter extends ArrayAdapter<EventBudgetItem> {

    private ArrayList<EventBudgetItem> subcategories;
    private ArrayList<Subcategory> subcats = new ArrayList<>();
    private ArrayList<Category> cats = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Service> services = new ArrayList<>();
    private ArrayList<Package> packages = new ArrayList<>();
    private EventBudget budget;


    public BudgetSubcategoriesAdapter(Context context, ArrayList<EventBudgetItem> subcategories,
                                      ArrayList<Subcategory> ss, ArrayList<Category> cs,
                                      ArrayList<Product> p, ArrayList<Service> s, ArrayList<Package> pa, EventBudget budget){
        super(context, R.layout.budget_subcategory_card, subcategories);
        this.subcategories = subcategories;
        this.subcats = ss;
        this.cats = cs;
        this.products = p;
        this.services = s;
        this.packages = pa;
        this.budget = budget;
    }

    @Override
    public int getCount() {
        return subcategories.size();
    }


    @Nullable
    @Override
    public EventBudgetItem getItem(int position) {
        return subcategories.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        EventBudgetItem s = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.budget_subcategory_card,
                    parent, false);
        }
        TextView title = convertView.findViewById(R.id.subcat_title);
        Subcategory ss = new Subcategory();
        for(Subcategory sub: subcats)
        {
            if(sub.getId().equals(s.getSubcategoryId())){
                ss = sub;
                break;
            }
        }

        title.setText(ss.getName());
        TextView category = convertView.findViewById(R.id.subcat_cat);
        Category category1 = new Category();
        for(Category sub: cats)
        {
            if(sub.getId().equals(ss.getCategoryId())){
                category1 = sub;
                break;
            }
        }
        category.setText(category1.getName());

        TextView total_price = convertView.findViewById(R.id.total_price);

        total_price.setText(Double.toString(getPrice(s)));

        TextView planned_price = convertView.findViewById(R.id.planned_price);
        planned_price.setText(Double.toString(s.getPlannedBudget()));

        //button see details
        Button btnDetails = convertView.findViewById(R.id.button_details);
        btnDetails.setOnClickListener(v -> {
            FragmentTransition.to(BudgetingDetailsFragment.newInstance(budget, s, subcats,
                    products, services, packages), (HomeActivity)getContext(), true, R.id.event_fragment);
        });

        return convertView;
    }

    private double getPrice(EventBudgetItem i){
        double sum = 0;
        for(String id: i.getItemsIds()){
            if(id.contains("product"))
            {
                for(Product p: products)
                {
                    if(p.getId().equals(id))
                    {
                        sum += p.getPrice();
                        break;
                    }
                }
            }
            else if(id.contains("service"))
                for(Service p: services)
                {
                    if(p.getId().equals(id))
                    {
                        sum += p.getPrice();
                        break;
                    }
                }
            else
                for(Package p: packages)
                {
                    if(p.getId().equals(id))
                    {
                        sum += p.getPrice();
                        break;
                    }
                }
        }
        return sum;
    }
}
