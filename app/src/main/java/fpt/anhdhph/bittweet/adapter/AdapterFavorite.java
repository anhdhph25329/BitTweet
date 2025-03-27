package fpt.anhdhph.bittweet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.FavoriteCoffeeItem;

public class AdapterFavorite extends BaseAdapter {
    @Override
    public int getCount() {
        return coffeeList.size();
    }

    @Override
    public Object getItem(int position) {
        return coffeeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // 1. Kiểm tra xem convertView đã được tạo trước đó chưa
        if (convertView == null) {
            // 2. Nếu chưa, inflate layout item_favorite
            convertView = inflater.inflate(R.layout.layout_item_favorite, parent, false);

            // 3. Tạo một ViewHolder để lưu trữ các view con
            holder = new ViewHolder();
            holder.imgCoffee = convertView.findViewById(R.id.imgCoffee);
            holder.tvCoffeeName = convertView.findViewById(R.id.tvCoffeeName);
            holder.btnBuy = convertView.findViewById(R.id.btnBuy);

            // 4. Đính kèm holder vào convertView
            convertView.setTag(holder);
        } else {
            // 5. Nếu convertView không null, tái sử dụng holder đã có
            holder = (ViewHolder) convertView.getTag();
        }

        // 6. Lấy dữ liệu item tương ứng với position
        FavoriteCoffeeItem coffeeItem = coffeeList.get(position);

        // 7. Gán dữ liệu vào các view con
        holder.imgCoffee.setImageResource(coffeeItem.getImageResource());
        holder.tvCoffeeName.setText(coffeeItem.getName());

        // 8. Xử lý sự kiện cho nút Buy
        holder.btnBuy.setOnClickListener(v -> {
            // Code khi nhấn Buy, ví dụ:
            // Toast.makeText(context, "Mua " + coffeeItem.getName(), Toast.LENGTH_SHORT).show();
        });

        // 9. Trả về convertView đã chứa dữ liệu
        return convertView;
    }
    static class ViewHolder {
        ImageView imgCoffee;
        TextView tvCoffeeName;
        Button btnBuy;
    }


    private Context context;          // Môi trường context, thường là Activity hoặc Fragment
    private List<FavoriteCoffeeItem> coffeeList;  // Danh sách dữ liệu (model) để hiển thị
    private LayoutInflater inflater;  // Dùng để "inflate" layout cho mỗi item

    public AdapterFavorite(Context context, List<FavoriteCoffeeItem> coffeeList) {
        this.context = context;
        this.coffeeList = coffeeList;
        this.inflater = LayoutInflater.from(context);
    }


}
