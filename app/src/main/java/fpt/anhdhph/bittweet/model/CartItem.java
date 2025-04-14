package fpt.anhdhph.bittweet.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String name, size, image;
    private int quantity;
    private long price;

    public CartItem() {}

    public CartItem(String name, String size, String image, int quantity, long price) {
        this.name = name;
        this.size = size;
        this.image = image;
        this.quantity = quantity;
        this.price = price;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
