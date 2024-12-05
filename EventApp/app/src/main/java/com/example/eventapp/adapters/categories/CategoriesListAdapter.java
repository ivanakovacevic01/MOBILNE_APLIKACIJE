package com.example.eventapp.adapters.categories;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.fragments.administration.EditingCategoryFragment;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.administration.SubcategoriesListFragment;
import com.example.eventapp.model.Category;

import com.example.eventapp.R;
import com.example.eventapp.repositories.CategoryRepo;
import com.example.eventapp.repositories.FirmRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.SubcategoryRepo;
import com.example.eventapp.repositories.SubcategorySuggestionsRepo;

import java.util.ArrayList;


public class CategoriesListAdapter extends ArrayAdapter<Category> {
    private ArrayList<Category> aCategories;

    public CategoriesListAdapter(Context context, ArrayList<Category> categories){
        super(context, R.layout.category_cart, categories);
        aCategories = categories;
    }
    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
    @Override
    public int getCount() {
        return aCategories.size();
    }


    @Nullable
    @Override
    public Category getItem(int position) {
        return aCategories.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Category category = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_cart,
                    parent, false);
        }
        LinearLayout categCart = convertView.findViewById(R.id.category_cart_item);

        TextView categName = convertView.findViewById(R.id.textViewName);
        TextView categDesctiption = convertView.findViewById(R.id.textViewDescription);
        ImageView imageViewEdit = convertView.findViewById(R.id.imageViewEdit);
        ImageView imageViewDelete = convertView.findViewById(R.id.imageViewDelete);
        Button subcategoriesButton = convertView.findViewById(R.id.buttonManageSubcategories);





        if(category != null) {

            categName.setText(category.getName());
            categDesctiption.setText(category.getDescription());

            //da se prikaze citav tekst
            categName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(category.getName())
                            .setTitle("Category Name")
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });

            categDesctiption.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(category.getDescription())
                            .setTitle("Category Description")
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });

            imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure?")
                            .setTitle("Delete category")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                /*public void onClick(DialogInterface dialog, int id) {
                                    CategoryRepo.deleteCategory(category.getId());  //tu brisem i njene potkategorije i potkateg brisem i iz tipova dogadjaja
                                    aCategories.remove(category);


                                    notifyDataSetChanged();
                                }*/
                                public void onClick(DialogInterface dialog, int id) {
                                    //provjerim da li ima potkategorije, ako ima, ne brisem je
                                    SubcategoryRepo.existsForCategory(category.getId(), new SubcategoryRepo.CategoryFetchCallback() {
                                        @Override
                                        public void onCheckComplete(boolean exists) {
                                            if(!exists) {
                                                SubcategorySuggestionsRepo.existsForCategory(category.getId(), new SubcategorySuggestionsRepo.CategoryFetchCallback() {
                                                @Override
                                                    public void onCheckComplete(boolean exists) {
                                                    if(!exists) {
                                                        //provjera da li postoji u firmi
                                                        FirmRepo.checkIfCategoryExistsInFirms(category.getId(), new FirmRepo.CategoryExistenceCallback() {
                                                            @Override
                                                            public void onExistence(boolean exists) {
                                                                if(!exists) {
                                                                    ProductRepo.containsCategory(category.getId(), new ProductRepo.ProductFetchCallback() {
                                                                        @Override
                                                                        public void onResult(boolean containsCategoryInProducts) {
                                                                            if (containsCategoryInProducts) {
                                                                                AlertDialog.Builder cannotDeleteBuilder = new AlertDialog.Builder(getContext());
                                                                                cannotDeleteBuilder.setMessage("Category is in use in products and cannot be deleted.")
                                                                                        .setTitle("Cannot Delete Category")
                                                                                        .setPositiveButton("OK", null);
                                                                                AlertDialog cannotDeleteDialog = cannotDeleteBuilder.create();
                                                                                cannotDeleteDialog.show();
                                                                            } else {
                                                                                // ako nije u proizvodima, provjerim i u uslugama
                                                                                ServiceRepo.containsCategory(category.getId(), new ServiceRepo.ServiceFetchCallback() {
                                                                                    @Override
                                                                                    public void onResult(boolean containsCategoryInServices) {
                                                                                        if (!containsCategoryInServices) {
                                                                                            CategoryRepo.deleteCategory(category.getId());  //tu brisem i njene potkategorije
                                                                                            aCategories.remove(category);
                                                                                            notifyDataSetChanged();
                                                                                        } else {
                                                                                            AlertDialog.Builder cannotDeleteBuilder = new AlertDialog.Builder(getContext());
                                                                                            cannotDeleteBuilder.setMessage("Category is in use in services and cannot be deleted.")
                                                                                                    .setTitle("Cannot Delete Category")
                                                                                                    .setPositiveButton("OK", null);
                                                                                            AlertDialog cannotDeleteDialog = cannotDeleteBuilder.create();
                                                                                            cannotDeleteDialog.show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                else {
                                                                    AlertDialog.Builder cannotDeleteBuilder = new AlertDialog.Builder(getContext());
                                                                    cannotDeleteBuilder.setMessage("Category is in use in firms and cannot be deleted.")
                                                                            .setTitle("Cannot Delete Category")
                                                                            .setPositiveButton("OK", null);
                                                                    AlertDialog cannotDeleteDialog = cannotDeleteBuilder.create();
                                                                    cannotDeleteDialog.show();
                                                                }
                                                            }
                                                        });

                                                    }
                                                    else {
                                                        AlertDialog.Builder cannotDeleteBuilder = new AlertDialog.Builder(getContext());
                                                        cannotDeleteBuilder.setMessage("Category is in use in subcategory suggestions and cannot be deleted.")
                                                                .setTitle("Cannot Delete Category")
                                                                .setPositiveButton("OK", null);
                                                        AlertDialog cannotDeleteDialog = cannotDeleteBuilder.create();
                                                        cannotDeleteDialog.show();
                                                    }
                                                }
                                                });

                                            }
                                            else {
                                                AlertDialog.Builder cannotDeleteBuilder = new AlertDialog.Builder(getContext());
                                                cannotDeleteBuilder.setMessage("Category has subcategories and cannot be deleted.")
                                                        .setTitle("Cannot Delete Category")
                                                        .setPositiveButton("OK", null);
                                                AlertDialog cannotDeleteDialog = cannotDeleteBuilder.create();
                                                cannotDeleteDialog.show();
                                            }
                                        }
                                    });


                                }
                            })
                            .setNegativeButton("NO", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });

            imageViewEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getContext() instanceof HomeActivity) {
                        FragmentManager fragmentManager = ((HomeActivity) getContext()).getSupportFragmentManager();
                        EditingCategoryFragment dialog = EditingCategoryFragment.newInstance(category);
                        dialog.show(fragmentManager, "EditingCategoryFragment");
                    }
                }
            });


            subcategoriesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getContext() instanceof HomeActivity) {
                        HomeActivity activity = (HomeActivity) getContext();
                        FragmentTransition.to(SubcategoriesListFragment.newInstance(category.getId()),activity ,
                                true, R.id.scroll_categories_list);
                    }
                }
            });
        }












            return convertView;
    }
}

