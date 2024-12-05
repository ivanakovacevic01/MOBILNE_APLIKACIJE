package com.example.eventapp.model;

public class ReportReview {
    public String id;
    public String userId; //ko je prijavio
    public String reason;
    public String date; //datum prijave
    public ReportReviewType type;
    public String ratingId;
    public ReportReview(){}

    public ReportReview(String id,String userId, String reason, String date, ReportReviewType type,String ratingId) {
        this.id=id;
        this.userId = userId;
        this.reason = reason;
        this.date = date;
        this.type=type;
        this.ratingId = ratingId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public ReportReviewType getType() {
        return type;
    }

    public void setType(ReportReviewType type) {
        this.type = type;
    }

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }
}
