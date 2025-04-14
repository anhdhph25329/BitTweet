package fpt.anhdhph.bittweet.model;

public class Favorite {
    private String userId;
    private String productId;
    private String proName;
    private String image;
    private String mPrice;

    public Favorite() {
    }

    public Favorite(String userId, String productId, String proName, String image, String mPrice) {
        this.userId = userId;
        this.productId = productId;
        this.proName = proName;
        this.image = image;
        this.mPrice = mPrice;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getmPrice() {
        return mPrice;
    }

    public void setmPrice(String mPrice) {
        this.mPrice = mPrice;
    }
}
