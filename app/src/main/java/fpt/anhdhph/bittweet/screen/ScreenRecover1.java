package fpt.anhdhph.bittweet.screen;

import static android.widget.Toast.makeText;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;

import fpt.anhdhph.bittweet.R;

public class ScreenRecover1 extends AppCompatActivity {
    TextInputEditText edtEmail;
    FirebaseAuth mAuth;
    Button btnSendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_recover1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Khởi tạo Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        //Ánh xạ giao diện
        btnSendCode = findViewById(R.id.btnSendCode);
        edtEmail = findViewById(R.id.edtEmail);
        //Sự kiện click gửi email đặt lại mật khẩu
        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();

                // Kiểm tra email có rỗng không
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ScreenRecover1.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra định dạng email hợp lệ
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ScreenRecover1.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Hiển thị ProgressDialog khi gửi yêu cầu
                ProgressDialog progressDialog = new ProgressDialog(ScreenRecover1.this);
                progressDialog.setMessage("Đang gửi email khôi phục...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Gửi email đặt lại mật khẩu qua Firebase
                mAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(aVoid -> {
                            progressDialog.dismiss();
                            makeText(ScreenRecover1.this,
                                    "Email khôi phục đã gửi! Vui lòng kiểm tra hộp thư.",
                                    Toast.LENGTH_LONG).show();

                            // Chuyển sang màn hình thông báo hoặc màn hình chính
                            Intent intent = new Intent(ScreenRecover1.this, ScreenLogin.class);
                            startActivity(intent);
                            finish(); // Đóng màn hình hiện tại
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(ScreenRecover1.this,
                                    "Lỗi: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            // Ở lại màn hình hiện tại nếu có lỗi
                        });
            }
        });

    }



}