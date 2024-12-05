package com.example.eventapp.adapters.services;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.R;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.eventOrganizer.EmployeesForPackageReservationFragment;
import com.example.eventapp.fragments.packages.PackageDetailsFragment;
import com.example.eventapp.fragments.reservations.PackageReservationTimeFragment;
import com.example.eventapp.fragments.services.ServiceDetails;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.Service;
import com.example.eventapp.repositories.CategoryRepo;

import java.util.ArrayList;

public class ServicePackageListAdapter extends ArrayAdapter<Service> {
    private PackageDetailsFragment packageDetailsFragment;
    private ArrayList<Service> aServices;
    private boolean isHomePage = false;
    private Event event;
    private String packageId;

    public ServicePackageListAdapter(Context context, ArrayList<Service> services, boolean isHomePage, Event eventToSend, String packageIdToSend, PackageDetailsFragment fragmentToSend){
        super(context, R.layout.service_card, services);
        aServices = services;
        this.isHomePage = isHomePage;
        event = eventToSend;
        packageId = packageIdToSend;
        packageDetailsFragment = fragmentToSend;
    }
    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_card_for_package,
                    parent, false);
        }
        LinearLayout productCard = convertView.findViewById(R.id.product_card_for_package_item);

        TextView productTitle = convertView.findViewById(R.id.productP_title);
        TextView productCategory = convertView.findViewById(R.id.productP_category);
        TextView productPrice = convertView.findViewById(R.id.productP_price);
        Button btnDelete=convertView.findViewById(R.id.btnDelete);
        Button btnReserve=convertView.findViewById(R.id.btnReserve);


        if(service != null){
            productTitle.setText(service.getName());
            CategoryRepo.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
                @Override
                public void onCategoryFetch(ArrayList<Category> categories) {
                    for(Category c:categories)
                        if(c.getId().equals(service.getCategory()))
                            productCategory.setText(c.getName());
                }
            });
            productPrice.setText(Double.toString(service.getPrice())+" din");
            HomeActivity activity = (HomeActivity) getContext();

            productCard.setOnClickListener(v -> {
                if(!isHomePage)
                {
                    if (getContext() instanceof HomeActivity) {
                        FragmentTransition.to(ServiceDetails.newInstance(service),activity ,
                                true, R.id.scroll_package_list);
                    }
                }else{
                    if (getContext() instanceof HomeActivity) {
                        FragmentTransition.to(ServiceDetails.newInstance(service),activity ,
                                true, R.id.home_page_fragment);
                    }
                }


            });

            SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String stringValue = prefs.getString("key_string", "OWNER");

            if(stringValue.equals("EMPLOYEE"))
                btnDelete.setVisibility(View.GONE);

            btnDelete.setOnClickListener(v->{
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setMessage("Are you sure you want to remove this service?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }

                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = dialog.create();
                alert.show();
            });

            btnReserve.setOnClickListener(v->{
                FragmentManager fragmentManager = ((HomeActivity) getContext()).getSupportFragmentManager();
                EmployeesForPackageReservationFragment dialog = EmployeesForPackageReservationFragment.newInstance(service, event, packageId, packageDetailsFragment);
                dialog.show(fragmentManager, "EmployeesForPackageReservationFragment");

            });


        }

        return convertView;
    }
    private void openServiceDetails(Service service) {
        // Otvaranje fragmenta za prikaz detalja o usluzi
        FragmentManager fragmentManager = ((HomeActivity) getContext()).getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.scroll_package_list, ServiceDetails.newInstance(service));
        transaction.addToBackStack(null); // Da omogući povratak nazad
        transaction.commit();
    }

}
