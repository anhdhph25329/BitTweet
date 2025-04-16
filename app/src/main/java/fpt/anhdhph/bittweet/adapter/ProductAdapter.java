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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private List<Product> productList;
    private final OnProductClickListener productClickListener;
    private final OnFavoriteClickListener favoriteClickListener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Product product, boolean isFavorite);
    }

    public ProductAdapter(Context context, List<Product> productList,
                          OnProductClickListener productClickListener,
                          OnFavoriteClickListener favoriteClickListener) {
        this.context = context;
        this.productList = productList != null ? productList : new ArrayList<>();
        this.productClickListener = productClickListener;
        this.favoriteClickListener = favoriteClickListener;
    }

    public void updateList(List<Product> newList) {
        Set<Product> uniqueProducts = new LinkedHashSet<>(newList != null ? newList : new ArrayList<>());
        this.productList = new ArrayList<>(uniqueProducts);
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
        return productList.size();
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
            tvPrice.setText(product.getSPrice() + " VND");

            Glide.with(itemView.getContext())
                    .load(product.getImage())
                    .placeholder(R.drawable.sample_coffee)
                    .into(imgProduct);

            // Sử dụng selector để hiển thị trạng thái yêu thích
            imgFavorite.setSelected(product.isFavorite());

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
                    imgFavorite.setSelected(newFavoriteState);
                    favoriteClickListener.onFavoriteClick(product, newFavoriteState);
                }
            });
        }
    }
}