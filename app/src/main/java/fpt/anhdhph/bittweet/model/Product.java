package fpt.anhdhph.bittweet.model;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Product implements Serializable {
    private String id;
    private String proName;
    private String des;
    private String sPrice;
    private String mPrice;
    private String lPrice;
    private String category;
    private String image;
    private boolean isFavorite;

    // Constructor rỗng cho Firestore
    public Product() {}

    public Product(String proName, String des, String sPrice, String mPrice, String lPrice, String category, String image) {
        this.proName = proName;
        this.des = des;
        this.sPrice = sPrice;
        this.mPrice = mPrice;
        this.lPrice = lPrice;
        this.category = category;
        this.image = image;
    }

    // Getters và setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getSPrice() {
        return sPrice;
    }

    public void setSPrice(String sPrice) {
        this.sPrice = sPrice;
    }

    public String getMPrice() {
        return mPrice;
    }

    public void setMPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public String getLPrice() {
        return lPrice;
    }

    public void setLPrice(String lPrice) {
        this.lPrice = lPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // Chuyển dữ liệu thành Map để đẩy lên Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("proName", proName != null ? proName : "");
        map.put("des", des != null ? des : "");
        map.put("sPrice", sPrice != null ? sPrice : "");
        map.put("mPrice", mPrice != null ? mPrice : "");
        map.put("lPrice", lPrice != null ? lPrice : "");
        map.put("category", category != null ? category : "");
        map.put("image", image != null ? image : "");
        // Không lưu isFavorite vì nó được quản lý trong collection Favorites
        return map;
    }

    // Tạo Product từ DocumentSnapshot
    public static Product fromDocument(DocumentSnapshot doc) {
        Product product = new Product();
        product.setId(doc.getId());
        product.setProName(doc.getString("proName") != null ? doc.getString("proName") : "");
        product.setDes(doc.getString("des") != null ? doc.getString("des") : "");
        product.setSPrice(doc.getString("sPrice") != null ? doc.getString("sPrice") : "");
        product.setMPrice(doc.getString("mPrice") != null ? doc.getString("mPrice") : "");
        product.setLPrice(doc.getString("lPrice") != null ? doc.getString("lPrice") : "");
        product.setCategory(doc.getString("category") != null ? doc.getString("category") : "");
        product.setImage(doc.getString("image") != null ? doc.getString("image") : "");
        return product;
    }
}