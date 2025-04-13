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
    private final OnProductClickListener productClickListener;
    private final OnFavoriteClickListener favoriteClickListener;

    // Interface: click vào product
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    // Interface: click vào favorite
    public interface OnFavoriteClickListener {
        void onFavoriteClick(Product product, boolean isFavorite);
    }

    public ProductAdapter(Context context, List<Product> productList,
                          OnProductClickListener productClickListener,
                          OnFavoriteClickListener favoriteClickListener) {
        this.context = context;
        this.productList = productList;
        this.productClickListener = productClickListener;
        this.favoriteClickListener = favoriteClickListener;
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
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
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

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

        public void bind(Product product) {
            tvName.setText(product.getProName());
            tvPrice.setText(product.getMPrice() + " VND");

            Glide.with(itemView.getContext())
                    .load(product.getImage())
                    .placeholder(R.drawable.sample_coffee)
                    .into(imgProduct);

            imgFavorite.setImageResource(product.isFavorite()
                    ? R.drawable.ic_favorite_filled
                    : R.drawable.ic_favorite_selector
            );

            // Bấm vào sản phẩm
            itemView.setOnClickListener(v -> {
                if (productClickListener != null) {
                    productClickListener.onProductClick(product);
                }
            });

            // Bấm vào trái tim
            imgFavorite.setOnClickListener(v -> {
                if (favoriteClickListener != null) {
                    boolean newFavoriteState = !product.isFavorite();
                    product.setFavorite(newFavoriteState);
                    favoriteClickListener.onFavoriteClick(product, newFavoriteState);
                    notifyItemChanged(getAdapterPosition()); // cập nhật đúng item
                }
            });
        }
    }
}
