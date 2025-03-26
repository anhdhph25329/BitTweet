package fpt.anhdhph.bittweet.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fpt.anhdhph.bittweet.R;

public class ScreenLogin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button btnLogin;
    TextView tvRecover, tvRegister;
    TextInputEditText edtTele, edtPass;
    CheckBox cbRemember;
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

        dangNhap();

        tvRegister.setOnClickListener(view -> {
            Intent intent = new Intent(ScreenLogin.this, ScreenRegister.class);
            startActivity(intent);
        });

        tvRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScreenLogin.this, ScreenLogin.class);
                startActivity(intent);
            }
        });

    }

    public void anhXa(){
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvRecover = findViewById(R.id.tvRecover);
        edtTele = findViewById(R.id.edtTele);
        edtPass = findViewById(R.id.edtPass);
        cbRemember = findViewById(R.id.cbRemember);
        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

    }

    public void dangNhap(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }
    public void loginUser(){
        String email = edtTele.getText().toString().trim();
        String pasword = edtPass.getText().toString().trim();
        // Kiem tra du lieu nhap vao
        if(email.isEmpty()){
            edtTele.setError("Vui long nhap email");
            return;

        }
        if(pasword.isEmpty()){
            edtPass.setError("Vui long nhap mat khau");
            return;
        }
        if(pasword.length()<6){
            edtPass.setError("mat khau phai co it nhat 6 ky tu");
            return;
        }
        mAuth.signInWithEmailAndPassword(email,pasword).addOnCompleteListener(ScreenLogin.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(ScreenLogin.this, "Đăng nhap thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ScreenLogin.this, ScreenLogin.class));
                    finish(); // Đóng màn hình đăng nhập
                }else{
                    Toast.makeText(ScreenLogin.this, "Lỗi:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}