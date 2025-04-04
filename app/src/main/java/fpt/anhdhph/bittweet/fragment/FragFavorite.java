package fpt.anhdhph.bittweet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.AdapterFavorite;
import fpt.anhdhph.bittweet.model.FavoriteItem;


public class FragFavorite extends Fragment {

    private RecyclerView recyclerView;
    private AdapterFavorite adapterFavorite;
    private List<FavoriteItem> favoriteItemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_frag_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rcFavorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        favoriteItemList = new ArrayList<>();
        favoriteItemList.add(new FavoriteItem("Caffee latte", R.drawable.logo));
        favoriteItemList.add(new FavoriteItem("Espresso", R.drawable.logo));
        favoriteItemList.add(new FavoriteItem("Cappuccino", R.drawable.logo));

        adapterFavorite = new AdapterFavorite(getContext(), favoriteItemList);
        recyclerView.setAdapter(adapterFavorite);
    }
}
