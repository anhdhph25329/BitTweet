package fpt.anhdhph.bittweet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.AdapterFavorite;
import fpt.anhdhph.bittweet.model.FavoriteCoffeeItem;

public class FragFavorite extends Fragment {

    private ListView lvFavorite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout chứa ListView (ví dụ: layout_frag_favorite.xml)
        return inflater.inflate(R.layout.layout_frag_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ ListView từ layout
        lvFavorite = view.findViewById(R.id.lvFavorite);

        // Tạo danh sách dữ liệu với 3 item CoffeeItem
        List<FavoriteCoffeeItem> coffeeList = new ArrayList<>();
        coffeeList.add(new FavoriteCoffeeItem(R.drawable.ic_launcher_background, "Caffee latte"));
        coffeeList.add(new FavoriteCoffeeItem(R.drawable.ic_launcher_background, "Cappuccino"));
        coffeeList.add(new FavoriteCoffeeItem(R.drawable.ic_launcher_background, "Mocha"));
        coffeeList.add(new FavoriteCoffeeItem(R.drawable.ic_launcher_background, "Mochi"));
        coffeeList.add(new FavoriteCoffeeItem(R.drawable.ic_launcher_background, "Sting"));
        coffeeList.add(new FavoriteCoffeeItem(R.drawable.ic_launcher_background, "Coca"));

        // Tạo adapter với requireContext() (trong Fragment)
        AdapterFavorite adapter = new AdapterFavorite(requireContext(), coffeeList);

        // Gán adapter cho ListView
        lvFavorite.setAdapter(adapter);
    }
}

