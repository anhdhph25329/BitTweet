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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import fpt.anhdhph.bittweet.R;

public class ScreenRecover1 extends AppCompatActivity {

    Button btnXacNhan;
    TextInputEditText edtTele;
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

        btnXacNhan = findViewById(R.id.btnXacNhan);
        edtTele = findViewById(R.id.edtTele);

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String input = edtTele.getText().toString().trim();
                if (input.isEmpty()) {
                    Toast.makeText(ScreenRecover1.this, "Vui lòng nhập email hoặc số điện thoại",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference usersRef = db.collection("Users");

                usersRef.whereEqualTo("email", input)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                Intent intent = new Intent(ScreenRecover1.this, ScreenRecover2.class);
                                intent.putExtra("userId", task.getResult().getDocuments().get(0).getId());
                                startActivity(intent);
                                finish();
                            } else {
                                usersRef.whereEqualTo("phone", input)
                                        .get()
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful() && !task2.getResult().isEmpty()) {
                                                Intent intent = new Intent(ScreenRecover1.this,
                                                        ScreenRecover2.class);
                                                intent.putExtra("userId",
                                                        task2.getResult().getDocuments().get(0).getId());
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(ScreenRecover1.this, "Tài khoản không tồn tại",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });

            }
        });

    }
}