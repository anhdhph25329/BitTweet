package fpt.anhdhph.bittweet.screen;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import fpt.anhdhph.bittweet.R;

public class ScreenRegister extends AppCompatActivity {

    ImageView dpDob;
    TextInputEditText edtDob;
    TextView tvLogin, tvRecover;
    Button btnSave;
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


    }

}