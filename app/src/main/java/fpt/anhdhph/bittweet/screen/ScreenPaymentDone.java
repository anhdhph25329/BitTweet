package fpt.anhdhph.bittweet.screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import fpt.anhdhph.bittweet.DAO.CartDAO;
import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.CartItem;

public class ScreenPaymentDone extends AppCompatActivity {

    private Handler handler;
    private Runnable runnable;
    private FirebaseFirestore db;
    private CartDAO cartDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_payment_done);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo Firebase và CartDAO
        db = FirebaseFirestore.getInstance();
        cartDAO = new CartDAO(this);

        // Lưu đơn hàng và xóa giỏ hàng
        saveOrderAndClearCart();

        // Nút X
        TextView btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> navigateToHome());

        // Tự động sau 3 giây
        handler = new Handler();
        runnable = this::navigateToHome;
        handler.postDelayed(runnable, 3000);
    }

    private void saveOrderAndClearCart() {
        // Lấy userId từ SharedPreferences
        String userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("user_id", null);
        if (userId == null) {
            Log.e("ScreenPaymentDone", "Người dùng chưa đăng nhập!");
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục!", Toast.LENGTH_SHORT).show();
            // Chuyển hướng đến màn hình đăng nhập
            Intent intent = new Intent(this, ScreenLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Lấy thông tin người dùng từ Firestore
        db.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Log.e("ScreenPaymentDone", "Không tìm thấy thông tin người dùng!");
                        Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String customerName = documentSnapshot.getString("name");
                    String phoneNumber = documentSnapshot.getString("phone");
                    String address = documentSnapshot.getString("address");

                    if (customerName == null || phoneNumber == null || address == null) {
                        Log.e("ScreenPaymentDone", "Thiếu thông tin người dùng (name, phoneNumber, hoặc address)!");
                        Toast.makeText(this, "Vui lòng cập nhật đầy đủ thông tin cá nhân!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Lấy danh sách sản phẩm trong giỏ hàng
                    cartDAO.getCartItems(cartItems -> {
                        if (cartItems.isEmpty()) {
                            Log.w("ScreenPaymentDone", "Giỏ hàng trống!");
                            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Tạo thông tin đơn hàng
                        Map<String, Object> orderData = new HashMap<>();
                        orderData.put("userId", userId);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                        String orderDate = sdf.format(new Date());
                        orderData.put("orderDate", orderDate);
                        orderData.put("customerName", customerName);
                        orderData.put("phoneNumber", phoneNumber);
                        orderData.put("address", address);
                        orderData.put("status", "Chờ xác nhận"); // Thêm trạng thái mặc định

                        // Tính tổng tiền
                        double totalPrice = 0;
                        List<Map<String, Object>> items = new ArrayList<>();
                        for (CartItem item : cartItems) {
                            Map<String, Object> itemData = new HashMap<>();
                            itemData.put("productId", item.getProductId());
                            itemData.put("name", item.getProName());
                            itemData.put("size", item.getSize());
                            itemData.put("price", item.getPrice());
                            itemData.put("quantity", item.getQuantity());
                            itemData.put("image", item.getImage());
                            itemData.put("category", item.getCategory());
                            items.add(itemData);

                            // Tính tổng tiền
                            try {
                                double price = Double.parseDouble(item.getPrice());
                                int quantity = Integer.parseInt(item.getQuantity());
                                totalPrice += price * quantity;
                            } catch (NumberFormatException e) {
                                Log.e("ScreenPaymentDone", "Lỗi tính tổng tiền: " + e.getMessage());
                            }
                        }
                        orderData.put("items", items);
                        orderData.put("totalPrice", String.valueOf(totalPrice));

                        // Lưu vào collection Orders
                        db.collection("Users")
                                .document(userId)
                                .collection("Orders")
                                .add(orderData)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("ScreenPaymentDone", "Đơn hàng đã được lưu: " + documentReference.getId() + ", orderDate: " + orderDate);
                                    // Xóa giỏ hàng sau khi lưu đơn hàng thành công
                                    clearCart(userId);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ScreenPaymentDone", "Lỗi khi lưu đơn hàng: " + e.getMessage());
                                    Toast.makeText(this, "Lỗi khi lưu đơn hàng!", Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("ScreenPaymentDone", "Lỗi khi lấy thông tin người dùng: " + e.getMessage());
                    Toast.makeText(this, "Lỗi khi lấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearCart(String userId) {
        // Lấy tất cả document trong collection cart và xóa
        db.collection("Users")
                .document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                    Log.d("ScreenPaymentDone", "Đã xóa toàn bộ giỏ hàng!");
                })
                .addOnFailureListener(e -> {
                    Log.e("ScreenPaymentDone", "Lỗi khi xóa giỏ hàng: " + e.getMessage());
                    Toast.makeText(this, "Lỗi khi xóa giỏ hàng!", Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToHome() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}