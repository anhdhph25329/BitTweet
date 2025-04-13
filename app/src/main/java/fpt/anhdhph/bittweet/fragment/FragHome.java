package fpt.anhdhph.bittweet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.ProductAdapter;
import fpt.anhdhph.bittweet.model.Product;
import fpt.anhdhph.bittweet.screen.ScreenDetail;
import android.content.SharedPreferences;
import android.content.Context;

public class FragHome extends Fragment implements ProductAdapter.OnProductClickListener, ProductAdapter.OnFavoriteClickListener {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> allProducts = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_frag_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        setupRecyclerView(view);
        setupTabs(view);
        loadProductsFromFirebase();
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductAdapter(getContext(), allProducts, this, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupTabs(View view) {
        TabLayout tabLayout = view.findViewById(R.id.tab_filter);
        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
        tabLayout.addTab(tabLayout.newTab().setText("Đặc biệt"));
        tabLayout.addTab(tabLayout.newTab().setText("Coffee"));
        tabLayout.addTab(tabLayout.newTab().setText("Đồ uống khác"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { filterProducts(tab.getPosition()); }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadProductsFromFirebase() {
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
                    checkAllFavorites();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi khi tải sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void checkAllFavorites() {
        String userId = getUserId();
        db.collection("Favorites")
                .document(userId)
                .collection("favorite")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<String> favoriteIds = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot) {
                        favoriteIds.add(doc.getId());  // documentId là productId
                    }

                    for (Product product : allProducts) {
                        product.setFavorite(favoriteIds.contains(product.getId()));
                    }

                    adapter.updateList(allProducts);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi khi kiểm tra yêu thích: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void filterProducts(int position) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : allProducts) {
            if (position == 0 ||
                    (position == 1 && "Đặc biệt".equals(product.getCategory())) ||
                    (position == 2 && "Coffee".equals(product.getCategory())) ||
                    (position == 3 && "Đồ uống khác".equals(product.getCategory()))) {
                filteredList.add(product);
            }
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
        String userId = getUserId();
        String productId = product.getId();

        if (isFavorite) {
            Map<String, Object> favData = new HashMap<>();
            favData.put("productId", product.getId());
            favData.put("proName", product.getProName());
            favData.put("image", product.getImage());
            favData.put("mPrice", product.getMPrice());

            db.collection("Favorites")
                    .document(userId)
                    .collection("favorite")
                    .document(productId)
                    .set(favData)
                    .addOnSuccessListener(aVoid -> {
                        product.setFavorite(true);
                        adapter.notifyItemChanged(allProducts.indexOf(product));
                        Toast.makeText(getContext(), "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                    });
        } else {
            db.collection("Favorites")
                    .document(userId)
                    .collection("favorite")
                    .document(productId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        product.setFavorite(false);
                        adapter.notifyItemChanged(allProducts.indexOf(product));
                        Toast.makeText(getContext(), "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private String getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        if (userId == null) {
            userId = UUID.randomUUID().toString();
            prefs.edit().putString("user_id", userId).apply();
        }
        return userId;
    }
    private void toggleFavorite(Product product, boolean isFavorite) {
        String userId = getUserId();
        if (userId == null) return;

        // Lưu trạng thái yêu thích vào SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("FavoritePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Lấy danh sách sản phẩm yêu thích từ SharedPreferences
        Set<String> favoriteSet = sharedPreferences.getStringSet("favoriteProducts", new HashSet<>());

        if (isFavorite) {
            // Thêm sản phẩm vào yêu thích
            favoriteSet.add(product.getId());
        } else {
            // Xóa sản phẩm khỏi yêu thích
            favoriteSet.remove(product.getId());
        }

        // Lưu lại danh sách yêu thích vào SharedPreferences
        editor.putStringSet("favoriteProducts", favoriteSet);
        editor.apply();

        // Cập nhật giao diện
        product.setFavorite(isFavorite);
        adapter.notifyItemChanged(allProducts.indexOf(product));
    }

}
