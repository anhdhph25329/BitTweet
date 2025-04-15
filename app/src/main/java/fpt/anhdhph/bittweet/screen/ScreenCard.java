package fpt.anhdhph.bittweet.screen;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ScreenCard.this, ScreenPaymentDone.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_out_top,R.anim.slide_out_down);
                finish();
            }
        }, 6000);

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
                qrCodeImg.setImageBitmap(null);
                generatedQR = null;
            }
        }.start();
    }

    private void saveQRCodeToStorage() {
        if (qrBitmap == null) {
            Toast.makeText(this, "Không có mã QR để lưu!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String fileName = "QRCode_" + System.currentTimeMillis() + ".png";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
                Toast.makeText(this, "Đã lưu mã QR vào thư viện ảnh!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lưu thất bại!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void checkQRCode(String scannedCode) {
        if (scannedCode != null && scannedCode.equals(generatedQR)) {
            Intent successIntent = new Intent(this, ScreenPaymentDone.class);
            startActivity(successIntent);
            finish();
        } else {
            Toast.makeText(this, "Mã QR không hợp lệ hoặc đã hết hạn!", Toast.LENGTH_SHORT).show();
        }
    }

}