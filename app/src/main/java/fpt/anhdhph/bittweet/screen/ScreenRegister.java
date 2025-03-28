package fpt.anhdhph.bittweet.screen;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.User;

public class ScreenRegister extends AppCompatActivity {
    ImageView dpDob;
    TextInputEditText edtName, edtDob, edtEmail, edtPass, edtPhone, edtAddress;
    TextView tvLogin, tvRecover;
    Button btnSignUp;
    User user;
    RadioGroup rgGender;
    RadioButton rbMale, rbFmale;

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
            }
        });

    }

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

    void datePick(){
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
    }

    public void dangKy(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                if(name.isEmpty() || birthdate.isEmpty() || phone.isEmpty() || email.isEmpty()
                || address.isEmpty() || password.isEmpty()){
                    Toast.makeText(ScreenRegister.this, "Fill the graph", Toast.LENGTH_SHORT).show();
                }else {
                    user = new User(name, gender, birthdate, phone, email, address, password);
                    db.collection("Users").document(email)
                            .set(user)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Đăng ký thành công!"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi đăng ký", e));
                    finish();
                }


            }
        });



    }
}