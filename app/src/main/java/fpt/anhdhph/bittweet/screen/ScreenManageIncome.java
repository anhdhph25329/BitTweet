package fpt.anhdhph.bittweet.screen;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fpt.anhdhph.bittweet.R;

public class ScreenManageIncome extends AppCompatActivity {

    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private Button btn_tungay, btn_denngay, btn_doanhthu;
    private EditText edt_tungay, edt_denngay;
    private TextView tv_doanhthu;
    private FirebaseFirestore db;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat sdfFirestore = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

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
            finish();
            return;
        }

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        anhXa();
        layNgay();
        doanhThu();
    }

    void anhXa() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quản lý doanh thu");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        btn_tungay = findViewById(R.id.btn_tungay);
        btn_denngay = findViewById(R.id.btn_denngay);
        btn_doanhthu = findViewById(R.id.btn_doanhthu);
        edt_tungay = findViewById(R.id.edt_tungay);
        edt_denngay = findViewById(R.id.edt_denngay);
        tv_doanhthu = findViewById(R.id.tv_doanhthu);
    }

    void layNgay() {
        btn_tungay.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            String dateText = edt_tungay.getText().toString().trim();
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
        });

        btn_denngay.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            String dateText = edt_denngay.getText().toString().trim();
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
        });
    }

    void doanhThu() {
        btn_doanhthu.setOnClickListener(v -> {
            String fromDateStr = edt_tungay.getText().toString().trim();
            String toDateStr = edt_denngay.getText().toString().trim();

            // Kiểm tra nếu ngày trống
            if (fromDateStr.isEmpty() || toDateStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn cả ngày bắt đầu và ngày kết thúc!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra định dạng ngày
            Date fromDate, toDate;
            try {
                fromDate = sdf.parse(fromDateStr);
                toDate = sdf.parse(toDateStr);
            } catch (ParseException e) {
                Toast.makeText(this, "Định dạng ngày không hợp lệ! (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra ngày hợp lệ (từ ngày không được sau đến ngày)
            if (fromDate.after(toDate)) {
                Toast.makeText(this, "Ngày bắt đầu không được sau ngày kết thúc!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đặt thời gian cho toDate là cuối ngày (23:59:59)
            Calendar toDateCal = Calendar.getInstance();
            toDateCal.setTime(toDate);
            toDateCal.set(Calendar.HOUR_OF_DAY, 23);
            toDateCal.set(Calendar.MINUTE, 59);
            toDateCal.set(Calendar.SECOND, 59);
            toDate = toDateCal.getTime();

            // Tính doanh thu từ Firestore
            calculateIncome(fromDate, toDate);
        });
    }

    private void calculateIncome(Date fromDate, Date toDate) {
        db.collectionGroup("Orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalIncome = 0;
                    int orderCount = 0;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String orderDateStr = document.getString("orderDate");
                        String totalPriceStr = document.getString("totalPrice");

                        if (orderDateStr == null || totalPriceStr == null) {
                            continue;
                        }

                        try {
                            Date orderDate = sdfFirestore.parse(orderDateStr);
                            if (orderDate != null && !orderDate.before(fromDate) && !orderDate.after(toDate)) {
                                double totalPrice = Double.parseDouble(totalPriceStr);
                                totalIncome += totalPrice;
                                orderCount++;
                            }
                        } catch (ParseException e) {
                            Log.e("ScreenManageIncome", "Lỗi parse ngày: " + e.getMessage());
                        } catch (NumberFormatException e) {
                            Log.e("ScreenManageIncome", "Lỗi parse totalPrice: " + e.getMessage());
                        }
                    }

                    // Hiển thị kết quả
                    if (orderCount == 0) {
                        tv_doanhthu.setText("Doanh thu: 0 VNĐ (Không có đơn hàng)");
                    } else {
                        DecimalFormat df = new DecimalFormat("#,### VNĐ");
                        tv_doanhthu.setText("Doanh thu:\n\n" + df.format(totalIncome) + " (" + orderCount + " đơn hàng)");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ScreenManageIncome", "Lỗi khi lấy đơn hàng: " + e.getMessage());
                    Toast.makeText(this, "Lỗi khi tính doanh thu!", Toast.LENGTH_SHORT).show();
                    tv_doanhthu.setText("Doanh thu: 0 VNĐ (Lỗi)");
                });
    }
}