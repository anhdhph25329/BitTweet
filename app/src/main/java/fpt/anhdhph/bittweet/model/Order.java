package fpt.anhdhph.bittweet.model;

import com.google.firebase.Timestamp;

public class Order {
    private String idUsers;
    private String status;
    private Timestamp timestamp;

    public Order(String idUsers, String status, Timestamp timestamp) {
        this.idUsers = idUsers;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getIdUsers() {
        return idUsers;
    }

    public void setIdUsers(String idUsers) {
        this.idUsers = idUsers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
