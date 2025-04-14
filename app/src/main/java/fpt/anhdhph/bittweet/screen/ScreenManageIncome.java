package fpt.anhdhph.bittweet.screen;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fpt.anhdhph.bittweet.R;

public class ScreenManageIncome extends AppCompatActivity {

    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    Button btn_tungay, btn_denngay, btn_doanhthu;
    EditText edt_tungay, edt_denngay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_manage_income);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "user");
        if (!role.equals("admin")) {
            Toast.makeText(this, "Bạn không có quyền truy cập ở đây", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        anhXa();

        layNgay();

        doanhThu();

    }

    void anhXa(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quản lý doanh thu");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_tungay = findViewById(R.id.btn_tungay);
        btn_denngay = findViewById(R.id.btn_denngay);
        btn_doanhthu = findViewById(R.id.btn_doanhthu);
        edt_tungay = findViewById(R.id.edt_tungay);
        edt_denngay = findViewById(R.id.edt_denngay);
    }

    void layNgay(){
        btn_tungay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                String dateText = edt_tungay.getText().toString().trim();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                sdf.setLenient(false);

                if (!dateText.isEmpty()) {
                    try {
                        Date date = sdf.parse(dateText);
                        calendar.setTime(date);
                        edt_tungay.setError(null);
                    } catch (ParseException e) {
                        edt_tungay.setError("Ngày không hợp lệ! (dd/MM/yyyy)");
                        return;
                    }
                }

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(
                        ScreenManageIncome.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                    selectedDay, selectedMonth + 1, selectedYear);
                            edt_tungay.setText(formattedDate);
                            edt_tungay.setError(null);
                        }, year, month, day);

                datePicker.show();
            }
        });

        btn_denngay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                String dateText = edt_denngay.getText().toString().trim();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                sdf.setLenient(false);

                if (!dateText.isEmpty()) {
                    try {
                        Date date = sdf.parse(dateText);
                        calendar.setTime(date);
                        edt_denngay.setError(null);
                    } catch (ParseException e) {
                        edt_denngay.setError("Ngày không hợp lệ! (dd/MM/yyyy)");
                        return;
                    }
                }

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(
                        ScreenManageIncome.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                    selectedDay, selectedMonth + 1, selectedYear);
                            edt_denngay.setText(formattedDate);
                            edt_denngay.setError(null);
                        }, year, month, day);

                datePicker.show();
            }
        });
    }

    void doanhThu(){
        btn_doanhthu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ScreenManageIncome.this, "Chưa có doanh thu đâu mà tính như thật :)", Toast.LENGTH_SHORT).show();
            }
        });
    }

}