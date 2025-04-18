package fpt.anhdhph.bittweet.screen;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fpt.anhdhph.bittweet.R;

public class ScreenProfile extends AppCompatActivity {

    Toolbar toolbar;
    ImageView dpDob;
    EditText edtName, edtDob, edtPhone, edtEmail, edtAddress, edtPass;
    Button btnSave;
    RadioButton rbMale, rbFemale;;
    RadioGroup rgGender;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhXa();

        getData();

        datePick();

        loadUserData();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
            }
        });

    }

    void anhXa(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Thông tin cá nhân");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dpDob = findViewById(R.id.dpDob);
        edtName = findViewById(R.id.edtName);
        edtDob = findViewById(R.id.edtDob);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        edtPass = findViewById(R.id.edtPass);
        btnSave = findViewById(R.id.btnSave);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rgGender = findViewById(R.id.rgGender);
    }

    void getData(){
        sharedPreferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String name = sharedPreferences.getString("name", "");
            String gender = sharedPreferences.getString("gender", "");
            String birthdate = sharedPreferences.getString("birthdate", "");
            String phone = sharedPreferences.getString("phone", "");
            String email = sharedPreferences.getString("email", "");
            String address = sharedPreferences.getString("address", "");
            String password = sharedPreferences.getString("password", "");

            edtName.setText(name);
            edtDob.setText(birthdate);
            edtPhone.setText(phone);
            edtEmail.setText(email);
            edtAddress.setText(address);
            edtPass.setText(password);

            if ("Nam".equals(gender)) {
                rbMale.setChecked(true);
                rbFemale.setChecked(false);
            } else if ("Nữ".equals(gender)) {
                rbFemale.setChecked(true);
                rbMale.setChecked(false);
            } else {
                rbMale.setChecked(false);
                rbFemale.setChecked(false);
            }
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để xem thông tin!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String name = sharedPreferences.getString("name", "");
            String gender = sharedPreferences.getString("gender", "");
            String birthdate = sharedPreferences.getString("birthdate", "");
            String phone = sharedPreferences.getString("phone", "");
            String email = sharedPreferences.getString("email", "");
            String address = sharedPreferences.getString("address", "");
            String password = sharedPreferences.getString("password", "");

            displayUserData(name, gender, birthdate, phone, email, address, password);
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để xem thông tin!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayUserData(String name, String gender, String birthdate, String phone, String email,
                                 String address, String password) {
        edtName.setText(name);
        edtDob.setText(birthdate);
        edtPhone.setText(phone);
        edtEmail.setText(email);
        edtAddress.setText(address);
        edtPass.setText(password);

        if ("Nam".equals(gender)) {
            rbMale.setChecked(true);
            rbFemale.setChecked(false);
        } else if ("Nữ".equals(gender)) {
            rbFemale.setChecked(true);
            rbMale.setChecked(false);
        } else {
            rbMale.setChecked(false);
            rbFemale.setChecked(false);
        }
    }

    void datePick(){
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
                        ScreenProfile.this,
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

    void updateUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String name = edtName.getText().toString().trim();
        int selectedId = rbMale.isChecked() ? R.id.rbMale : (rbFemale.isChecked() ? R.id.rbFemale : -1);
        String gender = selectedId == R.id.rbMale ? "Nam" : (selectedId == R.id.rbFemale ? "Nữ" : "");
        String birthdate = edtDob.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String password = edtPass.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Vui lòng nhập họ và tên");
            return;
        }
        if (!birthdate.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            edtDob.setError("Ngày sinh không hợp lệ");
            return;
        }

        String regex = "^(0[3|5|7|8|9])\\d{8,10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone);
        if (!matcher.matches()) {
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

        String documentId = sharedPreferences.getString("documentId", null);
        if (documentId != null) {
            db.collection("Users").document(documentId)
                    .update(users)
                    .addOnSuccessListener(aVoid -> {
                        // Cập nhật lại SharedPreferences sau khi update
                        sharedPreferences.edit()
                                .putString("name", name)
                                .putString("gender", gender)
                                .putString("birthdate", birthdate)
                                .putString("phone", phone)
                                .putString("email", email)
                                .putString("address", address)
                                .putString("password", password)
                                .apply();
                        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(),
                            Toast.LENGTH_LONG).show());
        } else {
            Toast.makeText(this, "Không tìm thấy ID tài khoản!", Toast.LENGTH_SHORT).show();
        }
    }

}