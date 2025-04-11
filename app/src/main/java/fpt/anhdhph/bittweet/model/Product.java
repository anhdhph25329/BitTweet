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
    private String category; // ✅ THÊM TRƯỜNG MỚI
    private String image;  // Trường image để chứa URL ảnh
    private boolean isFavorite;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // ✅ Constructor rỗng cần thiết cho Firestore
    public Product() {}

    public Product(String proName, String des, String sPrice, String mPrice, String lPrice, String category,String image) {
        this.proName = proName;
        this.des = des;
        this.sPrice = sPrice;
        this.mPrice = mPrice;
        this.lPrice = lPrice;
        this.category = category;
        this.image = image;
    }
    public Product(String proName, String des, String sPrice, String mPrice, String lPrice, String category) {
        this.proName = proName;
        this.des = des;
        this.sPrice = sPrice;
        this.mPrice = mPrice;
        this.lPrice = lPrice;
        this.category = category;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    // Chuyển dữ liệu thành Map để đẩy lên Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("proName", proName);
        map.put("des", des);
        map.put("sPrice", sPrice);
        map.put("mPrice", mPrice);
        map.put("lPrice", lPrice);
        map.put("category", category); // ✅ Gán thêm category
        map.put("image", image);  // Lưu URL ảnh vào Firestore
        return map;
    }

    public static Product fromDocument(DocumentSnapshot doc) {
        String proName = doc.getString("proName");
        String des = doc.getString("des");
        String sPrice = doc.getString("sPrice");
        String mPrice = doc.getString("mPrice");
        String lPrice = doc.getString("lPrice");
        String category = doc.getString("category");
        String image = doc.getString("image");

        Product product = new Product(proName, des, sPrice, mPrice, lPrice, category, image);
        return product;
    }

}
