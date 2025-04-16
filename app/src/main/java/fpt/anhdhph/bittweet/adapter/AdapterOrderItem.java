package fpt.anhdhph.bittweet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.CartItem;

public class AdapterOrderItem extends RecyclerView.Adapter<AdapterOrderItem.OrderItemViewHolder> {

    private Context context;
    private List<CartItem> items;

    public AdapterOrderItem(Context context, List<CartItem> items) {
        this.context = context;
        this.items = items != null ? items : new ArrayList<>();
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_order_product, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        CartItem item = items.get(position);

        // Hiển thị hình ảnh sản phẩm
        if (item.getImage() != null && !item.getImage().isEmpty()) {
            Picasso.get().load(item.getImage()).into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.sample_coffee);
        }

        holder.tvProductName.setText(item.getProName() != null ? item.getProName() : "Không có tên");
        holder.tvSize.setText("Size: " + (item.getSize() != null ? item.getSize() : "N/A"));

        // Hiển thị số lượng và giá
        String quantityPrice = "Số lượng: " + (item.getQuantity() != null ? item.getQuantity() : "1") +
                " - Giá: " + (item.getPrice() != null ? item.getPrice() : "0") + " VNĐ";
        holder.tvQuantityPrice.setText(quantityPrice);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvSize, tvQuantityPrice;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvSize = itemView.findViewById(R.id.tv_size);
            tvQuantityPrice = itemView.findViewById(R.id.tv_quantity_price);
        }
    }
}