package com.example.eventapp.adapters.eventOrganizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.R;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.packages.PackageDetailsFragment;
import com.example.eventapp.fragments.products.ProductDetails;
import com.example.eventapp.fragments.services.ServiceDetails;
import com.example.eventapp.model.Item;
import com.example.eventapp.model.ItemType;

import java.util.ArrayList;

public class BudgetItemsAdapter extends ArrayAdapter<Item> {

    private ArrayList<Item> items;

    public BudgetItemsAdapter(Context context, ArrayList<Item> items){
        super(context, R.layout.budget_item, items);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }


    @Nullable
    @Override
    public Item getItem(int position) {
        return items.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Item s = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.budget_item,
                    parent, false);
        }

        TextView name = convertView.findViewById(R.id.item_name);
        TextView type = convertView.findViewById(R.id.item_type);
        TextView price = convertView.findViewById(R.id.price_id);
        if(s.getType().equals(ItemType.Product)){
            name.setText(s.getProduct().getName());
            type.setText("Product");
            price.setText(Double.toString(s.getProduct().getPrice()));
            for(Item i: items)
            {
                if(i.getType().equals(ItemType.Package) && (i.getaPackage().getProducts()).contains(s.getProduct().getId()))
                {
                    price.setText(Double.toString(s.getProduct().getPrice()*(i.getaPackage().getDiscount()/100)));
                    break;
                }
            }
        }else if(s.getType().equals(ItemType.Service)){
            name.setText(s.getService().getName());
            type.setText("Service");
            price.setText(Double.toString(s.getService().getPrice()));
            for(Item i: items)
            {
                if(i.getType().equals(ItemType.Package) && (i.getaPackage().getServices()).contains(s.getService().getId()))
                {
                    price.setText(Double.toString(s.getService().getPrice()*(i.getaPackage().getDiscount()/100)));
                    break;
                }
            }
        }else{
            name.setText(s.getaPackage().getName());
            type.setText("Package");
        }
        LinearLayout item = convertView.findViewById(R.id.budget_item);
        item.setOnClickListener(v -> {
            if (getContext() instanceof HomeActivity) {
                HomeActivity activity = (HomeActivity) getContext();

                if (s.getType().equals(ItemType.Package)) {
                    FragmentTransition.to(PackageDetailsFragment.newInstance(s.getaPackage()), activity,
                            true, R.id.event_fragment);
                } else if (s.getType().equals(ItemType.Service)) {
                    FragmentTransition.to(ServiceDetails.newInstance(s.getService()), activity,
                            true, R.id.event_fragment);
                } else {
                   // Product product1 = new Product("1L", "Product 1", "Description of Product 1", 0, 50.0, 0.0, true, true, new ArrayList<>(), "Category1", "Subcategory1");
                    FragmentTransition.to(ProductDetails.newInstance(s.getProduct()), activity,
                            true, R.id.event_fragment);
                }
            }
        });


        return convertView;
    }
}
