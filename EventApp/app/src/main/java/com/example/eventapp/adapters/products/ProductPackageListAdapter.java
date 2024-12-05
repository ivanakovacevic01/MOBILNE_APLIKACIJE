package com.example.eventapp.adapters.products;

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

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.R;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.products.ProductDetails;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Product;
import com.example.eventapp.repositories.CategoryRepo;

import java.util.ArrayList;

public class ProductPackageListAdapter extends ArrayAdapter<Product> {
    private ArrayList<Product> aProducts;
    private boolean isHomePage = false;
    public ProductPackageListAdapter(Context context, ArrayList<Product> products, boolean isHomePage){
        super(context, R.layout.product_cart, products);
        aProducts = products;
        this.isHomePage = isHomePage;
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
    public Product getItem(int position) {
        return aProducts.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Product product = getItem(position);
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

        if(product != null){
            productTitle.setText(product.getName());
            CategoryRepo.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
                @Override
                public void onCategoryFetch(ArrayList<Category> categories) {
                    for(Category c:categories)
                        if(c.getId().equals(product.getCategory()))
                            productCategory.setText(c.getName());
                }
            });            productPrice.setText(Double.toString(product.getPrice())+" din");
            HomeActivity activity = (HomeActivity) getContext();

            productCard.setOnClickListener(v -> {
                if(!isHomePage)
                {
                    if (getContext() instanceof HomeActivity) {
                        FragmentTransition.to(ProductDetails.newInstance(product),activity ,
                                true, R.id.scroll_package_list);
                    }
                }else{
                    if (getContext() instanceof HomeActivity) {
                        FragmentTransition.to(ProductDetails.newInstance(product),activity ,
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
                dialog.setMessage("Are you sure you want to remove this product?")
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

            });




        }

        return convertView;
    }

}
