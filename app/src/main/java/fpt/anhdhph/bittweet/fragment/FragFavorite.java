package fpt.anhdhph.bittweet.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

public class FragFavorite extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> favoriteProducts;
    private FirebaseFirestore db;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_frag_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.rcFavorite);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        favoriteProducts = new ArrayList<>();
        productAdapter = new ProductAdapter(
                getContext(),
                favoriteProducts,
                product -> {
                    Intent intent = new Intent(getContext(), ScreenDetail.class);
                    intent.putExtra("product", product);
                    startActivity(intent);
                },
                (product, isFavorite) -> toggleFavorite(product, isFavorite)
        );

        recyclerView.setAdapter(productAdapter);
        loadFavoriteItems();
    }

    private void loadFavoriteItems() {
        if (isLoading) {
            Log.d("FragFavorite", "loadFavoriteItems skipped: already loading");
            return; // Ngăn chặn gọi lại nếu đang tải
        }
        isLoading = true;

        String userId = getUserId();
        if (userId == null) {
            Toast.makeText(getContext(), "Không thể xác định người dùng!", Toast.LENGTH_SHORT).show();
            isLoading = false;
            return;
        }

        // Xóa danh sách hiện tại để tránh trùng lặp
        favoriteProducts.clear();

        db.collection("Favorites")
                .document(userId)
                .collection("favorite")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Task<?>> tasks = new ArrayList<>();
                    Set<String> productIds = new HashSet<>();
                    Map<String, Product> tempProductMap = new HashMap<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String productId = doc.getString("productId");
                        String category = doc.getString("category");

                        if (productId != null && category != null && !productIds.contains(productId)) {
                            productIds.add(productId);
                            Task<DocumentSnapshot> task = db.collection("Products")
                                    .document(category)
                                    .collection("Items")
                                    .document(productId)
                                    .get()
                                    .addOnSuccessListener(productDoc -> {
                                        if (productDoc.exists()) {
                                            Product product = productDoc.toObject(Product.class);
                                            if (product != null) {
                                                product.setId(productDoc.getId());
                                                product.setCategory(category);
                                                product.setFavorite(true);
                                                tempProductMap.put(productId, product); // Lưu vào map tạm
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FragFavorite", "Error loading product: " + e.getMessage());
                                        // Không hiển thị Toast để tránh làm phiền người dùng
                                    });
                            tasks.add(task);
                        }
                    }

                    if (tasks.isEmpty()) {
                        Log.d("FragFavorite", "No favorite products found");
                        productAdapter.updateList(favoriteProducts);
                        isLoading = false;
                        return;
                    }

                    Tasks.whenAllComplete(tasks)
                            .addOnSuccessListener(results -> {
                                favoriteProducts.addAll(tempProductMap.values());
                                // Đảm bảo danh sách không trùng lặp
                                Set<Product> uniqueProducts = new LinkedHashSet<>(favoriteProducts);
                                favoriteProducts.clear();
                                favoriteProducts.addAll(uniqueProducts);
                                Log.d("FragFavorite", "Loaded " + favoriteProducts.size() + " unique favorite products");
                                productAdapter.updateList(favoriteProducts);
                                isLoading = false;
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FragFavorite", "Error completing tasks: " + e.getMessage());
                                productAdapter.updateList(favoriteProducts);
                                isLoading = false;
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("FragFavorite", "Error loading favorites: " + e.getMessage());
                    productAdapter.updateList(favoriteProducts);
                    isLoading = false;
                });
    }

    private void toggleFavorite(Product product, boolean isFavorite) {
        String userId = getUserId();
        if (userId == null) {
            Toast.makeText(getContext(), "Không thể xác định người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isFavorite) {
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
                        loadFavoriteItems();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi khi thêm vào yêu thích: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        product.setFavorite(false);
                        productAdapter.notifyItemChanged(favoriteProducts.indexOf(product));
                    });
        } else {
            db.collection("Favorites")
                    .document(userId)
                    .collection("favorite")
                    .document(product.getId())
                    .delete()
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(), "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                        loadFavoriteItems();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi khi xóa khỏi yêu thích: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        product.setFavorite(true);
                        productAdapter.notifyItemChanged(favoriteProducts.indexOf(product));
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
            loadFavoriteItems();
    }
}