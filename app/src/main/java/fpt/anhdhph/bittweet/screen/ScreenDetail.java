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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.Order;
import fpt.anhdhph.bittweet.model.OrderDetail;
import fpt.anhdhph.bittweet.model.Product;
import fpt.anhdhph.bittweet.model.CartItem;

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

        // Lấy thông tin sản phẩm từ Intent
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

            // Mặc định chọn size S
            rgSize.check(rbSizeS.getId());
            price.setText(product.getSPrice() + "đ");

            // Lắng nghe sự thay đổi size để cập nhật giá
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

    // Hàm thêm sản phẩm vào giỏ hàng
    void themGioHang() {

        btnAddToCart.setOnClickListener(v -> {
            String selectedSize;
            String selectedPrice = product.getSPrice(); // mặc định là S

            if (rgSize.getCheckedRadioButtonId() == rbSizeM.getId()) {
                selectedSize = "M";
                selectedPrice = product.getMPrice();
            } else if (rgSize.getCheckedRadioButtonId() == rbSizeL.getId()) {
                selectedSize = "L";
                selectedPrice = product.getLPrice();
            } else {
                selectedSize = "S";
            }

            SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
            String userId = prefs.getString("documentId", null);

            if (userId == null) {
                Log.e("ScreenDetail", "Người dùng chưa đăng nhập!");
                return;
            }

            OrderDetail orderDetail = new OrderDetail(
                    null,
                    product.getId(),
                    product.getProName(),
                    product.getImage(),
                    selectedSize,
                    selectedPrice,
                    1,
                    userId
            );

            db.collection("OrdersDetail")
                    .add(orderDetail)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("ScreenDetail", "Thêm vào Firestore thành công, id: " + documentReference.getId());
                        Toast.makeText(ScreenDetail.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ScreenDetail", "Lỗi khi thêm vào Firestore: " + e.getMessage());
                        Toast.makeText(ScreenDetail.this, "Thêm vào giỏ hàng thất bại!", Toast.LENGTH_SHORT).show();
                    });
        });


    }

}
