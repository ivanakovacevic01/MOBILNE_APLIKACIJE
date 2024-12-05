package com.example.eventapp.fragments.eventOrganizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventapp.R;
import com.example.eventapp.databinding.PriceInputBinding;
import com.example.eventapp.model.EventBudget;
import com.example.eventapp.model.EventBudgetItem;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.repositories.EventBudgetItemRepo;
import com.example.eventapp.repositories.EventBudgetRepo;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class PricePopupDialogFragment extends DialogFragment {

    private CatSubcatPopupDialogFragment f;
    private Subcategory subcategory;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        PriceInputBinding binding = PriceInputBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button exit = root.findViewById(R.id.price_back);
        TextInputEditText price = (TextInputEditText) root.findViewById(R.id.price_input_field);
        exit.setOnClickListener(e -> {
            dismiss();
        });

        Button add = root.findViewById(R.id.price_add);

        add.setOnClickListener(e -> {

            EventBudgetItem item = new EventBudgetItem();
            item.setPlannedBudget(Double.parseDouble(String.valueOf(price.getText())));
            item.setSubcategoryId(subcategory.getId());

            EventBudgetItemRepo repo = new EventBudgetItemRepo();
            EventBudgetRepo budgetRepo = new EventBudgetRepo();
            //BudgetingHomeFragment homeFragment = (BudgetingHomeFragment) getParentFragment().getParentFragmentManager().findFragmentByTag("BUDGET");
            repo.create(new EventBudgetItemRepo.EventBudgetItemFetchCallback() {
                @Override
                public void onEventBudgetItemFetch(ArrayList<EventBudgetItem> budgets) {
                    ArrayList<String> ids = f.eventBudget.getEventBudgetItemsIds();
                    ids.add(budgets.get(0).getId());
                    f.eventBudget.setEventBudgetItemsIds(ids);
                    budgetRepo.update(f.eventBudget);

                    if(f.budgetingHomeFragment != null)
                    {
                        f.budgetingHomeFragment.budgetItems.add(budgets.get(0));
                        f.budgetingHomeFragment.refreshView();
                    }
                }
            }, item);

            Toast.makeText(getActivity(), "Subcategory added to budgeting!", Toast.LENGTH_SHORT).show();

            dismiss();
            f.dismiss();
        });

        return root;
    }

    public static PricePopupDialogFragment newInstance(CatSubcatPopupDialogFragment f, Subcategory subcategory) {
        PricePopupDialogFragment dialog = new PricePopupDialogFragment();
        dialog.f = f;
        dialog.subcategory = subcategory;
        return dialog;
    }

}
