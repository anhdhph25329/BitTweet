package fpt.anhdhph.bittweet.screen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.Product;

public class ScreenDetail extends AppCompatActivity {

    private TextView name, desc, category, price;
    private ImageView imgProduct;
    private Product product;
    private RadioGroup rgSize;
    private RadioButton rbSizeS, rbSizeM, rbSizeL;
    private Button btnAddToCart;
    private Toolbar toolbar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_detail);

        name = findViewById(R.id.tvName);
        desc = findViewById(R.id.tvDesc);
        category = findViewById(R.id.tvCategory);
        price = findViewById(R.id.tvPrice);
        imgProduct = findViewById(R.id.imgProduct);
        rgSize = findViewById(R.id.rgSize);
        rbSizeS = findViewById(R.id.rbSizeS);
        rbSizeM = findViewById(R.id.rbSizeM);
        rbSizeL = findViewById(R.id.rbSizeL);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        db = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        product = (Product) getIntent().getSerializableExtra("product");
        getSupportActionBar().setTitle(product.getProName());

        themGioHang();

        if (product != null) {
            name.setText(product.getProName());
            desc.setText(product.getDes());
            category.setText("Loại: " + product.getCategory());

            Glide.with(this)
                    .load(product.getImage())
                    .into(imgProduct);

            rgSize.check(rbSizeS.getId());
            price.setText(product.getSPrice() + "đ");

            rgSize.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == rbSizeS.getId()) {
                    price.setText(product.getSPrice() + "đ");
                } else if (checkedId == rbSizeM.getId()) {
                    price.setText(product.getMPrice() + "đ");
                } else if (checkedId == rbSizeL.getId()) {
                    price.setText(product.getLPrice() + "đ");
                }
            });
        }
    }

    void themGioHang() {
        btnAddToCart.setOnClickListener(v -> {
            String selectedSize;
            String selectedPrice;

            if (rgSize.getCheckedRadioButtonId() == rbSizeM.getId()) {
                selectedSize = "M";
                selectedPrice = product.getMPrice();
            } else if (rgSize.getCheckedRadioButtonId() == rbSizeL.getId()) {
                selectedSize = "L";
                selectedPrice = product.getLPrice();
            } else {
                selectedSize = "S";
                selectedPrice = product.getSPrice();
            }

            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String userId = prefs.getString("user_id", null);

            if (userId == null) {
                Toast.makeText(ScreenDetail.this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
                return;
            }

            String cartItemId = product.getId() + "_" + selectedSize;

            DocumentReference cartItemRef = db.collection("Users")
                    .document(userId)
                    .collection("cart")
                    .document(cartItemId);

            cartItemRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    int currentQuantity = documentSnapshot.getLong("quantity").intValue();
                    cartItemRef.update("quantity", currentQuantity + 1)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ScreenDetail.this, "Đã cập nhật số lượng trong giỏ hàng!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ScreenDetail.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Map<String, Object> cartItem = new HashMap<>();
                    cartItem.put("productId", product.getId());
                    cartItem.put("category", product.getCategory());
                    cartItem.put("size", selectedSize);
                    cartItem.put("quantity", 1);
                    cartItem.put("price", selectedPrice);
                    cartItem.put("proName", product.getProName());
                    cartItem.put("image", product.getImage());

                    cartItemRef.set(cartItem)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ScreenDetail.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ScreenDetail.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(ScreenDetail.this, "Lỗi khi kiểm tra giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
    }
}