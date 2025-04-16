package fpt.anhdhph.bittweet.screen;

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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.AdapterOrder;
import fpt.anhdhph.bittweet.model.CartItem;
import fpt.anhdhph.bittweet.model.Order;

public class ScreenManageOrder extends AppCompatActivity {

    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private TextView tvEmptyOrders;
    private RecyclerView rvOrders;
    private AdapterOrder adapterOrder;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_manage_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Kiểm tra quyền admin
        sharedPreferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "user");
        if (!role.equals("admin")) {
            finish();
            return;
        }

        // Khởi tạo các view
        toolbar = findViewById(R.id.toolbar);
        tvEmptyOrders = findViewById(R.id.tv_empty_orders);
        rvOrders = findViewById(R.id.rv_orders);

        // Thiết lập Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quản lý đơn hàng");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Thiết lập RecyclerView
        adapterOrder = new AdapterOrder(this, new ArrayList<>());
        rvOrders.setAdapter(adapterOrder);

        // Lấy danh sách đơn hàng từ Firestore
        loadOrders();
    }

    private void loadOrders() {
        // Lấy tất cả đơn hàng từ tất cả người dùng (dành cho admin)
        List<Order> allOrders = new ArrayList<>();
        db.collectionGroup("Orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Order order = new Order();
                        order.setId(document.getId());
                        order.setUserId(document.getString("userId"));
                        order.setOrderDate(document.getString("orderDate"));
                        order.setTotalPrice(document.getString("totalPrice"));
                        order.setCustomerName(document.getString("customerName"));
                        order.setPhoneNumber(document.getString("phoneNumber"));
                        order.setAddress(document.getString("address"));

                        // Lấy danh sách sản phẩm trong đơn hàng
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
                        allOrders.add(order);
                    }

                    // Xử lý hiển thị
                    if (allOrders.isEmpty()) {
                        tvEmptyOrders.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                    } else {
                        tvEmptyOrders.setVisibility(View.GONE);
                        rvOrders.setVisibility(View.VISIBLE);
                        adapterOrder.setOrders(allOrders);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ScreenManageOrder", "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
                    Toast.makeText(this, "Lỗi khi lấy danh sách đơn hàng!", Toast.LENGTH_SHORT).show();
                    tvEmptyOrders.setVisibility(View.VISIBLE);
                    rvOrders.setVisibility(View.GONE);
                });
    }
}