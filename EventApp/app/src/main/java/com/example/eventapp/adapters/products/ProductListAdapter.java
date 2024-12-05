package com.example.eventapp.adapters.products;

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
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.products.ProductDetails;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Product;

import com.example.eventapp.R;
import com.example.eventapp.repositories.CategoryRepo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProductListAdapter extends ArrayAdapter<Product> {
    private ArrayList<Product> aProducts;

    private boolean isHomePage = false;
    public ProductListAdapter(Context context, ArrayList<Product> products, boolean isHomePage){
        super(context, R.layout.product_cart, products);
        aProducts = products;
        this.isHomePage = isHomePage;

        Log.i("PRODUCT", "a"+ aProducts.size());


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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_cart,
                    parent, false);
        }
        LinearLayout productCard = convertView.findViewById(R.id.product_card_item);
        ImageView imageView = convertView.findViewById(R.id.product_image);
        TextView productTitle = convertView.findViewById(R.id.product_title);
        TextView productDescription = convertView.findViewById(R.id.product_description);
        TextView productCategory = convertView.findViewById(R.id.product_category);
        TextView productPrice = convertView.findViewById(R.id.product_price);
        TextView productAvailable = convertView.findViewById(R.id.product_available);


        if(product != null){
            if (!product.getImageUris().isEmpty()) {
                Picasso.get().load(product.getImageUris().get(0)).into(imageView);
            }
            productTitle.setText(product.getName());
            productDescription.setText(product.getDescription());

            CategoryRepo.getAllCategories( new CategoryRepo.CategoryFetchCallback() {
                @Override
                public void onCategoryFetch(ArrayList<Category> categories) {
                    for(Category c:categories)
                        if(c.getId().equals(product.getCategory()))
                            productCategory.setText(c.getName());
                }
            });


            productPrice.setText(Double.toString(product.getPrice())+" din");
            if(product.getAvailable())
                productAvailable.setText("Dostupan");
            else
                productAvailable.setText("Nedostupan");
            productCard.setOnClickListener(v -> {
                if (getContext() instanceof HomeActivity) {
                    HomeActivity activity = (HomeActivity) getContext();
                    if(!isHomePage)
                    {
                        FragmentTransition.to(ProductDetails.newInstance(product),activity ,
                                true, R.id.scroll_products_list);
                    }else{
                        FragmentTransition.to(ProductDetails.newInstance(product),activity ,
                                true, R.id.home_page_fragment);
                    }
                }
            });




        }

        return convertView;
    }
}

