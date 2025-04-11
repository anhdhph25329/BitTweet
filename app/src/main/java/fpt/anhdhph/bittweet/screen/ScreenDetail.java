package fpt.anhdhph.bittweet.screen;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.Product;

public class ScreenDetail extends AppCompatActivity {

    private TextView name, desc, category, price;
    private ImageView imgProduct;
    private Product product;
    private RadioGroup rgSize;
    private RadioButton rbSizeS, rbSizeM, rbSizeL;
    private Button btnAddToCart;

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

        product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            name.setText(product.getProName());
            desc.setText(product.getDes());

            category.setText("Loại: " + product.getCategory());

            Glide.with(this)
                    .load(product.getImage())
                    .into(imgProduct);

            // Default chọn size S
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
}
