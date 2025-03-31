package fpt.anhdhph.bittweet.screen;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

import fpt.anhdhph.bittweet.R;

public class ScreenProfile extends AppCompatActivity {

    Toolbar toolbar;
    ImageView dpDob;
    EditText edtName, edtDob, edtPhone, edtEmail, edtAddress, edtPass;
    Button btnSave;
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

        chinhSua();

    }

    void anhXa(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
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
    }

    void chinhSua(){
            dpDob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePicker = new DatePickerDialog(ScreenProfile.this,
                            (view1, selectedYear, selectedMonth, selectedDay) -> {
                                String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                                edtDob.setText(formattedDate);
                            }, year, month, day);
                    datePicker.show();
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ScreenProfile.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                }
            });
    }

}