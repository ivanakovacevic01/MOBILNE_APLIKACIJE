package com.example.eventapp.adapters;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventapp.R;
import com.example.eventapp.fragments.reports.ReportReviewFragment;
import com.example.eventapp.model.Notification;
import com.example.eventapp.model.NotificationStatus;
import com.example.eventapp.model.Rating;
import com.example.eventapp.model.RatingStatus;
import com.example.eventapp.model.User;
import com.example.eventapp.model.UserType;
import com.example.eventapp.repositories.NotificationRepo;
import com.example.eventapp.repositories.RatingRepo;
import com.example.eventapp.repositories.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    private ArrayList<Rating> commentRatingList;
    private static FragmentActivity context;
    private Rating commentRating;

    public RatingAdapter(ArrayList<Rating> commentRatingList, FragmentActivity c) {
        context = c;
        this.commentRatingList = commentRatingList != null ? commentRatingList : new ArrayList<>();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rating_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rating commentRating = commentRatingList.get(position);
        UserRepo.getUserById(commentRating.getUserId(), new UserRepo.UserFetchCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onUserObjectFetched(User user, String errorMessage) {
                UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                holder.tvUsername.setText(user.getFirstName()+" "+user.getLastName());
            }
        });
        holder.ratingBarItem.setRating(commentRating.getRate());
        holder.tvComment.setText(commentRating.getComment());
        holder.tvDate.setText(formatDateString(commentRating.getCreatedDate()));
        RatingAdapter.ViewHolder viewHolder = (RatingAdapter.ViewHolder) holder;
        viewHolder.bind(viewHolder, commentRating,position);
    }

    @Override
    public int getItemCount() {
        return commentRatingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvComment, tvDate;
        RatingBar ratingBarItem;
        private View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ratingBarItem = itemView.findViewById(R.id.ratingBarItem);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
        public void bind(RatingAdapter.ViewHolder viewHolder,Rating rating, int position){

            Button btnReport = viewHolder.view.findViewById(R.id.btnReportComment);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user=mAuth.getCurrentUser();
            UserRepo.getUserById(user.getUid(), new UserRepo.UserFetchCallback() {
                @Override
                public void onUserObjectFetched(User user, String errorMessage) {
                    UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                    if(rating.status.equals(RatingStatus.REPORTED))
                        btnReport.setVisibility(View.GONE);
                    else if(!user.getType().equals(UserType.OWNER))
                        btnReport.setVisibility(View.GONE);
                }
            });

            btnReport.setOnClickListener(v -> {
                Bundle bundle=new Bundle();
                bundle.putString("ratingId",rating.getId());
                ReportReviewFragment reportReviewFragment=new ReportReviewFragment();
                reportReviewFragment.setArguments(bundle);
                reportReviewFragment.show(context.getSupportFragmentManager(), "ReportReviewFragment");
                context.getSupportFragmentManager().setFragmentResultListener("reportReviewResult", context, (requestKey, result) -> {
                    if (result.getBoolean("submitted")) {
                        rating.setStatus(RatingStatus.REPORTED);
                        RatingRepo.update(rating, new RatingRepo.RatingFetchCallback() {
                            @Override
                            public void onUpdateSuccess() {
                                RatingRepo.RatingFetchCallback.super.onUpdateSuccess();
                                btnReport.setVisibility(View.GONE);
                                notifyItemChanged(position);
                                createNotification();
                            }
                        });
                    }
                });

            });
        }


    }

    private String formatDateString(String dateString) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        try {
            Date date = originalFormat.parse(dateString);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<Rating> filteredComments) {
        this.commentRatingList.clear();
        this.commentRatingList.addAll(filteredComments);
        notifyDataSetChanged();
    }

    private void createNotification(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            UserRepo.getUserById(user.getUid(), new UserRepo.UserFetchCallback() {
                @Override
                public void onUserObjectFetched(User user, String errorMessage) {
                    UserRepo.UserFetchCallback.super.onUserObjectFetched(user, errorMessage);
                    Notification notification=new Notification();
                    notification.setMessage("Reported review by "+user.getFirstName()+" "+user.getLastName()+".");
                    notification.setStatus(NotificationStatus.NEW);
                    notification.setSenderId(user.getId());
                    notification.setReceiverId("");
                    notification.setReceiverRole(UserType.ADMIN);
                    notification.setDate((new Date()).toString());
                    NotificationRepo.create(notification);
                }
            });
        }



    }


}
