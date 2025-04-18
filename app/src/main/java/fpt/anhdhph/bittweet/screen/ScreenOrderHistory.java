package fpt.anhdhph.bittweet.screen;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.AdapterOrderHistory;
import fpt.anhdhph.bittweet.model.CartItem;
import fpt.anhdhph.bittweet.model.Order;

public class ScreenOrderHistory extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvEmptyHistory;
    private RecyclerView rvHistory;
    private AdapterOrderHistory adapterOrderHistory;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private List<Order> orderList = new ArrayList<>();
    private ListenerRegistration orderListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_order_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo các view
        toolbar = findViewById(R.id.toolbar);
        tvEmptyHistory = findViewById(R.id.tv_empty_history);
        rvHistory = findViewById(R.id.rv_history);

        // Thiết lập Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Lịch sử đặt hàng");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Lấy userId từ SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", null);
        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thiết lập RecyclerView
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapterOrderHistory = new AdapterOrderHistory(this, orderList);
        rvHistory.setAdapter(adapterOrderHistory);

        // Thêm sự kiện hủy đơn hàng
        adapterOrderHistory.setOnCancelOrderClickListener((order, position) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Hủy đơn hàng")
                    .setMessage("Bạn có chắc muốn hủy đơn hàng này?")
                    .setPositiveButton("Hủy đơn", (dialog, which) -> {
                        if (order.getReference() != null) {
                            order.getReference()
                                    .update("status", "Đã hủy")
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Đã hủy đơn hàng! Hãy tới quầy để được hoàn tiền!", Toast.LENGTH_SHORT).show();
                                        order.setStatus("Đã hủy");
                                        adapterOrderHistory.notifyItemChanged(position);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Hủy đơn hàng thất bại: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });

        // Thêm sự kiện xác nhận đã nhận
        adapterOrderHistory.setOnConfirmReceivedClickListener((order, position) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận nhận hàng")
                    .setMessage("Bạn có chắc đã nhận được đơn hàng này?")
                    .setPositiveButton("Đã nhận", (dialog, which) -> {
                        if (order.getReference() != null) {
                            order.getReference()
                                    .update("status", "Đã nhận")
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Đã xác nhận nhận hàng", Toast.LENGTH_SHORT).show();
                                        order.setStatus("Đã nhận");
                                        adapterOrderHistory.notifyItemChanged(position);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Xác nhận nhận hàng thất bại: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });

        setupRealTimeUpdates(userId);
    }

    private void setupRealTimeUpdates(String userId) {
        if (orderListener != null) {
            orderListener.remove();
        }

        orderListener = db.collection("Users")
                .document(userId)
                .collection("Orders")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("ScreenOrderHistory", "Lỗi khi lấy lịch sử đơn hàng: " + error.getMessage());
                        Toast.makeText(this, "Lỗi khi lấy lịch sử đơn hàng!", Toast.LENGTH_SHORT).show();
                        tvEmptyHistory.setVisibility(View.VISIBLE);
                        rvHistory.setVisibility(View.GONE);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        orderList.clear();
                        for (var document : queryDocumentSnapshots) {
                            Order order = new Order();
                            order.setId(document.getId());
                            order.setUserId(document.getString("userId"));
                            String orderDate = document.getString("orderDate");
                            Log.d("ScreenOrderHistory", "orderId: " + document.getId() + ", orderDate: " + orderDate);
                            order.setOrderDate(orderDate);
                            order.setTotalPrice(document.getString("totalPrice"));
                            order.setCustomerName(document.getString("customerName"));
                            order.setPhoneNumber(document.getString("phoneNumber"));
                            order.setAddress(document.getString("address"));
                            order.setStatus(document.getString("status"));
                            order.setReference(document.getReference());

                            List<CartItem> items = new ArrayList<>();
                            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) document.get("items");
                            if (itemsData != null) {
                                for (Map<String, Object> itemData : itemsData) {
                                    CartItem item = new CartItem();
                                    item.setProductId((String) itemData.get("productId"));
                                    item.setProName((String) itemData.get("name"));
                                    item.setSize((String) itemData.get("size"));
                                    item.setPrice((String) itemData.get("price"));
                                    item.setQuantity((String) itemData.get("quantity"));
                                    item.setImage((String) itemData.get("image"));
                                    item.setCategory((String) itemData.get("category"));
                                    items.add(item);
                                }
                            }
                            order.setItems(items);
                            orderList.add(order);
                        }

                        orderList.sort((o1, o2) -> {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                            try {
                                if (o1.getOrderDate() == null || o1.getOrderDate().isEmpty()) {
                                    return o2.getOrderDate() == null || o2.getOrderDate().isEmpty() ? 0 : 1;
                                }
                                if (o2.getOrderDate() == null || o2.getOrderDate().isEmpty()) {
                                    return -1;
                                }
                                Date date1 = dateFormat.parse(o1.getOrderDate());
                                Date date2 = dateFormat.parse(o2.getOrderDate());
                                return date2.compareTo(date1);
                            } catch (ParseException e) {
                                Log.e("ScreenOrderHistory", "Lỗi parse ngày, orderId: " + o1.getId() + ", orderDate: " + o1.getOrderDate() + ", error: " + e.getMessage());
                                return 1;
                            }
                        });

                        if (orderList.isEmpty()) {
                            tvEmptyHistory.setVisibility(View.VISIBLE);
                            rvHistory.setVisibility(View.GONE);
                        } else {
                            tvEmptyHistory.setVisibility(View.GONE);
                            rvHistory.setVisibility(View.VISIBLE);
                            adapterOrderHistory.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orderListener != null) {
            orderListener.remove();
        }
    }
}