package fpt.anhdhph.bittweet.model;

import com.google.firebase.firestore.DocumentReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {
    private String id;
    private String userId;
    private String orderDate;
    private String totalPrice;
    private String customerName;
    private String phoneNumber;
    private String address;
    private String status;
    private List<CartItem> items;
    private DocumentReference reference;

    public Order() {
        this.items = new ArrayList<>();
        this.status = "Chờ xác nhận";
    }

    public Order(String userId, String orderDate, String totalPrice, String customerName,
                 String phoneNumber, String address, String status, List<CartItem> items) {
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.status = status;
        this.items = items != null ? items : new ArrayList<>();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public DocumentReference getReference() {
        return reference;
    }

    public void setReference(DocumentReference reference) {
        this.reference = reference;
    }

    // Chuyển đổi Order thành Map để lưu vào Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("orderDate", orderDate);
        map.put("totalPrice", totalPrice);
        map.put("customerName", customerName);
        map.put("phoneNumber", phoneNumber);
        map.put("address", address);
        map.put("status", status);
        List<Map<String, Object>> itemsList = new ArrayList<>();
        for (CartItem item : items) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("productId", item.getProductId());
            itemMap.put("name", item.getProName());
            itemMap.put("size", item.getSize());
            itemMap.put("price", item.getPrice());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("image", item.getImage());
            itemMap.put("category", item.getCategory());
            itemsList.add(itemMap);
        }
        map.put("items", itemsList);
        return map;
    }
}