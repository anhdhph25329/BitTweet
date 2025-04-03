package fpt.anhdhph.bittweet.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.FavoriteItem;

public class AdapterFavorite extends RecyclerView.Adapter<AdapterFavorite.FavoriteViewHolder> {

    private Context context;
    private List<FavoriteItem> favoriteItemList;

    public AdapterFavorite(Context context, List<FavoriteItem> favoriteItemList) {
        this.context = context;
        this.favoriteItemList = favoriteItemList;
    }

    @NonNull
    @Override
    public AdapterFavorite.FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFavorite.FavoriteViewHolder holder, int position) {
        FavoriteItem favoriteItem = favoriteItemList.get(position);
        holder.tvCoffeeName.setText(favoriteItem.getName());
        holder.imgCoffee.setImageResource(favoriteItem.getImageResource());

        holder.btnBuy.setOnClickListener(v -> {
            // Xử lý sự kiện khi click vào nút Buy
        });
    }

    @Override
    public int getItemCount() {
        return favoriteItemList.size();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCoffee;
        TextView tvCoffeeName;
        Button btnBuy;
        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCoffee = itemView.findViewById(R.id.imgCoffee);
            tvCoffeeName = itemView.findViewById(R.id.tvCoffeeName);
            btnBuy = itemView.findViewById(R.id.btnBuy);
        }
    }
}
