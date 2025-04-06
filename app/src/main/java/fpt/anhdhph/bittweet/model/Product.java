package fpt.anhdhph.bittweet.model;

public class Product {
    private String name;
    private String price;
    private int imageResId;
    private String category; // Thêm trường category để filter

    public Product(String name, String price, int imageResId, String category) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.category = category;
    }

    // Getter methods
    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getCategory() { return category; }
}