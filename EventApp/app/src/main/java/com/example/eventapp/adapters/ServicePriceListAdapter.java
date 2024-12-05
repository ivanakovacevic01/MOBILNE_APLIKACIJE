package com.example.eventapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.R;
import com.example.eventapp.model.Pricelist;
import com.example.eventapp.model.Service;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ServiceRepo;

import java.util.ArrayList;
import java.util.Date;

public class ServicePriceListAdapter extends ArrayAdapter<Service> {
    private ArrayList<Service> aProducts;

    public ServicePriceListAdapter(Context context, ArrayList<Service> services){
        super(context, R.layout.price_list_products, services);
        aProducts = services;
    }

    @Override
    public int getCount() {
        return aProducts.size();
    }


    @Nullable
    @Override
    public Service getItem(int position) {
        return aProducts.get(position);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.price_list_products,
                    parent, false);
        }
        LinearLayout productCard = convertView.findViewById(R.id.product_price_list);
        TextView productName = convertView.findViewById(R.id.plproductname);
        EditText productPrice = convertView.findViewById(R.id.plproductprice);
        EditText productDiscount = convertView.findViewById(R.id.plproductdiscount);
        TextView productDiscountPrice = convertView.findViewById(R.id.plproductdiscountprice);
        Button edit = convertView.findViewById(R.id.edit);


        if(service != null){
            productName.setText(service.getName());
            productPrice.setText(Double.toString(service.getPrice()));
            productDiscount.setText(Double.toString(service.getDiscount()));
            productDiscountPrice.setText(Double.toString(service.getPrice()*(1-service.getDiscount()/100)));
        }
        edit.setVisibility(View.INVISIBLE);


        productPrice.setOnClickListener(s->{
            edit.setVisibility(View.VISIBLE);
        });
        productDiscount.setOnClickListener(p->{
            edit.setVisibility(View.VISIBLE);
        });

        edit.setOnClickListener(v->{
            Pricelist pricelist=new Pricelist();
            pricelist.setName(service.getName());
            pricelist.setPrice(Double.parseDouble(productPrice.getText().toString()));
            pricelist.setDiscount(Double.parseDouble(productDiscount.getText().toString()));
            Double discountPrice=pricelist.getPrice()*(1- pricelist.getDiscount()/100);
            pricelist.setDiscountPrice(Double.parseDouble(discountPrice.toString()));
            pricelist.setFrom(new Date().toString());
            service.setPrice(Double.parseDouble(productPrice.getText().toString()));
            service.setDiscount(Double.parseDouble(productDiscount.getText().toString()));
            service.setLastChange(new Date().toString());
            if(service.getPriceList().size()>0) {
                Pricelist p = service.getPriceList().get(service.getPriceList().size() - 1);
                p.setTo(new Date().toString());
            }
            ArrayList<Pricelist> pricelists=service.getPriceList();
            pricelists.add(pricelist);
            service.setPriceList(pricelists);
            productDiscountPrice.setText(Double.toString(Math.round(service.getPrice()*(1-service.getDiscount()/100))));

            ServiceRepo.updateService(service);

            edit.setVisibility(View.INVISIBLE);

        });

        return convertView;
    }
}
