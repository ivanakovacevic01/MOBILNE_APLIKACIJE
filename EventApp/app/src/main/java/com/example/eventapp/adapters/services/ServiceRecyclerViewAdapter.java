package com.example.eventapp.adapters.services;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.R;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.services.ServiceDetails;
import com.example.eventapp.model.Service;

import java.util.ArrayList;

public class ServiceRecyclerViewAdapter extends RecyclerView.Adapter<ServiceRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Service> services;

    public ServiceRecyclerViewAdapter(Context context, ArrayList<Service> services) {
        this.services = services;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service service = services.get(position);
        if (service != null) {
            //holder.imageView.setImageResource(service.getImages().get(0));
            holder.productTitle.setText(service.getName());
            holder.productDescription.setText(service.getDescription());
            holder.productCategory.setText(service.getCategory());
            holder.productPrice.setText(Double.toString(service.getPricePerHour()) + " din/h");
            holder.productAvailable.setText(service.getAvailable() ? "Dostupan" : "Nedostupan");
            holder.productCard.setOnClickListener(v -> {
                Context context = holder.productCard.getContext();
                if (context instanceof HomeActivity) {
                    HomeActivity activity = (HomeActivity) context;
                    if (!activity.IsVisible()) {
                        FragmentTransition.to(ServiceDetails.newInstance(service), activity, true, R.id.scroll_products_list_2);
                    } else {
                        FragmentTransition.to(ServiceDetails.newInstance(service), activity, true, R.id.scroll_employees_list);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout productCard;
        ImageView imageView;
        TextView productTitle;
        TextView productDescription;
        TextView productCategory;
        TextView productPrice;
        TextView productAvailable;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productCard = itemView.findViewById(R.id.service_card_item);
            imageView = itemView.findViewById(R.id.service_image);
            productTitle = itemView.findViewById(R.id.service_title);
            productDescription = itemView.findViewById(R.id.service_description);
            productCategory = itemView.findViewById(R.id.service_category);
            productPrice = itemView.findViewById(R.id.service_price);
            productAvailable = itemView.findViewById(R.id.service_available);
        }
    }
}
