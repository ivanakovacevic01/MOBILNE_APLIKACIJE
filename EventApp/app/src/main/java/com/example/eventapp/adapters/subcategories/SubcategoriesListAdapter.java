package com.example.eventapp.adapters.subcategories;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.fragments.administration.EditingSubcategoryFragment;

import com.example.eventapp.R;
import com.example.eventapp.model.Subcategory;
import com.example.eventapp.repositories.EventTypeRepo;
import com.example.eventapp.repositories.ProductRepo;
import com.example.eventapp.repositories.ServiceRepo;
import com.example.eventapp.repositories.SubcategoryRepo;

import java.util.ArrayList;


public class SubcategoriesListAdapter extends ArrayAdapter<Subcategory> {
    private ArrayList<Subcategory> aSubcategories;

    public SubcategoriesListAdapter(Context context, ArrayList<Subcategory> subcategories){
        super(context, R.layout.subcategory_cart, subcategories);
        aSubcategories = subcategories;

    }
    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
    @Override
    public int getCount() {
        return aSubcategories.size();
    }


    @Nullable
    @Override
    public Subcategory getItem(int position) {
        return aSubcategories.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Subcategory subcategory = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.subcategory_cart,
                    parent, false);
        }
        LinearLayout subcategCart = convertView.findViewById(R.id.subcategory_cart_item);

        TextView subcategName = convertView.findViewById(R.id.textViewNameSubcateg);
        TextView subcategDesctiption = convertView.findViewById(R.id.textViewDescriptionSubcateg);
        ImageView imageViewEdit = convertView.findViewById(R.id.imageViewEditSubcateg);
        ImageView imageViewDelete = convertView.findViewById(R.id.imageViewDeleteSubcateg);
        TextView type = convertView.findViewById(R.id.textViewTypeValue);






        if(subcategory != null) {

            subcategName.setText(subcategory.getName());
            subcategDesctiption.setText(subcategory.getDescription());
            type.setText(subcategory.getSubcategoryType().toString());

            //da se prikaze citav tekst
            subcategName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(subcategory.getName())
                            .setTitle("Subcategory Name")
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });

            subcategDesctiption.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(subcategory.getDescription())
                            .setTitle("Subcategory Description")
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
                            .setTitle("Delete subcategory")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ProductRepo.containsSubcategory(subcategory.getId(), new ProductRepo.ProductFetchCallback() {
                                        @Override
                                        public void onResult(boolean containsSubcategoryInProducts) {
                                            if (containsSubcategoryInProducts) {
                                                AlertDialog.Builder cannotDeleteBuilder = new AlertDialog.Builder(getContext());
                                                cannotDeleteBuilder.setMessage("Subcategory is in use in products and cannot be deleted.")
                                                        .setTitle("Cannot Delete Subcategory")
                                                        .setPositiveButton("OK", null);
                                                AlertDialog cannotDeleteDialog = cannotDeleteBuilder.create();
                                                cannotDeleteDialog.show();
                                            } else {
                                                // ako nije u proizvodima, provjerim i u uslugama
                                                ServiceRepo.containsSubcategory(subcategory.getId(), new ServiceRepo.ServiceFetchCallback() {
                                                    @Override
                                                    public void onResult(boolean containsSubcategoryInServices) {
                                                        if (!containsSubcategoryInServices) {
                                                            //provjera da li je ima u event typovima
                                                            EventTypeRepo.containsSubcategory(subcategory.getId(), new EventTypeRepo.OnSubcategoryCheckFetch() {
                                                                @Override
                                                                public void onCheckComplete(boolean exists) {
                                                                    if(!exists) {
                                                                        SubcategoryRepo.deleteSubcategory(subcategory.getId());
                                                                        aSubcategories.remove(subcategory);
                                                                        notifyDataSetChanged();
                                                                    }
                                                                    else  {
                                                                        AlertDialog.Builder cannotDeleteBuilder = new AlertDialog.Builder(getContext());
                                                                        cannotDeleteBuilder.setMessage("Subcategory is in use in event types and cannot be deleted.")
                                                                                .setTitle("Cannot Delete Subcategory")
                                                                                .setPositiveButton("OK", null);
                                                                        AlertDialog cannotDeleteDialog = cannotDeleteBuilder.create();
                                                                        cannotDeleteDialog.show();
                                                                    }
                                                                }
                                                            });



                                                        } else {
                                                            AlertDialog.Builder cannotDeleteBuilder = new AlertDialog.Builder(getContext());
                                                            cannotDeleteBuilder.setMessage("Subcategory is in use in services and cannot be deleted.")
                                                                    .setTitle("Cannot Delete Subcategory")
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
                            })
                            .setNegativeButton("NO", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });

            imageViewEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Provera da li je kontekst adaptera aktivnost
                    if (getContext() instanceof HomeActivity) {
                        FragmentManager fragmentManager = ((HomeActivity) getContext()).getSupportFragmentManager();
                        EditingSubcategoryFragment dialog = EditingSubcategoryFragment.newInstance(subcategory);
                        dialog.show(fragmentManager, "EditingSubcategoryFragment");
                    }
                }
            });
        }











        return convertView;
    }
}

