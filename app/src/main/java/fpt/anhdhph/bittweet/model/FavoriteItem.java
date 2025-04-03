package fpt.anhdhph.bittweet.model;

public class FavoriteItem {
    private String name;
    private int imageResource;

    public FavoriteItem(String name, int imageResource) {
        this.name = name;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResource;
    }
}
