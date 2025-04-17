package fpt.anhdhph.bittweet.screen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fpt.anhdhph.bittweet.DAO.ProductDAO;
import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.AdapterManagePro;
import fpt.anhdhph.bittweet.model.Product;

public class ScreenManagePro extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rvPro;
    AdapterManagePro adapterManagePro;
    List<Product> productList = new ArrayList<>();
    FirebaseFirestore db;
    ProductDAO productDAO;
    List<String> categoryList = new ArrayList<>();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_manage_pro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "user");
        if (!role.equals("admin")) {
            finish();
            return;
        }

        anhXa();
        setupRecyclerView();
        loadCategories(() -> {
            getData();
            setupRealTimeUpdates();
            themSanPham();
        });
    }

    void anhXa() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Product");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvPro = findViewById(R.id.rvPro);

        db = FirebaseFirestore.getInstance();
        productDAO = new ProductDAO();
    }

    void setupRecyclerView() {
        rvPro.setLayoutManager(new LinearLayoutManager(this));
        adapterManagePro = new AdapterManagePro(this, productList);
        adapterManagePro.setRefreshCallback(this::getData);
        rvPro.setAdapter(adapterManagePro);
    }

    void loadCategories(Runnable onComplete) {
        db.collection("Categories").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoryList.clear();
                    for (var doc : queryDocumentSnapshots) {
                        String categoryName = doc.getString("name");
                        if (categoryName != null) {
                            categoryList.add(categoryName);
                        }
                    }
                    if (categoryList.isEmpty()) {
                        Toast.makeText(this, "Không có danh mục nào!", Toast.LENGTH_SHORT).show();
                    }
                    onComplete.run();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    onComplete.run();
                });
    }

    void getData() {
        List<Product> allProducts = new ArrayList<>();
        List<Task<?>> tasks = new ArrayList<>();

        if (categoryList.isEmpty()) {
            productList.clear();
            if (adapterManagePro != null) {
                adapterManagePro.notifyDataSetChanged();
            }
            return;
        }

        for (String categoryName : categoryList) {
            Task<QuerySnapshot> task = db.collection("Products")
                    .document(categoryName)
                    .collection("Items")
                    .get()
                    .addOnSuccessListener(itemSnapshots -> {
                        for (var itemDoc : itemSnapshots.getDocuments()) {
                            Product p = Product.fromDocument(itemDoc);
                            p.setId(itemDoc.getId());
                            p.setCategory(categoryName);
                            allProducts.add(p);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi tải sản phẩm từ danh mục " + categoryName,
                                Toast.LENGTH_SHORT).show();
                    });
            tasks.add(task);
        }

        Tasks.whenAllComplete(tasks)
                .addOnSuccessListener(results -> {
                    productList.clear();
                    productList.addAll(allProducts);
                    if (adapterManagePro != null) {
                        adapterManagePro.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    void setupRealTimeUpdates() {
        if (categoryList.isEmpty()) return;

        for (String categoryName : categoryList) {
            db.collection("Products")
                    .document(categoryName)
                    .collection("Items")
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Toast.makeText(this, "Lỗi khi theo dõi thay đổi: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (value != null) {
                            List<Product> updatedProducts = new ArrayList<>();
                            for (var doc : value.getDocuments()) {
                                Product p = Product.fromDocument(doc);
                                p.setId(doc.getId());
                                p.setCategory(categoryName);
                                updatedProducts.add(p);
                            }
                            productList.clear();
                            productList.addAll(updatedProducts);
                            adapterManagePro.notifyDataSetChanged();
                        }
                    });
        }
    }

    void themSanPham() {
        findViewById(R.id.btnAddPro).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ScreenManagePro.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.layout_dialog_add_pro, null);
            builder.setView(dialogView);

            TextInputEditText edtProName = dialogView.findViewById(R.id.edtProName);
            TextInputEditText edtDes = dialogView.findViewById(R.id.edtDes);
            TextInputEditText edtSPrice = dialogView.findViewById(R.id.edtSPrice);
            TextInputEditText edtMPrice = dialogView.findViewById(R.id.edtMPrice);
            TextInputEditText edtLPrice = dialogView.findViewById(R.id.edtLPrice);
            TextInputEditText edtImageUrl = dialogView.findViewById(R.id.edtImageUrl);
            Button btnAddProDialog = dialogView.findViewById(R.id.btnAddPro);
            Spinner spCategory = dialogView.findViewById(R.id.spCategory);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(ScreenManagePro.this,
                    android.R.layout.simple_spinner_item, categoryList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCategory.setAdapter(adapter);

            AlertDialog dialog = builder.create();

            btnAddProDialog.setOnClickListener(view -> {
                String proName = edtProName.getText() != null ? edtProName.getText().toString().trim() : "";
                String des = edtDes.getText() != null ? edtDes.getText().toString().trim() : "";
                String sPrice = edtSPrice.getText() != null ? edtSPrice.getText().toString().trim() : "";
                String mPrice = edtMPrice.getText() != null ? edtMPrice.getText().toString().trim() : "";
                String lPrice = edtLPrice.getText() != null ? edtLPrice.getText().toString().trim() : "";
                String imageUrl = edtImageUrl.getText() != null ? edtImageUrl.getText().toString().trim() : "";
                String category = spCategory.getSelectedItem() != null ? spCategory.getSelectedItem().toString() : "";

                if (proName.isEmpty()) {
                    edtProName.setError("Vui lòng nhập tên sản phẩm");
                    return;
                }
                if (des.isEmpty()) {
                    edtDes.setError("Vui lòng nhập mô tả");
                    return;
                }
                if (category.isEmpty()) {
                    Toast.makeText(ScreenManagePro.this, "Vui lòng chọn danh mục!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Product product = new Product(proName, des, sPrice, mPrice, lPrice, category, imageUrl);

                db.collection("Products")
                        .document(category)
                        .collection("Items")
                        .add(product.toMap())
                        .addOnSuccessListener(ref -> {
                            Toast.makeText(ScreenManagePro.this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            getData(); // Vẫn giữ getData cho tính nhất quán
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ScreenManagePro.this, "Lỗi khi thêm sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });

            dialog.show();
        });
    }
}