package fpt.anhdhph.bittweet.screen;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import fpt.anhdhph.bittweet.adapter.AdapterManageOrder;
import fpt.anhdhph.bittweet.model.CartItem;
import fpt.anhdhph.bittweet.model.Order;

public class ScreenManageOrder extends AppCompatActivity {

    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private TextView tvEmptyOrders;
    private RecyclerView rvOrders;
    private AdapterManageOrder adapterManageOrder;
    private FirebaseFirestore db;
    private SearchView searchViewOrd;
    private List<Order> allOrders = new ArrayList<>();
    private ListenerRegistration ordersListener;
    private final String[] statuses = {"Chờ xác nhận", "Đang pha chế", "Hoàn tất", "Đã nhận", "Đã hủy"};

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
        searchViewOrd = findViewById(R.id.search_view_ord);

        // Thiết lập Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quản lý đơn hàng");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Thiết lập RecyclerView
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapterManageOrder = new AdapterManageOrder(this, new ArrayList<>());
        rvOrders.setAdapter(adapterManageOrder);

        // Thiết lập SearchView
        searchViewOrd.setQueryHint("Nhập 3 số SĐT cuối của khách hàng...");
        searchViewOrd.setIconified(false);
        setupSearchView();

        // Lấy danh sách đơn hàng theo thời gian thực
        setupRealTimeUpdates();

        // Thêm sự kiện long click để xóa đơn hàng
        adapterManageOrder.setOnOrderLongClickListener((order, position) -> {
            new AlertDialog.Builder(ScreenManageOrder.this)
                    .setTitle("Xóa đơn hàng")
                    .setMessage("Bạn có chắc muốn xóa đơn hàng này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        if (order.getReference() != null) {
                            order.getReference()
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(ScreenManageOrder.this, "Đã xóa đơn hàng",
                                                Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ScreenManageOrder.this, "Xóa thất bại",
                                                Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(ScreenManageOrder.this, "Không tìm thấy tài liệu để xóa",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        adapterManageOrder.setOnOrderClickListener((order, position) -> showUpdateStatusDialog(order));
    }

    private void setupSearchView() {
        EditText searchEditText = searchViewOrd.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(Color.GRAY);

        searchViewOrd.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchByPhoneSuffix(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    adapterManageOrder.setOrders(allOrders);
                } else {
                    searchByPhoneSuffix(newText.trim());
                }
                return true;
            }
        });
    }

    private void searchByPhoneSuffix(String suffix) {
        List<Order> filtered = new ArrayList<>();
        for (Order order : allOrders) {
            if (order.getPhoneNumber() != null && order.getPhoneNumber().endsWith(suffix)) {
                filtered.add(order);
            }
        }
        adapterManageOrder.setOrders(filtered);
    }

    private void showUpdateStatusDialog(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_update_status, null);
        builder.setView(dialogView);

        Spinner spinnerStatus = dialogView.findViewById(R.id.spinner_status);
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Đặt trạng thái hiện tại làm mặc định
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(order.getStatus())) {
                spinnerStatus.setSelection(i);
                break;
            }
        }

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newStatus = spinnerStatus.getSelectedItem().toString();
            if (order.getReference() != null) {
                order.getReference()
                        .update("status", newStatus)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Đã cập nhật trạng thái thành: " + newStatus,
                                    Toast.LENGTH_SHORT).show();
                            order.setStatus(newStatus);
                            adapterManageOrder.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Cập nhật trạng thái thất bại: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        });
        builder.setNegativeButton("Hủy", null);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Tùy chỉnh nút để cân đối
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.black));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.black));
    }

    private void setupRealTimeUpdates() {
        if (ordersListener != null) {
            ordersListener.remove();
        }

        ordersListener = db.collectionGroup("Orders")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("ScreenManageOrder", "Lỗi khi lấy danh sách đơn hàng: " + error.getMessage());
                        Toast.makeText(this, "Lỗi khi lấy danh sách đơn hàng!", Toast.LENGTH_SHORT).show();
                        tvEmptyOrders.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        allOrders.clear();
                        for (var document : queryDocumentSnapshots) {
                            Order order = new Order();
                            order.setId(document.getId());
                            order.setUserId(document.getString("userId"));
                            String orderDate = document.getString("orderDate");
                            Log.d("ScreenManageOrder", "orderId: " + document.getId() + ", orderDate: " + orderDate);
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
                            allOrders.add(order);
                        }

                        allOrders.sort((o1, o2) -> {
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
                                Log.e("ScreenManageOrder", "Lỗi parse ngày, orderId: " + o1.getId() + ", orderDate: " + o1.getOrderDate() + ", error: " + e.getMessage());
                                return 1;
                            }
                        });

                        if (allOrders.isEmpty()) {
                            tvEmptyOrders.setVisibility(View.VISIBLE);
                            rvOrders.setVisibility(View.GONE);
                        } else {
                            tvEmptyOrders.setVisibility(View.GONE);
                            rvOrders.setVisibility(View.VISIBLE);
                            if (searchViewOrd.getQuery().toString().trim().isEmpty()) {
                                adapterManageOrder.setOrders(allOrders);
                            } else {
                                searchByPhoneSuffix(searchViewOrd.getQuery().toString().trim());
                            }
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ordersListener != null) {
            ordersListener.remove();
        }
    }
}