package com.example.eventapp.adapters.services;

import android.content.Context;
import android.util.Log;
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
import com.example.eventapp.fragments.employees.EmployeeDetailsFragment;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.services.ServiceDetails;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Service;
import com.example.eventapp.repositories.CategoryRepo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ServiceListAdapter extends ArrayAdapter<Service> {
    private ArrayList<Service> aServices;

    public ServiceListAdapter(Context context, ArrayList<Service> services){
        super(context, R.layout.service_card, services);
        aServices = services;

    }
    @Override
    public int getCount() {
        return aServices.size();
    }


    @Nullable
    @Override
    public Service getItem(int position) {
        return aServices.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Service service = getItem(position);
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

        EmployeeDetailsFragment detailsFragment=new EmployeeDetailsFragment();
        if(service != null){
            if (!service.getImageUris().isEmpty()) {
                Picasso.get().load(service.getImageUris().get(0)).into(imageView);
            }              productTitle.setText(service.getName());
            productDescription.setText(service.getDescription());
            CategoryRepo.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
                @Override
                public void onCategoryFetch(ArrayList<Category> categories) {
                    for(Category c:categories)
                        if(c.getId().equals(service.getCategory()))
                            productCategory.setText(c.getName());
                }
            });            productPrice.setText(Double.toString(service.getPricePerHour())+" din/h");
            if(service.getAvailable())
                productAvailable.setText("Dostupan");
            else
                productAvailable.setText("Nedostupan");
            productCard.setOnClickListener(v -> {
                if (getContext() instanceof HomeActivity) {
                    HomeActivity activity = (HomeActivity) getContext();
                    Log.i("EventApp","aktivno "+activity.IsVisible());
                    if(!activity.IsVisible()) {
                        FragmentTransition.to(ServiceDetails.newInstance(service), activity,
                                true, R.id.scroll_products_list_2);
                    }else{
                        FragmentTransition.to(ServiceDetails.newInstance(service), activity,
                                true, R.id.scroll_employees_list);
                    }

                }


            });




        }

        return convertView;
    }

}

