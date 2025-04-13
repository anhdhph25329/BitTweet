package fpt.anhdhph.bittweet.screen;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    FloatingActionButton btnAddPro;
    AdapterManagePro adapterManagePro;
    List<Product> productList = new ArrayList<>();
    FirebaseFirestore db;
    ProductDAO productDAO;
    String docName = "Coffee"; // default

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

        // Nhận docName từ Intent
        docName = getIntent().getStringExtra("docName");
        if (docName == null) docName = "Coffee";

        anhXa();
        setupRecyclerView();
        getData();
        themSanPham();
    }

    void anhXa() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Manage Product");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        btnAddPro = findViewById(R.id.btnAddPro);
        rvPro = findViewById(R.id.rvPro);

        db = FirebaseFirestore.getInstance();
        productDAO = new ProductDAO();
    }

    void setupRecyclerView() {
        rvPro.setLayoutManager(new LinearLayoutManager(this));
        adapterManagePro = new AdapterManagePro(this, productList, docName, this::getData);
        rvPro.setAdapter(adapterManagePro);
    }

    void getData() {
        String[] docNames = {"Coffee", "Sinh tố", "Cooktail"};
        List<Product> allProducts = new ArrayList<>();

        List<Task<?>> tasks = new ArrayList<>();

        for (String name : docNames) {
            Task<QuerySnapshot> task = FirebaseFirestore.getInstance()
                    .collection("Products")
                    .document(name)
                    .collection("Items")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (var doc : queryDocumentSnapshots.getDocuments()) {
                            Product p = Product.fromDocument(doc);
                            p.setId(doc.getId());
                            allProducts.add(p);
                        }
                    });
            tasks.add(task);
        }

        // Sau khi tất cả Task hoàn tất thì cập nhật giao diện
        Tasks.whenAllComplete(tasks)
                .addOnSuccessListener(results -> {
                    productList.clear();
                    productList.addAll(allProducts);
                    adapterManagePro.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                });
    }

    void themSanPham() {
        btnAddPro.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ScreenManagePro.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.layout_dialog_add_pro, null);
            builder.setView(dialogView);

            TextInputEditText edtProName = dialogView.findViewById(R.id.edtProName);
            TextInputEditText edtDes = dialogView.findViewById(R.id.edtDes);
            TextInputEditText edtSPrice = dialogView.findViewById(R.id.edtSPrice);
            TextInputEditText edtMPrice = dialogView.findViewById(R.id.edtMPrice);
            TextInputEditText edtLPrice = dialogView.findViewById(R.id.edtLPrice);
            Button btnAddProDialog = dialogView.findViewById(R.id.btnAddPro);
            Spinner spCategory = dialogView.findViewById(R.id.spCategory);

            List<String> cateList = new ArrayList<>();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ScreenManagePro.this,
                    android.R.layout.simple_spinner_item, cateList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCategory.setAdapter(adapter);

            db.collection("Categories").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (var doc : queryDocumentSnapshots) {
                            String categoryName = doc.getString("name");
                            if (categoryName != null) cateList.add(categoryName);
                        }
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> Toast.makeText(ScreenManagePro.this, "Lỗi khi tải danh mục!", Toast.LENGTH_SHORT).show());

            AlertDialog dialog = builder.create();

            btnAddProDialog.setOnClickListener(view -> {
                String proName = edtProName.getText().toString();
                String des = edtDes.getText().toString();
                String sPrice = edtSPrice.getText().toString();
                String mPrice = edtMPrice.getText().toString();
                String lPrice = edtLPrice.getText().toString();
                String category = spCategory.getSelectedItem() != null ? spCategory.getSelectedItem().toString() : "";

                if (proName.isEmpty()) {
                    edtProName.setError("Vui lòng nhập tên sản phẩm");
                    return;
                }
                if (des.isEmpty()) {
                    edtDes.setError("Vui lòng nhập mô tả");
                    return;
                }

                Product product = new Product(proName, des, sPrice, mPrice, lPrice, category);

                db.collection("Products")
                        .document(docName)
                        .collection("Items")
                        .add(product.toMap())
                        .addOnSuccessListener(ref -> {
                            Toast.makeText(ScreenManagePro.this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            getData();
                        })
                        .addOnFailureListener(e -> Toast.makeText(ScreenManagePro.this, "Lỗi khi thêm sản phẩm!", Toast.LENGTH_SHORT).show());
            });

            dialog.show();
        });
    }
}
