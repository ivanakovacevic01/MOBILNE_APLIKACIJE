package com.example.eventapp.adapters.services;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.R;
import com.example.eventapp.adapters.products.ProductListForPackageAdapter;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.services.ServiceDetails;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Service;
import com.example.eventapp.repositories.CategoryRepo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ServiceListForPackageAdapter extends ArrayAdapter<Service> {

    private ArrayList<Service> aServices;
    private ArrayList<Service> services=new ArrayList<Service>();
    private ProductListForPackageAdapter.OnProductSelectionListener productSelectionListener;
    private String packageN;
    private ArrayList<Service> selected;

    private ArrayList<String> shouldBeChecked=new ArrayList<>();

    public ServiceListForPackageAdapter(Context context, ArrayList<Service> services, ArrayList<Service> selectedServices, Package paket, ProductListForPackageAdapter.OnProductSelectionListener listener ){
        super(context, R.layout.product_package_card, services);
        aServices = services;
        productSelectionListener=listener;
        selected=selectedServices;
        shouldBeChecked.clear();
        for(Service s:selectedServices)
            shouldBeChecked.add(s.getId());

        if(paket!=null && selectedServices.size()==0)
            for(String id:paket.getServices())
                shouldBeChecked.add(id);



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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_package_card,
                    parent, false);
        }
        LinearLayout productCard = convertView.findViewById(R.id.product_package_item);

        TextView productTitle = convertView.findViewById(R.id.product_package_title);
        TextView productCategory = convertView.findViewById(R.id.product_pacakge_category);
        TextView productPrice = convertView.findViewById(R.id.product_package_price);
        ImageView productImage = convertView.findViewById(R.id.product_package_image);
        CheckBox checkBox=convertView.findViewById(R.id.checkboxProduct);
        HomeActivity activity = (HomeActivity) getContext();


        if(service != null){
            if (!service.getImageUris().isEmpty()) {
                Picasso.get().load(service.getImageUris().get(0)).into(productImage);
            }              productTitle.setText(service.getName());
            CategoryRepo.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
                @Override
                public void onCategoryFetch(ArrayList<Category> categories) {
                    for(Category c:categories)
                        if(c.getId().equals(service.getCategory()))
                            productCategory.setText(c.getName());
                }
            });
            productPrice.setText(Double.toString(service.getPrice())+" din");

            productCard.setOnClickListener(v -> {
                if (getContext() instanceof HomeActivity) {
                    FragmentTransition.to(ServiceDetails.newInstance(service),activity ,
                            true, R.id.scroll_package_list);
                }


            });


            for(String s:shouldBeChecked){
                if(s.equals(service.getId()))
                    checkBox.setChecked(true);

            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        boolean contain = false;
                        for (String s : shouldBeChecked)
                            if (s.equals(service.getId()))
                                contain = true;
                        if (!contain)
                            shouldBeChecked.add(service.getId());
                    } else {
                        ArrayList<String> varList = new ArrayList<>();
                        for (String s : shouldBeChecked)
                            if (!s.equals(service.getId()))
                                varList.add(s);
                        shouldBeChecked.clear();
                        shouldBeChecked.addAll(varList);

                    }

                    //editor.apply();
                    if (productSelectionListener != null) {
                        ArrayList<Service> services1 = new ArrayList<>();
                        for (String id : shouldBeChecked)
                            for (Service p : aServices)
                                if (p.getId().equals(id))
                                    services1.add(p);
                        productSelectionListener.onServicesSelected(services1);

                    }
                }
            });




        }

        return convertView;
    }

    public void setValueFromEditText(String value) {
        packageN=value;
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
}
