package fpt.anhdhph.bittweet.DAO;

import com.google.firebase.firestore.FirebaseFirestore;
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