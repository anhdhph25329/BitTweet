package fpt.anhdhph.bittweet.model;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class Order {
    private String id;
    private String userId;
    private String orderDate;
    private String totalPrice;
    private String customerName;
    private String phoneNumber;
    private String address;
    private List<CartItem> items;
    private DocumentReference reference;
    public void setReference(DocumentReference reference) { this.reference = reference; }
    public DocumentReference getReference() { return reference; }
    public Order() {
    }

    public Order(String id, String userId, String orderDate, String totalPrice, String customerName,
                 String phoneNumber, String address, List<CartItem> items) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.items = items;
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

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}