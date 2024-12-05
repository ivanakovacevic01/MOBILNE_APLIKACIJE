package com.example.eventapp.fragments.eventOrganizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventapp.R;
import com.example.eventapp.adapters.eventOrganizer.BudgetItemsAdapter;
import com.example.eventapp.databinding.BudgetDetailsBinding;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.OverviewFragment;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.EventBudgetItem;
import com.example.eventapp.model.Item;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.SubcategoryType;
import com.example.eventapp.repositories.EventBudgetItemRepo;
import com.example.eventapp.repositories.EventBudgetRepo;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class BudgetingDetailsFragment extends Fragment {
    private BudgetDetailsBinding binding;
    private ArrayList<Subcategory> subcategories = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Service> services = new ArrayList<>();
    private ArrayList<Package> packages = new ArrayList<>();
    private EventBudgetItem item;
    private EventBudget budget;

    public static BudgetingDetailsFragment newInstance(EventBudget budget, EventBudgetItem item, ArrayList<Subcategory> subs,
                                                       ArrayList<Product> p, ArrayList<Service> s, ArrayList<Package> pa){
        BudgetingDetailsFragment fragment = new BudgetingDetailsFragment();
        fragment.subcategories = subs;
        fragment.budget = budget;
        fragment.item = item;
        fragment.products = p;
        fragment.services = s;
        fragment.packages = pa;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        binding = BudgetDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //data
        ArrayList<Item> items = createPackages(getPackage());
        items.addAll(createProducts(getProducts()));
        items.addAll(createServices(getService()));

        ListView listView = root.findViewById(R.id.budget_items);
        BudgetItemsAdapter adapter = new BudgetItemsAdapter(getActivity(), items);
        listView.setAdapter(adapter);

        TextView title = root.findViewById(R.id.details_title);
        for(Subcategory sub: subcategories)
        {
            if(sub.getId().equals(item.getSubcategoryId()))
                title.setText(sub.getName());
        }

        TextView total_price = (TextView)root.findViewById(R.id.total_price_details);
        total_price.setText(Double.toString(getTotalBudget()));

        TextInputEditText planned_price =(TextInputEditText)root.findViewById(R.id.planned_price_details);
        planned_price.setText(Double.toString(item.getPlannedBudget()));
        planned_price.addTextChangedListener(textWatcher);


        //button delete
        Button btnDelete = root.findViewById(R.id.delete_subcat);
        if(item.getItemsIds() != null && !item.getItemsIds().isEmpty())
            btnDelete.setVisibility(View.INVISIBLE);
        btnDelete.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to delete this subcategory from budget?")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteItem();
                            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.event_fragment, BudgetingHomeFragment.newInstance(budget.getEventId())).addToBackStack("BUDGET");
                            fragmentTransaction.commit();
                            Toast.makeText(getActivity(), "Successfully deleted subcategory!", Toast.LENGTH_SHORT).show();
                        }})
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }}).show();
        });

        //button add
        Button btnBudget = root.findViewById(R.id.button_add_item);
        btnBudget.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.event_fragment, OverviewFragment.newInstance()).addToBackStack(null);
            fragmentTransaction.commit();
        });

        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public Subcategory createSubcategory() {

        // Data for Potkategorija 1
        String id = "1"; // You can assign appropriate IDs
        String name = "Ketering i priprema hrane";
        String description = "Kompletna ketering usluga (hrana i piće), specijalizovani ketering (veganski, bez glutena), mobilni barovi i koktel usluge (gin-tonik bar, whiski bar), profesionalne usluge posluživanja.";
        String categoryId = "1"; // Assuming the category ID
        SubcategoryType subcategoryType = SubcategoryType.SERVICE; // Assuming SubcategoryType is an enum and SERVICE is one of its values

        // Create the Subcategory object and add it to the list
        return new Subcategory(id, name, description, categoryId, subcategoryType);
    }

    private ArrayList<Item> createPackages(List<Package> packages){
        ArrayList<Item> items = new ArrayList<Item>();
        for(Package p: packages){
            items.add(new Item(p));
        }
        return items;
    }

    private ArrayList<Item> createServices(List<Service> services){
        ArrayList<Item> items = new ArrayList<Item>();
        for(Service p: services){
            items.add(new Item(p));
        }
        return items;
    }

    private ArrayList<Item> createProducts(List<Product> products){
        ArrayList<Item> items = new ArrayList<Item>();
        for(Product p: products){
            items.add(new Item(p));
        }
        return items;
    }

    private List<Package> getPackage(){
       ArrayList<Package> p = new ArrayList<>();
        for(String id: item.getItemsIds()) {
            if (id.contains("package")) {
                for(Package pack: packages)
                {
                    if(pack.getId().equals(id))
                    {
                        p.add(pack);
                        break;
                    }
                }
            }
        }
        return p;
    }
    private List<Service> getService(){
        ArrayList<Service> p = new ArrayList<>();
        for(String id: item.getItemsIds())
        {
            if(id.contains("service"))
            {
                for(Service product: services)
                {
                    if(id.equals(product.getId()))
                    {
                        p.add(product);
                        break;
                    }
                }
            }
            else if(id.contains("package"))
            {
                for(Package pack: packages)
                {
                    for(String packProdId: pack.getServices())
                    {
                        for(Service product: services)
                        {
                            if(packProdId.equals(product.getId()))
                            {
                                p.add(product);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return p;
    }

    private List<Product> getProducts(){
        ArrayList<Product> p = new ArrayList<>();
        for(String id: item.getItemsIds())
        {
            if(id.contains("product"))
            {
                for(Product product: products)
                {
                    if(id.equals(product.getId()))
                    {
                        p.add(product);
                        break;
                    }
                }
            }
            else if(id.contains("package"))
            {
                for(Package pack: packages)
                {
                    for(String packProdId: pack.getProducts())
                    {
                        for(Product product: products)
                        {
                            if(packProdId.equals(product.getId()))
                            {
                                p.add(product);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return p;
    }

    private double getTotalBudget(){
        double sum = 0;

        for(String id: item.getItemsIds())
        {
            if(id.contains("product"))
                for(Product p: products)
                {
                    if(p.getId().equals(id))
                    {
                        sum += p.getPrice();
                        break;
                    }
                }
            else if(id.contains("service"))
                for(Service p: services)
                {
                    if(p.getId().equals(id))
                    {
                        sum += p.getPrice();
                        break;
                    }
                }
            else
                for(Package p: packages)
                {
                    if(p.getId().equals(id))
                    {
                        sum += p.getPrice();
                        break;
                    }
                }
        }
        return sum;
    }

    private void deleteItem(){
        EventBudgetItemRepo repo = new EventBudgetItemRepo();
        repo.delete(item.getId());

        EventBudgetRepo budgetRepo = new EventBudgetRepo();
        ArrayList<String> ids = budget.getEventBudgetItemsIds();
        ids.remove(item.getId());
        budget.setEventBudgetItemsIds(ids);
        budgetRepo.update(budget);
    }

    // textWatcher is for watching any changes in editText
    TextWatcher textWatcher = new TextWatcher() {
        EventBudgetItemRepo repo = new EventBudgetItemRepo();
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // this function is called before text is edited
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // this function is called when text is edited
        }

        @Override
        public void afterTextChanged(Editable s) {
            // this function is called after text is edited
            if(!String.valueOf(((TextInputEditText)binding.getRoot().findViewById(R.id.planned_price_details)).getText()).isEmpty()){
                item.setPlannedBudget(Double.parseDouble(String.valueOf(((TextInputEditText)binding.getRoot().findViewById(R.id.planned_price_details)).getText())));
                repo.update(item);
            }
        }
    };
}
