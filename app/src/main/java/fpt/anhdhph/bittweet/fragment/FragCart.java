package fpt.anhdhph.bittweet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.CartAdapter;
import fpt.anhdhph.bittweet.item.CartItem;

public class FragCart extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<CartItem> cartItemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_frag_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tìm RecyclerView trong layout
        recyclerView = view.findViewById(R.id.rv_cart_items);

        // Khởi tạo danh sách sản phẩm
        cartItemList = new ArrayList<>();
        cartItemList.add(new CartItem("Caffee latte", "S", 1, 65000, R.drawable.sample_coffee));
        cartItemList.add(new CartItem("Caffee latte", "S", 1, 65000, R.drawable.sample_coffee));

        // Khởi tạo Adapter và kết nối với RecyclerView
        adapter = new CartAdapter(cartItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }
}
