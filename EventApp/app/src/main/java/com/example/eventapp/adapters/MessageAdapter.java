package com.example.eventapp.adapters;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.eventapp.R;
import com.example.eventapp.SharedPreferencesManager;
import com.example.eventapp.fragments.ChatFragment;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.model.Message;
import com.example.eventapp.model.MessageStatus;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.MessageRepo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private List<Message> messages = new ArrayList<>();
    private FragmentActivity context;
    private User sender = new User();
    private User recipient = new User();

    public MessageAdapter(FragmentActivity c, ArrayList<Message> u, User s, User r){
        super(c, R.layout.message_holder_other, u);
        this.messages = u;
        this.context = c;
        if(SharedPreferencesManager.getEmail(getContext()).equals(s.getEmail()))
        {
            this.sender = s;
            this.recipient = r;
        }else{
            this.sender = r;
            this.recipient = s;
        }

    }

    @Override
    public int getCount() {
        return messages.size();
    }


    @Nullable
    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Message e = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_holder_other,
                    parent, false);
        }

        if(e.getStatus().equals(MessageStatus.NOT_OPENED) && e.getRecipientId().equals(sender.getId()))
        {
            e.setStatus(MessageStatus.OPENED);
            MessageRepo.update(e);
        }

        View l = convertView.findViewById(R.id.l_mess);
        TextView text = l.findViewById(R.id.message_text);
        text.setText(e.getText());

        TextView time = l.findViewById(R.id.message_time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        time.setText(dateFormat.format(e.getDate()) + "  " + e.getTime());

        ImageView seen = l.findViewById(R.id.seen);
        if(e.getStatus().equals(MessageStatus.NOT_OPENED))
            seen.setVisibility(View.INVISIBLE);
        LinearLayout linearLayout = convertView.findViewById(R.id.lin_layout);
        if(e.getSenderId().equals(sender.getId()))
        {
            // Get the layout params of the LinearLayout
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();

            // Set the alignment to align the LinearLayout to the right side of the parent RelativeLayout
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END); // Align to the end (right side) of the parent

            // Apply the updated layout params to the LinearLayout
            linearLayout.setLayoutParams(layoutParams);
        }else{
            // Get the layout params of the LinearLayout
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();

            // Set the alignment to align the LinearLayout to the right side of the parent RelativeLayout
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START); // Align to the end (right side) of the parent

            // Apply the updated layout params to the LinearLayout
            linearLayout.setLayoutParams(layoutParams);
        }

        return convertView;
    }
}
