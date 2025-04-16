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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fpt.anhdhph.bittweet.DAO.CartDAO;
import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.AdapterCart;
import fpt.anhdhph.bittweet.model.CartItem;
import fpt.anhdhph.bittweet.screen.ScreenPayment;

public class FragCart extends Fragment {

    private Button btnPay;
    private RecyclerView recyclerCart;
    private TextView totalPriceText;
    private AdapterCart cartAdapter;
    private CartDAO cartDAO;
    private List<CartItem> cartItemList = new ArrayList<>();

    public FragCart() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_frag_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPay = view.findViewById(R.id.btnPay);
        recyclerCart = view.findViewById(R.id.recyclerCart);
        totalPriceText = view.findViewById(R.id.total_price);

        recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));
        cartDAO = new CartDAO(requireContext());

        loadCartItems();

        btnPay.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ScreenPayment.class);
            startActivity(intent);
        });
    }

    private void loadCartItems() {
        cartDAO.getCartItems(cartItems -> {
            cartItemList.clear();
            cartItemList.addAll(cartItems);

            cartAdapter = new AdapterCart(getContext(), cartItemList);
            recyclerCart.setAdapter(cartAdapter);

            updateTotalPrice();

            // Khi số lượng thay đổi, cập nhật lại tổng tiền
            cartAdapter.setOnQuantityChangedListener(this::updateTotalPrice);
        });
    }

    // Hàm tính và hiển thị tổng tiền
    private void updateTotalPrice() {
        int total = 0;
        for (CartItem item : cartItemList) {
            try {
                int price = Integer.parseInt(item.getPrice());
                int quantity = Integer.parseInt(item.getQuantity());
                total += price * quantity;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        totalPriceText.setText("Tổng: \n" + formatter.format(total) + " VNĐ");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems();
    }
}