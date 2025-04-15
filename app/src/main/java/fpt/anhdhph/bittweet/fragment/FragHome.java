package fpt.anhdhph.bittweet.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.ProductAdapter;
import fpt.anhdhph.bittweet.model.Product;
import fpt.anhdhph.bittweet.screen.ScreenDetail;

public class FragHome extends Fragment implements ProductAdapter.OnProductClickListener, ProductAdapter.OnFavoriteClickListener {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();
    private List<String> categoryList = new ArrayList<>();
    private FirebaseFirestore db;
    private boolean isLoading = false;
    private EditText searchEditText;

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
        setupSearchBar(view);
        loadCategories(() -> loadProductsFromFirebase());
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductAdapter(getContext(), filteredProducts, this, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupTabs(View view) {
        TabLayout tabLayout = view.findViewById(R.id.tab_filter);
        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
        tabLayout.addTab(tabLayout.newTab().setText("Cocktail"));
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

    private void setupSearchBar(View view) {
        searchEditText = view.findViewById(R.id.edt_search);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterProductsBySearch(s.toString().toLowerCase());
            }
        });
    }

    private void filterProductsBySearch(String query) {
        filteredProducts.clear();
        if (query.isEmpty()) {
            filteredProducts.addAll(allProducts);
        } else {
            for (Product product : allProducts) {
                if (product.getProName() != null && product.getProName().toLowerCase().contains(query)) {
                    filteredProducts.add(product);
                }
            }
        }
        adapter.updateList(filteredProducts);
    }

    private void loadCategories(Runnable onComplete) {
        if (isLoading) return;
        isLoading = true;
        db.collection("Categories").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoryList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String categoryName = doc.getString("name");
                        if (categoryName != null) {
                            categoryList.add(categoryName);
                        }
                    }
                    if (categoryList.isEmpty()) {
                        Toast.makeText(getContext(), "Không có danh mục nào!", Toast.LENGTH_SHORT).show();
                    }
                    onComplete.run();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi tải danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    onComplete.run();
                })
                .addOnCompleteListener(task -> isLoading = false);
    }

    private void loadProductsFromFirebase() {
        List<Task<?>> tasks = new ArrayList<>();
        allProducts.clear();
        filteredProducts.clear();

        if (categoryList.isEmpty()) {
            adapter.updateList(filteredProducts);
            return;
        }

        for (String categoryName : categoryList) {
            Task<?> task = db.collection("Products")
                    .document(categoryName)
                    .collection("Items")
                    .get()
                    .addOnSuccessListener(itemSnapshots -> {
                        for (QueryDocumentSnapshot document : itemSnapshots) {
                            Product product = document.toObject(Product.class);
                            product.setId(document.getId());
                            product.setCategory(categoryName);
                            allProducts.add(product);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi khi tải sản phẩm từ danh mục " + categoryName, Toast.LENGTH_SHORT).show();
                    });
            tasks.add(task);
        }

        Tasks.whenAllComplete(tasks)
                .addOnSuccessListener(results -> {
                    checkAllFavorites();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi tải sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    adapter.updateList(filteredProducts);
                });
    }

    private void checkAllFavorites() {
        String userId = getUserId();
        if (userId == null) {
            Toast.makeText(getContext(), "Không thể xác định người dùng!", Toast.LENGTH_SHORT).show();
            adapter.updateList(filteredProducts);
            return;
        }

        db.collection("Favorites")
                .document(userId)
                .collection("favorite")
                .get()
                .addOnSuccessListener(snapshot -> {
                    Set<String> favoriteIds = new HashSet<>();
                    for (DocumentSnapshot doc : snapshot) {
                        String productId = doc.getString("productId");
                        if (productId != null) {
                            favoriteIds.add(productId);
                        }
                    }

                    for (Product product : allProducts) {
                        product.setFavorite(favoriteIds.contains(product.getId()));
                    }

                    Set<Product> uniqueProducts = new LinkedHashSet<>(allProducts);
                    allProducts.clear();
                    allProducts.addAll(uniqueProducts);
                    filteredProducts.clear();
                    filteredProducts.addAll(allProducts); // Cập nhật filteredProducts ban đầu

                    adapter.updateList(filteredProducts);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi kiểm tra yêu thích: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    adapter.updateList(filteredProducts);
                });
    }

    private void filterProducts(int position) {
        filteredProducts.clear();
        for (Product product : allProducts) {
            if (position == 0) {
                filteredProducts.add(product);
            } else if (position == 1 && "Cocktail".equals(product.getCategory())) {
                filteredProducts.add(product);
            } else if (position == 2 && "Coffee".equals(product.getCategory())) {
                filteredProducts.add(product);
            } else if (position == 3 && !"Coffee".equals(product.getCategory()) && !"Cocktail".equals(product.getCategory())) {
                filteredProducts.add(product);
            }
        }
        adapter.updateList(filteredProducts);
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
        if (userId == null) {
            Toast.makeText(getContext(), "Không thể xác định người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isFavorite) {
            db.collection("Favorites")
                    .document(userId)
                    .collection("favorite")
                    .document(product.getId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Toast.makeText(getContext(), "Sản phẩm đã có trong danh sách yêu thích", Toast.LENGTH_SHORT).show();
                        } else {
                            Map<String, Object> favoriteData = new HashMap<>();
                            favoriteData.put("productId", product.getId());
                            favoriteData.put("category", product.getCategory());

                            db.collection("Favorites")
                                    .document(userId)
                                    .collection("favorite")
                                    .document(product.getId())
                                    .set(favoriteData)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(getContext(), "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Lỗi khi thêm vào yêu thích: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        product.setFavorite(false);
                                        adapter.notifyItemChanged(filteredProducts.indexOf(product));
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi khi kiểm tra yêu thích: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        product.setFavorite(false);
                        adapter.notifyItemChanged(filteredProducts.indexOf(product));
                    });
        } else {
            db.collection("Favorites")
                    .document(userId)
                    .collection("favorite")
                    .document(product.getId())
                    .delete()
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(), "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi khi xóa khỏi yêu thích: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        product.setFavorite(true);
                        adapter.notifyItemChanged(filteredProducts.indexOf(product));
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

    @Override
    public void onResume() {
        super.onResume();
        checkAllFavorites();
    }
}