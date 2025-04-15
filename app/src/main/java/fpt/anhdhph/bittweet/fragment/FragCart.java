package fpt.anhdhph.bittweet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpt.anhdhph.bittweet.DAO.CartDAO;
import fpt.anhdhph.bittweet.R;

//import fpt.anhdhph.bittweet.adapter.AdapterCart;
import fpt.anhdhph.bittweet.model.CartItem;
import fpt.anhdhph.bittweet.screen.ScreenPayment;

public class FragCart extends Fragment {

    private Button btnPay;
    private RecyclerView recyclerCart;
    private TextView totalPriceText;
//    private AdapterCart cartAdapter;
    private CartDAO cartDAO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_frag_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ view
        btnPay = view.findViewById(R.id.btnPay);
        recyclerCart = view.findViewById(R.id.recyclerCart);
        totalPriceText = view.findViewById(R.id.total_price);

        // Setup RecyclerView
        recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));
        cartDAO = new CartDAO();

        // Lấy dữ liệu từ Firestore
        cartDAO.getCartItems(cartItems -> {
//            cartAdapter = new AdapterCart(cartItems);
//            recyclerCart.setAdapter(cartAdapter);
            updateTotalPrice(cartItems);
        });

        // Sự kiện click nút thanh toán
        btnPay.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ScreenPayment.class);
            startActivity(intent);
        });
    }

    private void updateTotalPrice(List<CartItem> cartItems) {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceText.setText("Tổng: " + total + " VND");
    }
}
