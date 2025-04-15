package fpt.anhdhph.bittweet.model;

public class OrderDetail {
    private String idOrders;
    private String idProducts;
    private String proName;
    private String image;
    private String size;
    private String price;
    private int quantity;
    private String userId;

    public OrderDetail(String idOrders, String idProducts, String proName, String image, String size, String price, int quantity, String userId) {
        this.idOrders = idOrders;
        this.idProducts = idProducts;
        this.proName = proName;
        this.image = image;
        this.size = size;
        this.price = price;
        this.quantity = quantity;
        this.userId = userId;
    }

    public String getIdOrders() {
        return idOrders;
    }

    public void setIdOrders(String idOrders) {
        this.idOrders = idOrders;
    }

    public String getIdProducts() {
        return idProducts;
    }

    public void setIdProducts(String idProducts) {
        this.idProducts = idProducts;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
