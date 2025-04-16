package fpt.anhdhph.bittweet.screen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    private List<User> allUsers = new ArrayList<>();
    private androidx.appcompat.widget.SearchView searchViewCli;
    private FirebaseFirestore db;


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

        // Sử dụng biến searchView đã khai báo ở lớp, không khai báo lại
        searchViewCli= findViewById(R.id.search_view_cli);
        searchViewCli.setQueryHint("Tìm kiếm khách hàng...");
        searchViewCli.setIconified(false);

        // Khởi tạo RecyclerView
        setupRecyclerView();
        getData();
        setupSearchView();


    }
    private void setupSearchView() {
        // Đổi màu hint nếu cần
        EditText searchEditText = searchViewCli.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(Color.GRAY);

        searchViewCli.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchByPhoneSuffix(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    adapterManageCli.updateList(allUsers);
                } else {
                    searchByPhoneSuffix(newText.trim());
                }
                return true;
            }
        });
    }

    private void searchByPhoneSuffix(String suffix) {
        List<User> filtered = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getPhone() != null && user.getPhone().endsWith(suffix)) {
                filtered.add(user);
            }
        }
        adapterManageCli.updateList(filtered);
    }

    private void fetchAllUsers() {
        db.collection("Users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allUsers.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        User user = doc.toObject(User.class);
                        allUsers.add(user);
                    }
                    adapterManageCli.updateList(allUsers);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi khi tải danh sách khách hàng", Toast.LENGTH_SHORT).show()
                );
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
                allUsers.clear();         // <--- Thêm dòng này
                allUsers.addAll(users);   // <--- Và dòng này
                adapterManageCli.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ScreenManageCli.this, "Lỗi khi tải khách hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
