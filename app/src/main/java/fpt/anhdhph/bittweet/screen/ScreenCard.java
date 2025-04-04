package fpt.anhdhph.bittweet.screen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import fpt.anhdhph.bittweet.R;

public class ScreenCard extends AppCompatActivity {

    ImageView qrCodeImg;
    String generatedQR;
    TextView tvCountDown;
    Button btnDownload;
    Bitmap qrBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_card);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        qrCodeImg = findViewById(R.id.qrCodeImg);
        tvCountDown = findViewById(R.id.tvCountDown);
        btnDownload = findViewById(R.id.btnDownload);

        setGeneratedQR();
        startCountdownTimer();


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQRCodeToStorage();
            }
        });


    }

    private void setGeneratedQR() {
        generatedQR = UUID.randomUUID().toString();
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(generatedQR, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder encoder = new BarcodeEncoder();
            qrBitmap = encoder.createBitmap(bitMatrix);

            qrCodeImg.setImageBitmap(qrBitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi tạo mã QR", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void startCountdownTimer() {
        new CountDownTimer(60000, 1000) { // 60 giây
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                tvCountDown.setText("Mã QR tồn tại trong: " + String.format("%02d:%02d", seconds / 60, seconds % 60));
            }
            public void onFinish() {
                tvCountDown.setText("Mã QR đã hết hạn!");
                Toast.makeText(ScreenCard.this, "Mã QR đã hết hạn!", Toast.LENGTH_SHORT).show();
                qrCodeImg.setImageBitmap(null);
            }
        }.start();
    }

    private void saveQRCodeToStorage() {
        if (qrBitmap == null) {
            Toast.makeText(this, "Không có mã QR để lưu!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File qrFile = new File(path, "QRCode_" + System.currentTimeMillis() + ".png");

            FileOutputStream outputStream = new FileOutputStream(qrFile);
            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Toast.makeText(this, "Đã lưu mã QR vào: " + qrFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Lưu thất bại!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void checkQRCode(String scannedCode) {
        if (scannedCode.equals(generatedQR)) {
            Intent successIntent = new Intent(this, ScreenPaymentDone.class);
            startActivity(successIntent);
            finish();
        } else {
            Toast.makeText(this, "Mã QR không hợp lệ hoặc đã hết hạn!", Toast.LENGTH_SHORT).show();
        }
    }

}