package fpt.anhdhph.bittweet.model;

public class CartItem {
    private String name;
    private String note;
    private int price;
    private int quantity;
    private String imageUrl;

    // Constructor rỗng cần thiết cho Firestore
    public CartItem() { }

    public CartItem(String name, String note, int price, int quantity, String imageUrl) {
        this.name = name;
        this.note = note;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    // Getter và Setter
    public String getName() { return name; }
    public String getNote() { return note; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
