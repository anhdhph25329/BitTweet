package fpt.anhdhph.bittweet.screen;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fpt.anhdhph.bittweet.R;

public class ScreenManagePro extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rvPro;
    FloatingActionButton btnAddPro;
    Button btnDemo;
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

        anhXa();

        themSanPham();


        //demo sua xoa
        btnDemo = findViewById(R.id.btnDemo);
        tuongTacSP();


    }

    void anhXa(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quản lý sản phẩm");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAddPro = findViewById(R.id.btnAddPro);
        rvPro = findViewById(R.id.rvPro);
    }

    void themSanPham(){
        btnAddPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                AlertDialog.Builder builder = new AlertDialog.Builder(ScreenManagePro.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.layout_dialog_add_pro, null);
                builder.setView(dialogView);

                TextInputEditText edtProName = dialogView.findViewById(R.id.edtProName);
                TextInputEditText edtDes = dialogView.findViewById(R.id.edtDes);
                TextInputEditText edtSPrice = dialogView.findViewById(R.id.edtSPrice);
                TextInputEditText edtMPrice = dialogView.findViewById(R.id.edtMPrice);
                TextInputEditText edtLPrice = dialogView.findViewById(R.id.edtLPrice);
                Button btnAddPro = dialogView.findViewById(R.id.btnAddPro);
                Spinner spCategory = dialogView.findViewById(R.id.spCategory);


                List<String> cateList = new ArrayList<>();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ScreenManagePro.this,
                        android.R.layout.simple_spinner_item, cateList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategory.setAdapter(adapter);

                db.collection("Categories").get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String categoryName = document.getString("name");
                                if (categoryName != null) {
                                    cateList.add(categoryName);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ScreenManagePro.this, "Lỗi khi tải danh mục!",
                                    Toast.LENGTH_SHORT).show();
                        });


                AlertDialog dialog = builder.create();

                btnAddPro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String proName = edtProName.getText().toString();
                        String category = spCategory.getSelectedItem().toString();
                        String des = edtDes.getText().toString();
                        String sPrice = edtSPrice.getText().toString();
                        String mPrice = edtMPrice.getText().toString();
                        String lPrice = edtLPrice.getText().toString();

                        if (proName.isEmpty()) {
                            edtProName.setError("Vui lòng nhập tên sản phẩm");
                            return;
                        }
                        if (des.isEmpty()) {
                            edtDes.setError("Vui lòng nhập mô tả");
                            return;
                        }
                        if (sPrice.isEmpty()) {
                            edtSPrice.setError("Vui lòng nhập giá size S");
                            return;
                        }
                        if (mPrice.isEmpty()) {
                            edtMPrice.setError("Vui lòng nhập giá size M");
                            return;
                        }
                        if (lPrice.isEmpty()) {
                            edtLPrice.setError("Vui lòng nhập giá size L");
                            return;
                        }

                        Map<String, Object> product = new HashMap<>();
                        product.put("proName", proName);
                        product.put("category", category);
                        product.put("des", des);
                        product.put("sPrice", sPrice);
                        product.put("mPrice", mPrice);
                        product.put("lPrice", lPrice);

                        db.collection("Products")
                                .document(category)
                                .collection("Items")
                                .add(product)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(ScreenManagePro.this, "Thêm sản phẩm thành công!",
                                            Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ScreenManagePro.this, "Lỗi khi thêm sản phẩm!",
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                });

                dialog.show();
            }
        });
    }

    void tuongTacSP(){
        btnDemo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View popupView = LayoutInflater.from(ScreenManagePro.this).inflate(R.layout.layout_menu_pro, null);
                PopupWindow popupWindow = new PopupWindow(popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

                // Optional: set background & animation
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupWindow.setElevation(10f);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenWidth = displayMetrics.widthPixels;

                // Đo chiều rộng popup
                popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int popupWidth = popupView.getMeasuredWidth();

                // Tính margin 16dp
                int marginInPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics());

                // Tính xOffset để cách phải 16dp
                int xOffset = screenWidth - popupWidth - marginInPx;

                // Lấy tọa độ Y của view (cho popup hiện gần nó)
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                int yOffset = location[1] + v.getHeight();

                // Hiện popup ở vị trí tính toán
                popupWindow.showAtLocation(v, Gravity.TOP | Gravity.START, xOffset, yOffset);


                LinearLayout editBtn = popupView.findViewById(R.id.btnEdit);
                LinearLayout deleteBtn = popupView.findViewById(R.id.btnDel);

                editBtn.setOnClickListener(view -> {
                    Toast.makeText(ScreenManagePro.this, "Sửa custom", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                });
                deleteBtn.setOnClickListener(view -> {
                    Toast.makeText(ScreenManagePro.this, "Xóa custom", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                });

                return true;
            }
        });
    }

}