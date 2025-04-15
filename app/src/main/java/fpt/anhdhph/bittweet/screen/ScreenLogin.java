package fpt.anhdhph.bittweet.screen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fpt.anhdhph.bittweet.R;

public class ScreenLogin extends AppCompatActivity {
    Button btnLogin;
    TextView tvRecover, tvRegister;
    TextInputEditText edtTele, edtPass;
    CheckBox cbRemember;
    FirebaseFirestore db;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhXa();

        kiemTraGhiNho();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangNhap();
            }
        });

        tvRegister.setOnClickListener(view -> {
            Intent intent = new Intent(ScreenLogin.this, ScreenRegister.class);
            startActivity(intent);
        });

        tvRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScreenLogin.this, ScreenRecover1.class);
                startActivity(intent);
                Toast.makeText(ScreenLogin.this, "", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Hàm ánh xạ
    void anhXa() {
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvRecover = findViewById(R.id.tvRecover);
        edtTele = findViewById(R.id.edtTele);
        edtPass = findViewById(R.id.edtPass);
        cbRemember = findViewById(R.id.cbRemember);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
    }

    // Hàm kiểm tra ghi nhớ
    void kiemTraGhiNho() {
        String savedTele = sharedPreferences.getString("tele", "");
        String savedPass = sharedPreferences.getString("pass", "");
        if (!savedTele.isEmpty() && !savedPass.isEmpty()) {
            Intent intent = new Intent(ScreenLogin.this, ScreenHome.class);
            startActivity(intent);
            finish();
        }
    }

    // Hàm đăng nhập
    void dangNhap() {
        String tele = edtTele.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();

        if (tele.isEmpty()) {
            edtTele.setError("Vui lòng nhập email hoặc số điện thoại");
            return;
        }
        if (pass.isEmpty()) {
            edtPass.setError("Vui lòng nhập mật khẩu");
            return;
        }
        if (pass.length() < 6) {
            edtPass.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        // Kiểm tra trong collection Users trước
        db.collection("Users")
                .whereEqualTo("email", tele)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        checkPassword(document, pass);
                    } else {
                        // Không tìm thấy qua email, kiểm tra qua phone trong Users
                        db.collection("Users")
                                .whereEqualTo("phone", tele)
                                .get()
                                .addOnSuccessListener(phoneQuery -> {
                                    if (!phoneQuery.isEmpty()) {
                                        DocumentSnapshot document = phoneQuery.getDocuments().get(0);
                                        checkPassword(document, pass);
                                    } else {
                                        // Không tìm thấy trong Users, kiểm tra trong Admin
                                        db.collection("Admin")
                                                .whereEqualTo("tele", tele)
                                                .get()
                                                .addOnSuccessListener(adminQuery -> {
                                                    if (!adminQuery.isEmpty()) {
                                                        DocumentSnapshot document = adminQuery.getDocuments().get(0);
                                                        checkPassword(document, pass);
                                                    } else {
                                                        Toast.makeText(ScreenLogin.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(ScreenLogin.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ScreenLogin.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ScreenLogin.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    void checkPassword(DocumentSnapshot document, String inputPassword) {
        // Lấy password từ Firestore (có thể là "password" hoặc "pass")
        String storedPassword = document.getString("password");
        if (storedPassword == null) {
            storedPassword = document.getString("pass");
        }

        // So sánh với password người dùng nhập vào
        if (storedPassword != null && storedPassword.equals(inputPassword)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Nếu tick vào checkbox nhớ mật khẩu
            if (cbRemember.isChecked()) {
                editor.putString("tele", edtTele.getText().toString().trim());
                editor.putString("pass", edtPass.getText().toString().trim());
            }

            // Lấy thông tin người dùng từ document
            String documentId = document.getId();

            // Dùng hàm phụ để lấy dữ liệu an toàn
            String name = getSafeField(document, "name");
            String gender = getSafeField(document, "gender");
            String birthdate = getSafeField(document, "birthdate");
            String phone = getSafeField(document, "phone");
            String email = getSafeField(document, "email");
            String address = getSafeField(document, "address");

            // Nếu thiếu phone hoặc email, dùng tele để fallback
            String tele = getSafeField(document, "tele");
            if (phone.isEmpty()) phone = tele;
            if (email.isEmpty()) email = tele;

            // Xác định vai trò dựa vào collection
            String role = document.getReference().getParent().getId().equals("Admin") ? "admin" : "user";

            // Lưu toàn bộ thông tin vào SharedPreferences
            editor.putString("documentId", documentId);
            editor.putString("name", name);
            editor.putString("gender", gender);
            editor.putString("birthdate", birthdate);
            editor.putString("phone", phone);
            editor.putString("email", email);
            editor.putString("address", address);
            editor.putString("password", storedPassword);
            editor.putString("role", role);
            editor.putBoolean("isLoggedIn", true);

            // ⚠️ Gọi apply sau cùng để lưu tất cả
            editor.apply();

            Log.d("Login", "✅ Đã lưu documentId và thông tin người dùng vào SharedPreferences: " + documentId);

            // Chuyển sang màn hình chính
            Intent intent = new Intent(ScreenLogin.this, ScreenHome.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm phụ để lấy field an toàn (tránh null)
    private String getSafeField(DocumentSnapshot document, String key) {
        String value = document.getString(key);
        return value != null ? value : "";
    }

}