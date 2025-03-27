package fpt.anhdhph.bittweet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.ProductAdapter;
import fpt.anhdhph.bittweet.model.Product;

public class FragHome extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_frag_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo RecyclerView từ view
        recyclerView = view.findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Tạo danh sách sản phẩm
        productList = new ArrayList<>();
        productList.add(new Product("Đen", "65.000 VND", R.drawable.sample_coffee));
        productList.add(new Product("Cappuchino", "65.000 VND", R.drawable.sample_coffee));
        productList.add(new Product("Matcha Latte", "65.000 VND", R.drawable.sample_coffee));

        // Khởi tạo Adapter và set lên RecyclerView
        adapter = new ProductAdapter(getContext(), productList);
        recyclerView.setAdapter(adapter);
    }
}
