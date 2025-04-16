package fpt.anhdhph.bittweet.model;

import android.util.Log;

public class CartItem {
    private String id; // Thêm trường id để lưu documentId
    private String idProducts;
    private String name;
    private String size;
    private String price;
    private String quantity;
    private String image;
    private String category;

    public CartItem() {
        Log.d("CartItem", "Constructor rỗng được gọi");
    }

    public CartItem(String id, String idProducts, String name, String size, String price, String quantity, String image, String category) {
        this.id = id;
        this.idProducts = idProducts;
        this.name = name;
        this.size = size;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.category = category;
        Log.d("CartItem", "Constructor đầy đủ được gọi với: " + name);
    }

    // Getter và Setter cho id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter và Setter cho category
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Các getter/setter khác
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

    public void logCartItem() {
        Log.d("CartItem", "Thông tin sản phẩm: " +
                "\n- ID: " + id +
                "\n- ID Product: " + idProducts +
                "\n- Tên: " + name +
                "\n- Size: " + size +
                "\n- Giá: " + price +
                "\n- Số lượng: " + quantity +
                "\n- Ảnh: " + image +
                "\n- Category: " + category);
    }
}