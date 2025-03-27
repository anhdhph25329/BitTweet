package fpt.anhdhph.bittweet.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import fpt.anhdhph.bittweet.R;

public class ScreenLogin extends AppCompatActivity {
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

    }

    public void dangNhap(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScreenLogin.this, ScreenHome.class);
                startActivity(intent);
                finish();
            }
        });
    }
}