package com.example.eventapp.adapters.packages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.R;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.packages.PackageDetailsFragment;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Package;
import com.example.eventapp.repositories.CategoryRepo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PackageListAdapter extends ArrayAdapter<Package> {

    private ArrayList<Package> aPackages;

    public PackageListAdapter(Context context, ArrayList<Package> packages){
        super(context, R.layout.service_card, packages);
        aPackages = packages;

    }

    @Override
    public int getCount() {
        return aPackages.size();
    }


    @Nullable
    @Override
    public Package getItem(int position) {
        return aPackages.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Package p = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.service_card,
                    parent, false);
        }
        LinearLayout productCard = convertView.findViewById(R.id.service_card_item);
        ImageView imageView = convertView.findViewById(R.id.service_image);
        TextView productTitle = convertView.findViewById(R.id.service_title);
        TextView productDescription = convertView.findViewById(R.id.service_description);
        TextView productCategory = convertView.findViewById(R.id.service_category);
        TextView productPrice = convertView.findViewById(R.id.service_price);
        TextView productAvailable = convertView.findViewById(R.id.service_available);


        if(p != null){
            if (!p.getImageUris().isEmpty()) {
                Picasso.get().load(p.getImageUris().get(0)).into(imageView);
            }              productTitle.setText(p.getName());
            productDescription.setText(p.getDescription());
            CategoryRepo.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
                @Override
                public void onCategoryFetch(ArrayList<Category> categories) {
                    for(Category c:categories)
                        if(c.getId().equals(p.getCategory()))
                            productCategory.setText(c.getName());
                }
            });
            productPrice.setText(Double.toString(p.getPrice())+" din");
            if(p.getAvailable())
                productAvailable.setText("Dostupan");
            else
                productAvailable.setText("Nedostupan");
            productCard.setOnClickListener(v -> {
                if (getContext() instanceof HomeActivity) {
                    HomeActivity activity = (HomeActivity) getContext();
                    FragmentTransition.to(PackageDetailsFragment.newInstance(p),activity ,
                            true, R.id.scroll_package_list);
                }


            });




        }

        return convertView;
    }
}
