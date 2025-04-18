package fpt.anhdhph.bittweet.model;

public class CartItem {
    private String id;
    private String productId;
    private String proName;
    private String size;
    private String price;
    private String quantity;
    private String image;
    private String category;

    public CartItem() {
    }

    public CartItem(String id, String productId, String proName, String size, String price, String quantity, String image, String category) {
        this.id = id;
        this.productId = productId;
        this.proName = proName;
        this.size = size;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}