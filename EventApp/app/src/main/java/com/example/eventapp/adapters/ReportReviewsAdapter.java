package com.example.eventapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.eventapp.R;
import com.example.eventapp.fragments.reports.RejectingReasonFragment;
import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.fragments.FragmentTransition;
import com.example.eventapp.fragments.employees.OwnerProfileFragment;
import com.example.eventapp.model.Owner;
import com.example.eventapp.model.ReportReview;
import com.example.eventapp.model.ReportReviewType;
import com.example.eventapp.model.User;
import com.example.eventapp.repositories.OwnerRepo;
import com.example.eventapp.repositories.RatingRepo;
import com.example.eventapp.repositories.ReportReviewRepo;
import com.example.eventapp.repositories.UserRepo;

import java.util.ArrayList;

public class ReportReviewsAdapter extends ArrayAdapter<ReportReview> {
    private ArrayList<ReportReview> aReports;
    private TextView reporterName;
    private TextView date;
    private TextView reportingReason;
    private TextView status;
    private Button accept;
    private Button reject;
    private Context context;
    private AppCompatActivity mActivity;

    public ReportReviewsAdapter(Context context, ArrayList<ReportReview> reports, AppCompatActivity activity){
        super(context, R.layout.report_review_card, reports);
        this.context=context;
        aReports = reports;
        mActivity=activity;
    }
    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
    @Override
    public int getCount() {
        return aReports.size();
    }


    @Nullable
    @Override
    public ReportReview getItem(int position) {
        return aReports.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }
    private static class ViewHolder {
        TextView reporterName;
        TextView date;
        TextView reportingReason;
        TextView status;
        Button accept;
        Button reject;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ReportReview report = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.report_review_card, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.reporterName = convertView.findViewById(R.id.tvReporter);
            viewHolder.date = convertView.findViewById(R.id.tvReportDate);
            viewHolder.reportingReason = convertView.findViewById(R.id.tvReportReason);
            viewHolder.status = convertView.findViewById(R.id.tvReportStatus);
            viewHolder.accept = convertView.findViewById(R.id.btnAccept);
            viewHolder.reject = convertView.findViewById(R.id.btnReject);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.reportingReason.setText(report.getReason());
        viewHolder.date.setText(report.getDate().substring(0, report.getDate().indexOf("GMT")).trim());
        viewHolder.status.setText(report.getType().toString());

        UserRepo.getUserById(report.getUserId(), new UserRepo.UserFetchCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onUserObjectFetched(User user, String errorMessage) {
                if (user != null) {
                    viewHolder.reporterName.setText(user.getFirstName() + " " + user.getLastName());
                    viewHolder.reporterName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OwnerRepo.get(user.getId(), new OwnerRepo.OwnerFetchCallback() {
                                @Override
                                public void onOwnerObjectFetched(Owner owner, String errorMessage) {
                                    OwnerRepo.OwnerFetchCallback.super.onOwnerObjectFetched(owner, errorMessage);
                                    FragmentTransition.to(OwnerProfileFragment.newInstance(owner),mActivity,true,R.id.scroll_reports);

                                }
                            });

                        }
                    });

                } else {
                    viewHolder.reporterName.setText("Unknown User");
                }
            }
        });
        if(report.getType().equals(ReportReviewType.REJECTED) || report.getType().equals(ReportReviewType.ACCEPTED)){
            viewHolder.accept.setVisibility(View.GONE);
            viewHolder.reject.setVisibility(View.GONE);
        }



        viewHolder.accept.setOnClickListener(v -> {
            HomeActivity activity = (HomeActivity) getContext();
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setMessage("Are you sure you want to accept this report?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            report.setType(ReportReviewType.ACCEPTED);
                            ReportReviewRepo.update(report, new ReportReviewRepo.ReportReviewFetchCallback() {
                                @Override
                                public void onUpdateSuccess() {
                                    ReportReviewRepo.ReportReviewFetchCallback.super.onUpdateSuccess();
                                    viewHolder.status.setText(report.getType().toString());
                                    viewHolder.accept.setVisibility(View.GONE);
                                    viewHolder.reject.setVisibility(View.GONE);
                                    RatingRepo.delete(report.getRatingId());
                                }
                            });

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = dialog.create();
            alert.show();
        });

        viewHolder.reject.setOnClickListener(v -> {
            HomeActivity activity = (HomeActivity) getContext();
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setMessage("Are you sure you want to reject this report?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            report.setType(ReportReviewType.REJECTED);
                            ReportReviewRepo.update(report, new ReportReviewRepo.ReportReviewFetchCallback() {
                                @Override
                                public void onUpdateSuccess() {
                                    ReportReviewRepo.ReportReviewFetchCallback.super.onUpdateSuccess();
                                    RejectingReasonFragment rejectingReasonFragment=new RejectingReasonFragment();
                                    Bundle bundle=new Bundle();
                                    bundle.putString("reportId",report.getId());
                                    rejectingReasonFragment.setArguments(bundle);
                                    ((HomeActivity) getContext()).getSupportFragmentManager().setFragmentResultListener("requestKey", (FragmentActivity) context, (requestKey, result) -> {
                                        viewHolder.status.setText(report.getType().toString());
                                        viewHolder.reject.setVisibility(View.GONE);
                                        viewHolder.accept.setVisibility(View.GONE);
                                    });
                                    rejectingReasonFragment.show(((HomeActivity) getContext()).getSupportFragmentManager(), "RejectingReasonFragment");
                                }
                            });

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = dialog.create();
            alert.show();
        });

        return convertView;
    }

}
