package fpt.anhdhph.bittweet.screen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import fpt.anhdhph.bittweet.DAO.UserDAO;
import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.AdapterManageCli;

import fpt.anhdhph.bittweet.model.User;

public class ScreenManageCli extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rvCli;
    List<User> userList = new ArrayList<>();
    AdapterManageCli adapterManageCli;
    UserDAO userDAO;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_manage_cli);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Kiểm tra vai trò
        sharedPreferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "user");
        if (!role.equals("admin")) {
            Toast.makeText(this, "Không có quyền truy cập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quản lý khách hàng");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Ánh xạ view
        rvCli = findViewById(R.id.rvCli);
        userDAO = new UserDAO();

        // Khởi tạo RecyclerView
        setupRecyclerView();
        getData();

    }

    private void setupRecyclerView() {
        rvCli.setLayoutManager(new LinearLayoutManager(this));
        adapterManageCli = new AdapterManageCli(this, userList, this::getData); // callback reload
        rvCli.setAdapter(adapterManageCli);
    }

    private void getData() {
        userDAO.getAllUsers(new UserDAO.UserCallback() {
            @Override
            public void onSuccess(List<User> users) {
                userList.clear();
                userList.addAll(users);
                adapterManageCli.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ScreenManageCli.this, "Lỗi khi tải khách hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
