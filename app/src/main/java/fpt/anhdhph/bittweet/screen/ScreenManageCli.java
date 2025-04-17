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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

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
    private SearchView searchViewCli;
    private FirebaseFirestore db;
    private ListenerRegistration userListener;

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

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quản lý khách hàng");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Ánh xạ view
        rvCli = findViewById(R.id.rvCli);
        searchViewCli = findViewById(R.id.search_view_cli);
        searchViewCli.setQueryHint("Nhập 3 số cuối khách hàng...");
        searchViewCli.setIconified(false);

        userDAO = new UserDAO();

        // Khởi tạo RecyclerView
        setupRecyclerView();
        setupRealTimeUpdates();
        setupSearchView();
    }

    private void setupSearchView() {
        EditText searchEditText = searchViewCli.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(Color.GRAY);

        searchViewCli.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    adapterManageCli.updateList(allUsers);
                } else {
                    searchUsers(newText.trim());
                }
                return true;
            }
        });
    }

    private void searchUsers(String query) {
        List<User> filtered = new ArrayList<>();
        for (User user : allUsers) {
            if ((user.getPhone() != null && user.getPhone().toLowerCase().contains(query.toLowerCase())) ||
                    (user.getName() != null && user.getName().toLowerCase().contains(query.toLowerCase())) ||
                    (user.getEmail() != null && user.getEmail().toLowerCase().contains(query.toLowerCase()))) {
                filtered.add(user);
            }
        }
        userList.clear();
        userList.addAll(filtered);
        adapterManageCli.notifyDataSetChanged();
    }

    private void setupRecyclerView() {
        rvCli.setLayoutManager(new LinearLayoutManager(this));
        adapterManageCli = new AdapterManageCli(this, userList); // Bỏ callback
        rvCli.setAdapter(adapterManageCli);
    }

    private void setupRealTimeUpdates() {
        if (userListener != null) {
            userListener.remove();
        }

        userListener = db.collection("Users")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Lỗi khi tải danh sách khách hàng: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        List<User> updatedUsers = new ArrayList<>();
                        for (var doc : value.getDocuments()) {
                            User user = doc.toObject(User.class);
                            user.setId(doc.getId());
                            updatedUsers.add(user);
                        }
                        synchronized (allUsers) {
                            allUsers.clear();
                            allUsers.addAll(updatedUsers);
                            if (searchViewCli.getQuery().toString().trim().isEmpty()) {
                                userList.clear();
                                userList.addAll(allUsers);
                            } else {
                                searchUsers(searchViewCli.getQuery().toString().trim());
                            }
                            adapterManageCli.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userListener != null) {
            userListener.remove();
        }
    }
}