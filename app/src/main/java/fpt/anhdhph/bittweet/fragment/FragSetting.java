package fpt.anhdhph.bittweet.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.screen.ScreenHistory;
import fpt.anhdhph.bittweet.screen.ScreenLogin;
import fpt.anhdhph.bittweet.screen.ScreenManageCli;
import fpt.anhdhph.bittweet.screen.ScreenManageIncome;
import fpt.anhdhph.bittweet.screen.ScreenManageOrder;
import fpt.anhdhph.bittweet.screen.ScreenManagePro;
import fpt.anhdhph.bittweet.screen.ScreenProfile;

public class FragSetting extends Fragment {

    SharedPreferences sharedPreferences;
    LinearLayout btnProfile, btnHistory, btnManageCli, btnManagePro, btnManageIncome, btnManageOrder, btnLogOut;
    ImageView lock1, lock2, lock3, lock4;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_frag_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnProfile = view.findViewById(R.id.btnProfile);
        btnHistory = view.findViewById(R.id.btnHistory);
        btnManageCli = view.findViewById(R.id.btnManageCli);
        btnManagePro = view.findViewById(R.id.btnManagePro);
        btnManageIncome = view.findViewById(R.id.btnManageIncome);
        btnManageOrder = view.findViewById(R.id.btnManageOrder);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        lock1 = view.findViewById(R.id.lock1);
        lock2 = view.findViewById(R.id.lock2);
        lock3 = view.findViewById(R.id.lock3);
        lock4 = view.findViewById(R.id.lock4);

        sharedPreferences = requireActivity().getSharedPreferences("LoginPref", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        String role = sharedPreferences.getString("role", "user");
        if (!role.equals("admin")) {
            lock1.setVisibility(View.VISIBLE);
            lock2.setVisibility(View.VISIBLE);
            lock3.setVisibility(View.VISIBLE);
            lock4.setVisibility(View.VISIBLE);
            btnManageCli.setClickable(false);
            btnManagePro.setClickable(false);
            btnManageIncome.setClickable(false);
            btnManageOrder.setClickable(false);
        }else {
            lock1.setVisibility(View.GONE);
            lock2.setVisibility(View.GONE);
            lock3.setVisibility(View.GONE);
            lock4.setVisibility(View.GONE);
        }

        if (isLoggedIn) {
            btnProfile.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ScreenProfile.class);
                startActivity(intent);
            });
        } else {
            Toast.makeText(getContext(), "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), ScreenLogin.class);
            startActivity(intent);
            requireActivity().finish();
        }

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScreenHistory.class);
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

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                SharedPreferences myAppPrefs = getContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor myAppEditor = myAppPrefs.edit();
                myAppEditor.clear();
                myAppEditor.apply();

                Intent intent = new Intent(getContext(), ScreenLogin.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }

}
