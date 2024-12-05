package com.example.eventapp.fragments.eventOrganizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.eventapp.R;
import com.example.eventapp.adapters.eventOrganizer.BudgetSubcategoriesAdapter;
import com.example.eventapp.databinding.BudgetingHomeBinding;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.DialogType;
import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.EventBudgetItem;
import com.example.eventapp.model.Package;
import com.example.eventapp.model.Product;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EventBudgetItemRepo;
import com.example.eventapp.repositories.EventBudgetRepo;
import com.example.eventapp.repositories.PackageRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BudgetingHomeFragment extends Fragment {
    private BudgetingHomeBinding binding;
    private String eventId;
    private EventBudget budget;
    public ArrayList<EventBudgetItem> budgetItems = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Service> services = new ArrayList<>();
    private ArrayList<Package> packages = new ArrayList<>();
    private ArrayList<Subcategory> subcategories = new ArrayList<>();
    private ArrayList<Category> categories = new ArrayList<>();

    public static BudgetingHomeFragment newInstance(String eventId){
        BudgetingHomeFragment fragment = new BudgetingHomeFragment();
        fragment.eventId = eventId;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        binding = BudgetingHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EventBudgetRepo repo = new EventBudgetRepo();
        EventBudgetItemRepo itemRepo = new EventBudgetItemRepo();
        this.getCategories(root);
        this.getSubcategories(root);
        repo.getByEventId(new EventBudgetRepo.EventBudgetFetchCallback() {
            @Override
            public void onEventBudgetFetch(ArrayList<EventBudget> budgets) {
            }

            @Override
            public void onEventBudgetFetchByEvent(EventBudget b) {
                budget = b;
                itemRepo.getByBudget(new EventBudgetItemRepo.EventBudgetItemFetchCallback() {
                    @Override
                    public void onEventBudgetItemFetch(ArrayList<EventBudgetItem> budgets) {
                        budgetItems = budgets;
                        getBudgetItemsFromDatabase(root);
                    }
                }, budget);
            }
        }, eventId);


        TextView plannedPrice = (TextView) root.findViewById(R.id.planned_budget);
        plannedPrice.setText(Double.toString(this.getPlannedBudget()));

        FloatingActionButton btnNewPackage = (FloatingActionButton) root.findViewById(R.id.btnNewItem);
        btnNewPackage.setOnClickListener(v -> {
            CatSubcatPopupDialogFragment dialogFragment = CatSubcatPopupDialogFragment.newInstance(false, DialogType.SubCategories, budget, null, this, null);
            dialogFragment.show(getChildFragmentManager(), "tag");

            getChildFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                    super.onFragmentViewDestroyed(fm, f);
                    refreshView();
                    getChildFragmentManager().unregisterFragmentLifecycleCallbacks(this);
                }
            }, false);
        });

        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getBudgetItemsFromDatabase(View root)
    {
        ListView listView = root.findViewById(R.id.budget_items);
        EventBudgetItemRepo repo = new EventBudgetItemRepo();
        repo.getByBudget(new EventBudgetItemRepo.EventBudgetItemFetchCallback() {
            @Override
            public void onEventBudgetItemFetch(ArrayList<EventBudgetItem> budgets) {
                getBudgetItems();
                BudgetSubcategoriesAdapter adapter = new BudgetSubcategoriesAdapter(getActivity(), budgetItems, subcategories, categories,
                        products, services, packages, budget);
                listView.setAdapter(adapter);
                refreshView();
            }
        }, budget);

    }
    public void getBudgetItems() {
        ProductRepo productRepo = new ProductRepo();
        ServiceRepo serviceRepo = new ServiceRepo();
        PackageRepo packageRepo = new PackageRepo();
        productRepo.getAllProducts(new ProductRepo.ProductFetchCallback() {
            @Override
            public void onProductFetch(ArrayList<Product> p) {
                products = p;
                refreshView();
            }
        });

        serviceRepo.getAllServices(new ServiceRepo.ServiceFetchCallback() {
            @Override
            public void onServiceFetch(ArrayList<Service> p) {
                services = p;
                refreshView();
            }
        });

        packageRepo.getAllPackages(new PackageRepo.PackageFetchCallback() {
            @Override
            public void onPackageFetch(ArrayList<Package> p) {
                packages = p;
                refreshView();
            }
        });


    }

    private double getTotalBudget(){
        double sum = 0;
        for(EventBudgetItem item: budgetItems)
        {
            for(String id: item.getItemsIds())
            {
                if(id.contains("product"))
                {
                    for(Product p: products)
                    {
                        if(p.getId().equals(id))
                        {
                            sum += p.getPrice();
                            break;
                        }
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
        }
        return sum;
    }

    private double getPlannedBudget(){
        double sum = 0;
        for(EventBudgetItem item: budgetItems)
        {
            sum += item.getPlannedBudget();
        }
        return sum;
    }

    private void getSubcategories(View root){
        SubcategoryRepo repo = new SubcategoryRepo();
        ListView listView = root.findViewById(R.id.budget_items);
        repo.getAllSubcategories(new SubcategoryRepo.SubcategoryFetchCallback() {
            @Override
            public void onSubcategoryFetch(ArrayList<Subcategory> s) {
                subcategories = s;
                BudgetSubcategoriesAdapter adapter = new BudgetSubcategoriesAdapter(getActivity(), budgetItems, subcategories, categories,
                        products, services, packages, budget);
                listView.setAdapter(adapter);
            }
        });
    }

    private void getCategories(View root){
        CategoryRepo repo = new CategoryRepo();
        ListView listView = root.findViewById(R.id.budget_items);
        repo.getAllCategories(new CategoryRepo.CategoryFetchCallback() {
            @Override
            public void onCategoryFetch(ArrayList<Category> s) {
                categories = s;
                BudgetSubcategoriesAdapter adapter = new BudgetSubcategoriesAdapter(getActivity(), budgetItems, subcategories, categories,
                        products, services, packages, budget);
                listView.setAdapter(adapter);
            }
        });
    }

    public void refreshView(){
        View root = binding.getRoot();
        ListView listView = root.findViewById(R.id.budget_items);
        BudgetSubcategoriesAdapter adapter = new BudgetSubcategoriesAdapter(getActivity(), budgetItems, subcategories, categories,
                products, services, packages, budget);
        listView.setAdapter(adapter);

        TextView totalPrice = (TextView) root.findViewById(R.id.total_budget);
        totalPrice.setText(Double.toString(this.getTotalBudget()));
        TextView plannedPrice = (TextView) root.findViewById(R.id.planned_budget);
        plannedPrice.setText(Double.toString(this.getPlannedBudget()));
    }
}
