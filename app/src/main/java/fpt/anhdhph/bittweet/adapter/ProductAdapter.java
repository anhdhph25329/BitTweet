package fpt.anhdhph.bittweet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private List<Product> productList;
    private final ProductClickListener productClickListener;

    public interface ProductClickListener {
        void onProductClick(Product product);
        void onFavoriteClick(Product product, boolean isFavorite);
    }

    public ProductAdapter(Context context, List<Product> productList, ProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.productClickListener = listener;
    }

    public void updateList(List<Product> newList) {
        productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, productClickListener);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgProduct;
        private final TextView tvName;
        private final TextView tvPrice;
        private final ImageView imgFavorite;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            imgFavorite = itemView.findViewById(R.id.img_favorite);
        }

        public void bind(Product product, ProductClickListener listener) {
            tvName.setText(product.getProName());
            tvPrice.setText(product.getMPrice() + " VND");

            // Load image from Firebase using Glide
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImage())
                        .placeholder(R.drawable.sample_coffee)
                        .into(imgProduct);
            }

            // Set favorite icon based on product status (you need to implement this logic)
            imgFavorite.setImageResource(product.isFavorite() ?
                    R.drawable.ic_favorite_filled : R.drawable.ic_favorite_selector);

            // Handle click events
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });

            imgFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(product, !product.isFavorite());
                }
            });
        }
    }
}