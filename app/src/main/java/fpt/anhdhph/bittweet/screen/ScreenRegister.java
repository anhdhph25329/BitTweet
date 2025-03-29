package fpt.anhdhph.bittweet.screen;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.User;

public class ScreenRegister extends AppCompatActivity {
    ImageView dpDob;
    TextInputEditText edtName, edtDob, edtEmail, edtPass, edtPhone, edtAddress;
    TextView tvLogin, tvRecover;
    Button btnSignUp;
    RadioGroup rgGender;

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

        datePick();

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
                finish();
            }
        });

    }

    // Hàm ánh xạ
    public void anhXa(){
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        dpDob = findViewById(R.id.dpDob);
        edtDob = findViewById(R.id.edtDob);
        tvLogin = findViewById(R.id.tvLogin);
        tvRecover = findViewById(R.id.tvRecover);
        btnSignUp = findViewById(R.id.btnSignUp);
        rgGender = findViewById(R.id.rgGender);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
    }

    // Hàm chọn ngày sinh
    void datePick() {
        dpDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                String dateText = edtDob.getText().toString().trim();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                sdf.setLenient(false);

                if (!dateText.isEmpty()) {
                    try {
                        Date date = sdf.parse(dateText);
                        calendar.setTime(date);
                        edtDob.setError(null);
                    } catch (ParseException e) {
                        edtDob.setError("Ngày không hợp lệ! (dd/MM/yyyy)");
                        return;
                    }
                }

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(
                        ScreenRegister.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                    selectedDay, selectedMonth + 1, selectedYear);
                            edtDob.setText(formattedDate);
                            edtDob.setError(null);
                        }, year, month, day);

                datePicker.show();
            }
        });
    }

    // Hàm đăng ký
    public void dangKy(){
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String name = edtName.getText().toString();
                int selectedId = rgGender.getCheckedRadioButtonId();
                String gender = "";
                if (selectedId == R.id.rbMale) {
                    gender = "Nam";
                } else if (selectedId == R.id.rbFemale) {
                    gender = "Nữ";
                }
                String birthdate = edtDob.getText().toString();
                String phone = edtPhone.getText().toString();
                String email = edtEmail.getText().toString();
                String address = edtAddress.getText().toString();
                String password = edtPass.getText().toString();

                if (name.isEmpty()) {
                    edtName.setError("Vui lòng nhập họ và tên");
                    return;
                }
                if (selectedId == -1) {
                    Toast.makeText(ScreenRegister.this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!birthdate.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
                    edtDob.setError("Ngày sinh không hợp lệ");
                    return;
                }
                if (!phone.matches("^0\\\\d{8,10}$")) {
                    edtPhone.setError("Số điện thoại không hợp lệ");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtEmail.setError("Email không hợp lệ");
                    return;
                }
                if (address.isEmpty()) {
                    edtAddress.setError("Vui lòng nhập địa chỉ");
                    return;
                }
                if (password.length() < 6) {
                    edtPass.setError("Mật khẩu phải có ít nhất 6 ký tự");
                    return;
                }

                Map<String, Object> users = new HashMap<>();
                users.put("name", name);
                users.put("gender", gender);
                users.put("birthdate", birthdate);
                users.put("phone", phone);
                users.put("email", email);
                users.put("address", address);
                users.put("password", password);

                db.collection("Users").document(email)
                        .set(users)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", "Đăng ký thành công!");
                            Toast.makeText(ScreenRegister.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Lỗi khi đăng ký", e);
                            Toast.makeText(ScreenRegister.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                finish();
            }

        });



    }
}