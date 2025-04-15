package fpt.anhdhph.bittweet.screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import fpt.anhdhph.bittweet.R;

public class ScreenPaymentDone extends AppCompatActivity {

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_payment_done);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Nút X
        TextView btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> navigateToHome());

        // Tự động sau 3 giây
        handler = new Handler();
        runnable = this::navigateToHome;
        handler.postDelayed(runnable, 10000);
    }

    private void navigateToHome() {
        Intent intent = new Intent(ScreenPaymentDone.this, ScreenHome.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
