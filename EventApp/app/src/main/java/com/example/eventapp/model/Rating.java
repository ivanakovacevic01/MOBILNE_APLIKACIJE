package com.example.eventapp.model;

public class Rating {
    public String id;
    public int rate;
    public String comment;
    public String companyId;
    public String userId; //onaj koji je ocijenio
    public String createdDate; //datum ostavljanja komentara
    public RatingStatus status;
    public Rating(){}

    public Rating(String id, int rate, String comment, String companyId, String userId, String createdDate, RatingStatus status) {
        this.id = id;
        this.rate = rate;
        this.comment = comment;
        this.companyId = companyId;
        this.userId = userId;
        this.createdDate = createdDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public RatingStatus getStatus() {
        return status;
    }

    public void setStatus(RatingStatus status) {
        this.status = status;
    }
}
