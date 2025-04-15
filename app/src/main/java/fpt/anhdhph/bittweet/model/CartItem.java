package fpt.anhdhph.bittweet.model;

import android.util.Log;

public class CartItem {
    private String idOrders;
    private String idProducts;
    private String name;
    private String size;
    private String price; // Đổi từ int sang String
    private String quantity; // Đổi từ int sang String
    private String image;

    public CartItem() {
        Log.d("CartItem", "Constructor rỗng được gọi");
    }

    public CartItem(String idOrders, String idProducts, String name, String size, String price, String quantity, String image) {
        this.idOrders = idOrders;
        this.idProducts = idProducts;
        this.name = name;
        this.size = size;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        Log.d("CartItem", "Constructor đầy đủ được gọi với: " + name);
    }

    // Getter và Setter
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // 📌 Phương thức debug tiện lợi
    public void logCartItem() {
        Log.d("CartItem", "Thông tin sản phẩm: " +
                "\n- ID Order: " + idOrders +
                "\n- ID Product: " + idProducts +
                "\n- Tên: " + name +
                "\n- Size: " + size +
                "\n- Giá: " + price +
                "\n- Số lượng: " + quantity +
                "\n- Ảnh: " + image);
    }
}
