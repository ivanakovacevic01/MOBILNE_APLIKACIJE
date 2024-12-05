package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Report implements Parcelable {
    private String id;
    private String reporterEmail;
    private String reportedEmail;
    private String reason;
    private String date;
    private ReportingStatus status;
    private String reportedType;
    public Report(){}

    public Report(String id, String reporterEmail, String reportedEmail, String reason, String date, ReportingStatus status, String reportedType) {
        this.id = id;
        this.reporterEmail = reporterEmail;
        this.reportedEmail = reportedEmail;
        this.reason = reason;
        this.date = date;
        this.status = status;
        this.reportedType=reportedType;
    }

    public String getReportedType() {
        return reportedType;
    }

    public void setReportedType(String reportedType) {
        this.reportedType = reportedType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterId) {
        this.reporterEmail = reporterId;
    }

    public String getReportedEmail() {
        return reportedEmail;
    }

    public void setReportedEmail(String reportedId) {
        this.reportedEmail = reportedId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ReportingStatus getStatus() {
        return status;
    }

    public void setStatus(ReportingStatus status) {
        this.status = status;
    }
    protected Report(Parcel in) {
        id = in.readString();
        reportedEmail = in.readString();
        reporterEmail = in.readString();
        date=in.readString();
        reason=in.readString();
        status=ReportingStatus.valueOf(in.readString());
        reportedType=in.readString();
    }

    public static final Creator<Report> CREATOR = new Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        @Override
        public Report[] newArray(int size) {
            return new Report[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(reason);
        dest.writeString(reportedEmail);
        dest.writeString(reporterEmail);
        dest.writeString(date);
        dest.writeString(status.name());
        dest.writeString(reportedType);

    }
}

