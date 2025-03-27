package fpt.anhdhph.bittweet.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fpt.anhdhph.bittweet.fragment.FragCart;
import fpt.anhdhph.bittweet.fragment.FragFavorite;
import fpt.anhdhph.bittweet.fragment.FragHome;
import fpt.anhdhph.bittweet.fragment.FragSetting;

public class AdapterFooter extends FragmentStateAdapter {
    FragHome fragHome;
    FragFavorite fragFavorite;
    FragCart fragCart;
    FragSetting fragSetting;
    int pageNumber = 4;
    public AdapterFooter (Fragment fragment){
        super(fragment);
        fragHome = new FragHome();
        fragFavorite = new FragFavorite();
        fragCart = new FragCart();
        fragSetting = new FragSetting();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1: return  fragFavorite;
            case 2: return  fragCart;
            case 3: return fragSetting;
            default: return  fragHome;
        }
    }

    @Override
    public int getItemCount() {
        return pageNumber;
    }
}
