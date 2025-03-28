package fpt.anhdhph.bittweet.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import fpt.anhdhph.bittweet.R;

public class ScreenRecover2 extends AppCompatActivity {

    Button btnConfirm;
    TextInputEditText edtPass1, edtPass2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_recover2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnConfirm = findViewById(R.id.btnConfirm);
        edtPass1 = findViewById(R.id.edtPass1);
        edtPass2 = findViewById(R.id.edtPass2);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass1 = edtPass1.getText().toString().trim();
                String pass2 = edtPass2.getText().toString().trim();

                if (pass1.isEmpty() || pass2.isEmpty()) {
                    Toast.makeText(ScreenRecover2.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!pass1.equals(pass2)) {
                    Toast.makeText(ScreenRecover2.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pass1.length() < 6) {
                    Toast.makeText(ScreenRecover2.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = getIntent().getStringExtra("userId");
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users").document(userId)
                        .update("password", pass1)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ScreenRecover2.this, "Cập nhật mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ScreenRecover2.this, ScreenLogin.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(ScreenRecover2.this, "Lỗi khi cập nhật mật khẩu", Toast.LENGTH_SHORT).show());
            }
        });

    }
}