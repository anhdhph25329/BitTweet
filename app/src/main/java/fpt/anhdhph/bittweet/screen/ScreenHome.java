package fpt.anhdhph.bittweet.screen;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.fragment.FragClient;
import fpt.anhdhph.bittweet.fragment.FragIncome;
import fpt.anhdhph.bittweet.fragment.FragOrder;
import fpt.anhdhph.bittweet.fragment.FragProduct;
import fpt.anhdhph.bittweet.fragment.FragProfile;

public class ScreenHome extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;

    FragmentManager fm;

    FragProfile fragProfile;
    FragClient fragClient;
    FragProduct fragProduct;
    FragIncome fragIncome;
    FragOrder fragOrder;


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

        anhXa();

        thanhCongCu();

        nhanMo();

    }

    void anhXa(){
        drawerLayout = findViewById(R.id.main);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_drawer);

        setSupportActionBar( toolbar );
        fm = getSupportFragmentManager();

        fragProfile = new FragProfile();
        fragClient = new FragClient();
        fragProduct = new FragProduct();
        fragIncome = new FragIncome();
        fragOrder = new FragOrder();
    }

    void thanhCongCu(){
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.chuoi_open,
                R.string.chuoi_close
        );
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener( drawerToggle );
    }

    void nhanMo(){
        fm.beginTransaction().add(R.id.frag_container, fragProfile).commit();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.mnu_profile){
                    fm.beginTransaction().replace(R.id.frag_container, fragProfile).commit();
                    toolbar.setTitle("Profile");
                }else if(menuItem.getItemId() == R.id.mnu_managecli){
                    fm.beginTransaction().replace(R.id.frag_container, fragClient).commit();
                    toolbar.setTitle("Manage Client");
                }else if(menuItem.getItemId() == R.id.mnu_managepro){
                    fm.beginTransaction().replace(R.id.frag_container, fragProduct).commit();
                    toolbar.setTitle("Manage Product");
                }else if(menuItem.getItemId() == R.id.mnu_manageincome){
                    fm.beginTransaction().replace(R.id.frag_container, fragIncome).commit();
                    toolbar.setTitle("Manage Income");
                }else if(menuItem.getItemId() == R.id.mnu_manageorder){
                    fm.beginTransaction().replace(R.id.frag_container, fragOrder).commit();
                    toolbar.setTitle("Manage Order");
                }
                drawerLayout.close();
                return true;
            }
        });
    }

}