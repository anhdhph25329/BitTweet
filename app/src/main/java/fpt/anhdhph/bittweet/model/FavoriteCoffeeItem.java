package fpt.anhdhph.bittweet.model;

public class FavoriteCoffeeItem {
    private int imageResource;  // Lưu R.drawable.xxx
    private String name;

    public FavoriteCoffeeItem(int imageResource, String name) {
        this.imageResource = imageResource;
        this.name = name;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getName() {
        return name;
    }
}
