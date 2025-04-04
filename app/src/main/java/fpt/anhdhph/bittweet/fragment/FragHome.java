package fpt.anhdhph.bittweet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.ProductAdapter;
import fpt.anhdhph.bittweet.adapter.ProductClickListener;
import fpt.anhdhph.bittweet.model.Product;
import fpt.anhdhph.bittweet.screen.ScreenDetail;

public class FragHome extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho FragHome
        return inflater.inflate(R.layout.layout_frag_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up RecyclerView
        setupRecyclerView(view);

        // Set up TabLayout
        setupTabs(view);
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Thêm các sản phẩm mẫu
        productList = new ArrayList<>();
        productList.add(new Product("Black", "65.000 VND", R.drawable.sample_coffee));
        productList.add(new Product("Cappuchino", "65.000 VND", R.drawable.sample_coffee));
        productList.add(new Product("Matcha Latte", "65.000 VND", R.drawable.sample_coffee));
        productList.add(new Product("Matchachino", "65.000 VND", R.drawable.sample_coffee));
        productList.add(new Product("Coffee egg", "65.000 VND", R.drawable.sample_coffee));
        productList.add(new Product("Awake", "65.000 VND", R.drawable.sample_coffee));

        // Khởi tạo adapter
        adapter = new ProductAdapter(getContext(), productList, new ProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                // Chuyển đến màn hình chi tiết sản phẩm
                Intent intent = new Intent(getContext(), ScreenDetail.class);
                intent.putExtra("product_name", product.getName());
                intent.putExtra("product_price", product.getPrice());
                intent.putExtra("product_image", product.getImageResId());
                startActivity(intent);
            }
        });


        recyclerView.setAdapter(adapter);
    }

    private void setupTabs(View view) {
        TabLayout tabLayout = view.findViewById(R.id.tab_filter);
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Special"));
        tabLayout.addTab(tabLayout.newTab().setText("Coffee"));
        tabLayout.addTab(tabLayout.newTab().setText("Other drinks"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productList.clear();

                switch (tab.getPosition()) {
                    case 0: // All
                        productList.add(new Product("Black", "65.000 VND", R.drawable.sample_coffee));
                        productList.add(new Product("Cappuchino", "65.000 VND", R.drawable.sample_coffee));
                        productList.add(new Product("Matcha Latte", "65.000 VND", R.drawable.sample_coffee));
                        productList.add(new Product("Matchachino", "65.000 VND", R.drawable.sample_coffee));
                        productList.add(new Product("Coffee egg", "65.000 VND", R.drawable.sample_coffee));
                        productList.add(new Product("Awake", "65.000 VND", R.drawable.sample_coffee));
                        break;
                    case 1: // Special
                        productList.add(new Product("Coffee egg", "65.000 VND", R.drawable.sample_coffee));
                        productList.add(new Product("Awake", "65.000 VND", R.drawable.sample_coffee));
                        break;
                    case 2: // Coffee
                        productList.add(new Product("Black", "65.000 VND", R.drawable.sample_coffee));
                        productList.add(new Product("Cappuchino", "65.000 VND", R.drawable.sample_coffee));
                        break;
                    case 3: // Other drinks
                        productList.add(new Product("Matcha Latte", "65.000 VND", R.drawable.sample_coffee));
                        productList.add(new Product("Matchachino", "65.000 VND", R.drawable.sample_coffee));
                        break;
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}
