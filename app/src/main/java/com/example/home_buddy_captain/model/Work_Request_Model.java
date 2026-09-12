package com.example.home_buddy_captain.model;

public class Work_Request_Model {
    private String serviceProviderUID, status, serviceRequested, user_name, user_mobile, user_uid, description;
    public Work_Request_Model() {
    }

    public Work_Request_Model(String serviceProviderUID, String user_name, String serviceRequested, String status, String user_mobile, String user_uid, String description) {
        this.serviceProviderUID = serviceProviderUID;
        this.status = status;
        this.serviceRequested = serviceRequested;
        this.user_name = user_name;
        this.user_mobile = user_mobile;
        this.user_uid = user_uid;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getuser_name() {
        return user_name;
    }

    public void setuser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getserviceProviderUID() {
        return serviceProviderUID;
    }

    public void setserviceProviderUID(String serviceProviderUID) {
        this.serviceProviderUID = serviceProviderUID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getserviceRequested() {
        return serviceRequested;
    }

    public void setserviceRequested(String serviceRequested) {
        this.serviceRequested = serviceRequested;
    }
}
