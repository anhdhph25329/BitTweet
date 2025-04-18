package fpt.anhdhph.bittweet.screen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
                        db.collection("Users")
                                .whereEqualTo("phone", tele)
                                .get()
                                .addOnSuccessListener(phoneQuery -> {
                                    if (!phoneQuery.isEmpty()) {
                                        DocumentSnapshot document = phoneQuery.getDocuments().get(0);
                                        checkPassword(document, pass);
                                    } else {
                                        // Nếu không tìm thấy trong collection Users, kiểm tra trong collection Admin
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
        String storedPassword = document.getString("password") != null ? document.getString("password") : document.getString("pass");

        if (storedPassword != null && storedPassword.equals(inputPassword)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (cbRemember.isChecked()) {
                editor.putString("tele", edtTele.getText().toString().trim());
                editor.putString("pass", edtPass.getText().toString().trim());
            }

            String documentId = document.getId();
            String name = document.getString("name") != null ? document.getString("name") : "";
            String gender = document.getString("gender") != null ? document.getString("gender") : "";
            String birthdate = document.getString("birthdate") != null ? document.getString("birthdate") : "";
            String phone = document.getString("phone") != null ? document.getString("phone") : document.getString("tele") != null ? document.getString("tele") : "";
            String email = document.getString("email") != null ? document.getString("email") : document.getString("tele") != null ? document.getString("tele") : "";
            String address = document.getString("address") != null ? document.getString("address") : "";
            String password = storedPassword;

            // Lưu thông tin cơ bản vào SharedPreferences LoginPref
            editor.putString("documentId", documentId);
            editor.putString("name", name);
            editor.putString("gender", gender);
            editor.putString("birthdate", birthdate);
            editor.putString("phone", phone);
            editor.putString("email", email);
            editor.putString("address", address);
            editor.putString("password", password);
            editor.putBoolean("isLoggedIn", true);

            // Kiểm tra vai trò
            if (document.getReference().getParent().getId().equals("Admin")) {
                editor.putString("role", "admin");
            } else {
                editor.putString("role", "user");
            }
            editor.apply();

            // Lưu documentId vào MyAppPrefs làm user_id
            SharedPreferences myAppPrefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor myAppEditor = myAppPrefs.edit();
            myAppEditor.putString("user_id", documentId);
            myAppEditor.apply();

            Intent intent = new Intent(ScreenLogin.this, ScreenHome.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }
}