package fpt.anhdhph.bittweet.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fpt.anhdhph.bittweet.fragment.FragCart;
import fpt.anhdhph.bittweet.fragment.FragFavorite;
import fpt.anhdhph.bittweet.fragment.FragHome;

public class AdapterFooter extends FragmentStateAdapter {
    FragHome fragHome;
    FragFavorite fragFavorite;
    FragCart fragCart;
    int pageNumber = 3;
    public AdapterFooter (Fragment fragment){
        super(fragment);
        fragHome = new FragHome();
        fragFavorite = new FragFavorite();
        fragCart = new FragCart();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1: return  fragFavorite;
            case 2: return  fragCart;
            default: return  fragHome;
        }
    }

    @Override
    public int getItemCount() {
        return pageNumber;
    }
}
