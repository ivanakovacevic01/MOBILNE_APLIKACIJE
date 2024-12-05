package com.example.eventapp.adapters.subcategories;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.fragments.administration.EditingSuggestionFragment;
import com.example.eventapp.fragments.administration.ExistingSubcategoriesFragment;
import com.example.eventapp.model.Category;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.Product;

import com.example.eventapp.R;
import com.example.eventapp.model.Service;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.model.SubcategorySuggestion;
import com.example.eventapp.model.SubcategoryType;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.example.eventapp.repositories.SubcategorySuggestionsRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;


public class SubcategorySuggestionListAdapter extends ArrayAdapter<SubcategorySuggestion> {
    private ArrayList<SubcategorySuggestion> aSuggestions;
    private Subcategory newSubcat;

    public SubcategorySuggestionListAdapter(Context context, ArrayList<SubcategorySuggestion> suggestions){
        super(context, R.layout.suggestion_cart, suggestions);
        aSuggestions = suggestions;

    }
    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
    @Override
    public int getCount() {
        return aSuggestions.size();
    }


    @Nullable
    @Override
    public SubcategorySuggestion getItem(int position) {
        return aSuggestions.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SubcategorySuggestion suggestion = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.suggestion_cart,
                    parent, false);
        }
        LinearLayout suggestionCart = convertView.findViewById(R.id.suggestion_cart_item);

        TextView suggestionName = convertView.findViewById(R.id.textViewNameSubcategSuggestion);
        TextView suggestionDesctiption = convertView.findViewById(R.id.textViewDescriptionSuggestion);
        TextView categoryName = convertView.findViewById(R.id.textViewCategSuggestionValue);
        TextView itemName = convertView.findViewById(R.id.textViewItemNameValue);
        TextView item = convertView.findViewById(R.id.textViewItemName);
        Button addExistingSubcategory = convertView.findViewById(R.id.buttonAddExistingSubcategory);
        ImageView imageViewEdit = convertView.findViewById(R.id.imageViewEditSuggestion);
        ImageView imageViewAccept = convertView.findViewById(R.id.imageViewAcceptSuggestion);


        if(suggestion != null) {

            CategoryRepo.getCategoryById(suggestion.getCategory(), new CategoryRepo.CategoryFetchCallback() {
                @Override
                public void onCategoryByIdFetch(Category category) {
                    if (category != null) {
                        Log.d("REZ_DB", "Category name: " + category.getName());
                        categoryName.setText(category.getName());
                    } else {
                        Log.d("REZ_DB", "Category not found or error occurred");
                    }
                }
            });
            suggestionName.setText(suggestion.getName());
            suggestionDesctiption.setText(suggestion.getDescription());


            //prikaz servisa/proizvoda
            if(suggestion.getSubcategoryType().equals(SubcategoryType.PRODUCT)) {
                item.setText("Product:");
                itemName.setText(suggestion.getProduct().getName());
            }

            else {
                item.setText("Service: ");
                itemName.setText(suggestion.getService().getName());
            }



            //da se prikaze citav tekst
            suggestionName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(suggestion.getName())
                            .setTitle("Suggestion Name")
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });

            suggestionDesctiption.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(suggestion.getDescription())
                            .setTitle("Suggestion Description")
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });
            categoryName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(suggestion.getCategory())
                            .setTitle("Suggestion Category")
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });

            itemName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Suggestion")
                            .setTitle("New Suggestion")
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });



            imageViewEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getContext() instanceof HomeActivity) {
                        FragmentManager fragmentManager = ((HomeActivity) getContext()).getSupportFragmentManager();
                        EditingSuggestionFragment dialog = EditingSuggestionFragment.newInstance(suggestion);
                        dialog.show(fragmentManager, "EditingSuggestionFragment");
                    }
                }
            });

            imageViewAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure you want to accept suggestion?")
                            .setTitle("Accept suggestion")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    SubcategorySuggestionsRepo.approve(suggestion.getId(), new SubcategorySuggestionsRepo.SubcategorySuggestionUpdateCallback() {
                                        @Override
                                        public void onUpdateSuccess() {
                                            aSuggestions.remove(suggestion);
                                            createNewSubcategoryObject(suggestion);
                                            SubcategoryRepo.createSubcategory(newSubcat, new SubcategoryRepo.SubcategoryFetchCallback() {
                                                @Override
                                                public void onSubcategoryFetched(Subcategory subcategory, String errorMessage) {
                                                    if (subcategory != null) {

                                                        //kreiram uslugu/proizvod
                                                        if(suggestion.getSubcategoryType().toString().equals(SubcategoryType.PRODUCT.toString())) {
                                                            Product toCreate = suggestion.getProduct();
                                                            toCreate.setSubcategory(subcategory.getId());

                                                            Notification newNotification = new Notification();
                                                            newNotification.setMessage("1 new subcategory suggestion accepted, product created - " + subcategory.getName());
                                                            newNotification.setDate(new Date().toString());
                                                            newNotification.setReceiverRole(UserType.OWNER);
                                                            newNotification.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                            newNotification.setReceiverId(suggestion.getUserId());
                                                            NotificationRepo.create(newNotification);
                                                            ProductRepo.createProduct(toCreate);
                                                        }

                                                        else {
                                                            Service toCreate = suggestion.getService();
                                                            toCreate.setSubcategory(subcategory.getId());
                                                            Notification newNotification = new Notification();
                                                            newNotification.setMessage("1 new subcategory suggestion accepted, service created - " + subcategory.getName());
                                                            newNotification.setReceiverRole(UserType.OWNER);
                                                            newNotification.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                            newNotification.setReceiverId(suggestion.getUserId());
                                                            newNotification.setDate(new Date().toString());
                                                            NotificationRepo.create(newNotification);
                                                            ServiceRepo.createService(toCreate);
                                                        }

                                                        notifyDataSetChanged();
                                                        Toast.makeText(getContext(), "Successfully approved.", Toast.LENGTH_SHORT).show();


                                                    } else {
                                                        Log.w("REZ_DB", "Error creating subcategory: " + errorMessage);
                                                    }
                                                }
                                            });
                                           }

                                        @Override
                                        public void onUpdateFailure(String error) {
                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("NO", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            addExistingSubcategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getContext() instanceof HomeActivity) {
                        FragmentManager fragmentManager = ((HomeActivity) getContext()).getSupportFragmentManager();
                        ExistingSubcategoriesFragment dialog = ExistingSubcategoriesFragment.newInstance(suggestion);
                        dialog.show(fragmentManager, "AddExistingSubcategoryFragment");
                    }
                }
            });
        }

        return convertView;
    }

    private void createNewSubcategoryObject(SubcategorySuggestion suggestion) {
        newSubcat = new Subcategory();
        newSubcat.setName(suggestion.getName().toString());
        newSubcat.setDescription(suggestion.getDescription().toString());
        newSubcat.setCategoryId(suggestion.getCategory());
        newSubcat.setSubcategoryType(suggestion.getSubcategoryType());
    }


}

