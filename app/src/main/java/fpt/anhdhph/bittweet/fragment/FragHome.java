package fpt.anhdhph.bittweet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.ProductAdapter;
import fpt.anhdhph.bittweet.model.Product;
import fpt.anhdhph.bittweet.screen.ScreenDetail;

public class FragHome extends Fragment implements ProductAdapter.ProductClickListener {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> allProducts = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private androidx.appcompat.widget.SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_frag_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        // Sử dụng biến searchView đã khai báo ở lớp, không khai báo lại
        searchView = view.findViewById(R.id.search_view);
        searchView.setQueryHint("Tìm kiếm sản phẩm...");
        searchView.setIconified(false);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.gray)); // bạn có thể chọn màu khác
        setupSearchView();
        setupRecyclerView(view);
        setupTabs(view);
        loadProductsFromFirebase();
    }

    private void setupSearchView() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProductsByName(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    adapter.updateList(allProducts); // nếu xoá text, hiển thị lại toàn bộ
                } else {
                    searchProductsByName(newText.trim());
                }
                return true;
            }
        });
    }
    private void searchProductsByName(String name) {
        String lowerCaseName = name.toLowerCase();
        List<Product> filteredList = new ArrayList<>();

        for (Product product : allProducts) {
            if (product.getProName().toLowerCase().contains(lowerCaseName)) {
                filteredList.add(product);
            }
        }

        adapter.updateList(filteredList);
    }


    private void loadProductsFromFirebase() {
        // Lấy data từ: Products -> Coffee -> Items
        db.collection("Products")
                .document("Coffee")
                .collection("Items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allProducts.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = document.toObject(Product.class);
                        product.setId(document.getId());
                        allProducts.add(product);
                    }
                    adapter.updateList(allProducts);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error loading products: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductAdapter(getContext(), allProducts, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupTabs(View view) {
        TabLayout tabLayout = view.findViewById(R.id.tab_filter);
        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
        tabLayout.addTab(tabLayout.newTab().setText("Đặc biệt"));
        tabLayout.addTab(tabLayout.newTab().setText("Coffee"));
        tabLayout.addTab(tabLayout.newTab().setText("Đồ uống khác"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterProducts(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterProducts(int position) {
        List<Product> filteredList = new ArrayList<>();

        switch (position) {
            case 0: // All
                filteredList.addAll(allProducts);
                break;
            case 1: // Special
                for (Product product : allProducts) {
                    if ("Đặc biệt".equals(product.getCategory())) {
                        filteredList.add(product);
                    }
                }
                break;
            case 2: // Coffee
                for (Product product : allProducts) {
                    if ("Coffee".equals(product.getCategory())) {
                        filteredList.add(product);
                    }
                }
                break;
            case 3: // Other drinks
                for (Product product : allProducts) {
                    if ("Đồ uống khác".equals(product.getCategory())) {
                        filteredList.add(product);
                    }
                }
                break;
        }

        adapter.updateList(filteredList);
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(getContext(), ScreenDetail.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }


    @Override
    public void onFavoriteClick(Product product, boolean isFavorite) {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            if (isFavorite) {
                db.collection("users")
                        .document(userId)
                        .collection("favorites")
                        .document(product.getId())
                        .set(product)
                        .addOnSuccessListener(aVoid -> {})
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show());
            } else {
                db.collection("users")
                        .document(userId)
                        .collection("favorites")
                        .document(product.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {})
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show());
            }
        } else {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để thêm sản phẩm yêu thích", Toast.LENGTH_SHORT).show();
        }
    }
}
