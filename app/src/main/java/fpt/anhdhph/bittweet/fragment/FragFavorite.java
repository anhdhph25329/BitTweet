package fpt.anhdhph.bittweet.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.ProductAdapter;
import fpt.anhdhph.bittweet.model.Favorite;
import fpt.anhdhph.bittweet.model.Product;
import fpt.anhdhph.bittweet.screen.ScreenDetail;

public class FragFavorite extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> favoriteProducts;
    private FirebaseFirestore db;

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
        String userId = getUserId();

        db.collection("Favorites")
                .document(userId)
                .collection("favorite")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    favoriteProducts.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String productId = doc.getString("productId");

                        db.collection("Products")
                                .document("Coffee")
                                .collection("Items")
                                .document(productId)
                                .get()
                                .addOnSuccessListener(productDoc -> {
                                    if (productDoc.exists()) {
                                        Product product = productDoc.toObject(Product.class);
                                        if (product != null) {
                                            product.setId(productDoc.getId());
                                            product.setFavorite(true);
                                            favoriteProducts.add(product);
                                            productAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                    }

                    if (favoriteProducts.isEmpty()) {
                        Toast.makeText(getContext(), "Không có sản phẩm yêu thích nào", Toast.LENGTH_SHORT).show();
                    } else {
                        productAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi khi tải danh sách yêu thích", Toast.LENGTH_SHORT).show());
    }

    private void toggleFavorite(Product product, boolean isFavorite) {
        String userId = getUserId();
        if (userId == null) return;

        if (isFavorite) {
            Favorite fav = new Favorite(userId, product.getId(), product.getProName(), product.getImage(), product.getMPrice());
            db.collection("Favorites")
                    .document(userId)
                    .collection("favorite")
                    .document(product.getId())
                    .set(fav)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(), "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                        loadFavoriteItems();
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
}
