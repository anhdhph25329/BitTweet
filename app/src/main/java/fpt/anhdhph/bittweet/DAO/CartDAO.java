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

    private final FirebaseFirestore db;
    private final Context context;

    public CartDAO(Context context) {
        this.context = context.getApplicationContext(); // 🔁 Luôn nên dùng getApplicationContext() để tránh memory leak
        db = FirebaseFirestore.getInstance();
    }

    // ✅ Thêm sản phẩm vào giỏ hàng (dùng idOrders từ SharedPreferences)
    public void addToCart(CartItem item) {
        SharedPreferences prefs = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String userId = prefs.getString("documentId", null);

        if (userId == null) {
            Log.e("CartDAO", "❌ Không tìm thấy documentId trong SharedPreferences");
            return;
        }

        String cartId = "cart_" + userId;
        item.setIdOrders(cartId); // Gán đúng ID đơn hàng

        db.collection("OrdersDetail")
                .add(item)
                .addOnSuccessListener(documentReference ->
                        Log.d("CartDAO", "✅ Thêm vào giỏ hàng thành công: " + item.getName() + " - size: " + item.getSize()))
                .addOnFailureListener(e ->
                        Log.e("CartDAO", "❌ Lỗi khi thêm vào giỏ hàng", e));
    }

    // ✅ Lấy danh sách giỏ hàng
    public void getCartItems(OnCartItemsLoadedListener listener) {
        SharedPreferences prefs = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String userId = prefs.getString("documentId", null);
        Log.d("CartDAO", "🧾 userId từ SharedPreferences = " + userId);

        if (userId == null) {
            Log.e("CartDAO", "❌ Không tìm thấy documentId trong SharedPreferences");
            listener.onCartItemsLoaded(new ArrayList<>());
            return;
        }

        String cartId = "cart_" + userId;
        Log.d("CartDAO", "📥 Đang lấy giỏ hàng với idOrders = " + cartId);

        db.collection("OrdersDetail")
                .whereEqualTo("idOrders", cartId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CartItem> cartItems = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            CartItem item = doc.toObject(CartItem.class);
                            Log.d("CartDAO", "📦 Đã lấy item: " + item.getName() + " - size: " + item.getSize());
                            cartItems.add(item);
                        } catch (Exception e) {
                            Log.e("CartDAO", "❌ Lỗi khi convert document thành CartItem", e);
                        }
                    }
                    listener.onCartItemsLoaded(cartItems);
                })
                .addOnFailureListener(e -> {
                    Log.e("CartDAO", "❌ Lỗi khi lấy giỏ hàng", e);
                    listener.onCartItemsLoaded(new ArrayList<>());
                });
    }

    // ✅ Interface callback để xử lý danh sách giỏ hàng sau khi lấy
    public interface OnCartItemsLoadedListener {
        void onCartItemsLoaded(List<CartItem> cartItems);
    }
}
