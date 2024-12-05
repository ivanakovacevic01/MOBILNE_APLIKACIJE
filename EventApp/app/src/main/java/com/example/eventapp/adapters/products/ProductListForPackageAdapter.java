package com.example.eventapp.adapters.products;

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
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.products.ProductDetails;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Service;
import com.example.eventapp.repositories.CategoryRepo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductListForPackageAdapter extends ArrayAdapter<Product> {
    private ArrayList<Product> aProducts;
    private ArrayList<Product> products=new ArrayList<Product>();
    private OnProductSelectionListener productSelectionListener;
    private String packageN;
    private ArrayList<Product> selected;
    private ArrayList<String> shouldBeChecked=new ArrayList<>();
    private boolean isHomePage = false;

    public ProductListForPackageAdapter(Context context, ArrayList<Product> products, ArrayList<Product> selectedProducts, Package paket, OnProductSelectionListener listener, boolean isHomePage){
        super(context, R.layout.product_package_card, products);
        aProducts = products;
        productSelectionListener = listener;
        selected=selectedProducts;
        shouldBeChecked.clear();
        for(Product p:selectedProducts)
            shouldBeChecked.add(p.getId());
        if(paket!=null && selectedProducts.size()==0) {
            for (String p : paket.getProducts()) {
                shouldBeChecked.add(p);
            }
        }
        this.isHomePage =  isHomePage;

    }
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_package_card,
                    parent, false);
        }
        LinearLayout productCard = convertView.findViewById(R.id.product_package_item);
        TextView productTitle = convertView.findViewById(R.id.product_package_title);
        TextView productCategory = convertView.findViewById(R.id.product_pacakge_category);
        TextView productPrice = convertView.findViewById(R.id.product_package_price);
        ImageView productImage=convertView.findViewById(R.id.product_package_image);

        if(product != null){
            if (!product.getImageUris().isEmpty()) {
                Picasso.get().load(product.getImageUris().get(0)).into(productImage);
            }
            productTitle.setText(product.getName());
            CategoryRepo.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
                @Override
                public void onCategoryFetch(ArrayList<Category> categories) {
                    for(Category c:categories)
                        if(c.getId().equals(product.getCategory()))
                            productCategory.setText(c.getName());
                }
            });
            productPrice.setText(Double.toString(product.getPrice())+" din");
            HomeActivity activity = (HomeActivity) getContext();
            CheckBox checkBox=convertView.findViewById(R.id.checkboxProduct);

            for(String s:shouldBeChecked){
                if(s.equals(product.getId()))
                    checkBox.setChecked(true);
            }
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

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        boolean contain=false;
                        for(String s:shouldBeChecked)
                            if(s.equals(product.getId()))
                                contain=true;
                        if(!contain)
                            shouldBeChecked.add(product.getId());
                    }
                    else {
                        ArrayList<String> varList=new ArrayList<>();
                        for(String s:shouldBeChecked)
                            if(!s.equals(product.getId()))
                                varList.add(s);
                        shouldBeChecked.clear();
                        shouldBeChecked.addAll(varList);

                    }

                    if (productSelectionListener != null) {
                       ArrayList<Product> products1=new ArrayList<>();
                       for(String id:shouldBeChecked)
                           for(Product p:aProducts)
                                if(p.getId().equals(id))
                                    products1.add(p);
                        productSelectionListener.onProductsSelected(products1);
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

    public interface OnProductSelectionListener {
        void onProductsSelected(ArrayList<Product> selectedProducts);
        void onServicesSelected(ArrayList<Service> selectedServices);

    }
}
