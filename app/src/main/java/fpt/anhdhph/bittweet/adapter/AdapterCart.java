package fpt.anhdhph.bittweet.adapter;

import android.content.Context;
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

    // Listener để thông báo khi số lượng thay đổi
    public interface OnQuantityChangedListener {
        void onQuantityChanged();
    }

    private OnQuantityChangedListener listener;

    // Setter để FragCart truyền listener vào
    public void setOnQuantityChangedListener(OnQuantityChangedListener listener) {
        this.listener = listener;
    }


    public AdapterCart(Context context, List<CartItem> cartItems) {
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

        holder.tvTitle.setText(item.getName());
        holder.tvPrice.setText(item.getPrice() + " VNĐ");
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.btnSize.setText(item.getSize());

        // Load ảnh sản phẩm
        if (item.getImage() != null && !item.getImage().isEmpty()) {
            Picasso.get().load(item.getImage()).into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.sample_coffee); // Ảnh mặc định
        }

        // (Tuỳ chọn) xử lý tăng/giảm số lượng tại đây nếu bạn muốn
        // Ví dụ:

        holder.btnIncrease.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            if (listener != null) listener.onQuantityChanged();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            try {
                int currentQuantity = Integer.parseInt(item.getQuantity()); // Ép kiểu từ String sang int

                if (currentQuantity > 1) {
                    currentQuantity--; // Giảm số lượng
                    item.setQuantity(String.valueOf(currentQuantity)); // Gán lại (nếu quantity là String)

                    notifyItemChanged(position); // Cập nhật lại item hiển thị
                    if (listener != null) listener.onQuantityChanged(); // Cập nhật tổng tiền
                }
            } catch (NumberFormatException e) {
                Log.e("AdapterCart", "Lỗi chuyển đổi quantity sang số nguyên", e);
            }
        });



    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }



    public class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView tvTitle, tvNote, tvPrice, tvQuantity;
        Button btnSize, btnIncrease, btnDecrease;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.image_product);
            tvTitle = itemView.findViewById(R.id.text_title);
            tvNote = itemView.findViewById(R.id.text_note);
            tvPrice = itemView.findViewById(R.id.text_price);
            tvQuantity = itemView.findViewById(R.id.text_quantity);
            btnSize = itemView.findViewById(R.id.btn_size);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
        }
    }
}
