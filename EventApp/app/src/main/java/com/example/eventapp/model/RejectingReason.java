package com.example.eventapp.model;

public class RejectingReason {
    public String id;
    public String reason;
    public String reportId;
    public RejectingReason(){}

    public RejectingReason(String id, String reason, String reportId) {
        this.id = id;
        this.reason = reason;
        this.reportId = reportId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
}
