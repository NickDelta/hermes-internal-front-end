package org.hua.hermes.backend.entity;

public enum ApplicationState {
    SUBMITTED("Submitted"),
    APPROVED("Approved"),
    COMPLETED("Completed"),
    CANCELED("Canceled"),
    REJECTED("Rejected"),
    RESUBMISSION_REQUIRED("Resubmission Required");

    String name;

    ApplicationState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
