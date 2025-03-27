package fpt.anhdhph.bittweet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.screen.ScreenManageCli;
import fpt.anhdhph.bittweet.screen.ScreenManageIncome;
import fpt.anhdhph.bittweet.screen.ScreenManageOrder;
import fpt.anhdhph.bittweet.screen.ScreenManagePro;
import fpt.anhdhph.bittweet.screen.ScreenProfile;

public class FragSetting extends Fragment {

    LinearLayout btnProfile, btnManageCli, btnManagePro, btnManageIncome, btnManageOrder, btnLogOut;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_frag_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnProfile = view.findViewById(R.id.btnProfile);
        btnManageCli = view.findViewById(R.id.btnManageCli);
        btnManagePro = view.findViewById(R.id.btnManagePro);
        btnManageIncome = view.findViewById(R.id.btnManageIncome);
        btnManageOrder = view.findViewById(R.id.btnManageOrder);
        btnLogOut = view.findViewById(R.id.btnLogOut);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScreenProfile.class);
                startActivity(intent);
            }
        });

        btnManageCli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScreenManageCli.class);
                startActivity(intent);
            }
        });

        btnManagePro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScreenManagePro.class);
                startActivity(intent);
            }
        });

        btnManageIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScreenManageIncome.class);
                startActivity(intent);
            }
        });

        btnManageOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScreenManageOrder.class);
                startActivity(intent);
            }
        });

    }

}
