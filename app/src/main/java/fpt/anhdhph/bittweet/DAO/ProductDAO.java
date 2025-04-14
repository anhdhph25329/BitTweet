package fpt.anhdhph.bittweet.DAO;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fpt.anhdhph.bittweet.model.Product;

public class ProductDAO {

    public interface OnProductLoadListener {
        void onSuccess(List<Product> productList);
        void onFailure(String errorMessage);
    }

    private final FirebaseFirestore db;

    public ProductDAO() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void getAllProducts(String category, final OnProductLoadListener listener) {
        db.collection("Products")
                .document(category)
                .collection("Items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> productList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = document.toObject(Product.class);
                        product.setId(document.getId());
                        productList.add(product);
                    }
                    listener.onSuccess(productList);
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductDAO", "Lỗi khi lấy dữ liệu: ", e);
                    listener.onFailure("Lỗi: " + e.getMessage());
                });
    }

    public void addProduct(String category, Product product, OnProductLoadListener listener) {
        db.collection("Products")
                .document(category)
                .collection("Items")
                .add(product.toMap())
                .addOnSuccessListener(documentReference -> listener.onSuccess(null))
                .addOnFailureListener(e -> listener.onFailure("Lỗi khi thêm sản phẩm: " + e.getMessage()));
    }

    public void deleteProduct(String category, String productId, OnProductLoadListener listener) {
        db.collection("Products")
                .document(category)
                .collection("Items")
                .document(productId)
                .delete()
                .addOnSuccessListener(unused -> listener.onSuccess(null))
                .addOnFailureListener(e -> listener.onFailure("Lỗi khi xóa sản phẩm: " + e.getMessage()));
    }

    public void updateProduct(String category, Product product, OnProductLoadListener listener) {
        db.collection("Products")
                .document(category)
                .collection("Items")
                .document(product.getId())
                .set(product.toMap())
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(e -> listener.onFailure("Lỗi khi cập nhật: " + e.getMessage()));
    }
}