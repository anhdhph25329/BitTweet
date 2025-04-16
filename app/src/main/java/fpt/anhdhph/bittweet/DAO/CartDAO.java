package fpt.anhdhph.bittweet.DAO;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fpt.anhdhph.bittweet.model.CartItem;

public class CartDAO {
    private FirebaseFirestore db;
    private Context context;

    public CartDAO(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    public interface CartItemsCallback {
        void onCartItemsLoaded(List<CartItem> cartItems);
    }

    public void getCartItems(CartItemsCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Log.e("CartDAO", "Người dùng chưa đăng nhập!");
            callback.onCartItemsLoaded(new ArrayList<>());
            return;
        }

        db.collection("Users")
                .document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CartItem> cartItems = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        CartItem cartItem = new CartItem();
                        cartItem.setId(document.getId());
                        cartItem.setIdProducts(document.getString("productId"));
                        cartItem.setName(document.getString("proName"));
                        cartItem.setImage(document.getString("image"));
                        cartItem.setSize(document.getString("size"));
                        cartItem.setPrice(document.getString("price"));
                        // Xử lý quantity linh hoạt
                        Object quantityObj = document.get("quantity");
                        if (quantityObj instanceof String) {
                            cartItem.setQuantity((String) quantityObj);
                        } else if (quantityObj instanceof Number) {
                            cartItem.setQuantity(String.valueOf(((Number) quantityObj).intValue()));
                        } else {
                            cartItem.setQuantity("1");
                        }
                        cartItem.setCategory(document.getString("category"));
                        cartItems.add(cartItem);
                    }
                    callback.onCartItemsLoaded(cartItems);
                })
                .addOnFailureListener(e -> {
                    Log.e("CartDAO", "Lỗi khi lấy giỏ hàng: " + e.getMessage());
                    callback.onCartItemsLoaded(new ArrayList<>());
                });
    }

    public void updateQuantity(String cartItemId, int newQuantity, Runnable onSuccess, Runnable onFailure) {
        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Log.e("CartDAO", "Người dùng chưa đăng nhập!");
            onFailure.run();
            return;
        }

        db.collection("Users")
                .document(userId)
                .collection("cart")
                .document(cartItemId)
                .update("quantity", String.valueOf(newQuantity))
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(e -> {
                    Log.e("CartDAO", "Lỗi khi cập nhật số lượng: " + e.getMessage());
                    onFailure.run();
                });
    }
}