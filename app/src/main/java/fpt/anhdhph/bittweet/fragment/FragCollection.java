package fpt.anhdhph.bittweet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.adapter.AdapterFooter;

public class FragCollection extends Fragment {

    ViewPager2 viewPager2;
    TabLayout tabLayout;
    AdapterFooter adapterFooter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_frag_collection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager2 = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        adapterFooter = new AdapterFooter(this);
        viewPager2.setAdapter(adapterFooter);


        TabLayoutMediator mediator = new TabLayoutMediator(
                tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position){
                            case 0:
                                tab.setText("Home");
                                tab.setIcon(R.drawable.ic_home);
                                break;
                            case 1:
                                tab.setText("Favorite");
                                tab.setIcon(R.drawable.ic_favorite);
                                break;
                            case 2:
                                tab.setText("Cart");
                                tab.setIcon(R.drawable.ic_cart);
                                break;
                            case 3:
                                tab.setText("Setting");
                                tab.setIcon(R.drawable.ic_setting);
                                break;
                        }
                    }
                }
        );
        mediator.attach();

    }
}