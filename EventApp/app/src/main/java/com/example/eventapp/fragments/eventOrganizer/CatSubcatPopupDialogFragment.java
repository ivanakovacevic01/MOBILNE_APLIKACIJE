package com.example.eventapp.fragments.eventOrganizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventapp.R;
import com.example.eventapp.adapters.eventOrganizer.SuggestedCategoriesAdapter;
import com.example.eventapp.adapters.eventOrganizer.SuggestedSubcategoriesAdapter;
import com.example.eventapp.databinding.SubcatListDialogBinding;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.DialogType;
import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.EventBudgetItem;
import com.example.eventapp.model.EventType;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.EventBudgetItemRepo;
import com.example.eventapp.repositories.SubcategoryRepo;

import java.util.ArrayList;
import java.util.List;

public class CatSubcatPopupDialogFragment extends DialogFragment {

    private boolean readOnly;
    private DialogType type;
    private ArrayList<Subcategory> subcategories = new ArrayList<>();
    private ArrayList<Category> categories = new ArrayList<>();
    public EventBudget eventBudget;
    private EventType eventType;
    public BudgetingHomeFragment budgetingHomeFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        SubcatListDialogBinding binding = SubcatListDialogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if(type.equals(DialogType.SubCategories)) {
            createSubcategories(root);
        }
        else if(type.equals(DialogType.Caterogies)) {
            suggestCategories(root);
        } else if(type.equals(DialogType.CategoriesForFirm))
        {
            displayCategories(root);
        }
        else{
            SuggestedCategoriesAdapter adapter = new SuggestedCategoriesAdapter(getActivity(), createCategories(), this.readOnly);

            ListView view = root.findViewById(R.id.list_subcat);
            view.setAdapter(adapter);
        }


        Button exit = root.findViewById(R.id.button_back_subcat);
        exit.setOnClickListener(e -> {
            dismiss();
        });

        Button apply = root.findViewById(R.id.button_add_subcat);
        ListView view = root.findViewById(R.id.list_subcat);
        final Subcategory[] subcategory = {new Subcategory()};

        if(!this.readOnly)
        {
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    subcategory[0] = subcategories.get(position);
                }
            });
        }

        if(this.readOnly){
            apply.setVisibility(View.INVISIBLE);
            view.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        }else{

            apply.setOnClickListener(e -> {
                PricePopupDialogFragment pricePopupDialogFragment = PricePopupDialogFragment.newInstance(this, subcategory[0]);
                pricePopupDialogFragment.show(getChildFragmentManager(), "tag");
            });
        }
        return root;
    }

    public static CatSubcatPopupDialogFragment newInstance(boolean readOnly, DialogType type, EventBudget eventBudget, EventType eventType, BudgetingHomeFragment budgetFragment, ArrayList<Category> firmCategories) {
        CatSubcatPopupDialogFragment f = new CatSubcatPopupDialogFragment();
        f.readOnly = readOnly;
        f.type = type;
        f.eventBudget = eventBudget;
        f.eventType = eventType;
        f.budgetingHomeFragment = budgetFragment;
        if(firmCategories != null)
            f.categories = firmCategories;
        return f;
    }

    private ArrayList<Category> createCategories() {
        ArrayList<Category> categories = new ArrayList<>();

        categories.add(new Category("1", "Ugostiteljski objekti, hrana, ketering, torte i kolači",
                "Pružanje usluga pripreme i posluživanja hrane i pića za događaje, uključujući koktele, svečane večere, švedske stolove, tematske obroke i drugo. Lokacije koje nude mogućnost organizacije događaja unutar njihovih prostora, često s kompletnim ugostiteljskim uslugama. Ovo može uključivati restorane sa privatnim sobama ili sale za bankete specijalizovane za veće događaje. Usluge poput degustacija vina, koktel barmena, mobilnih barova, tematskih hrana i pića koje dodaju poseban dodir događajima. Specijalizovane pekare i poslastičarnice koje nude dizajnirane torte, slatke stolove i druge slatkiše prilagođene temi događaja."));
        categories.add(new Category("2", "Smeštaj",
                "Hoteli, vile, apartmani i druge opcije smeštaja za goste događaja."));
        categories.add(new Category("3", "Muzika i zabava",
                "DJ-evi, bendovi, solo izvođači, animatori za decu, plesači."));
        categories.add(new Category("4", "Foto i video",
                "Profesionalno fotografisanje i snimanje događaja."));
        categories.add(new Category("5", "Dekoracija i rasveta",
                "Ova kategorija može uključivati ne samo tematsku dekoraciju prostora, cvetne aranžmane, balone, stolnjake, itd. već i specijalističko osvetljenje koje može transformisati prostor događaja, uključujući scensko osvetljenje, LED rasvetu, laserske efekte i druge."));
        categories.add(new Category("6", "Garderoba i stilizovanje",
                "Iznajmljivanje ili kupovina formalne odeće, usluge stilista."));
        categories.add(new Category("7", "Nega i lepota",
                "Frizerske usluge, makeup artisti, manikir/pedikir."));
        categories.add(new Category("8", "Planiranje i koordinacija",
                "Agencije ili pojedinci specijalizovani za planiranje i koordinaciju događaja."));
        categories.add(new Category("9", "Pozivnice i papirna galanterija",
                "Dizajn i štampa pozivnica, menija, rasporeda sedenja."));

        return categories;
    }

    public void createSubcategories(View root) {
        SubcategoryRepo repo = new SubcategoryRepo();

        if(!this.readOnly)
        {
            EventBudgetItemRepo itemRepo = new EventBudgetItemRepo();
            repo.getAllSubcategories(new SubcategoryRepo.SubcategoryFetchCallback() {
                @Override
                public void onSubcategoryFetch(ArrayList<Subcategory> p) {
                    subcategories.clear();
                    itemRepo.getByBudget(new EventBudgetItemRepo.EventBudgetItemFetchCallback() {
                        @Override
                        public void onEventBudgetItemFetch(ArrayList<EventBudgetItem> i) {
                            for(Subcategory s: p)
                            {
                                if(i.stream().noneMatch(item -> item.getSubcategoryId().equals(s.getId())))
                                {
                                    subcategories.add(s);
                                }
                            }
                            SuggestedSubcategoriesAdapter adapter = new SuggestedSubcategoriesAdapter(getActivity(), subcategories, readOnly);
                            ListView view = root.findViewById(R.id.list_subcat);
                            view.setAdapter(adapter);
                        }
                    }, eventBudget);
                }
            });
        }else{
            SubcategoryRepo subcategoryRepo = new SubcategoryRepo();
                    subcategoryRepo.getSubcategoriesByIds( new ArrayList<>(eventType.getSuggestedSubcategoriesIds()), new SubcategoryRepo.SubcategoryFetchCallback() {
                        @Override
                        public void onSubcategoryFetch(ArrayList<Subcategory> s) {
                            SubcategoryRepo.SubcategoryFetchCallback.super.onSubcategoryFetch(s);
                            subcategories = s;
                            SuggestedSubcategoriesAdapter adapter = new SuggestedSubcategoriesAdapter(getActivity(), subcategories, readOnly);
                            ListView view = root.findViewById(R.id.list_subcat);
                            view.setAdapter(adapter);
                        }
                    });
        }

    }

    private void suggestCategories(View root)
    {
        CategoryRepo repo = new CategoryRepo();
        SubcategoryRepo subcategoryRepo = new SubcategoryRepo();
        subcategoryRepo.getSubcategoriesByIds( new ArrayList<>(eventType.getSuggestedSubcategoriesIds()), new SubcategoryRepo.SubcategoryFetchCallback() {
            @Override
            public void onSubcategoryFetch(ArrayList<Subcategory> subs) {
                    ArrayList<String> ids = new ArrayList<>();

                    for(Subcategory sub: subs)
                    {
                        ids.add(sub.getCategoryId());
                    }
                    repo.getByIds( new CategoryRepo.CategoryFetchCallback() {
                        @Override
                        public void onCategoryFetch(ArrayList<Category> s) {
                            categories = s;
                            SuggestedCategoriesAdapter adapter = new SuggestedCategoriesAdapter(getActivity(), categories, readOnly);
                            ListView view = root.findViewById(R.id.list_subcat);
                            view.setAdapter(adapter);
                        }
                    }, ids);

            }
        });

    }

    private void displayCategories(View root)
    {
        SuggestedCategoriesAdapter adapter = new SuggestedCategoriesAdapter(getActivity(), categories, readOnly);
        ListView view = root.findViewById(R.id.list_subcat);
        view.setAdapter(adapter);
    }
}
