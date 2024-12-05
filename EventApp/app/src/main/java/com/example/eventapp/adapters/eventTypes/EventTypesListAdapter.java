package com.example.eventapp.adapters.eventTypes;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.fragments.administration.EditingEventTypeFragment;
import com.example.eventapp.fragments.administration.EventTypeSubcategoriesFragment;
import com.example.eventapp.model.EventType;

import com.example.eventapp.R;
import com.example.eventapp.repositories.EventTypeRepo;

import java.util.ArrayList;


public class EventTypesListAdapter extends ArrayAdapter<EventType> {
    private ArrayList<EventType> aTypes;
    public String firmId = "";
    /*private EventTypeUpdateListener eventTypeUpdateListener;
    public void setEventTypeUpdateListener(EventTypeUpdateListener listener) {
        this.eventTypeUpdateListener = listener;
    }


    public interface EventTypeUpdateListener {
        void onEventTypesUpdated(ArrayList<EventType> eventTypes);
    }*/


    public EventTypesListAdapter(Context context, ArrayList<EventType> types) {
        super(context, R.layout.event_type_cart, types);
        aTypes = types;

    }

    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
    @Override
    public int getCount() {
        return aTypes.size();
    }


    @Nullable
    @Override
    public EventType getItem(int position) {
        return aTypes.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        EventType type = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_type_cart,
                    parent, false);
        }
        LinearLayout eventTypeCart = convertView.findViewById(R.id.event_type_cart_item);

        TextView eventTypeName = convertView.findViewById(R.id.textViewNameEventTpe);
        TextView eventTypeDesctiption = convertView.findViewById(R.id.textViewDescriptionEventType);

        Button activateButton = convertView.findViewById(R.id.buttonActivation);
        Button showSubcategoriesButton = convertView.findViewById(R.id.buttonSuggestedSubcategories);
        ImageView imageViewEdit = convertView.findViewById(R.id.imageViewEditEventType);


        if (type != null) {

            eventTypeName.setText(type.getName());
            eventTypeDesctiption.setText(type.getDescription());

            if (type.getDeactivated())   //moze se aktivirati
                activateButton.setText("Activate");
            else
                activateButton.setText("Deactivate");


           /* if(suggestion.getSubcategoryType().equals(SubcategoryType.PRODUCT))
                item.setText("Product:");
            else
                item.setText("Service: ");*/

            //da se prikaze citav tekst
            eventTypeName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(type.getName())
                            .setTitle("Event Type Name")
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });

            eventTypeDesctiption.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(type.getDescription())
                            .setTitle("Event Type Description")
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
                        EditingEventTypeFragment dialog = EditingEventTypeFragment.newInstance(type);
                        dialog.firmId =firmId;
                        dialog.show(fragmentManager, "EditingEventTypeFragment");
                    }
                }
            });


            showSubcategoriesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getContext() instanceof HomeActivity) {
                        FragmentManager fragmentManager = ((HomeActivity) getContext()).getSupportFragmentManager();


                        EventTypeSubcategoriesFragment dialog = EventTypeSubcategoriesFragment.newInstance((ArrayList<String>)type.getSuggestedSubcategoriesIds());
                        dialog.show(fragmentManager, "EventTypeSubcategoryFragment");
                    }
                }
            });

            activateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    if (type.getDeactivated()) {
                        builder.setMessage("Are you sure you want to activate event type?")
                                .setTitle("Activate event type")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                      
                                        EventTypeRepo.activate(type.getId(), new EventTypeRepo.EventTypeActivationCallback() {
                                            @Override
                                            public void onActivationSuccess() {
                                                activateButton.setText("Deactivate");
                                                type.setDeactivated(false);
                                            }

                                            @Override
                                            public void onActivationFailure(String error) {
                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("NO", null);
                    } else {
                        builder.setMessage("Are you sure you want to deactivate event type?")
                                .setTitle("Deactivate event type")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                       
                                        EventTypeRepo.deactivate(type.getId(), new EventTypeRepo.EventTypeDeactivationCallback() {
                                            @Override
                                            public void onDeactivationSuccess() {
                                                activateButton.setText("Activate");
                                                type.setDeactivated(true);
                                            }

                                            @Override
                                            public void onDeactivationFailure(String error) {
                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("NO", null);
                    }
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });



        }


        return convertView;
    }




}

