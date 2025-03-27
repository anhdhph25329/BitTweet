package fpt.anhdhph.bittweet.screen;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.fragment.FragClient;
import fpt.anhdhph.bittweet.fragment.FragIncome;
import fpt.anhdhph.bittweet.fragment.FragOrder;
import fpt.anhdhph.bittweet.fragment.FragProduct;
import fpt.anhdhph.bittweet.fragment.FragProfile;

public class ScreenHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

}