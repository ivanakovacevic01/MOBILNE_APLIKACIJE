package com.example.eventapp.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.packages.PackageDetailsFragment;
import com.example.eventapp.fragments.products.ProductDetails;
import com.example.eventapp.fragments.services.ServiceDetails;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Favourites;
import com.example.eventapp.model.Item;
import com.example.eventapp.model.ItemType;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Service;
import com.example.eventapp.repositories.FavouritesRepo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder>{
    private List<Item> items;
    private FragmentActivity context;
    private ArrayList<Category> categories = new ArrayList<>();
    private Favourites favourites = new Favourites();

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class ViewHolderPackage extends ViewHolder {
        private View view;
        public ViewHolderPackage(View v) {
            super(v);
            this.view = v;
        }

        public void bind(ViewHolderPackage viewHolder, Package s){
            TextView title = viewHolder.view.findViewById(R.id.product_title);
            title.setText(s.getName());
            TextView desc = viewHolder.view.findViewById(R.id.product_description);
            desc.setText(s.getDescription());
            TextView cat = viewHolder.view.findViewById(R.id.product_category);
            //cat.setText(s.getCategory());
            TextView available = viewHolder.view.findViewById(R.id.product_available);
            if(s.getAvailable())
                available.setText("available");
            else
                available.setText("not available");
            TextView price = viewHolder.view.findViewById(R.id.product_price);
            price.setText(Double.toString(s.getPrice()));

            ImageButton heart = viewHolder.view.findViewById(R.id.heart_product);
            heart.setVisibility(View.VISIBLE);

            Drawable fullHeartDrawable = AppCompatResources.getDrawable(heart.getContext(), R.drawable.ic_full_heart);
            Drawable emptyHeartDrawable = AppCompatResources.getDrawable(heart.getContext(), R.drawable.ic_heart);

            if (favourites.getPackages().stream().anyMatch(i -> i.getId().equals(s.getId()))) {
                heart.setImageDrawable(fullHeartDrawable);
            } else {
                heart.setImageDrawable(emptyHeartDrawable);
            }
            heart.setOnClickListener(v3 -> {

                if (favourites.getPackages().stream().anyMatch(i -> i.getId().equals(s.getId()))) {
                    heart.setImageDrawable(emptyHeartDrawable);
                    for(Package p1: favourites.getPackages())
                    {
                        if(p1.getId().equals(s.getId()))
                        {
                            favourites.getPackages().remove(p1);
                            break;
                        }
                    }
                } else {
                    heart.setImageDrawable(fullHeartDrawable);
                    favourites.getPackages().add(s);
                }
                Favourites newFavourites = new Favourites();
                newFavourites.getPackages().add(s);
                newFavourites.setUserEmail(SharedPreferencesManager.getEmail(context));
                FavouritesRepo favouritesRepo = new FavouritesRepo();
                favouritesRepo.getByOrganizer(new FavouritesRepo.FavouriteFetchCallback() {
                    @Override
                    public void onFavouriteFetch(ArrayList<Favourites> favourites) {
                        if(favourites == null || favourites.isEmpty())
                        {
                            Toast.makeText(context, "Added to favourites!", Toast.LENGTH_SHORT).show();
                            FavouritesRepo.create(newFavourites);
                        }
                        else
                        {
                            favourites.get(0).getPackages().add(s);
                            FavouritesRepo.update(favourites.get(0));
                        }
                    }
                }, SharedPreferencesManager.getEmail(context));

            });

            LinearLayout productCard = viewHolder.view.findViewById(R.id.product_card_item);
            productCard.setOnClickListener(v -> {
                PackageDetailsFragment pdf = PackageDetailsFragment.newInstance(s);
                pdf.isHomePage = true;
                FragmentTransition.to(pdf, context,
                        true, R.id.home_page_fragment);
            });
        }

    }
    public class ViewHolderService extends ViewHolder {
        private View view;
        public ViewHolderService(View v) {
            super(v);
            view = v;
        }

        public void bind(ViewHolderService viewHolder, Service s){
           TextView title = viewHolder.view.findViewById(R.id.service_title);
           title.setText(s.getName());
           TextView desc = viewHolder.view.findViewById(R.id.service_description);
           desc.setText(s.getDescription());
           TextView cat = viewHolder.view.findViewById(R.id.service_category);
           //cat.setText(s.getCategory());
           TextView available = viewHolder.view.findViewById(R.id.service_available);
            if(s.getAvailable())
                available.setText("available");
            else
                available.setText("not available");
           TextView price = viewHolder.view.findViewById(R.id.service_price);
           price.setText(Double.toString(s.getPrice()));

            ImageButton heart = viewHolder.view.findViewById(R.id.heart_service);
            heart.setVisibility(View.VISIBLE);

            Drawable fullHeartDrawable = AppCompatResources.getDrawable(heart.getContext(), R.drawable.ic_full_heart);
            Drawable emptyHeartDrawable = AppCompatResources.getDrawable(heart.getContext(), R.drawable.ic_heart);

            if (favourites.getServices().stream().anyMatch(i -> i.getId().equals(s.getId()))) {
                heart.setImageDrawable(fullHeartDrawable);
            } else {
                heart.setImageDrawable(emptyHeartDrawable);
            }
            heart.setOnClickListener(v3 -> {

                if (favourites.getServices().stream().anyMatch(i -> i.getId().equals(s.getId()))) {
                    heart.setImageDrawable(emptyHeartDrawable);
                    for(Service p1: favourites.getServices())
                    {
                        if(p1.getId().equals(s.getId()))
                        {
                            favourites.getServices().remove(p1);
                            break;
                        }
                    }
                } else {
                    heart.setImageDrawable(fullHeartDrawable);
                    favourites.getServices().add(s);
                }

                    Favourites newFavourites = new Favourites();
                    newFavourites.getServices().add(s);
                    newFavourites.setUserEmail(SharedPreferencesManager.getEmail(context));
                    FavouritesRepo favouritesRepo = new FavouritesRepo();
                    favouritesRepo.getByOrganizer(new FavouritesRepo.FavouriteFetchCallback() {
                        @Override
                        public void onFavouriteFetch(ArrayList<Favourites> favourites) {
                            if(favourites == null || favourites.isEmpty())
                            {
                                FavouritesRepo.create(newFavourites);
                                Toast.makeText(context, "Added to favourites!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                favourites.get(0).getServices().add(s);
                                FavouritesRepo.update(favourites.get(0));
                            }
                        }
                    }, SharedPreferencesManager.getEmail(context));

                });

            LinearLayout productCard = viewHolder.view.findViewById(R.id.service_card_item);
            productCard.setOnClickListener(v -> {
                ServiceDetails sf = ServiceDetails.newInstance(s);
                sf.isHomePage = true;
                FragmentTransition.to(sf, context,
                        true, R.id.home_page_fragment);
            });
        }

    }

    public class ViewHolderProduct extends ViewHolder {

        private View view;
        public ViewHolderProduct(View v) {
            super(v);
            view = v;
        }
        public void bind(ViewHolderProduct viewHolder, Product s){
            TextView title = viewHolder.view.findViewById(R.id.product_title);
            title.setText(s.getName());
            TextView desc = viewHolder.view.findViewById(R.id.product_description);
            desc.setText(s.getDescription());
            TextView cat = viewHolder.view.findViewById(R.id.product_category);
            //cat.setText(s.getCategory());
            TextView available = viewHolder.view.findViewById(R.id.product_available);
            if(s.getAvailable())
                available.setText("available");
            else
                available.setText("not available");
            TextView price = viewHolder.view.findViewById(R.id.product_price);
            price.setText(Double.toString(s.getPrice()));

            ImageButton heart = viewHolder.view.findViewById(R.id.heart_product);
            heart.setVisibility(View.VISIBLE);

            Drawable fullHeartDrawable = AppCompatResources.getDrawable(heart.getContext(), R.drawable.ic_full_heart);
            Drawable emptyHeartDrawable = AppCompatResources.getDrawable(heart.getContext(), R.drawable.ic_heart);

            if (favourites.getProducts().stream().anyMatch(i -> i.getId().equals(s.getId()))) {
                heart.setImageDrawable(fullHeartDrawable);
            } else {
                heart.setImageDrawable(emptyHeartDrawable);
            }
            heart.setOnClickListener(v3 -> {

                if (favourites.getProducts().stream().anyMatch(i -> i.getId().equals(s.getId()))) {
                    heart.setImageDrawable(emptyHeartDrawable);
                    for(Product p1: favourites.getProducts())
                    {
                        if(p1.getId().equals(s.getId()))
                        {
                            favourites.getProducts().remove(p1);
                            break;
                        }
                    }
                } else {
                    heart.setImageDrawable(fullHeartDrawable);
                    favourites.getProducts().add(s);
                }
                Favourites newFavourites = new Favourites();
                newFavourites.getProducts().add(s);
                newFavourites.setUserEmail(SharedPreferencesManager.getEmail(context));
                FavouritesRepo favouritesRepo = new FavouritesRepo();
                favouritesRepo.getByOrganizer(new FavouritesRepo.FavouriteFetchCallback() {
                    @Override
                    public void onFavouriteFetch(ArrayList<Favourites> favourites) {
                        if(favourites == null || favourites.isEmpty())
                        {
                            Toast.makeText(context, "Added to favourites!", Toast.LENGTH_SHORT).show();
                            FavouritesRepo.create(newFavourites);
                        }
                        else
                        {
                            favourites.get(0).getProducts().add(s);
                            FavouritesRepo.update(favourites.get(0));
                        }
                    }
                }, SharedPreferencesManager.getEmail(context));
            });
            LinearLayout productCard = viewHolder.view.findViewById(R.id.product_card_item);
            productCard.setOnClickListener(v -> {
                ProductDetails pf = ProductDetails.newInstance(s);
                pf.isHomePage = true;
                FragmentTransition.to(pf, context,
                        true, R.id.home_page_fragment);
            });
        }

    }

    public ItemListAdapter(List<Package> packages, List<Service> services, List<Product> products, FragmentActivity c, Favourites f) {
        super();
        items = new ArrayList<Item>();
        items.addAll(createPackages(packages));
        items.addAll(createServices(services));
        items.addAll(createProducts(products));
        favourites = f;
        this.context = c;
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item

        ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case 0: //TODO packages
                View v1 = inflater.inflate(R.layout.product_cart, viewGroup, false);
                viewHolder = new ViewHolderPackage(v1);
                break;
            case 1:
                View v2 = inflater.inflate(R.layout.service_card, viewGroup, false);
                viewHolder = new ViewHolderService(v2);
                break;
            default:
                View v3 = inflater.inflate(R.layout.product_cart, viewGroup, false);
                viewHolder = new ViewHolderProduct(v3);
                break;
        }

        return viewHolder;
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        if(items.get(position).getType() == ItemType.Package)
            return 0;
        if(items.get(position).getType() == ItemType.Service)
            return 1;
        if(items.get(position).getType() == ItemType.Product)
            return 2;
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Item item = items.get(position);

        if (item.getType().equals(ItemType.Package)) {
            ViewHolderPackage packageViewHolder = (ViewHolderPackage) viewHolder;
            Package packageItem = item.getaPackage();

            packageViewHolder.bind(packageViewHolder, packageItem);
        } else if (item.getType().equals(ItemType.Service)) {
            ViewHolderService serviceViewHolder = (ViewHolderService) viewHolder;
            Service serviceItem = item.getService();

            serviceViewHolder.bind(serviceViewHolder, serviceItem);
        } else if (item.getType().equals(ItemType.Product)) {
            ViewHolderProduct productViewHolder = (ViewHolderProduct) viewHolder;

            Product productItem = item.getProduct();
            productViewHolder.bind(productViewHolder, productItem);
        }
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    private List<Item> createPackages(List<Package> packages){
        List<Item> items = new ArrayList<Item>();
        for(Package p: packages){
            items.add(new Item(p));
        }
        return items;
    }

    private List<Item> createServices(List<Service> services){
        List<Item> items = new ArrayList<Item>();
        for(Service p: services){
            items.add(new Item(p));
        }
        return items;
    }

    private List<Item> createProducts(List<Product> products){
        List<Item> items = new ArrayList<Item>();
        for(Product p: products){
            items.add(new Item(p));
        }
        return items;
    }
}
