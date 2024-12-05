package com.example.eventapp.adapters;

import android.content.Context;
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
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Pricelist;
import com.example.eventapp.model.Product;
import com.example.eventapp.repositories.PackageRepo;
import com.example.eventapp.repositories.ProductRepo;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PackagePriceListAdapter extends ArrayAdapter<Package> {
    private ArrayList<Package> aProducts;

    public PackagePriceListAdapter(Context context, ArrayList<Package> products){
        super(context, R.layout.package_price_list, products);
        aProducts = products;



    }
    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
    @Override
    public int getCount() {
        return aProducts.size();
    }


    @Nullable
    @Override
    public Package getItem(int position) {
        return aProducts.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Package product = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.package_price_list,
                    parent, false);
        }
        TextView productName = convertView.findViewById(R.id.plproductname);
        TextView productPrice = convertView.findViewById(R.id.plproductprice);
        EditText productDiscount = convertView.findViewById(R.id.plproductdiscount);
        TextView productDiscountPrice = convertView.findViewById(R.id.plproductdiscountprice);
        Button edit = convertView.findViewById(R.id.edit);
        LinearLayout layout=convertView.findViewById(R.id.package_price_list);
        layout.setOnClickListener(i->{
            edit.setVisibility(View.VISIBLE);
        });


        if(product != null){
            productName.setText(product.getName());
            productPrice.setText(Double.toString(product.getPrice()));
            productDiscount.setText(Double.toString(product.getDiscount()));
            productDiscountPrice.setText(Double.toString(product.getPrice()*(1-product.getDiscount()/100)));
        }
        edit.setVisibility(View.INVISIBLE);


        if(product != null){
            productName.setText(product.getName());
            productPrice.setText(Double.toString(product.getPrice()));
            productDiscount.setText(Double.toString(product.getDiscount()));
            productDiscountPrice.setText(Double.toString(Math.round(product.getPrice()*(1-product.getDiscount()/100))));
        }

        productDiscount.setOnClickListener(p->{
            edit.setVisibility(View.VISIBLE);
        });

        edit.setOnClickListener(v->{
            Pricelist pricelist=new Pricelist();
            pricelist.setName(product.getName());
            pricelist.setDiscount(Double.parseDouble(productDiscount.getText().toString()));
            Double discountPrice=pricelist.getPrice()*(1- pricelist.getDiscount()/100);
            pricelist.setDiscountPrice(Double.parseDouble(discountPrice.toString()));
            pricelist.setFrom(new Date().toString());
            product.setDiscount(Double.parseDouble(productDiscount.getText().toString()));
            product.setLastChange(new Date().toString());
            if(product.getPricelist().size()>0) {
                Pricelist p = product.getPricelist().get(product.getPricelist().size() - 1);
                p.setTo(new Date().toString());
            }
            List<Pricelist> pricelists=product.getPricelist();
            pricelists.add(pricelist);
            product.setPricelist(pricelists);
            productDiscountPrice.setText(Double.toString(Math.round(product.getPrice()*(1-product.getDiscount()/100))));

            PackageRepo.updatePackage(product);

            edit.setVisibility(View.INVISIBLE);

        });

        return convertView;
    }
}