package fpt.anhdhph.bittweet.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.CartItem;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.CartViewHolder> {

    private List<CartItem> cartItems;

    public AdapterCart(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
    @NonNull
    @Override
    public AdapterCart.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCart.CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.itemName.setText(item.getName());
        holder.itemNote.setText(item.getNote());
        holder.itemPrice.setText(item.getPrice() + " VND");
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));

        // In ra URL ảnh để kiểm tra
        Log.d("ImageURL", "URL ảnh: " + item.getImageUrl());

        // Load ảnh từ Firestore URL bằng Picasso
        Picasso.get()
                .load(item.getImageUrl())
                .placeholder(R.drawable.logo)  // Ảnh mặc định khi đang tải
                .error(R.drawable.ic_launcher_background)        // Ảnh nếu lỗi
                .into(holder.itemImage);


        holder.btnIncrease.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName, itemNote, itemPrice, itemQuantity;
        Button btnIncrease, btnDecrease;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemNote = itemView.findViewById(R.id.item_note);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
        }
    }
}
