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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
            }
        });

    }

    void anhXa(){
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvRecover = findViewById(R.id.tvRecover);
        edtTele = findViewById(R.id.edtTele);
        edtPass = findViewById(R.id.edtPass);
        cbRemember = findViewById(R.id.cbRemember);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
    }

    void kiemTraGhiNho(){
        String savedTele = sharedPreferences.getString("tele", "");
        String savedPass = sharedPreferences.getString("pass", "");
        if (!savedTele.isEmpty() && !savedPass.isEmpty()) {
            Intent intent = new Intent(ScreenLogin.this, ScreenHome.class);
            startActivity(intent);
            finish();
        }
    }

    void dangNhap(){
        String tele = edtTele.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        if (tele.isEmpty() || pass.isEmpty()) {
            Toast.makeText(ScreenLogin.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
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
                                        Toast.makeText(ScreenLogin.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    void checkPassword(DocumentSnapshot document, String inputPassword) {
        String storedPassword = document.getString("password");
        if (storedPassword != null && storedPassword.equals(inputPassword)) {
            if (cbRemember.isChecked()) {
                sharedPreferences.edit()
                        .putString("tele", edtTele.getText().toString().trim())
                        .putString("pass", edtPass.getText().toString().trim())
                        .apply();
            }
            Intent intent = new Intent(ScreenLogin.this, ScreenHome.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }
}