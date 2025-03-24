package fpt.anhdhph.bittweet.screen;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
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

import java.util.Calendar;

import fpt.anhdhph.bittweet.R;

public class ScreenRegister extends AppCompatActivity {
    FirebaseAuth mAuth;
    ImageView dpDob;
    TextInputEditText edtDob,edtEmail,edtPass;
    TextView tvLogin, tvRecover;
    Button btnSave;

    private ProgressDialog progressDialog;  // Hiển thị trạng thái loading
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhXa();

        dangKy();


        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScreenRegister.this, ScreenRecover1.class);
                startActivity(intent);
            }
        });

    }

    public void anhXa(){
        mAuth = FirebaseAuth.getInstance();
        edtEmail = findViewById(R.id.emailInput);
        edtPass = findViewById(R.id.passInput);
        dpDob = findViewById(R.id.dpDob);
        edtDob = findViewById(R.id.edtDob);
        tvLogin = findViewById(R.id.tvLogin);
        tvRecover = findViewById(R.id.tvRecover);
        btnSave = findViewById(R.id.btnSave);
    }

    public void dangKy(){
        dpDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(ScreenRegister.this,
                        (view1, selectedYear, selectedMonth, selectedDay) -> {
                            String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                            edtDob.setText(formattedDate);
                        }, year, month, day);

                datePicker.show();



            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng ký..."); // Thông báo loading
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });
    }
    public void signUpUser(){
        String email =edtEmail.getText().toString().trim();
        String passWord = edtPass.getText().toString().trim();
        //Kiem tra email va password không được để trống
        if (email.isEmpty() || passWord.isEmpty()) {
            Toast.makeText(ScreenRegister.this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra độ dài mật khẩu
        if (passWord.length() < 6) {
            Toast.makeText(ScreenRegister.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show(); // Hiển thị dialog loading

        mAuth.createUserWithEmailAndPassword(email,passWord).addOnCompleteListener(ScreenRegister.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss(); // Ẩn dialog loading khi xong

                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(ScreenRegister.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ScreenRegister.this, ScreenLogin.class));
                } else {
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Đăng ký thất bại";
                    Log.w(TAG, "Đăng ký thất bại", task.getException());
                    Toast.makeText(ScreenRegister.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}